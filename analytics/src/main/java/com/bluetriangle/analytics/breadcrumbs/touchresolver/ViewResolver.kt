package com.bluetriangle.analytics.breadcrumbs.touchresolver

import android.view.View
import android.widget.TextView

fun View.toViewTargetInfo(tapX: Float, tapY: Float): TapTarget.ViewTarget {
    val resourceId: String? = runCatching {
        if (id != View.NO_ID) resources.getResourceName(id) else null
    }.getOrNull()

    val text: String? = (this as? TextView)?.text?.toString()?.takeIf { it.isNotEmpty() }

    return TapTarget.ViewTarget(
        className = javaClass.simpleName,
        resourceId = resourceId,
        contentDescription = contentDescription?.toString(),
        text = text,
        tapX,
        tapY
    )
}
