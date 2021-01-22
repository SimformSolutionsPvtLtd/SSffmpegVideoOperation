package com.simform.videooperations

import com.simform.videooperations.Statistics
import com.simform.videooperations.LogMessage

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