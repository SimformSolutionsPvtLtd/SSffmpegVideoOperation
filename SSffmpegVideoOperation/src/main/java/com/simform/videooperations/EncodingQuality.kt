package com.simform.videooperations


enum class EncodingQuality {
    FASTEST,     // Lowest quality, fastest processing
    BALANCED,    // Good compromise
    HIGHEST;     // Best quality, slowest processing

    companion object {
        fun getQualityString(quality: EncodingQuality): String {
            return when (quality) {
                FASTEST -> "ultrafast"
                BALANCED -> "medium"
                HIGHEST -> "slow"
            }
        }
    }
}