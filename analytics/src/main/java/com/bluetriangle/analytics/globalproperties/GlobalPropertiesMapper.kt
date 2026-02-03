package com.bluetriangle.analytics.globalproperties

import android.content.SharedPreferences

internal object GlobalPropertiesMapper {
    private const val AB_TEST_IDENTIFIER = "AB_TEST_IDENTIFIER"
    private const val DATA_CENTER = "DATA_CENTER"
    private const val CAMPAIGN_NAME = "CAMPAIGN_NAME"
    private const val CAMPAIGN_SOURCE = "CAMPAIGN_SOURCE"
    private const val CAMPAIGN_MEDIUM = "CAMPAIGN_MEDIUM"

    fun SharedPreferences.loadGlobalProperties(): GlobalProperties {
        val abTestIdentifier = getString(AB_TEST_IDENTIFIER, "")?:""
        val dataCenter = getString(DATA_CENTER, "")?:""
        val campaignName = getString(CAMPAIGN_NAME, "")?:""
        val campaignMedium = getString(CAMPAIGN_MEDIUM, "")?:""
        val campaignSource = getString(CAMPAIGN_SOURCE,  "")?:""

        val customCategories:Map<CustomCategory, String> = buildMap {
            CustomCategory.values().forEach {
                if(this@loadGlobalProperties.contains(it.name)) {
                    put(it, getString(it.name, "")?:"")
                }
            }
        }

        return GlobalProperties(
            abTestIdentifier,
            dataCenter,
            campaignSource,
            campaignMedium,
            campaignName,
            customCategories
        )
    }

    fun SharedPreferences.Editor.setAbTestIdentifier(value: String?) {
        if(value == null) {
            remove(AB_TEST_IDENTIFIER)
            return
        }
        putString(AB_TEST_IDENTIFIER, value)
    }

    fun SharedPreferences.Editor.setDataCenter(value: String?) {
        if(value == null) {
            remove(DATA_CENTER)
            return
        }
        putString(DATA_CENTER, value)
    }

    fun SharedPreferences.Editor.setCampaignSource(value: String?) {
        if(value == null) {
            remove(CAMPAIGN_SOURCE)
            return
        }
        putString(CAMPAIGN_SOURCE, value)
    }

    fun SharedPreferences.Editor.setCampaignMedium(value: String?) {
        if(value == null) {
            remove(CAMPAIGN_MEDIUM)
            return
        }
        putString(CAMPAIGN_MEDIUM, value)
    }

    fun SharedPreferences.Editor.setCampaignName(value: String?) {
        if(value == null) {
            remove(CAMPAIGN_NAME)
            return
        }
        putString(CAMPAIGN_NAME, value)
    }

    fun SharedPreferences.Editor.setCustomCategory(category: CustomCategory, value: String?) {
        if(value == null) {
            remove(category.name)
            return
        }
        putString(category.name, value)
    }
}