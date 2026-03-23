package com.bluetriangle.analytics.breadcrumbs.config

import com.bluetriangle.analytics.Constants.DEFAULT_BREADCRUMBS_CAPACITY
import com.bluetriangle.analytics.Constants.DEFAULT_ENABLE_BREADCRUMBS
import com.bluetriangle.analytics.utility.getBooleanOrNull
import com.bluetriangle.analytics.utility.getJsonArrayOrNull
import org.json.JSONArray
import org.json.JSONObject

internal object BreadcrumbsConfigMapper {
    private const val ENABLE_BREADCRUMBS = "enableBreadcrumbs"
    private const val IGNORE_BREADCRUMBS = "ignoreBreadcrumbs"

    private val featureMap = BreadcrumbsFeature.values().associateBy { it.value.lowercase() }

    fun loadFromJsonObject(jsonObject: JSONObject): BreadcrumbsConfig {
        val isEnabled = jsonObject.getBooleanOrNull(ENABLE_BREADCRUMBS)?: DEFAULT_ENABLE_BREADCRUMBS
        val ignoredFeatures = jsonObject.getJsonArrayOrNull(IGNORE_BREADCRUMBS)?.let { array ->
            buildList {
                for (i in 0 until array.length()) {
                    array.optString(i, null)?.also { feature ->
                        add(feature.trim().lowercase())
                    }
                }
            }
        } ?: listOf()
        return BreadcrumbsConfig(
            isEnabled,
            DEFAULT_BREADCRUMBS_CAPACITY,
            ignoredFeatures.mapNotNull { featureMap[it] }
        )
    }

    fun loadIntoJsonObject(jsonObject: JSONObject, config: BreadcrumbsConfig) {
        val ignoredFeaturesArray = JSONArray()
        config.ignoredFeatures.forEach {
            ignoredFeaturesArray.put(it.value)
        }
        jsonObject.put(ENABLE_BREADCRUMBS, config.isEnabled)
        jsonObject.put(IGNORE_BREADCRUMBS, ignoredFeaturesArray)
    }
}