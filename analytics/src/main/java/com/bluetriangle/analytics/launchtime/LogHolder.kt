package com.bluetriangle.analytics.launchtime

import android.util.Log

interface LogHolder {

    val logs:List<LogData>

    fun log(logData: LogData)

    fun clearLogs()

}

data class LogData(val level:Int = Log.DEBUG, val message: String)