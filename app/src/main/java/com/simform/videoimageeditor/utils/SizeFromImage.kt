package com.simform.videoimageeditor.utils

import android.graphics.BitmapFactory

//Not thread safe
class SizeFromImage(private val path: String) : ISize {
    private var width: Int
    private var height: Int
    override fun width(): Int {
        if (width == -1) {
            init()
        }
        return width
    }

    override fun height(): Int {
        if (height == -1) {
            init()
        }
        return height
    }

    private fun init() {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        width = options.outWidth
        height = options.outHeight
    }

    init {
        width = -1
        height = -1
    }
}