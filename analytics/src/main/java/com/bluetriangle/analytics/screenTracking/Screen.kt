package com.bluetriangle.analytics.screenTracking

internal data class Screen(
    val id:String,
    val name:String
) {
    override fun toString(): String {
        return "$name#$id"
    }
}