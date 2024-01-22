package com.bluetriangle.android.demo

import android.app.Application
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

fun FragmentActivity.replaceFragment(
    @IdRes containerViewId: Int,
    fragment: Fragment,
    tag: String,
    args: Bundle? = null,
    addToBackStack: Boolean = false
) {
    if (args != null)
        fragment.arguments = args

    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(containerViewId, fragment, tag)
    if (addToBackStack) {
        transaction.addToBackStack(tag);
    }

    transaction.commit()
}

fun Fragment.replaceFragment(
    @IdRes containerViewId: Int,
    fragment: Fragment,
    tag: String,
    args: Bundle? = null,
    addToBackStack: Boolean = false
) {
    if (args != null)
        fragment.arguments = args

    val transaction = childFragmentManager.beginTransaction()
    transaction.replace(containerViewId, fragment, tag)
    if (addToBackStack) {
        transaction.addToBackStack(tag);
    }

    transaction.commit()
}

inline fun <reified T : ViewModel> FragmentActivity.getViewModel() =
    ViewModelProvider(this)[T::class.java]

inline fun <reified T : ViewModel> FragmentActivity.getAndroidViewModel() =
    ViewModelProvider(this)[T::class.java]

inline fun <reified T : ViewModel> Fragment.getViewModel() =
    ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[T::class.java]

inline fun <reified T : ViewModel> Fragment.getAndroidViewModel() =
    ViewModelProvider(this)[T::class.java]