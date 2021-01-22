package com.simform.videooperations

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg

/**
 * Created by Ashvin Vavaliya on 22,January,2021
 * Simform Solutions Pvt Ltd.
 */
object CallBackOfQuery {
    fun callQuery(context: AppCompatActivity, query: Array<String>, fFmpegCallBack: FFmpegCallBack) {
        Config.enableLogCallback { logMessage ->
            val logs = LogMessage(logMessage.executionId, logMessage.level, logMessage.text);
            fFmpegCallBack.process(logs)
        }
        Config.enableStatisticsCallback { statistics ->
            val statisticsLog = Statistics(statistics.executionId, statistics.videoFrameNumber, statistics.videoFps, statistics.videoQuality, statistics.size, statistics.time, statistics.bitrate, statistics.speed)
            fFmpegCallBack.statisticsProcess(statisticsLog)
        }
        when (FFmpeg.execute(query)) {
            Config.RETURN_CODE_SUCCESS -> {
                context.runOnUiThread {
                    fFmpegCallBack.success()
                }
            }
            Config.RETURN_CODE_CANCEL -> {
                fFmpegCallBack.cancel()
                FFmpeg.cancel()
            }
            else -> {
                fFmpegCallBack.failed()
                Config.printLastCommandOutput(Log.INFO)
            }
        }
    }
}