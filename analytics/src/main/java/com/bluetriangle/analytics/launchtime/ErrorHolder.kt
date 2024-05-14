package com.bluetriangle.analytics.launchtime

interface ErrorHolder {

    val errors:List<String>

    fun logError(error:String)

}