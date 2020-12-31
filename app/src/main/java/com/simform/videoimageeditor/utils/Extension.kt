package com.simform.videoimageeditor.utils

/**
 * Created by Ashvin Vavaliya on 10,December,2020
 * Simform Solutions Pvt Ltd.
 */
object Extension {
    fun cutVideo(
        inputVideoPath: String,
        startTime: String?,
        endTime: String?,
        output: String
    ): Array<String> {
        Common.getFrameRate(inputVideoPath)
        return arrayOf(
            "-i",
            inputVideoPath,
            "-ss",
            startTime.toString(), //hh:mm:ss
            "-to",
            endTime.toString(), //hh:mm:ss
            "-r",
            "${Common.FRAME_RATE}",
            "-preset",
            "ultrafast",
            output
        )
    }

    fun imageToVideo(
        input: String,
        output: String,
        second: Int,
        width: Int?,
        height: Int?
    ): Array<String> {
        val fadeEndDuration = second - 0.5
        Common.getFrameRate(input)
        val fade =
            "fps=${Common.FRAME_RATE},fade=type=in:duration=1,fade=type=out:duration=0.5:start_time=$fadeEndDuration"
        return arrayOf(
            "-loop",
            "1",
            "-i",
            input,
            "-s",
            "${width}x${height}",
            "-vf",
            "format=yuv420p,$fade",
            "-t",
            "$second",
            "-preset",
            "ultrafast",
            output
        )
    }

    fun addVideoWaterMark(
        inputVideo: String,
        imageInput: String,
        posX: Float?,
        posY: Float?,
        output: String
    ): Array<String> {
        return arrayOf(
            "-i",
            inputVideo,
            "-i",
            imageInput,
            "-filter_complex",
            "overlay=$posX:$posY",
            "-preset",
            "ultrafast",
            output
        )
    }

    fun combineImagesAndVideos(
        paths: ArrayList<Paths>,
        width: Int?,
        height: Int?,
        second: String,
        output: String
    ): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        for (i in 0 until paths.size) {
            //for input
            if (paths[i].isImageFile) {
                inputs.add("-loop")
                inputs.add("1")
                inputs.add("-framerate")
                inputs.add("${Common.FRAME_RATE}")
                inputs.add("-t")
                inputs.add(second)
                inputs.add("-i")
                inputs.add(paths[i].filePath)
            } else {
                inputs.add("-i")
                inputs.add(paths[i].filePath.toString())
            }
        }

        var query: String? = ""
        var queryAudio: String? = ""
        for (i in 0 until paths.size) {
            query = query?.trim()
            query += "[" + i + ":v]scale=${width}x${height},setdar=$width/$height[" + i + "v];"

            queryAudio = queryAudio?.trim()
            queryAudio += if (paths[i].isImageFile) {
                "[" + i + "v][" + paths.size + ":a]"
            } else {
                "[" + i + "v][" + i + ":a]"
            }
        }
        return getResult(inputs, query, queryAudio, paths, output)
    }

    fun combineVideos(
        paths: ArrayList<Paths>,
        width: Int?,
        height: Int?,
        output: String
    ): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        var query: String? = ""
        var queryAudio: String? = ""
        for (i in 0 until paths.size) {
            //for input
            inputs.add("-i")
            inputs.add(paths[i].filePath.toString())

            //for video setting with width and height
            query = query?.trim()
            query += "[" + i + ":v]scale=${width}x${height},setdar=$width/$height[" + i + "v];"

            //for video and audio combine {without audio this query not supported so applied this function}
            queryAudio = queryAudio?.trim()
            queryAudio += if (paths[i].isImageFile) {
                "[" + i + "v][" + paths.size + ":a]"
            } else {
                "[" + i + "v][" + i + ":a]"
            }
        }
        return getResult(inputs, query, queryAudio, paths, output)
    }

    private fun getResult(
        inputs: java.util.ArrayList<String>,
        query: String?,
        queryAudio: String?,
        paths: ArrayList<Paths>,
        output: String
    ): Array<String> {
        inputs.apply {
            add("-f")
            add("lavfi")
            add("-t")
            add("0.1")
            add("-i")
            add("anullsrc")
            add("-filter_complex")
            add(query + queryAudio + "concat=n=" + paths.size + ":v=1:a=1 [v][a]")
            add("-map")
            add("[v]")
            add("-map")
            add("[a]")
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    fun compressor(
        inputVideo: String,
        width: Int?,
        height: Int?,
        outputVideo: String
    ): Array<String> {
        Common.getFrameRate(inputVideo)
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-y")
            add("-i")
            add(inputVideo)
            add("-s")
            add("{${width}x${height}")
            add("-r")
            add("${if (Common.FRAME_RATE >= 10) Common.FRAME_RATE - 5 else Common.FRAME_RATE}")
            add("-vcodec")
            add("mpeg4")
            add("-b:v")
            add("150k")
            add("-b:a")
            add("48000")
            add("-ac")
            add("2")
            add("-ar")
            add("22050")
            add("-preset")
            add("ultrafast")
            add(outputVideo)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    fun extractImages(inputVideo: String, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-i")
            add(inputVideo)
            add("-vf")
            add("fps=4") // here It is 4 frames per second,
            // If you will get frame per second then replace fps=1 and
            // if you will get frames per 30 seconds then replace it into fps = 1/30
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    fun extractAudio(inputVideo: String, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-y")
            add("-i")
            add(inputVideo)
            add("-vn")
            add("-ar")
            add("44100")
            add("-ac")
            add("2")
            add("-b:a")
            add("256k")
            add("-f")
            add("mp3")
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    fun videoMotion(inputVideo: String, isFast: Boolean) {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            //"-y", "-i", inputFileAbsolutePath, "-filter_complex", "[0:v]setpts=0.5*PTS[v];[0:a]atempo=2.0[a]", "-map", "[v]", "-map", "[a]", "-b:v", "2097k", "-r", "60", "-vcodec", "mpeg4", outputFileAbsolutePath
        }

    }
}

