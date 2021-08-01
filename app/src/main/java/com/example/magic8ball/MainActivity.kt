package com.example.magic8ball

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.TransitionDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var button: Button
    private lateinit var image: ImageView
    private lateinit var response: TextView
    private lateinit var questionInput: TextInputLayout
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var db: DatabaseReference
    private lateinit var mPrefs: SharedPreferences
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

        db = Firebase.database.reference
        mPrefs = getSharedPreferences("Prefs", MODE_PRIVATE)

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
                    val question = questionInput.editText?.text.toString()
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
                    response.text = EightBallLogic.generateResponse(this@MainActivity, question)

                    // Write user to database
                    writeNewUser(NanoIdUtils.randomNanoId(), question, response.text.toString())

                    response.startAnimation(
                        AnimationUtils.loadAnimation(
                            this@MainActivity,
                            R.anim.fadein
                        )
                    )
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

            // Calculate for acceleration by getting the square root of the (x^2 + y^2 + z^2) coordinates of the phone
            lastAcceleration = currentAcceleration
            currentAcceleration = kotlin.math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta

            // Only proceed is acceleration is greater than 20
            if (acceleration > 20f) {
                val animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.shake)
                image.startAnimation(animation)

                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        image.setImageResource(R.drawable.magic_8_ball)
                        response.text = ""
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        val question = questionInput.editText?.text.toString()
                        val td = TransitionDrawable(
                            arrayOf(
                                ResourcesCompat.getDrawable(
                                    resources,
                                    R.drawable.magic_8_ball,
                                    theme
                                ),
                                ResourcesCompat.getDrawable(
                                    resources,
                                    R.drawable.magic_8_ball_response,
                                    theme
                                )
                            )
                        )
                        image.setImageDrawable(td)
                        td.startTransition(500)
                        response.text = EightBallLogic.generateResponse(
                            this@MainActivity,
                            question
                        )
                        writeNewUser(NanoIdUtils.randomNanoId(), question, response.text.toString())
                        response.startAnimation(
                            AnimationUtils.loadAnimation(
                                this@MainActivity,
                                R.anim.fadein
                            )
                        )
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
        sensorManager?.registerListener(
            sensorListener, sensorManager!!.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            ), SensorManager.SENSOR_DELAY_NORMAL
        )
        super.onResume()
    }

    override fun onPause() {
        sensorManager!!.unregisterListener(sensorListener)
        super.onPause()
    }

    // Navbar item selected handler
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.toString()) {
            "Open question history" -> {
                val intent = Intent(this, ScrollingActivity::class.java)
                startActivity(intent)
            }
            "Clear question history" -> {
                val editor = mPrefs.edit()
                editor.remove("QHIS").apply()
                Toast.makeText(this, "Question History deleted!", Toast.LENGTH_SHORT).show()
            }
        }
        return false
    }

    // Private method for adding user to database
    private fun writeNewUser(userId: String, question: String, response: String) {
        val user = User()
        user.question.add(question)
        user.response.add(response)
        db.child("users").child(userId).setValue(user)
        val editor = mPrefs.edit()
        editor.putString("UID", userId).apply()


    }


}