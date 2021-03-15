package com.simform.videooperations

import com.jaiselrahman.filepicker.model.MediaFile

/**
 * Created by Ashvin Vavaliya on 31,December,2020
 * Simform Solutions Pvt Ltd.
 */
interface FileSelection {
    fun selectedFiles(mediaFiles:List<MediaFile>?,requestCode: Int){}
}