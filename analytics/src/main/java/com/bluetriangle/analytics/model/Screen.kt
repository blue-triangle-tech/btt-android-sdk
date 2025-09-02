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

        postDelayedMain({
            val name = activity.getToolbarTitle()
            title = name
        }, 400)
    }
}