package com.simform.videooperations

import android.graphics.BitmapFactory

/**
 * Created by Ashvin Vavaliya on 29,December,2020
 * Simform Solutions Pvt Ltd.
 */
class SizeOfImage(private val path: String) : ISize {
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