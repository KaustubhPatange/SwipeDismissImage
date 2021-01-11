package com.kpstv.swipe_dismiss_image

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import coil.load
import coil.request.CachePolicy
import com.kpstv.dismiss.image.SwipeDismissImageLayout

class ImageActivity : AppCompatActivity() {
    companion object {
        private const val LOAD_FROM_NETWORK = "load_from_network"
        private const val IMAGE_URI = "image_uri"

        fun startAndLoadFromNetwork(context: Context, imageUrl: String) {
            context.startActivity(Intent(context, ImageActivity::class.java).apply {
                putExtra(LOAD_FROM_NETWORK, true)
                putExtra(IMAGE_URI, imageUrl)
            })
        }
    }

    private lateinit var sdLayout: SwipeDismissImageLayout
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        progressBar = findViewById(R.id.progressBar)
        sdLayout = findViewById(R.id.sdl_layout)
        sdLayout.setSwipeDismissListener { finish() }

        val loadFromNetwork = intent.getBooleanExtra(LOAD_FROM_NETWORK, false)
        if (loadFromNetwork) {
            val imageUrl = intent.getStringExtra(IMAGE_URI)
            sdLayout.getRootImageView().load(imageUrl) {
                memoryCachePolicy(CachePolicy.DISABLED)
                listener(
                    onStart = {
                        progressBar.show()
                    },
                    onError = { _, _ ->
                        progressBar.hide()
                    }, onSuccess = { _, _ ->
                        progressBar.hide()
                    }
                )
            }
        } else {
            progressBar.hide()
            sdLayout.getRootImageView().setImageResource(R.drawable.image1)
        }
    }

    private fun View.show() {
        visibility = View.VISIBLE
    }

    private fun View.hide() {
        visibility = View.INVISIBLE
    }
}