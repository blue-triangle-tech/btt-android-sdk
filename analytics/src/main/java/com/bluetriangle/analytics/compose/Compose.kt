package com.bluetriangle.analytics.compose

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.model.Screen
import com.bluetriangle.analytics.model.ScreenType
import com.bluetriangle.analytics.screenTracking.ScreenLifecycleTracker

@Composable
@NonRestartableComposable
fun BttTimerEffect(screenName: String) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(Unit) {
        val screenTracker = Tracker.instance?.screenTrackMonitor
        val observer = ComposableLifecycleObserver(screenTracker, screenName)
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun BTTTrackNavigation(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Only trigger side effect on route change
    LaunchedEffect(currentRoute) {
        currentRoute?.let { route ->
            Log.d("ComposeNavigation", "route: $route")
        }
    }
}


internal class ComposableLifecycleObserver(
    private val screenTracker: ScreenLifecycleTracker?,
    screenName: String
) : LifecycleEventObserver {

    val screen = Screen(screenName.hashCode().toString(), screenName, ScreenType.Composable)

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        screenTracker?.apply {
            when (event) {
                ON_CREATE -> onLoadStarted(screen)
                ON_START -> onLoadEnded(screen)
                ON_RESUME -> onViewStarted(screen)
                ON_STOP -> onViewEnded(screen)
                else -> {}
            }
        }
    }
}