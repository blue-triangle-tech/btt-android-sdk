package com.bluetriangle.android.demo

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.io.File

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


fun Context.generateDemoWebsiteFromTemplate() {
    var webSiteContent = String(assets.open("template.html").readBytes())
    val tagUrl = (applicationContext as? DemoApplication)?.getTagUrl() ?: DemoApplication.DEFAULT_TAG_URL
    Log.d("HybridDemoWebsiteTag", "Generating demo website: $tagUrl")
    webSiteContent = webSiteContent.replace(
        "<!--- SCRIPT_TAG_GOES_HERE --->",
        "<script type=\"text/javascript\" id=\"\" src=\"https://$tagUrl\"></script>"
    )
    val file = File(filesDir, "index.html")
    if (!file.exists()) {
        file.createNewFile()
    }
    file.writeText(webSiteContent)
}
