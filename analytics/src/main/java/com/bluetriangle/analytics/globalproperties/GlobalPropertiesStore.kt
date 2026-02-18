package com.bluetriangle.analytics.globalproperties

import android.content.Context
import androidx.core.content.edit
import com.bluetriangle.analytics.globalproperties.GlobalPropertiesMapper.loadGlobalProperties
import com.bluetriangle.analytics.globalproperties.GlobalPropertiesMapper.setAbTestIdentifier
import com.bluetriangle.analytics.globalproperties.GlobalPropertiesMapper.setCampaignMedium
import com.bluetriangle.analytics.globalproperties.GlobalPropertiesMapper.setCampaignName
import com.bluetriangle.analytics.globalproperties.GlobalPropertiesMapper.setCampaignSource
import com.bluetriangle.analytics.globalproperties.GlobalPropertiesMapper.setCustomCategory
import com.bluetriangle.analytics.globalproperties.GlobalPropertiesMapper.setDataCenter

internal class GlobalPropertiesStore(context: Context) {

    companion object {
        private const val GLOBAL_PROPERTIES_PREFS = "com.bluetriangle.analytics.GlobalPropertiesStore"
    }

    private val sharedPrefs = context.getSharedPreferences(GLOBAL_PROPERTIES_PREFS, Context.MODE_PRIVATE)

    fun loadGlobalProperties(): GlobalProperties {
        synchronized(this) {
            return sharedPrefs.loadGlobalProperties()
        }
    }

    fun setAbTestIdentifier(value: String?) {
        synchronized(this) {
            sharedPrefs.edit {
                setAbTestIdentifier(value)
            }
        }
    }

    fun setDataCenter(value: String?) {
        synchronized(this) {
            sharedPrefs.edit {
                setDataCenter(value)
            }
        }
    }

    fun setCampaignName(value: String?) {
        synchronized(this) {
            sharedPrefs.edit {
                setCampaignName(value)
            }
        }
    }

    fun setCampaignMedium(value: String?) {
        synchronized(this) {
            sharedPrefs.edit {
                setCampaignMedium(value)
            }
        }
    }

    fun setCampaignSource(value: String?) {
        synchronized(this) {
            sharedPrefs.edit {
                setCampaignSource(value)
            }
        }
    }

    fun setCustomCategory(customCategory: CustomCategory, value: String?) {
        synchronized(this) {
            sharedPrefs.edit {
                setCustomCategory(customCategory, value)
            }
        }
    }

}