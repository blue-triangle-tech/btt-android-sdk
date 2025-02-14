package com.bluetriangle.analytics.sdkevents

data class SDKConfiguration(
    val siteID: String,
    val sessionID: String?,
    val clarityProjectID: String?,
    val networkCapturingEnabled: Boolean
)