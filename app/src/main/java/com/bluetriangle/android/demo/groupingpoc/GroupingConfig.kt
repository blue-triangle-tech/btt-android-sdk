package com.bluetriangle.android.demo.groupingpoc

import androidx.lifecycle.LiveData
import com.bluetriangle.android.demo.DemoApplication
import kotlinx.coroutines.flow.Flow

object GroupingConfig {
    const val GROUPING_KEY = "GROUPING"
    const val IDLE_TIME_KEY = "IDLE_TIME"

    fun setGrouping(grouping: Boolean) {
        DemoApplication.sharedPreferencesMgr.setBoolean(GROUPING_KEY, grouping)
    }

    fun getGrouping(): Boolean {
        return DemoApplication.sharedPreferencesMgr.getBoolean(GROUPING_KEY)
    }

    fun observeGrouping(): Flow<Boolean> {
        return DemoApplication.sharedPreferencesMgr.observeBoolean(GROUPING_KEY)
    }

    fun getIdleTime(): Int {
        return DemoApplication.sharedPreferencesMgr.getInt(IDLE_TIME_KEY, 2)
    }

    fun setIdleTime(idleTime: Int) {
        DemoApplication.sharedPreferencesMgr.setInt(IDLE_TIME_KEY, idleTime)
    }
}