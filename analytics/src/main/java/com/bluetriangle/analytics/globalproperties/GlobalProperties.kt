package com.bluetriangle.analytics.globalproperties

internal class GlobalProperties(
    val abTestIdentifier: String,
    val dataCenter: String,
    val campaignSource: String,
    val campaignMedium: String,
    val campaignName: String,
    val customCategories: Map<CustomCategory, String>
)