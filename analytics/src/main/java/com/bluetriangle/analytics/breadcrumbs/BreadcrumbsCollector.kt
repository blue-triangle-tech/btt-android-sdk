package com.bluetriangle.analytics.breadcrumbs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.bluetriangle.analytics.Constants.BREADCRUMBS
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.Tracker.Companion.SHARED_PREFERENCES_NAME
import org.json.JSONArray
import org.json.JSONObject
import java.lang.ref.WeakReference

internal class BreadcrumbsCollector(
    private val capacity: Int, private val context: WeakReference<Context>
) {
    private val buffer = arrayOfNulls<JSONObject>(capacity)
    private var head = 0
    private var size = 0

    private val prefs: SharedPreferences?
        get() {
            val context = context.get() ?: return null
            return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        }

    init {
        // Load cached breadcrumbs from SharedPreferences
        val cachedBreadcrumbs = prefs?.getString(BREADCRUMBS, null)
        cachedBreadcrumbs?.let {
            try {
                val jsonArray = JSONArray(it)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    BreadcrumbEvent.fromJson(jsonObject)?.let { breadcrumb -> add(breadcrumb) }
                }
            } catch (e: Exception) {
            }
        }
    }

    @Synchronized
    fun add(item: BreadcrumbEvent) {
        buffer[head] = item.toJson()
        Tracker.instance?.configuration?.logger?.verbose("Added breadcrumb: ${buffer[head].toString()}")
        head = (head + 1) % capacity
        if (size < capacity) {
            size++
        }
    }

    @Synchronized
    fun snapshot(): JSONArray {
        val result = JSONArray()

        val start = if (size == capacity) head else 0

        repeat(size) { i ->
            val index = (start + i) % capacity
            buffer[index]?.let { result.put(it) }
        }

        return result
    }

    @Synchronized
    fun clear() {
        head = 0
        size = 0
        buffer.fill(null)
    }

    @Synchronized
    fun currentSize(): Int = size

    fun capacity(): Int = capacity

    @Synchronized
    fun dump() {
        val breadcrumbs = snapshot()?.toString() ?: ""
        prefs?.edit {
            putString(BREADCRUMBS, breadcrumbs)
        }
    }
}