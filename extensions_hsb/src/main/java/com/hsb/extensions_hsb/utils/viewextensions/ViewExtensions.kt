package com.hsb.extensions_hsb.utils.viewextensions

import android.animation.AnimatorSet
import android.animation.LayoutTransition
import android.animation.LayoutTransition.TransitionListener
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.VideoView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.hsb.extensions_hsb.R
import com.hsb.extensions_hsb.utils.globalextensions.Extensions.toast
import com.hsb.extensions_hsb.utils.viewextensions.ViewExtensions.beInvisible
import com.hsb.extensions_hsb.utils.viewextensions.ViewExtensions.beVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.math.atan2

/**
 * Nov 14, 2023
 * Developed by Syed Haseeb
 * Github: https://github.com/syedhaseeb1
 *
 * Updated on May 30, 2024
 */
object ViewExtensions {
    fun ImageView.tint(color: Int) {
        this.setColorFilter(ContextCompat.getColor(this.context, color))
    }

    fun ImageView.loadImage(src: Any) {
        Glide.with(this.context).load(src)
            .error(android.R.drawable.stat_notify_error)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(this)

    }

    fun ImageView.loadImageFromAssets(fileName: String) {
        val assetManager = context.assets
        try {
            val inputStream = assetManager.open(fileName)
            val drawable = Drawable.createFromStream(inputStream, null)
            this.setImageDrawable(drawable)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun ImageView.loadImage(src: Any, @DrawableRes errorIco: Int) {
        Glide.with(context).load(src)
            .error(errorIco)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(this)

    }

    fun ImageView.loadImage(src: Any, cache: Boolean = false) {
        if (cache) {
            Glide.with(context)
                .load(src)
                .error(android.R.drawable.stat_notify_error)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(this)

        } else {
            loadImage(src)
        }
    }

    fun View.setTint(color: Int) {
        this.backgroundTintList = ColorStateList.valueOf(color)
    }

    fun View.setOnSwipeListener(onSwipe: (left: Boolean, right: Boolean, up: Boolean, down: Boolean) -> Unit) {
        try {
            val gestureDetector =
                GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                    private val SWIPE_THRESHOLD = 100
                    private val SWIPE_VELOCITY_THRESHOLD = 100

                    override fun onDown(e: MotionEvent): Boolean {
                        return true
                    }

                    override fun onFling(
                        e1: MotionEvent?,
                        e2: MotionEvent,
                        velocityX: Float,
                        velocityY: Float,
                    ): Boolean {
                        val diffX = e2.x - e1!!.x
                        val diffY = e2.y - e1.y

                        if (Math.abs(diffX) > Math.abs(diffY) &&
                            Math.abs(diffX) > SWIPE_THRESHOLD &&
                            Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD
                        ) {
                            if (diffX > 0) {
                                // Right swipe
                                onSwipe(true, false, false, false)
                            } else {
                                // Left swipe
                                onSwipe(false, true, false, false)
                            }
                            return true
                        } else if (Math.abs(diffY) > Math.abs(diffX) &&
                            Math.abs(diffY) > SWIPE_THRESHOLD &&
                            Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD
                        ) {
                            if (diffY > 0) {
                                // Down swipe
                                onSwipe(false, false, false, true)
                            } else {
                                // Up swipe
                                onSwipe(false, false, true, false)
                            }
                            return true
                        }
                        return false
                    }
                })
            setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
        } catch (e: java.lang.NullPointerException) {
            e.printStackTrace()
        }
    }
    fun setOnTouchListener(vararg views: View, listener: View.OnTouchListener) {
        views.forEach { it.setOnTouchListener(listener) }
    }
    fun View.safeClickListener(
        debounceTime: Long = 800L,
        action: (view: View) -> Unit,
    ) {
        this.setOnClickListener(object : View.OnClickListener {
            private var lastClickTime: Long = 0
            override fun onClick(v: View) {
                if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
                else action.invoke(this@safeClickListener)
                lastClickTime = SystemClock.elapsedRealtime()
            }
        })
    }

    fun safeClickListeners(vararg views: View, listener: View.OnClickListener) {
        views.forEach {
            it.safeClickListener { view ->
                listener.onClick(view)
            }
        }
    }


    fun View.showKeyboard() {
        val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        this.requestFocus()
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    fun View.hideKeyboard() {
        val inputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        this.clearFocus()
        inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
    }

    fun EditText.setOnSearchClickListener(callback: (String) -> Unit) {
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // Handle the search action here
                val text = text.trim().toString()
                callback.invoke(text)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    fun EditText.setOnTextChangeListener(callback: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = text.trim().toString()
                callback.invoke(text)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    fun EditText.autoFillFromClipBoard() {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboardManager.primaryClip
        if (clipData != null && clipData.itemCount > 0) {
            val pastedText = clipData.getItemAt(0).coerceToText(context).toString()
            if (pastedText.startsWith("http")) {
                text?.clear()
                text?.append(pastedText)
                showKeyboard()
            } else {
                context.toast("Clipboard is empty")
            }

        } else {
            //context.toast("Clipboard is empty")
        }
    }

    fun View.focusListener() {
        this.viewTreeObserver.addOnPreDrawListener {
            val currentVisibility = this.visibility
            if (currentVisibility == View.VISIBLE) {
                showKeyboard()
            } else {
                hideKeyboard()
            }
            this.isEnabled = currentVisibility == View.VISIBLE
            true
        }
    }

    //Buttons
    fun View.considerAsBackButton(activity: Activity) {
        this.setOnClickListener {
            activity.finish()
        }
    }

    fun Activity.setSystemUIColor(
        statusBar: Boolean = true,
        navigation: Boolean = true,
        color: Int,
    ) {
        val myColor = ContextCompat.getColor(this, color)
        if (statusBar) {
            window?.statusBarColor = myColor
        }
        if (navigation) {
            window?.navigationBarColor = myColor
        }
    }


    fun withDelay(timeInMillis: Long = 500, callback: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed({ callback.invoke() }, timeInMillis)
    }

    fun View.beGone() {
        this.apply {
            clearAnimation()
            visibility = View.GONE
        }
    }

    fun View.beVisible() {
        this.visibility = View.VISIBLE
    }

    fun View.beInvisible() {
        this.apply {
            clearAnimation()
            visibility = View.INVISIBLE
        }
    }


    fun VideoView.playVideoFromRaw(
        rawResId: Int,
        playCallback: ((mediaPlayer: MediaPlayer) -> Unit),
    ) {
        val videoUri: Uri = Uri.parse("android.resource://${context.packageName}/$rawResId")
        setVideoURI(videoUri)
        setOnPreparedListener { mediaPlayer ->
            playCallback.invoke(mediaPlayer)
        }
    }


    fun View.showPopUpMenu(@MenuRes menuId: Int, callBack: (clickedID: Int) -> Unit) {
        val popupMenu = PopupMenu(this.context, this)
        popupMenu.menuInflater.inflate(menuId, popupMenu.menu);
        popupMenu.setOnMenuItemClickListener {
            callBack.invoke(it.itemId)
            true
        }
        popupMenu.show()
    }
}


