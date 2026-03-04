package com.bluetriangle.analytics.breadcrumbs.config

internal class BreadcrumbsConfig(
    val capacity: Int,
    val features: List<BreadcrumbsFeature>
) {
    override fun toString(): String {
        return """BreadcrumbsConfig(
            |capacity: ${capacity},
            |features: ${features.joinToString()}
            |)
        """.trimMargin()
    }

    companion object {
        val DEFAULT = BreadcrumbsConfig(
            300,
            BreadcrumbsFeature.values().toList()
        )
    }
}