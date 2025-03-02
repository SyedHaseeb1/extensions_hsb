package com.hsb.extensions_hsb.utils.globalextensions

import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.activity.enableEdgeToEdge
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfDocument
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.FileUtils
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import android.widget.VideoView
import androidx.annotation.RawRes
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.os.BuildCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.Model
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hsb.extensions_hsb.R
import com.hsb.extensions_hsb.utils.annotaions.ExperimentalCrashTestApi
import com.hsb.extensions_hsb.utils.fileextensions.FileExtensions
import com.hsb.extensions_hsb.utils.globalextensions.Extensions.convertMillisToDateFormat
import com.hsb.extensions_hsb.utils.globalextensions.Extensions.formatTo01
import com.hsb.extensions_hsb.utils.globalextensions.Extensions.getAvailableRAM
import com.hsb.extensions_hsb.utils.globalextensions.Extensions.intentData
import com.hsb.extensions_hsb.utils.globalextensions.Extensions.log
import com.hsb.extensions_hsb.utils.globalextensions.Extensions.rout
import com.hsb.extensions_hsb.utils.globalextensions.Extensions.toast
import com.hsb.extensions_hsb.utils.permissionH.PermissionH
import com.hsb.extensions_hsb.utils.stringExtensions.StringExtensions.short
import com.hsb.extensions_hsb.utils.viewextensions.ViewExtensions.beGone
import com.hsb.extensions_hsb.utils.viewextensions.ViewExtensions.loadImage
import com.hsb.extensions_hsb.utils.viewextensions.ViewExtensions.safeClickListeners
import com.permissionx.guolindev.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.Serializable
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.math.hypot

/**
 * Nov 14, 2023
 * Developed by Syed Haseeb
 * Github: https://github.com/syedhaseeb1
 *
 * Updated on Dec 20, 2024
 */
object Extensions {
    @RequiresApi(Build.VERSION_CODES.M)
    fun AppCompatActivity.hideSystemUI() {
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 (API 30+) and above
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // For Android 10 (API 29) and below
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    )

