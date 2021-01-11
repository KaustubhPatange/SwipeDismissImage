package com.kpstv.dismiss.image

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class SwipeDismissImageLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnTouchListener {

    private val TAG = javaClass.simpleName

    private var rootV: View
    private var rootImageView: ImageView
    private val displayMetrics = Resources.getSystem().displayMetrics

    private var swipeDismissEnabled: Boolean = true
    private var affectOpacity: Boolean = true
    private var thresholdCollapseDistance: Int = displayMetrics.heightPixels / 3

    private var pointer1: TranslationPointer = TranslationPointer()
    private var pointer2: ScalePointer = ScalePointer()

    private var lastScaleFactor: Float = 0f

    private var swipeDirection: SwipeDirection = SwipeDirection.Bottom

    private var swipeDismissListener: SwipeDismissListener? = null

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

    init {
        rootImageView = ImageView(context)
        rootImageView.layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        rootImageView.transitionName = IMAGE_TRANSITION_NAME
        rootImageView.tag = IMAGE_TAG_NAME
        rootImageView.translationZ = 1.dp(context)

        rootV = View(context)
        rootV.layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        rootV.background = ColorDrawable(ContextCompat.getColor(context, android.R.color.black))

        context.withStyledAttributes(attrs, R.styleable.SwipeDismissImageLayout) {
            swipeDismissEnabled = getBoolean(R.styleable.SwipeDismissImageLayout_swipeDismissEnabled, true)
            affectOpacity = getBoolean(R.styleable.SwipeDismissImageLayout_affectOpacity, true)
            thresholdCollapseDistance = getDimensionPixelSize(R.styleable.SwipeDismissImageLayout_swipeOffsetDistance, displayMetrics.heightPixels / 3)

            rootImageView.transitionName = getString(R.styleable.SwipeDismissImageLayout_imageTransitionName) ?: IMAGE_TRANSITION_NAME

            rootImageView.setImageDrawable(getDrawable(R.styleable.SwipeDismissImageLayout_android_drawable))
            rootV.background = getDrawable(R.styleable.SwipeDismissImageLayout_rootBackground)
        }

        addView(rootImageView)
        addView(rootV)

        setOnTouchListener(this)
    }

    fun setSwipeDismissListener(swipeDismissListener: SwipeDismissListener) {
        this.swipeDismissListener = swipeDismissListener
    }

    fun getRootImageView(): ImageView = rootImageView

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_POINTER_UP -> {
                lastPointUp = true
            }
            MotionEvent.ACTION_DOWN -> {
                pointer1.firstX = event.rawX
                pointer1.firstY = event.rawY

                val currentMs = System.currentTimeMillis()
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
                        rootImageView.scale(1f, true)
                        rootImageView.translate(0f, 0f, true)
                        return true
                    }

                    if (factor <= 2f) {
                        lastScaleFactor = factor
                        rootImageView.scale((1 + factor).coerceAtLeast(1f))
                    }
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

                        rootImageView.translate(pointer1.swipeDistanceX * 1.6f, pointer1.swipeDistanceY * 1.6f)
                    } else if (!lastPointUp) {
                        val totalDistanceTravelled = sqrt((pointer1.firstX - pointer1X).pow(2) + (pointer1.firstY - pointer1Y).pow(2))
                        if (totalDistanceTravelled > thresholdCollapseDistance)
                            shouldCollapse = true

                        val maxDistance = max(abs(totalSwipeDistance1X), abs(totalSwipeDistance1Y))
                        val alphaFactor = 1 - abs(maxDistance * 1.5f / displayMetrics.heightPixels)

                        if (affectOpacity) modifyChildOpacity(alphaFactor)
                        rootImageView.translate(totalSwipeDistance1X, totalSwipeDistance1Y)

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
                    modifyChildOpacity(0f, true)
                    rootImageView.translate(pointer1.totalSwipeDistanceX * swipeDirection.deltaX, pointer1.totalSwipeDistanceY * swipeDirection.deltaY, true) {
                        swipeDismissListener?.onCollapse()
                    }
                    return true
                }
                shouldCollapse = false
                pointer2.lastScaleFactor = 0f
                pointer1.swipeDistanceX = 0f
                pointer1.swipeDistanceY = 0f

                modifyChildOpacity(1f, true)
                rootImageView.translate(0f, 0f, true)
            }
        }
        return swipeDismissEnabled
    }

    private fun modifyChildOpacity(factor: Float, animate: Boolean = false) {
        children.forEach { child ->
            if (child.tag != IMAGE_TAG_NAME) {
                child.modifyOpacity(factor, animate)
            }
        }
    }

    private fun toggleDoubleTapAction() {
        val rectLocal = Rect()
        rootImageView.getLocalVisibleRect(rectLocal)

        if (rectLocal.left == 0 && rectLocal.top == 0) {
            isScaling = true
            pointer2.lastScaleFactor = lastScaleFactor
            rootImageView.scale(3f, true)
        } else {
            isScaling = false
            val transX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f)
            val transY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0f)
            val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)
            val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f)
            ObjectAnimator.ofPropertyValuesHolder(rootImageView, transX, transY, scaleX, scaleY).start()
        }
    }

    companion object {
        internal const val IMAGE_TRANSITION_NAME = "sdl_imageView"
        internal const val IMAGE_TAG_NAME = "sdl_imageView_tagName"
    }
}