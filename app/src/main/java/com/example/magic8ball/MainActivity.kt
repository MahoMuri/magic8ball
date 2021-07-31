package com.example.magic8ball

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    private lateinit var button : Button
    private lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Magic 8 Ball"

        button = findViewById(R.id.btn)
        image = findViewById(R.id.magic8ball)

        button.setOnClickListener(View.OnClickListener {
            image.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
        })
    }
}