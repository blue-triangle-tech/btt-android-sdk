package com.bluetriangle.analytics.thirdpartyintegration

internal interface CustomVariablesAdapter {
    fun getCustomVariable(key: String):String?
    fun setCustomVariable(key: String, value: String)
    fun clearCustomVariable(key: String)
}