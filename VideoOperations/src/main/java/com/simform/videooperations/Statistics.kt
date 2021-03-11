package com.simform.videooperations

/**
 *
 * Statistics for running executions.
 *
 * @author Taner Sener
 * @since v2.1
 */
class Statistics {
    var executionId: Long
    var videoFrameNumber: Int
    var videoFps: Float
    var videoQuality: Float
    var size: Long
    var time: Int
    var bitrate: Double
    var speed: Double

    constructor() {
        executionId = 0
        videoFrameNumber = 0
        videoFps = 0f
        videoQuality = 0f
        size = 0
        time = 0
        bitrate = 0.0
        speed = 0.0
    }

    constructor(executionId: Long, videoFrameNumber: Int, videoFps: Float, videoQuality: Float, size: Long, time: Int, bitrate: Double, speed: Double) {
        this.executionId = executionId
        this.videoFrameNumber = videoFrameNumber
        this.videoFps = videoFps
        this.videoQuality = videoQuality
        this.size = size
        this.time = time
        this.bitrate = bitrate
        this.speed = speed
    }

    fun update(newStatistics: Statistics?) {
        if (newStatistics != null) {
            executionId = newStatistics.executionId
            if (newStatistics.videoFrameNumber > 0) {
                videoFrameNumber = newStatistics.videoFrameNumber
            }
            if (newStatistics.videoFps > 0) {
                videoFps = newStatistics.videoFps
            }
            if (newStatistics.videoQuality > 0) {
                videoQuality = newStatistics.videoQuality
            }
            if (newStatistics.size > 0) {
                size = newStatistics.size
            }
            if (newStatistics.time > 0) {
                time = newStatistics.time
            }
            if (newStatistics.bitrate > 0) {
                bitrate = newStatistics.bitrate
            }
            if (newStatistics.speed > 0) {
                speed = newStatistics.speed
            }
        }
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("Statistics{")
        stringBuilder.append("executionId=")
        stringBuilder.append(executionId)
        stringBuilder.append(", videoFrameNumber=")
        stringBuilder.append(videoFrameNumber)
        stringBuilder.append(", videoFps=")
        stringBuilder.append(videoFps)
        stringBuilder.append(", videoQuality=")
        stringBuilder.append(videoQuality)
        stringBuilder.append(", size=")
        stringBuilder.append(size)
        stringBuilder.append(", time=")
        stringBuilder.append(time)
        stringBuilder.append(", bitrate=")
        stringBuilder.append(bitrate)
        stringBuilder.append(", speed=")
        stringBuilder.append(speed)
        stringBuilder.append('}')
        return stringBuilder.toString()
    }
}