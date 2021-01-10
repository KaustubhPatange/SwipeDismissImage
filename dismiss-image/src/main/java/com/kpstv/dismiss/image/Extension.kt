package com.kpstv.dismiss.image

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat

internal fun View.modifyOpacity(factor: Float, animate: Boolean = false) {
    if (animate) {
        ObjectAnimator
            .ofPropertyValuesHolder(this, PropertyValuesHolder.ofFloat(View.ALPHA, factor))
            .apply { duration = 200 }
            .start()
        return
    }
    alpha = factor
}

internal fun View.translate(x: Float, y: Float, animate: Boolean = false, onAnimationEnd: () -> Unit = {}) {
    if (animate) {
        val transX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, x)
        val transY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, y)
        val animator = ObjectAnimator.ofPropertyValuesHolder(this, transX, transY)
        animator.addListener(onEnd = {
            onAnimationEnd()
        })
        animator.duration = 300
        animator.start()
        return
    }
    this.translationX = x
    this.translationY = y
}

internal fun Context.drawableFrom(@DrawableRes resId: Int): Drawable? {
    return ContextCompat.getDrawable(this, resId)
}