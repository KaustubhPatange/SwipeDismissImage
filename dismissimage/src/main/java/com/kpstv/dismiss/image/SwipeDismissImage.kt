package com.kpstv.dismiss.image

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import android.os.TransactionTooLargeException
import androidx.annotation.DrawableRes
import java.io.File
import java.io.FileOutputStream

object SwipeDismissImage {
//
//    /**
//     * Starts [SwipeDismissImageActivity] and sets the drawable on the ImageView.
//     *
//     * @throws NullPointerException when drawable [resId] is not found
//     */
//    @JvmStatic
//    fun startActivity(context: Context, @DrawableRes resId: Int) {
//        startActivity(context, BitmapFactory.decodeResource(context.resources, resId))
//    }
//
//    /**
//     * Starts [SwipeDismissImageActivity] and sets the drawable on the ImageView.
//     */
//    @JvmStatic
//    fun startActivity(context: Context, drawable: Drawable) {
//        startActivity(context, drawable.toBitmap())
//    }
//
//    /**
//     * Starts the [SwipeDismissImageActivity] and sets the bitmap on the ImageView.
//     *
//     * @exception TransactionTooLargeException
//     * When bitmap size is greater than 1Mb. To avoid such issue inherit [SwipeDismissImageActivity]
//     * and create a custom implementation instead.
//     */
//    @JvmStatic
//    fun startActivity(context: Context, bitmap: Bitmap) {
//        val tempFile = File.createTempFile("sdl","${context.packageName}.png")
//        FileOutputStream(tempFile).use {
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
//        }
//        val intent: Intent = Intent(context, SwipeDismissImageActivity::class.java).apply {
//            putExtra(SwipeDismissImageActivity.ARG_BITMAP_FILE, tempFile.absolutePath)
//        }
//        context.startActivity(intent)
//    }
}