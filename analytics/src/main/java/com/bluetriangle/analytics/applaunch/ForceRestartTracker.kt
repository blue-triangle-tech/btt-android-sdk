package com.bluetriangle.analytics.applaunch

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.app.ApplicationExitInfo
import android.content.Context
import android.os.Build
import com.bluetriangle.analytics.Constants
import com.bluetriangle.analytics.Logger
import com.bluetriangle.analytics.Timer.Companion.FIELD_CONTENT_GROUP_NAME
import com.bluetriangle.analytics.Timer.Companion.FIELD_PAGE_NAME
import com.bluetriangle.analytics.Timer.Companion.FIELD_TRAFFIC_SEGMENT_NAME
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.eventhub.AppEventConsumer
import com.bluetriangle.analytics.eventhub.AppEventHub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class ForceRestartTracker(
    private val logger: Logger?, private val context: Context, private val reporter:AppLaunchReporter, forceRestartDuration: Double
) : AppEventConsumer {
    private var _forceRestartDuration: Long = 10_000
    private var appForceKillReportJob: Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        _forceRestartDuration = (forceRestartDuration * 1000).toLong()
    }

    fun setForceRestartDuration(forceRestartDuration: Double) {
        _forceRestartDuration = (forceRestartDuration * 1000).toLong()
    }

    fun start() {
        val prefs =
            context.getSharedPreferences(Tracker.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        val lastForegroundTime = prefs.getLong(Constants.APP_LAST_FOREGROUND_TIME, 0L)
        val backgroundTime = prefs.getLong(Constants.APP_BACKGROUND_TIME, 0L)

        checkAppPreviousExit(System.currentTimeMillis(), lastForegroundTime, backgroundTime)

        AppEventHub.instance.addConsumer(this)
    }

    fun stop() {
        AppEventHub.instance.removeConsumer(this)
        scope.cancel()
    }

    // When the app goes to the background, so a kill long after that (e.g. swiped away from Recents
    // 30 min later) isn't reported as a force restart. Reset to 0 while the app is in the foreground.
    override fun onAppMovedToBackground(application: Application) {
        saveBackgroundTime(System.currentTimeMillis())
    }

    override fun onActivityStarted(activity: Activity) {
        saveBackgroundTime(0L)
    }

    private fun saveBackgroundTime(time: Long) {
        context.getSharedPreferences(Tracker.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(Constants.APP_BACKGROUND_TIME, time)
            .apply()
    }

    private fun checkAppPreviousExit(currentTime: Long, lastForegroundTime: Long, backgroundTime: Long) {
        if (lastForegroundTime == 0L) {
            logger?.debug("ForceRestartTracker::checkAppPreviousExit lastForegroundTime=0, so it's first launch after install")
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

            // Retrieve the most recent exit reason for this app
            val exitList =
                activityManager.getHistoricalProcessExitReasons(context.packageName, 0, 1)
            if (exitList.isNotEmpty()) {
                val lastExit = exitList[0]

                // Measured from when the user left the app: when it went to the background, or when it
                // was killed if it was still in the foreground (e.g. swiped away from a Recents live tile).
                val leftAppAt = leftAppAt(lastExit.timestamp, backgroundTime)
                logger?.debug("ForceRestartTracker::checkAppPreviousExit - Relaunch Time Interval = ${currentTime - leftAppAt} ms (since ${if (leftAppAt == lastExit.timestamp) "process exit" else "app moved to background"}, exit was ${currentTime - lastExit.timestamp} ms ago)")

                if (isForceRestart(currentTime, lastExit.timestamp, lastExit.reason, backgroundTime, _forceRestartDuration)) {
                    logger?.debug("ForceRestartTracker::checkAppPreviousExit App Unexpectedly Exit at ${lastExit.timestamp}, Status: ${lastExit.status}, Description: ${lastExit.description}")
                    reportForceKill(lastExit.timestamp)
                } else {
                    logger?.debug("ForceRestartTracker::checkAppPreviousExit - Process Exist Reason:${lastExit.reason} Status: ${lastExit.status}, Description: ${lastExit.description}")
                }
            } else {
                logger?.debug("ForceRestartTracker::checkAppPreviousExit - Last process exit reasons is empty")
            }
        } else {
            // If the app was restarted within 10 seconds, we assume it was died unexpectedly
            val reLaunchTime = currentTime - lastForegroundTime
            logger?.debug("ForceRestartTracker::checkAppPreviousExit - Relaunch Time Interval = $reLaunchTime ms")
            if (reLaunchTime < _forceRestartDuration) {
                //reportForceKill()
                logger?.debug("ForceRestartTracker::checkAppPreviousExit App Unexpectedly Exit - lastForegroundTime=$lastForegroundTime")
            }
        }
    }

    private fun reportForceKill(exitTime: Long) {
        logger?.debug("ForceRestartTracker::reportForceKill")

        val prefs =
            context.getSharedPreferences(Tracker.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        // Page the user was on when the app was force restarted, saved by Tracker.onScreenPause
        val forceRestartScreen = ForceRestartScreen(
            nst = prefs.getString(Constants.LAST_SCREEN_NST, null)?.toLongOrNull(),
            sessionId = prefs.getString(Constants.LAST_SCREEN_SESSION_ID, null).nonBlank(),
            pageName = prefs.getString(FIELD_PAGE_NAME, null).nonBlank(),
            pageType = prefs.getString(FIELD_CONTENT_GROUP_NAME, null).nonBlank(),
            txnName = prefs.getString(FIELD_TRAFFIC_SEGMENT_NAME, null).nonBlank()
        )

        appForceKillReportJob?.cancel()

        appForceKillReportJob = scope.launch {
            while (Tracker.instance == null) delay(5)

            reporter.reportForceKill(forceRestartScreen, exitTime)
            appForceKillReportJob?.cancel()
        }
    }

    private fun String?.nonBlank(): String? = this?.takeIf { it.isNotBlank() && it != "null" }

    internal companion object {
        /**
         * A user force restart: the process was stopped by the user (swiped away from Recents or
         * force stopped) and the app was launched again within the force restart duration.
         */
        fun isForceRestart(
            currentTime: Long,
            exitTime: Long,
            exitReason: Int,
            backgroundTime: Long,
            forceRestartDuration: Long
        ): Boolean {
            return exitReason == ApplicationExitInfo.REASON_USER_REQUESTED &&
                    currentTime - leftAppAt(exitTime, backgroundTime) in 0 until forceRestartDuration
        }

        /**
         * When the user left the app: the time it went to the background if it was in the background
         * when it was killed, otherwise the time it was killed.
         */
        fun leftAppAt(exitTime: Long, backgroundTime: Long): Long {
            return if (backgroundTime in 1..exitTime) backgroundTime else exitTime
        }
    }

    internal data class ForceRestartScreen(
        val nst: Long?,
        val sessionId: String?,
        val pageName: String?,
        val pageType: String?,
        val txnName: String?
    )
}