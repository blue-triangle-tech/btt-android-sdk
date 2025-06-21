package com.bluetriangle.analytics.model

internal data class Screen(
    val id:String,
    val name:String,
    val type: ScreenType
) {
    var title: String? = null

    fun pageName(grouping: Boolean)  = if(grouping) (title ?: name) else name

    override fun toString(): String {
        return "$name#$id"
    }
}