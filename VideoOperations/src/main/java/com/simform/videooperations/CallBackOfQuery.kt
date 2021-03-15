package com.simform.videooperations

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import java.util.concurrent.CyclicBarrier

/**
 * Created by Ashvin Vavaliya on 22,January,2021
 * Simform Solutions Pvt Ltd.
 */
object CallBackOfQuery {
    fun callQuery(context: AppCompatActivity, query: Array<String>, fFmpegCallBack: FFmpegCallBack) {
        val gate = CyclicBarrier(2)
        object : Thread() {
            override fun run() {
                gate.await()
                process(context, query, fFmpegCallBack)
            }
        }.start()
        gate.await()
    }

    private fun process(context: AppCompatActivity, query: Array<String>, ffmpegCallBack: FFmpegCallBack) {
        Config.enableLogCallback { logMessage ->
            val logs = LogMessage(logMessage.executionId, logMessage.level, logMessage.text)
            ffmpegCallBack.process(logs)
        }
        Config.enableStatisticsCallback { statistics ->
            val statisticsLog =
                Statistics(statistics.executionId, statistics.videoFrameNumber, statistics.videoFps, statistics.videoQuality, statistics.size, statistics.time, statistics.bitrate, statistics.speed)
            ffmpegCallBack.statisticsProcess(statisticsLog)
        }
        when (FFmpeg.execute(query)) {
            Config.RETURN_CODE_SUCCESS -> {
                context.runOnUiThread {
                    ffmpegCallBack.success()
                }
            }
            Config.RETURN_CODE_CANCEL -> {
                ffmpegCallBack.cancel()
                FFmpeg.cancel()
            }
            else -> {
                ffmpegCallBack.failed()
                Config.printLastCommandOutput(Log.INFO)
            }
        }
    }
}