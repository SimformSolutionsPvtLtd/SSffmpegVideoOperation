package com.simform.videoimageeditor.utils

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by Ashvin Vavaliya on 22,January,2021
 * Simform Solutions Pvt Ltd.
 */
object utils {
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