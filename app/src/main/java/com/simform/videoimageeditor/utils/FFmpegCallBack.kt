package com.simform.videoimageeditor.utils

import com.arthenica.mobileffmpeg.LogMessage
import com.arthenica.mobileffmpeg.Statistics

/**
 * Created by Ashvin Vavaliya on 01,January,2021
 * Simform Solutions Pvt Ltd.
 */
interface FFmpegCallBack {
    fun process(logMessage: LogMessage){}
    fun statisticsProcess(statistics: Statistics) {}
    fun success(){}
    fun cancel(){}
    fun failed(){}
}