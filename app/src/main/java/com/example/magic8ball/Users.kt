package com.example.magic8ball

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var question: MutableList<String> = ArrayList(),
    var response: MutableList<String> = ArrayList()
)