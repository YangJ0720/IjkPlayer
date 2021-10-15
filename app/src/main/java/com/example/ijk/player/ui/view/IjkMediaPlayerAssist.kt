package com.example.ijk.player.ui.view

import android.content.Context
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest

object IjkMediaPlayerAssist {

    fun getLastPosition(context: Context, path: String): Long {
        val dir = context.externalCacheDir
        val file = File(dir, path.md5())
        if (file.exists()) {
            var inputStream: FileInputStream? = null
            var outputStream: ByteArrayOutputStream? = null
            try {
                inputStream = FileInputStream(file)
                outputStream = ByteArrayOutputStream()
                var len: Int
                val buffer = ByteArray(1024)
                while (true) {
                    len = inputStream.read(buffer)
                    if (len == -1) {
                        break
                    }
                    outputStream.write(buffer, 0, len)
                }
                val str = String(outputStream.toByteArray())
                return str.toLong()
            } catch (e: Exception) {

            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        }
        return -1
    }

    fun setLastPosition(context: Context, path: String, position: Long) {
        val dir = context.externalCacheDir
        val file = File(dir, path.md5())
        if (!file.exists()) {
            file.createNewFile()
        }
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(file)
            outputStream.write(position.toString().toByteArray())
        } catch (e: Exception) {

        } finally {
            outputStream?.close()
        }
    }

    private fun String.md5(): String {
        val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
        return bytes.hex()
    }

    private fun ByteArray.hex(): String {
        return joinToString("") { "%02X".format(it) }
    }
}