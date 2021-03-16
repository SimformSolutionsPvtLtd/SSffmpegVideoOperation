package com.simform.videooperations

import com.arthenica.mobileffmpeg.Level

class LogMessage(val executionId: Long, val level: Level, val text: String) {
    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("LogMessage{")
        stringBuilder.append("executionId=")
        stringBuilder.append(executionId)
        stringBuilder.append(", level=")
        stringBuilder.append(level)
        stringBuilder.append(", text=")
        stringBuilder.append("\'")
        stringBuilder.append(text)
        stringBuilder.append('\'')
        stringBuilder.append('}')
        return stringBuilder.toString()
    }
}