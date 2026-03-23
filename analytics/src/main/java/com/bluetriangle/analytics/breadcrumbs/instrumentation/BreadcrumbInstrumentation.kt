package com.bluetriangle.analytics.breadcrumbs.instrumentation

import com.bluetriangle.analytics.breadcrumbs.BreadcrumbsCollector
import java.lang.ref.WeakReference

internal abstract class BreadcrumbInstrumentation(breadcrumbsCollector: BreadcrumbsCollector) {
    protected val collector = WeakReference(breadcrumbsCollector)

    abstract fun enable()
    abstract fun disable()
}