            // Optional: For API levels below 30, ensure status bar and navigation bar are hidden
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.navigationBarColor = getColor(R.color.black)
        }
    }


    fun FragmentActivity.sendNotificationAlert(title: String, subTitle: String, icon: Int) {
        if (isAndroid13OrAbove()) {
            PermissionH.requestPermissions(
                listOf(Manifest.permission.POST_NOTIFICATIONS),
                this
            ) { isGranted ->
                if (isGranted) {
                    sendNotificationNow(title, subTitle, icon)
                } else {
                    toast("Permission Denied")
                }
            }
        } else {
            sendNotificationNow(title, subTitle, icon)
        }

    }

    private fun Context.sendNotificationNow(title: String, subTitle: String, icon: Int) {
        val channelId = "extension_hsb"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "important",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(subTitle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(901, notification)
    }

    @SuppressLint("GetInstance")
    fun String.encryptWithKey(secretKey: String): String {
        val cipher = Cipher.getInstance("AES")
        val keySpec = SecretKeySpec(secretKey.toByteArray(), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)

        val encryptedBytes = cipher.doFinal(this.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP) // Encode to Base64 for storage
    }

    @SuppressLint("GetInstance")
    fun String.decryptWithKey(secretKey: String): String {
        val cipher = Cipher.getInstance("AES")
        val keySpec = SecretKeySpec(secretKey.toByteArray(), "AES")
        cipher.init(Cipher.DECRYPT_MODE, keySpec)

        val decodedBytes = Base64.decode(this, Base64.NO_WRAP)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes)
    }

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

    fun Context.safeRouteTo(cls: Class<*>, exception: ((error: String) -> Unit)? = null) {
        try {
            startActivity(Intent(this, cls))
        } catch (e: ActivityNotFoundException) {
            exception?.invoke(e.message.toString())
        }

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


    fun Long.convertMillisToDateFormat(format: String = "MMM d, yyyy 'at' hh:mm a"): String {
        val currentTimeMillis = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat(format, Locale.US)

        if (this > currentTimeMillis) {
            val currentDate = Date(currentTimeMillis)
            return dateFormat.format(currentDate)
        } else {
            val date = Date(this)
            return dateFormat.format(date)
        }
    }

    fun getDayCurrentName(): String {
        val formatLetterDay = SimpleDateFormat("EEEEE", Locale.getDefault())
        return formatLetterDay.format(Date())
    }

    fun Context.copyToClipboard(text: String) {
        if (text.trim().isEmpty()) {
            toast("No text to copy!")
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


    suspend fun Bitmap.rotateBitmap(angle: Float = 90f) = withContext(Dispatchers.IO) {
        async {
            val matrix = Matrix().apply { postRotate(angle) }
            Bitmap.createBitmap(this@rotateBitmap, 0, 0, width, height, matrix, true)
        }.await()
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

    fun Context.sendFeedbackEmailTo(email: String, feedBackText: String) {
        val appName = getString(R.string.app_name)
        val deviceModel = Build.MODEL
        val deviceManufacturer = Build.MANUFACTURER
        val deviceBrand = Build.BRAND
        val deviceName = "$deviceManufacturer $deviceBrand $deviceModel"
        val currentTime = System.currentTimeMillis().convertMillisToDateFormat()
        val msg = if (feedBackText.isEmpty()) {
            "$currentTime\nDevice: $deviceName\n\nTell us which issues you are facing using $appName?\n"
        } else {
            "$currentTime\nDevice: $deviceName\n\n$feedBackText"
        }


        val selectorIntent = Intent(Intent.ACTION_SENDTO)
        selectorIntent.data = Uri.parse("mailto:$email")
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback: $appName")
        emailIntent.putExtra(Intent.EXTRA_TEXT, msg)
        emailIntent.selector = selectorIntent
        startActivity(Intent.createChooser(emailIntent, "Send email..."))
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

    @SuppressLint("HardwareIds")
    fun Context.getDeviceHashID(): String {
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        return try {
            // Create MD5 Hash
            val digest = MessageDigest.getInstance("MD5")
            digest.update(androidId.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuffer()
            for (i in messageDigest.indices) hexString.append(Integer.toHexString(0xFF and messageDigest[i].toInt()))
            hexString.toString().uppercase(Locale.getDefault()).trim()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            ""
        }
    }

    fun Activity.fullScreenUi() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    fun Context.openLinkInBrowser(url: String) {
        try {
            // Create the Intent to open the URL
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            // Check if the device is running Android 6.0 Marshmallow (API level 23) or higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Use the Chrome Custom Tab to open the URL (preferred method on newer devices)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.`package` = "com.android.chrome"
            }

            // Create a chooser for the Intent to allow the user to select a web browser
            val chooserIntent = Intent.createChooser(intent, "Open link with:")

            // Start the activity to open the URL in the browser
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(chooserIntent)
        } catch (e: ActivityNotFoundException) {
            // If no web browser is installed on the device, catch the exception and handle it
            // You can display a message to the user to install a web browser to open the link
        }
    }

    fun Context.sendSafeEmail(
        email: String,
        subj: String = "",
        msg: String,
        exception: ((error: String) -> Unit)? = null,
    ) {
        val selectorIntent = Intent(Intent.ACTION_SENDTO)
        selectorIntent.data = Uri.parse("mailto:$email")
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Email: $subj")
        emailIntent.putExtra(Intent.EXTRA_TEXT, msg)
        emailIntent.selector = selectorIntent
        try {
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        } catch (e: ActivityNotFoundException) {
            exception?.invoke(e.localizedMessage.toString())
        }
    }

    fun View.applyCircularRevealEffect(
        reveal: Boolean = true,
        rootView: ViewGroup,
        color: Int? = null,
        drawable: Drawable? = null,
        animSpeed: Int = 1000,
        onAnimEnd: (() -> Unit),
    ) {
        val splashView = ImageView(this.context)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        splashView.layoutParams = params
        drawable?.let {
            splashView.loadImage(it, false)
        }
        color?.let {
            splashView.loadImage(it, false)
        }
        splashView.scaleType = ImageView.ScaleType.FIT_XY
        rootView.removeView(splashView)
        rootView.addView(splashView)
        if (reveal) {
            // Coordinates for the center of the button
            val x: Int = (this.left + this.right) / 2
            val y: Int = this.bottom / 1
            // End radius to cover the entire screen
            val endRadius = hypot(
                rootView.width.toDouble(),
                rootView.height.toDouble()
            ).toInt()

            // Starting radius is 0 since the reveal starts from the button
            val startRadius = 0

            // Create circular reveal animation
            val anim = ViewAnimationUtils.createCircularReveal(
                splashView, // Reveal the entire root layout
                x,
                y,
                startRadius.toFloat(),
                endRadius.toFloat()
            )

            // Make the invisible layout visible for the reveal animation
            rootView.visibility = View.VISIBLE
            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    CoroutineScope(Dispatchers.Main).launch {
                        onAnimEnd.invoke()
                    }

                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
            // Start the reveal animation
            anim.duration = animSpeed.toLong()
            anim.start()
        } else {
            // Coordinates for the center of the button
            val x: Int = (this.left + this.right) / 2
            val y: Int = this.bottom / 1

            // Starting radius is the full width of the root layout
            val startRadius: Int = hypot(
                rootView.width.toDouble(),
                rootView.height.toDouble()
            ).toInt()

            // End radius is 0 to close the reveal layout
            val endRadius = 0

            // Create circular reveal animation
            val anim = ViewAnimationUtils.createCircularReveal(
                splashView, // Reveal the entire root layout
                x,
                y,
                startRadius.toFloat(),
                endRadius.toFloat()
            )

            // Listen for animation end to hide the root layout
            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    CoroutineScope(Dispatchers.Main).launch {
                        onAnimEnd.invoke()
                        splashView.beGone()
                    }
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
            // Start the reveal animation
            anim.duration = animSpeed.toLong()
            anim.start()
        }
    }

    fun Context.safeOpenExternalApp(
        packageName: String,
        exception: ((error: String) -> Unit)? = null,
    ) {
        val pm: PackageManager = packageManager
        val launchIntent: Intent? = pm.getLaunchIntentForPackage(packageName)
        try {
            if (launchIntent != null) {
                startActivity(launchIntent)
            } else {
                throw ActivityNotFoundException("Launch intent not found for package: $packageName")
            }
        } catch (e: ActivityNotFoundException) {
            exception?.invoke(e.message.toString())
        } finally {
            println("Attempt to launch package $packageName ")
        }
    }


    fun Context.getRawResourcePath(@RawRes rawResId: Int): Uri {
        return Uri.parse("android.resource://${packageName}/$rawResId")
    }

    fun Context.openPlayStoreWithAccount(accountName: String) {
        val playStoreIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/developer?id=$accountName")
        )
        if (playStoreIntent.resolveActivity(packageManager) != null) {
            startActivity(playStoreIntent)
        } else {
            toast("Play Store app not installed")
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/developer?id=$accountName")
            )

            if (browserIntent.resolveActivity(packageManager) != null) {
                startActivity(browserIntent)
            } else {
                toast("No browser app found")
            }
        }
    }

    fun Context.openPlayStoreWithAPID(packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
            intent.setPackage("com.android.vending")
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // If the Play Store app is not available, open the Play Store website
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
            startActivity(intent)
        }
    }

    @ExperimentalCrashTestApi
    fun Context.fillRam() {
        CoroutineScope(Dispatchers.Main).launch {

            val linearLayout = LinearLayout(this@fillRam)
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            linearLayout.orientation = LinearLayout.VERTICAL

            val size = 2500
            val tile = 10
            val bmp =
                Bitmap.createBitmap(
                    size,
                    size,
                    Bitmap.Config.ARGB_4444
                ) // this creates a MUTABLE bitmap
            log("Before:${getAvailableRAM()}")
            val canvas = Canvas(bmp)
            val paint = Paint()
            paint.color = Color.RED
            for (x in 0 until size step tile) {
                for (y in 0 until size step tile) {
                    // Draw smaller rectangles (tiles) to create the larger bitmap
                    canvas.drawRect(
                        x.toFloat(),
                        y.toFloat(),
                        (x + tile).toFloat(),
                        (y + tile).toFloat(),
                        paint
                    )
                }
            }
            repeat(50) {
                val imageView = ImageView(this@fillRam)
                imageView.setImageBitmap(bmp)
                linearLayout.addView(imageView)
            }
        }

    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    fun Context.vibratePhone(timeInMilliSec: Long = 50) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    timeInMilliSec,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            vibrator.vibrate(timeInMilliSec)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun Activity.checkThisPermission(permission: String): Boolean {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }


    inline fun <reified T : ViewBinding> Context.showDialog(
        crossinline bindingInflater: (LayoutInflater) -> T,
        themeResId: Int = 0,
        crossinline initView: (T, Dialog) -> Unit = { _, _ ->
        },
    ): Dialog {
        val dialog = Dialog(this, themeResId)
        val binding = bindingInflater(LayoutInflater.from(this))
        dialog.setContentView(binding.root)
        initView(binding, dialog)
        dialog.show()
        return dialog
    }

    fun withDelay(delay: Long = 500, callBack: (() -> Unit)) {
        Handler(Looper.getMainLooper()).postDelayed({
            callBack.invoke()
        }, delay)
    }

    // Function to get resource ID from resource name
    fun Context.getResourceId(resName: String, resType: String): Int {
        return resources.getIdentifier(resName, resType, packageName)
    }

    fun Context.readJsonFromAssets(fileName: String): String {
        val inputStream = assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer)
    }

    fun Activity.routWithData(
        data: String,
        cls: Class<*>,
        reqCode: Int? = null,
    ) {
        val intent = Intent(this, cls)
        intent.putExtra(intentData, data)
        if (reqCode != null) {
            startActivityForResult(intent, reqCode)
        } else {
            startActivity(intent)
        }
    }

    fun Activity.rout(
        cls: Class<*>,
        reqCode: Int? = null,
    ) {
        val intent = Intent(this, cls)
        if (reqCode != null) {
            startActivityForResult(intent, reqCode)
        } else {
            startActivity(intent)
        }
    }

    fun isAndroid13OrAbove() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    fun Any.logIt(
        tag: String = "ExtensionsHsb",
    ): String {
        Log.e(tag, "//--------------------\n$this\n--------------------//")
        return "$this"
    }


    fun View.convertToPdf(fileName: String = "temp_${System.currentTimeMillis()}"): String {
        // Create a PDF document
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(width, height, 1).create()
        val page = document.startPage(pageInfo)
        draw(page.canvas)
        document.finishPage(page)
        // Save the document
        val filePath = context.getExternalFilesDir(null)?.absolutePath + "/$fileName.pdf"
        val file = File(filePath)
        FileOutputStream(file).use { out ->
            document.writeTo(out)
        }
        val targetFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            "$fileName.pdf"
        )
        file.copyTo(targetFile, true)
        document.close()

        return filePath
    }

    inline fun <reified T : ViewBinding> Context.showBottomSheetDialog(
        crossinline bindingInflater: (LayoutInflater) -> T,
        themeResId: Int = R.style.TransparentBottomSheetDialog,
        crossinline initView: (T, BottomSheetDialog) -> Unit = { _, _ -> }
    ): BottomSheetDialog {
        val dialog = BottomSheetDialog(this, themeResId)
        val binding = bindingInflater(LayoutInflater.from(this))
        dialog.setContentView(binding.root)
        initView(binding, dialog)
        dialog.show()
        return dialog
    }
}