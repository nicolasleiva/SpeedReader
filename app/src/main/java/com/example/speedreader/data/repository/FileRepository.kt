package com.example.speedreader.data.repository

import android.content.Context
import android.net.Uri
import java.io.BufferedReader
import java.io.InputStreamReader

class FileRepository(private val context: Context) {
    fun readTextFromUri(uri: Uri): List<String> {
        val inputStream = context.contentResolver.openInputStream(uri)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val text = reader.readText()
        reader.close()
        return text.split("\\s+".toRegex()).filter { it.isNotEmpty() }
    }
}