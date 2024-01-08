package com.hsb.extensions_hsb.utils.globalextensions

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.hsb.extensions_hsb.R
import com.hsb.extensions_hsb.utils.fileextensions.FileExtensions
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Nov 14, 2023
 * Developed by Syed Haseeb
 * Github: https://github.com/syedhaseeb1
 *
 * Updated on Jan 08, 2024
 */
object Extensions {
    val intentData = "intentData"
    fun Context.toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun Context.routWithData(data: String, cls: Class<*>) {
        val intent = Intent(this, cls)
        intent.putExtra(intentData, data)
        startActivity(intent)
    }


    fun Context.rout(cls: Class<*>) {
        startActivity(Intent(this, cls))
    }

    fun <T> T?.isInitialized(): Boolean {
        return this != null
    }

    fun Context.routFlow(cls: Class<*>) {
        val intent = Intent(this, cls)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        startActivity(intent)
    }


    fun Context.saveFolder(): File? = getExternalFilesDir(null)?.absoluteFile

    fun Int.formatTo01() = String.format("%02d", this)

    fun Context.verticalRv() = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    fun Context.gridV(span: Int) = GridLayoutManager(this, span)

    fun ArrayList<*>.lastIndex(): Int {
        return if (this.isNotEmpty()) {
            this.size - 1
        } else {
            0
        }
    }


    fun Long.convertMillisToDateFormat(): String {
        val currentTimeMillis = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MMM d, yyyy 'at' hh:mm a", Locale.US)

        if (this > currentTimeMillis) {
            val currentDate = Date(currentTimeMillis)
            return dateFormat.format(currentDate)
        } else {
            val date = Date(this)
            return dateFormat.format(date)
        }
    }

    fun formatToDay(): String {
        val formatLetterDay = SimpleDateFormat("EEEEE", Locale.getDefault())
        return formatLetterDay.format(Date())
    }

    fun Context.copyToClipboard(text: String) {
        if (text.trim().isEmpty()) {
            toast("No text to copy!")
            return
        }
        if (!text.startsWith("http")) {
            toast("No URL to copy!")
            return
        }
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(getString(R.string.app_name), text)
        clipboard.setPrimaryClip(clip)
        toast("Copied ${text.short()}")
    }

    fun Context.bitArray(link: String): ByteArray {
        val stream = ByteArrayOutputStream()
        val favicon: Bitmap?
        try {
            favicon = Glide
                .with(this)
                .asBitmap()
                .load(link)
                .submit()
                .get()
            favicon.compress(Bitmap.CompressFormat.PNG, 100, stream)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return stream.toByteArray()
    }

    fun Long.convertToMinutesSeconds(): String {
        val minutes = (this / 1000) / 60
        val seconds = (this / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun calculateNetworkSpeed(downloadedBytes: Long, elapsedTime: Long): String {
        val speed = downloadedBytes.toFloat() / (elapsedTime.toFloat() / 1000) // Bytes per second
        return FileExtensions.formatFileSize(speed.toLong()) + "/s"
    }

    fun Context.log(msg: String): String {

        Log.e(this.javaClass.simpleName, msg)

        return this.javaClass.simpleName + ": $msg"
    }


    fun Context.shareLink(link: String) {
        if (link.isEmpty()) {
            toast("No link to share!")
            return
        }
        if (!link.startsWith("http")) {
            toast("No URL to share!")
            return
        }
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, link);
        startActivity(intent)
    }

    fun Context.shareText(text: String) {
        if (text.isEmpty()) {
            toast("No text to share!")
            return
        }
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, null))
    }

    fun Context.getAvailableRAM(): Long {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val availableRam = memoryInfo.availMem / (1024 * 1024)
        log("Available Ram: $availableRam")
        return availableRam
    }

    fun Context.downloadThisFile(url: String, destinationPath: String?, fileName: String): File? {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val request = DownloadManager.Request(Uri.parse(url))
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(fileName)
            .setMimeType("*/*")

        downloadManager.enqueue(request)
        return File(destinationPath, fileName)
    }

    fun String.short() = if (this.length > 30) {
        this.substring(0, 30).trim()
    } else {
        this.trim()
    }


    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun Context.isWifiAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }

    fun String.replaceCharacters(): String {
        val regex = "[^a-zA-Z0-9 ]".toRegex()
        return replace(regex, "")
    }

    fun Context.getStringByName(resourceName: String): String? {
        val resourceId = resources.getIdentifier(resourceName, "string", packageName)
        return if (resourceId != 0) {
            resources.getString(resourceId)
        } else {
            null
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun Context.isInternetAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetworkInfo: NetworkInfo? = null
        activeNetworkInfo = cm.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }

    fun getCurrentTime(): String {
        val currentTime = Date()
        val dateFormat = SimpleDateFormat("EEE, dd/yyyy", Locale.getDefault())
        return dateFormat.format(currentTime)
    }

    fun Bitmap.byteArray(): ByteArray {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.PNG, 90, stream)
        return stream.toByteArray()
    }

    fun Bitmap.resizeBy(size: Int): Bitmap {
        return Bitmap.createScaledBitmap(this, width / size, height / size, false)
    }

    fun shortenUrl(longUrl: String, callback: (String?) -> Unit) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://is.gd/create.php?format=simple&url=$longUrl")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val shortUrl = response.body?.string()
                callback(shortUrl)
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }
        })
    }

    fun Context.openLinkInChrome(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.`package` = "com.android.chrome"
            }

            val chooserIntent = Intent.createChooser(intent, "Open link with:")

            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(chooserIntent)
        } catch (e: ActivityNotFoundException) {
            toast("No default browser was found")
        }
    }

    fun Context.sendFeedbackEmailTo(email: String) {
        val appName = getString(R.string.app_name)
        val msg = "Help us improve $appName. Let us know how we can enhance your experience."
        val selectorIntent = Intent(Intent.ACTION_SENDTO)
        selectorIntent.data = Uri.parse("mailto:$email")
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback: $appName")
        emailIntent.putExtra(Intent.EXTRA_TEXT, msg)
        emailIntent.selector = selectorIntent
        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    fun String.capitalizeFirstLetter(): String {
        if (isEmpty()) return this
        return this.substring(0, 1).toUpperCase() + this.substring(1)
    }

    fun convertSecToTime(timeInMilliSec: Long): String {
        val duration = timeInMilliSec / 1000
        val hours = duration / 3600
        val minutes = (duration - hours * 3600) / 60
        val seconds = duration - (hours * 3600 + minutes * 60)
        return "${hours.toInt().formatTo01()}:${minutes.toInt().formatTo01()}:${
            seconds.toInt().formatTo01()
        }"
    }
}