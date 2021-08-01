package com.example.magic8ball


import android.content.ContentValues.TAG
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.magic8ball.databinding.ActivityScrollingBinding


import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class ScrollingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScrollingBinding
    private lateinit var userReference: DatabaseReference
    private lateinit var displayHistory: TextView
    private lateinit var mPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = "Question history"

        // Variable declaration
        displayHistory = findViewById(R.id.questionHistory)
        mPrefs = getSharedPreferences("Prefs", MODE_PRIVATE)
        val uid = mPrefs.getString("UID", "")
        userReference = uid?.let { Firebase.database.reference.child("users").child(it) }!!

        // Enable the back button
        val actionBar = actionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val userListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue<User>()
                if (user != null) {
                    displayHistory.append(getString(R.string.questionHistory, user.question, user.response))
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to get user", error.toException())
            }
        }

        userReference.addValueEventListener(userListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        return true
    }

}