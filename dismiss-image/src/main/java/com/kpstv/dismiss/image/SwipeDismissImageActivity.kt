package com.kpstv.dismiss.image

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

open class SwipeDismissImageActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    private lateinit var rootV: View
    private lateinit var rootImageView: ImageView
    private val displayMetrics = Resources.getSystem().displayMetrics

    fun setImageDrawable(@DrawableRes resId: Int) {
        rootImageView.setImageDrawable(drawableFrom(resId))
    }

    fun setImageDrawable(drawable: Drawable) {
        rootImageView.setImageDrawable(drawable)
    }

    fun setImageBitmap(bitmap: Bitmap) {
        rootImageView.setImageBitmap(bitmap)
    }

    fun getRootImageView(): ImageView = rootImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_dismiss)

        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        rootV = findViewById(R.id.sdl_emptyView)
        rootV.background = ColorDrawable(ContextCompat.getColor(this, android.R.color.black))
        rootImageView = findViewById(R.id.sdl_imageView)
    }

    private var pointer1: TranslationPointer = TranslationPointer()
    private var pointer2: ScalePointer = ScalePointer()

    private var lastScaleFactor: Float = 0f

    private var swipeDirection: SwipeDirection = SwipeDirection.Bottom
    private var thresholdCollapseDistance: Int = displayMetrics.heightPixels / 3

    private var isScaling: Boolean = false

    /**
     * This will be used to ignore when second input is ended, but still
     * consider as translation.
     */
    private var lastPointUp: Boolean = false
    private var shouldCollapse: Boolean = false

    /**
     * This will be used to detect second tap with offset of 250ms.
     */
    private var firstTouchMillisecond: Long = 0L

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_POINTER_UP -> {
                lastPointUp = true
            }
            MotionEvent.ACTION_DOWN -> {
                pointer1.firstX = event.rawX
                pointer1.firstY = event.rawY

                val currentMs = System.currentTimeMillis()
                Log.e(TAG, "Current: $currentMs, first: $firstTouchMillisecond, offset: ${currentMs - firstTouchMillisecond}")
                if (currentMs - firstTouchMillisecond <= 250) {
                    toggleDoubleTapAction()
                }
                firstTouchMillisecond = currentMs
            }
            MotionEvent.ACTION_MOVE -> {
                val pointer1X = event.rawX
                val pointer1Y = event.rawY

                val totalSwipeDistance1X = pointer1X - pointer1.firstX
                val totalSwipeDistance1Y = pointer1Y - pointer1.firstY

                // Scaling
                if (event.pointerCount > 1) {
                    isScaling = true

                    val pointer2X = event.getX(1)
                    val pointer2Y = event.getY(1)

                    val distanceBetween = sqrt((pointer2X - pointer1X).pow(2) + (pointer2Y - pointer1Y).pow(2))
                    if (pointer2.initialDistance == 0f) {
                        pointer2.initialDistance = distanceBetween
                        return true
                    }

                    val factor = (distanceBetween - pointer2.initialDistance) * 2f / displayMetrics.heightPixels + pointer2.lastScaleFactor
                    // Reset
                    if (factor < 0.005f) {
                        isScaling = false
                        scale(1f, true)
                        translateView(0f, 0f, true)
                        return true
                    }

                    if (factor <= 2f) {
                        lastScaleFactor = factor
                        scale((1 + factor).coerceAtLeast(1f))
                    }
                    Log.e(TAG, "Scale Factor: $factor")
                } else {
                    // Translation

                    if (isScaling) {
                        val dx = totalSwipeDistance1X - pointer1.totalSwipeDistanceX
                        val dy = totalSwipeDistance1Y - pointer1.totalSwipeDistanceY

                        // Do not respond for higher translation
                        if (abs(dx) > 200 || abs(dy) > 200) {
                            pointer1.totalSwipeDistanceX = 0f
                            pointer1.totalSwipeDistanceY = 0f
                            return true
                        }

                        if (pointer1.swipeDistanceX == 0f || pointer1.swipeDistanceY == 0f) {
                            pointer1.swipeDistanceX = dx
                            pointer1.swipeDistanceY = dy
                            return true
                        }

                        pointer1.swipeDistanceX += dx.coerceIn(-20f, 20f)
                        pointer1.swipeDistanceY += dy.coerceIn(-20f, 20f)

                        translateView(pointer1.swipeDistanceX * 1.6f, pointer1.swipeDistanceY * 1.6f)
                    } else if (!lastPointUp) {
                        val totalDistanceTravelled = sqrt((pointer1.firstX - pointer1X).pow(2) + (pointer1.firstY - pointer1Y).pow(2))
                        if (totalDistanceTravelled > thresholdCollapseDistance)
                            shouldCollapse = true

                        val maxDistance = max(abs(totalSwipeDistance1X), abs(totalSwipeDistance1Y))
                        val alphaFactor = 1 - abs(maxDistance * 1.5f / displayMetrics.heightPixels)

                        reduceOpacity(alphaFactor)
                        translateView(totalSwipeDistance1X, totalSwipeDistance1Y)

                        swipeDirection = SwipeDirection.detectSwipeDirection(totalSwipeDistance1X, totalSwipeDistance1Y)
                    }
                    pointer1.totalSwipeDistanceX = totalSwipeDistance1X
                    pointer1.totalSwipeDistanceY = totalSwipeDistance1Y
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                pointer2.initialDistance = 0f
                pointer2.lastScaleFactor = lastScaleFactor

                if (isScaling) return true
                if (lastPointUp) {
                    lastPointUp = false
                    return true
                }
                if (shouldCollapse) {
                    reduceOpacity(0f, true)
                    translateView(pointer1.totalSwipeDistanceX * swipeDirection.deltaX, pointer1.totalSwipeDistanceY * swipeDirection.deltaY, true) {
                        shouldCollapse()
                    }
                    return true
                }
                shouldCollapse = false
                pointer2.lastScaleFactor = 0f
                pointer1.swipeDistanceX = 0f
                pointer1.swipeDistanceY = 0f
                reduceOpacity(1f, true)
                translateView(0f, 0f, true)
            }
        }
        return super.onTouchEvent(event)
    }

    private fun toggleDoubleTapAction() {
        val rectLocal = Rect()
        rootImageView.getLocalVisibleRect(rectLocal)

        if (rectLocal.left == 0 && rectLocal.top == 0) {
            isScaling = true
            pointer2.lastScaleFactor = lastScaleFactor
            scale(3f, true)
        } else {
            isScaling = false
            val transX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f)
            val transY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0f)
            val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)
            val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f)
            ObjectAnimator.ofPropertyValuesHolder(rootImageView, transX, transY, scaleX, scaleY).start()
        }
    }

    private fun translateView(x: Float, y: Float, animate: Boolean = false, onAnimationEnd: () -> Unit = {}) {
        if (animate) {
            val transX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, x)
            val transY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, y)
            val animator = ObjectAnimator.ofPropertyValuesHolder(rootImageView, transX, transY)
            animator.addListener(onEnd = {
                onAnimationEnd()
            })
            animator.duration = 300
            animator.start()
            return
        }
        rootImageView.translationX = x
        rootImageView.translationY = y
    }

    private fun scale(factor: Float, animate: Boolean = false) {
        if (animate) {
            ObjectAnimator.ofPropertyValuesHolder(
                rootImageView,
                PropertyValuesHolder.ofFloat(View.SCALE_X, factor),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, factor)
            ).apply { duration = 200 }.start()
            return
        }
        rootImageView.scaleX = factor
        rootImageView.scaleY = factor
    }

    private fun reduceOpacity(factor: Float, animate: Boolean = false) {
        if (animate) {
            ObjectAnimator
                .ofPropertyValuesHolder(rootV, PropertyValuesHolder.ofFloat(View.ALPHA, factor))
                .apply { duration = 200 }
                .start()
            return
        }
        rootV.alpha = factor
    }

    private fun shouldCollapse() {
        finish()
    }

    companion object {
        const val IMAGE_TRANSITION_NAME = "sdl_imageView"
    }
}