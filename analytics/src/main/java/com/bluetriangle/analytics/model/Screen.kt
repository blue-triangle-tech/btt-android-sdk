package com.bluetriangle.analytics.model

import android.app.Activity
import com.bluetriangle.analytics.utility.getToolbarTitle
import com.bluetriangle.analytics.utility.postDelayedMain

internal data class Screen(
    val id:String,
    val name:String,
    val type: ScreenType
) {
    var title: String? = null
        set(value) {
            field = value
            onTitleUpdated()
        }

    var onTitleUpdated: () -> Unit = {}

    fun pageName(grouping: Boolean)  = if(grouping) (title ?: name) else name

    override fun toString(): String {
        return "$name#$id"
    }

    fun fetchTitle(activity: Activity?) {
        if(activity == null) return

        // Since the title of the screen isn't available instantly. We wait for 400 ms for the screen's title to stabilize.
        postDelayedMain({
            val name = activity.getToolbarTitle()
            title = name
        }, 400)
    }
}