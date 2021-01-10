package com.kpstv.swipe_dismiss_image

import android.content.Context
import android.content.Intent
import android.os.Bundle
import coil.load
import com.kpstv.dismiss.image.SwipeDismissImageActivity

class ImageActivity : SwipeDismissImageActivity() {
    companion object {
        private const val LOAD_FROM_NETWORK = "load_from_network"
        private const val IMAGE_URI = "image_uri"

        fun startAndLoadForNetwork(context: Context, imageUrl: String) {
            context.startActivity(Intent(context, ImageActivity::class.java).apply {
                putExtra(LOAD_FROM_NETWORK, true)
                putExtra(IMAGE_URI, imageUrl)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val loadFromNetwork = intent.getBooleanExtra(LOAD_FROM_NETWORK, false)

        if (!loadFromNetwork) {
            setImageDrawable(R.drawable.image1)
        } else {
            val imageUrl = intent.getStringExtra(IMAGE_URI)
            getRootImageView().load(imageUrl)
        }
    }
}