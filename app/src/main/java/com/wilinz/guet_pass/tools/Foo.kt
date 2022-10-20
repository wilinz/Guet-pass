package com.wilinz.guet_pass.tools

import android.R

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri


private val CONTENT_URI: Uri =
    Uri.parse("content://com.android.launcher.settings/favorites?notify=true")

private val PROJECTION = arrayOf(
    "_id",
    "title",
    "iconResource"
)

private fun hasShortCut(context: Context): Boolean {
    val resolver: ContentResolver = context.contentResolver
    val cursor: Cursor? = resolver.query(
        CONTENT_URI,
        PROJECTION,
        "title=?",
        arrayOf("桂电畅行证"),
        null
    )
    if (cursor != null && cursor.moveToFirst()) {
        cursor.close()
        return true
    }
    return false
}