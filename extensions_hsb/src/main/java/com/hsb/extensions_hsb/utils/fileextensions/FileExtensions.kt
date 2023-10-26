package com.hsb.extensions_hsb.utils.fileextensions

import android.content.ContentResolver
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.text.format.Formatter
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.text.DecimalFormat

object FileExtensions {
    fun File.formatFileSize(): String {
        val kiloByte = 1024
        val megaByte = kiloByte * 1024
        val formatter = DecimalFormat("0.00")

        return when {
            length() < kiloByte -> "${length()} B"
            length() < megaByte -> "${formatter.format(length().toDouble() / kiloByte)} KB"
            else -> "${formatter.format(length().toDouble() / megaByte)} MB"
        }
    }

    fun formatFileSize(size: Long): String {
        val kiloByte = 1024
        val megaByte = kiloByte * 1024
        val formatter = DecimalFormat("0.00")

        return when {
            size < kiloByte -> "${size} B"
            size < megaByte -> "${formatter.format(size.toDouble() / kiloByte)} KB"
            else -> "${formatter.format(size.toDouble() / megaByte)} MB"
        }
    }

    fun File.deleteDir(): Boolean {
        if (isDirectory) {
            val children = list()
            if (children != null) {
                for (i in children.indices) {
                    val success = File(this.path, children[i]).deleteDir()
                    if (!success) {
                        return false
                    }
                }
            }
        }
        return this.delete()
    }

    fun File.getFolderSize(): Long {
        var size: Long = 0
        try {
            val dirFile = this
            val fileList = dirFile.listFiles()
            if (fileList != null) {
                for (i in fileList.indices) {
                    if (fileList[i].isDirectory) {
                        size += File(fileList[i].absolutePath).getFolderSize()
                    } else {
                        size += fileList[i].length()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    fun Context.getAppFolderSize(): String {
        val packageName = packageName
        val packageManager = packageManager

        try {
            val ai: ApplicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val appDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                packageManager.getApplicationInfo(
                    packageName,
                    PackageManager.MATCH_UNINSTALLED_PACKAGES
                ).dataDir
            } else {
                ai.dataDir
            }

            val appSize = File(appDir).getFolderSize()
            return Formatter.formatFileSize(this, appSize)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "0b"
    }

    fun File.getMediaDuration(): String {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(this.path)
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val timeInmillisec = time!!.toLong()
        val duration = timeInmillisec / 1000
        val hours = duration / 3600
        val minutes = (duration - hours * 3600) / 60
        val seconds = duration - (hours * 3600 + minutes * 60)
        return "$hours:$minutes:$seconds"
    }

    fun File.is_MP4_Or_MP3(): String {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(this.path)

        val hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO)

        return if (hasVideo != null) {
            ".mp4"
        } else {
            ".mp3"
        }
    }

    fun File.getMediaThumbnail(): Bitmap? {
        val mMMR = MediaMetadataRetriever()
        mMMR.setDataSource(this.path);
        val embeddedArt = mMMR.embeddedPicture
        if (embeddedArt != null) {
            return BitmapFactory.decodeByteArray(embeddedArt, 0, embeddedArt.size)
        }
        return null
    }

    fun getFileSizeOnline(url: String, callback: (Long) -> Unit) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .head() // Use HEAD method to fetch only the headers
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback.invoke(-1) // Return -1 as the file size on failure
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        val contentLength = response.header("Content-Length")?.toLong() ?: -1
                        callback.invoke(contentLength)
                    } else {
                        callback.invoke(-1)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback.invoke(-1)
                } finally {
                    response.close()
                }
            }
        })
    }

    fun File.getMediaFrame(): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(path)
            retriever.frameAtTime
        } catch (e: Exception) {
            null
        } finally {
            retriever.release()
        }
    }

    fun File.getFilePathFromContentUri(context: Context, uri: Uri): String? {
        val contentResolver: ContentResolver = context.contentResolver
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        var cursor: Cursor? = null

        try {
            cursor = contentResolver.query(uri, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return cursor.getString(columnIndex)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }

        // If retrieval fails, try another approach (for some cases like SAF)
        try {
            val path = uri.path
            if (path != null) {
                if (path.startsWith("/storage")) {
                    // This is a standard file path, return it
                    return path
                } else if (path.startsWith("/document/raw:")) {
                    // Handle SAF (Storage Access Framework) paths
                    val file = File(
                        context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                        path.substringAfter("/document/raw:")
                    )
                    if (file.exists()) {
                        return file.absolutePath
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // If all else fails, return null
        return null
    }

    fun File.getThumbFromAudio(): Bitmap? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                ThumbnailUtils.createVideoThumbnail(
                    path,
                    MediaStore.Images.Thumbnails.FULL_SCREEN_KIND
                )
            } catch (e: Exception) {
                ThumbnailUtils.createAudioThumbnail(
                    path,
                    MediaStore.Images.Thumbnails.FULL_SCREEN_KIND
                )
            }
        } else {
            null
        }
    }

}