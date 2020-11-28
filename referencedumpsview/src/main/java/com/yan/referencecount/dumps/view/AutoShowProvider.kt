package com.yan.referencecount.dumps.view

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/11/28
 */
class AutoShowProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        val app = context?.applicationContext as Application
        WindowPop.attachDumpView(app)
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw NotImplementedError()
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        throw NotImplementedError()
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        throw NotImplementedError()
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw NotImplementedError()
    }

    override fun getType(uri: Uri): String? {
        throw NotImplementedError()
    }
}