package com.hsb.extensions_hsb.utils.anims

import android.animation.AnimatorSet
import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.addListener
import androidx.core.view.children
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.RecyclerView
import com.hsb.extensions_hsb.utils.viewextensions.ViewExtensions.beInvisible
import com.hsb.extensions_hsb.utils.viewextensions.ViewExtensions.beVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.atan2

/**
 * Developed by Syed Haseeb
 * syedhaseeb1.github.io
 */

object Animations {
    fun ViewGroup.animateOnViewChanges(
        animDelay: Int = 50,
        hideViews: Boolean = false,
        transitionListener: LayoutTransition.TransitionListener? = null,
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
        if (this !is FragmentContainerView) {
            this.layoutTransition = layoutTransition
        }
        this.children.forEach { v1 ->
            CoroutineScope(Dispatchers.Main).launch {
                delay(animDelay.toLong())
                if (hideViews) {
                    v1.beVisible()
                }
                if (v1 is ViewGroup) {
                    if (v1 !is RecyclerView && v1 !is FragmentContainerView) {
                        v1.layoutTransition = layoutTransition
                        v1.children.forEach { v2 ->
                            if (hideViews) {
                                v2.beVisible()
                            }
                            if (v2 is ViewGroup) {
                                if (v2 !is RecyclerView && v2 !is FragmentContainerView) {
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

    fun View.apply3DEffectHorizontal(sensitivity: Float = 2f) {
        val sensorManager: SensorManager =
            context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                //Not needed
            }

            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_GRAVITY) {
                    val y = event.values[0] * sensitivity
                    val rY = y / 2 * (-1)
                    this@apply3DEffectHorizontal.rotationY = rY * (-1)
                }
            }
        }, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), Sensor.TYPE_ACCELEROMETER)
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
}