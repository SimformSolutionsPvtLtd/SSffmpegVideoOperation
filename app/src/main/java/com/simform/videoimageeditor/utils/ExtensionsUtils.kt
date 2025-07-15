package com.simform.videoimageeditor.utils

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun AppCompatActivity.enableEdgeToEdge(view: View?) {
    view?.let {
        ViewCompat.setOnApplyWindowInsetsListener(it) { view, windowInsets ->
            val systemBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBarInsets.left,
                systemBarInsets.top,
                systemBarInsets.right,
                0
            )
            windowInsets
        }
    }
}