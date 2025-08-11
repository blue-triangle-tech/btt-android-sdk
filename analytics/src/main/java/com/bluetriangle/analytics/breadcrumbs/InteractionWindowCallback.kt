package com.bluetriangle.analytics.breadcrumbs

import android.app.Activity
import android.os.Build
import android.view.ActionMode
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.SearchEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent

class InteractionWindowCallback(
    val activity: Activity,
    val wrapped: Window.Callback,
) : Window.Callback {
    val gestureDetector = GestureDetector(activity, InteractionGestureListener(activity))

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return wrapped.dispatchKeyEvent(event)
    }

    override fun dispatchKeyShortcutEvent(event: KeyEvent?): Boolean {
        return wrapped.dispatchKeyShortcutEvent(event)
    }

    override fun dispatchTouchEvent(event:  MotionEvent?): Boolean {
        val result = wrapped.dispatchTouchEvent(event)

        if (event != null) {
            gestureDetector.onTouchEvent(event)
        }
        return result
    }

    override fun dispatchTrackballEvent(event: MotionEvent?): Boolean {
        return wrapped.dispatchTrackballEvent(event)
    }

    override fun dispatchGenericMotionEvent(event: MotionEvent?): Boolean {
        return wrapped.dispatchGenericMotionEvent(event)
    }

    override fun dispatchPopulateAccessibilityEvent(event: AccessibilityEvent?): Boolean {
        return wrapped.dispatchPopulateAccessibilityEvent(event)
    }

    override fun onCreatePanelView(featureId: Int): View? {
        return wrapped.onCreatePanelView(featureId)
    }

    override fun onCreatePanelMenu(
        featureId: Int,
        menu: Menu,
    ): Boolean {
        return wrapped.onCreatePanelMenu(featureId, menu)
    }

    override fun onPreparePanel(
        featureId: Int,
        view: View?,
        menu: Menu,
    ): Boolean {
        return wrapped.onPreparePanel(featureId, view, menu)
    }

    override fun onMenuOpened(
        featureId: Int,
        menu: Menu,
    ): Boolean {
        return wrapped.onMenuOpened(featureId, menu)
    }

    override fun onMenuItemSelected(
        featureId: Int,
        item: MenuItem,
    ): Boolean {
        return wrapped.onMenuItemSelected(featureId, item)
    }

    override fun onWindowAttributesChanged(params: WindowManager.LayoutParams?) {
        return wrapped.onWindowAttributesChanged(params)
    }

    override fun onContentChanged() {
        return wrapped.onContentChanged()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        return wrapped.onWindowFocusChanged(hasFocus)
    }

    override fun onAttachedToWindow() {
        return wrapped.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        return wrapped.onDetachedFromWindow()
    }

    override fun onPanelClosed(
        featureId: Int,
        menu: Menu,
    ) {
        return wrapped.onPanelClosed(featureId, menu)
    }

    override fun onSearchRequested(): Boolean {
        return wrapped.onSearchRequested()
    }

    override fun onSearchRequested(event: SearchEvent?): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            wrapped.onSearchRequested(event)
        } else {
            // Do nothing
            false
        }
    }

    override fun onWindowStartingActionMode(callback: ActionMode.Callback?): ActionMode? {
        return wrapped.onWindowStartingActionMode(callback)
    }

    override fun onWindowStartingActionMode(
        callback: ActionMode.Callback?,
        type: Int,
    ): ActionMode? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            wrapped.onWindowStartingActionMode(callback, type)
        } else {
            null
        }
    }

    override fun onActionModeStarted(mode: ActionMode?) {
        return wrapped.onActionModeStarted(mode)
    }

    override fun onActionModeFinished(mode: ActionMode?) {
        return wrapped.onActionModeFinished(mode)
    }
}
