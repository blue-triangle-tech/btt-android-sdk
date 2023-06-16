package com.bluetriangle.analytics.model

internal data class Screen(
    val id:String,
    val name:String,
    val type: ViewType
) {
    override fun toString(): String {
        return "$name#$id"
    }
}