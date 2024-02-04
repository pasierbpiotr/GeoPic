package com.geopic

import android.graphics.Bitmap
import com.geopic.theme.GeoPicTheme
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.MenuItem
import android.widget.ImageButton
import androidx.core.view.GravityCompat
import java.io.File

class FullScreenImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.full_screen_image)

        val imageView = findViewById<ImageView>(R.id.fullScreenImageView)
        val returnButton = findViewById<ImageButton>(R.id.returnButton)
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER

        val imagePath = intent.getStringExtra("image")
        val options = BitmapFactory.Options().apply {
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }

        val bitmap = BitmapFactory.decodeFile(imagePath, options)
        imageView.setImageBitmap(bitmap)
        imageView.setBackgroundColor(Color.BLACK)

        returnButton.setOnClickListener {
            finish()
        }
    }
}
