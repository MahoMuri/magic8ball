package com.example.magic8ball

import android.graphics.drawable.TransitionDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Transition
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat


class MainActivity : AppCompatActivity() {
    private lateinit var button: Button
    private lateinit var image: ImageView
    private lateinit var response: TextView
    private lateinit var question: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Magic 8 Ball"

        button = findViewById(R.id.btn)
        image = findViewById(R.id.magic8ball)
        response = findViewById(R.id.responseGenerator)
        question = findViewById(R.id.questionInput)

        button.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this, R.anim.shake)
            image.startAnimation(animation)

            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    response.alpha = 0f;
                    image.setImageResource(R.drawable.magic_8_ball)
                }

                override fun onAnimationEnd(animation: Animation?) {
                    val td = TransitionDrawable(
                        arrayOf(
                            ResourcesCompat.getDrawable(resources, R.drawable.magic_8_ball, theme),
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.magic_8_ball_response,
                                theme
                            )
                        )
                    )
                    image.setImageDrawable(td)
                    td.startTransition(500)

                    response.alpha = 1f;
                    response.text =
                        EightBallLogic.generateResponse(this@MainActivity, question.text)

                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })

        }
    }
}