package com.kpstv.swipe_dismiss_image

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat

class Main : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainImageView = findViewById<ImageView>(R.id.am_imageView)
        mainImageView.transitionName = "rootImageView"

        findViewById<Button>(R.id.am_btn_basic).setOnClickListener {
            val intent = Intent(this, ImageActivity::class.java)
            val options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(
                    this, mainImageView, mainImageView.transitionName
                )
            startActivity(intent, options.toBundle())
        }

        findViewById<Button>(R.id.am_btn_network).setOnClickListener {
            ImageActivity.startAndLoadFromNetwork(this, "https://source.unsplash.com/random")
        }
    }
}