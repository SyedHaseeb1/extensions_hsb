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
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
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
 * Updated on May 17, 2024
 */
object ViewExtensions {
    fun ImageView.tint(color: Int) {
        this.setColorFilter(ContextCompat.getColor(this.context, color))
    }

    fun ImageView.loadImage(src: Any) {
        Glide.with(this.context).load(src)
            .error(com.google.android.material.R.drawable.mtrl_ic_error)
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

    fun ImageView.loadImage(src: Any, errorIco: Int) {
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
                .error(com.google.android.material.R.drawable.mtrl_ic_error)
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


    fun View.animIn(left: Boolean = false, right: Boolean = false) {
        val translationX = ObjectAnimator.ofFloat(
            this,
            View.TRANSLATION_X,
            if (left) {
                -800f
            } else {
                800f
            },
            0f
        )
        val animatorSet = AnimatorSet()
        val scaleX =
            ObjectAnimator.ofFloat(this, View.SCALE_X, 1f, 0.5f, 1f)
        animatorSet.playTogether(
            translationX, scaleX
        )
        animatorSet.start()
        this.visibility = View.VISIBLE
    }

    fun View.animOut(left: Boolean = false, right: Boolean = false) {
        val translationX = ObjectAnimator.ofFloat(
            this,
            View.TRANSLATION_X,
            0f,
            if (left) {
                -800f
            } else {
                800f
            }
        )
        val animatorSet = AnimatorSet()
        val scaleX =
            ObjectAnimator.ofFloat(this, View.SCALE_X, 1f, 0.5f, 1f)
        animatorSet.playTogether(
            translationX, scaleX
        )
        animatorSet.start()
        animatorSet.addListener {
            this.visibility = View.GONE
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

    //Effects
    fun View.applyGravityEffect(sensitivity: Int = 2) {
        val sensorManager: SensorManager =
            context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                //Not needed
            }

            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_GRAVITY) {
                    val x = event.values[0] * sensitivity
                    val y = event.values[1] * sensitivity
                    this@applyGravityEffect.rotation = x
                }
            }
        }, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), Sensor.TYPE_ACCELEROMETER)
    }

    // Extension function to apply gravity fall effect
    fun View.applyGravityFallEffect(sensitivity: Int = 2) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

        var gravityX = 0f
        var gravityY = 0f

        val accelerometerListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }

            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor == accelerometerSensor) {
                    val alpha = 0.8f
                    gravityX = alpha * gravityX + (1 - alpha) * event!!.values[0]
                    gravityY = alpha * gravityY + (1 - alpha) * event.values[1]
                } else if (event?.sensor == gravitySensor) {
                    gravityX = event!!.values[0]
                    gravityY = event.values[1]
                }

                val angle = atan2(gravityX, gravityY) * (180 / Math.PI)
                val maxRotation = 30f // Adjust this value to control max rotation angle

                val rotation =
                    if (angle > maxRotation) maxRotation else if (angle < -maxRotation) -maxRotation else angle.toFloat()

                this@applyGravityFallEffect.rotation = (rotation * (-1)) * sensitivity
            }
        }

        sensorManager.registerListener(
            accelerometerListener,
            accelerometerSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            accelerometerListener,
            gravitySensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun View.apply3DEffect(sensitivity: Int = 2) {
        val sensorManager: SensorManager =
            context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                //Not needed
            }

            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_GRAVITY) {
                    val x = event.values[0] * sensitivity
                    val y = event.values[1] * sensitivity
                    val rX = x / 2 * (-1)
                    val rY = y * (2)
                    this@apply3DEffect.rotationY = rX
                    this@apply3DEffect.rotationX = rY
                }
            }
        }, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), Sensor.TYPE_ACCELEROMETER)
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
}

fun ViewGroup.animateOnViewChanges(
    animDelay: Int = 50,
    hideViews: Boolean = false,
    transitionListener: TransitionListener? = null,
) {
    if (hideViews) {
        this.children.forEach { v1 ->
            v1.beInvisible()
            if (v1 is ViewGroup) {
                v1.children.forEach { v2 ->
                    v2.beInvisible()
                }
            }
        }
    }
    val layoutTransition = LayoutTransition()
    layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    transitionListener?.let {
        layoutTransition.addTransitionListener(it)
    }
    this.layoutTransition = layoutTransition
    this.children.forEach { v1 ->
        CoroutineScope(Dispatchers.Main).launch {
            delay(animDelay.toLong())
            if (hideViews) {
                v1.beVisible()
            }
            if (v1 is ViewGroup) {
                if (v1 !is RecyclerView) {
                    v1.layoutTransition = layoutTransition
                    v1.children.forEach { v2 ->
                        if (hideViews) {
                            v2.beVisible()
                        }
                        if (v2 is ViewGroup) {
                            if (v2 !is RecyclerView) {
                                v2.layoutTransition = layoutTransition
                                v2.children.forEach { v3 ->
                                    if (hideViews) {
                                        v3.beVisible()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
