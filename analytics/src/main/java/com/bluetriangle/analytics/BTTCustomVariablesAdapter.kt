package com.bluetriangle.analytics

import com.bluetriangle.analytics.thirdpartyintegration.CustomVariablesAdapter

internal class BTTCustomVariablesAdapter: CustomVariablesAdapter {
    override fun getCustomVariable(key: String): String? {
        return Tracker.instance?.getCustomVariable(key)
    }

    override fun setCustomVariable(key: String, value: String) {
        Tracker.instance?.setCustomVariable(key, value)
    }

    override fun clearCustomVariable(key: String) {
        Tracker.instance?.clearCustomVariable(key)
    }
}