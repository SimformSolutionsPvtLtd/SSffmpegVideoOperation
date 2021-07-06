package com.simform.videoimageeditor

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by Ashvin Vavaliya on 06,July,2021
 * Simform Solutions Pvt Ltd.
 */
class Utils {
    fun addSupportActionBar(context: AppCompatActivity, title: Int) {
        if (context.supportActionBar != null) {
            context.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            context.supportActionBar?.setDisplayShowHomeEnabled(true)
            context.supportActionBar?.title = context.getString(title)
        }
    }

    fun openActivity(context: Context, activity: AppCompatActivity) {
        context.startActivity(Intent(context, activity::class.java))
    }
}