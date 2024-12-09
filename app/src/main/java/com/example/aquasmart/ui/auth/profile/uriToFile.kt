package com.example.aquasmart.ui.auth.profile

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import java.io.File
import java.io.InputStream

fun uriToFile(uri: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val fileName = getFileName(uri, context)
    val inputStream: InputStream? = contentResolver.openInputStream(uri)
    val file = File(context.cacheDir, fileName)

    inputStream?.let { input ->
        file.outputStream().use { outputStream ->
            input.copyTo(outputStream)
        }
    }

    return file
}

fun getFileName(uri: Uri, context: Context): String {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        val nameIndex = it.getColumnIndex("_display_name")
        if (it.moveToFirst()) {
            return it.getString(nameIndex)
        }
    }
    return "default_name.jpg"
}
