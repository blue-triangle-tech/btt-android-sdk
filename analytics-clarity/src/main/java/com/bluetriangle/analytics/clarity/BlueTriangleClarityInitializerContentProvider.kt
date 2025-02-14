package com.bluetriangle.analytics.clarity

import android.content.ContentProvider
import android.content.ContentValues
import android.net.Uri
import android.util.Log

class BlueTriangleClarityInitializerContentProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        Log.d("BlueTriangle", "ClarityInitializer")
        ClarityInitializer.init()
        return true
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?) = -1

    override fun getType(uri: Uri) = null

    override fun insert(uri: Uri, values: ContentValues?) = null

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ) = null

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ) = -1

}