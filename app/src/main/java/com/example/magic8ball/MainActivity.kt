package com.example.magic8ball

import android.content.Context
import android.content.Intent
import android.graphics.drawable.TransitionDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var button : Button
    private lateinit var image: ImageView
    private lateinit var response: TextView
    private lateinit var questionInput: TextInputLayout
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Magic 8 Ball"

        // Variable initializations
        button = findViewById(R.id.btn)
        image = findViewById(R.id.magic8ball)
        response = findViewById(R.id.responseGenerator)
        questionInput = findViewById(R.id.textInputLayout)
        toolbar = findViewById(R.id.main_toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        // Set custom Action bar
        setSupportActionBar(toolbar)
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.openNavDrawer,
            R.string.closeNavDrawer
        )

        // Implement nav bar
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navView.setNavigationItemSelectedListener(this)


        // Variables for shake detection
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH

        // Activate action in button click
        button.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this, R.anim.shake)
            image.startAnimation(animation)

            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    image.setImageResource(R.drawable.magic_8_ball)
                    response.text = ""
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
                    response.text = EightBallLogic.generateResponse(this@MainActivity, questionInput.editText?.text.toString())
                    response.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, R.anim.fadein))
                }
                override fun onAnimationRepeat(animation: Animation?) {
                    // Ignore since animation does not repeat. Cannot omit either since it is required to implement the function
                }

            })
        }
    }

    // Declaring function for hardware shake detection
    private val sensorListener: SensorEventListener = object : SensorEventListener {

        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration
            currentAcceleration = kotlin.math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta

            // Only proceed is acceleration is greater than 25
            if (acceleration > 25f) {
                Log.d("Sensor", "Shake detected w/ speed $acceleration")
                val animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.shake)
                image.startAnimation(animation)

                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        image.setImageResource(R.drawable.magic_8_ball)
                        response.text = ""
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
                        response.text = EightBallLogic.generateResponse(this@MainActivity, questionInput.editText?.text.toString())
                        response.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, R.anim.fadein))
                    }
                    override fun onAnimationRepeat(animation: Animation?) {
                        // Ignore since animation does not repeat. Cannot omit either since it is required to implement the function
                    }

                })
            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }
    override fun onResume() {
        sensorManager?.registerListener(sensorListener, sensorManager!!.getDefaultSensor(
            Sensor .TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        super.onResume()
    }
    override fun onPause() {
        sensorManager!!.unregisterListener(sensorListener)
        super.onPause()
    }

    // Navbar item selected handler
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val itemName = item.toString()

        if (itemName == "Open question history") {
            val intent = Intent(this, ScrollingActivity::class.java)
            startActivity(intent)
        }
        return false
    }


}