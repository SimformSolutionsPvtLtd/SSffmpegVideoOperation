package com.simform.videooperations

/**
 * Created by Ashvin Vavaliya on 10,December,2020
 * Simform Solutions Pvt Ltd.
 */
public class FFmpegQueryExtension {
    public var FRAME_RATE: Int = 25 // Default value

    /**
     * startTime = 00:00:00 HH:MM:SS
     * endTime = 00:00:00 HH:MM:SS
     */
    fun cutVideo(inputVideoPath: String, startTime: String?, endTime: String?, output: String): Array<String> { Common.getFrameRate(inputVideoPath)
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-i")
            add(inputVideoPath)
            add("-ss")
            add(startTime.toString())
            add("-to")
            add(endTime.toString())
            add("-r")
            add("$FRAME_RATE")
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    /**
     * second = how many long create the video in second
     * input = Local image path
     * width = video width
     * height = video height
     */
    fun imageToVideo(input: String, output: String, second: Int, width: Int?, height: Int?): Array<String> {
        val fadeEndDuration = second - 0.5
        Common.getFrameRate(input)
        val fade = "fps=$FRAME_RATE,fade=type=in:duration=1,fade=type=out:duration=0.5:start_time=$fadeEndDuration"
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-loop")
            add("1")
            add("-i")
            add(input)
            add("-s")
            add("${width}x${height}")
            add("-vf")
            add("format=yuv420p,$fade")
            add("-t")
            add("$second")
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    /**
     * posX = X position of water-mark in percentage (1% to 100%)
     * posY = Y position of water-mark in percentage (1% to 100%)
     */
    fun addVideoWaterMark(inputVideo: String, imageInput: String, posX: Float?, posY: Float?, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-i")
            add(inputVideo)
            add("-i")
            add(imageInput)
            add("-filter_complex")
            add("overlay=$posX:$posY")
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    fun addTextOnVideo(inputVideo: String, textInput: String, posX: Float?, posY: Float?, fontPath: String, isTextBackgroundDisplay: Boolean, fontSize: Int, fontcolor: String, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        var borderQuery = ""
        if (isTextBackgroundDisplay) {
            borderQuery = ":box=1:boxcolor=black@0.5:boxborderw=5"
        }
        inputs.apply {
            add("-i")
            add(inputVideo)
            add("-vf")
            add("drawtext=text='$textInput':fontfile=$fontPath:x=$posX:y=$posY:fontsize=$fontSize:fontcolor=$fontcolor${borderQuery.trim()}")
            add("-c:a")
            add("copy")
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    fun combineImagesAndVideos(paths: ArrayList<Paths>, width: Int?, height: Int?, second: String, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        for (i in 0 until paths.size) {
            //for input
            if (paths[i].isImageFile) {
                inputs.add("-loop")
                inputs.add("1")
                inputs.add("-framerate")
                inputs.add("$FRAME_RATE")
                inputs.add("-t")
                inputs.add(second)
                inputs.add("-i")
                inputs.add(paths[i].filePath)
            } else {
                inputs.add("-i")
                inputs.add(paths[i].filePath)
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

    fun combineVideos(paths: ArrayList<Paths>, width: Int?, height: Int?, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        var query: String? = ""
        var queryAudio: String? = ""
        for (i in 0 until paths.size) {
            //for input
            inputs.add("-i")
            inputs.add(paths[i].filePath)

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

    private fun getResult(inputs: java.util.ArrayList<String>, query: String?, queryAudio: String?, paths: ArrayList<Paths>, output: String): Array<String> {
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

    fun compressor(inputVideo: String, width: Int?, height: Int?, outputVideo: String): Array<String> {
        Common.getFrameRate(inputVideo)
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-y")
            add("-i")
            add(inputVideo)
            add("-s")
            add("${width}x${height}")
            add("-r")
            add("${if (FRAME_RATE >= 10) FRAME_RATE - 5 else FRAME_RATE}")
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

    fun extractImages(inputVideo: String, output: String, spaceOfFrame: Float): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-i")
            add(inputVideo)
            add("-vf")
            add("fps=$spaceOfFrame") // here It is 4 frames per second,
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

    fun videoMotion(inputVideo: String, output: String, setpts: Double, atempo: Double): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-y")
            add("-i")
            add(inputVideo)
            add("-filter_complex")
            add("[0:v]setpts=${setpts}*PTS[v];[0:a]atempo=${atempo}[a]")
            add("-map")
            add("[v]")
            add("-map")
            add("[a]")
            add("-b:v")
            add("2097k")
            add("-r")
            add("60")
            add("-vcodec")
            add("mpeg4")
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    fun videoReverse(inputVideo: String, isWithAudioReverse: Boolean, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-i")
            add(inputVideo)
            if (isWithAudioReverse) {
                add("-vf")
                add("reverse")
                add("-af")
                add("areverse")
            } else {
                add("-an")
                add("-vf")
                add("reverse")
            }
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    fun videoFadeInFadeOut(inputVideo: String, duration: Long, fadeInEndSeconds: Int, fadeOutStartSeconds: Int, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-y")
            add("-i")
            add(inputVideo)
            add("-acodec")
            add("copy")
            add("-vf")
            add("fade=t=in:st=0:d=$fadeInEndSeconds,fade=t=out:st=${duration - fadeOutStartSeconds}:d=$fadeOutStartSeconds")
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    fun convertVideoToGIF(inputVideo: String, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-i")
            add(inputVideo)
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    fun rotateVideo(inputVideo: String, degree: Int, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-i")
            add(inputVideo)
            add("-map_metadata")
            add("0")
            add("-metadata:s:v")
            add("rotate=$degree")
            add("-codec")
            add("copy")
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    /**
     * degree = 0: 90 Counter Clockwise and Vertical Flip  (default)
     * degree = 1: 90 Clockwise
     * degree = 2: 90 Counter Clockwise
     * degree = 3: 90 Clockwise and Vertical Flip
    */
    fun flipVideo(inputVideo: String, degree: Int, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-i")
            add(inputVideo)
            add("-vf")
            add("transpose=$degree")
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    fun mergeAudioVideo(inputVideo: String, inputAudio: String, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-i")
            add(inputVideo)
            add("-i")
            add(inputAudio)
            add("-filter_complex")
            add("[1:a]volume=0.15,apad[A];[0:a][A]amerge[out]")
            add("-c:v")
            add("copy")
            add("-map")
            add("0:v")
            add("-map")
            add("[out]")
            add("-y")
            add("-shortest")
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    fun removeAudioFromVideo(inputVideo: String, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-i")
            add(inputVideo)
            add("-an")
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    fun mergeImageAndAudio(inputImage: String, inputAudio: String, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-y")
            add("-loop")
            add("1")
            add("-i")
            add(inputImage)
            add("-i")
            add(inputAudio)
            add("-shortest")
            add("-c:a")
            add("copy")
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    fun applyRatio(inputVideo: String, ratio: String, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-i")
            add(inputVideo)
            add("-aspect")
            add(ratio)
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    fun mergeGIF(gifInput: ArrayList<Paths>, posX: Float?, posY: Float?, width: Float?, height: Float?, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-y")
            add("-ignore_loop")
            add("0")
            for (i in 0 until gifInput.size) {
                add("-i")
                add(gifInput[i].filePath)
            }
            add("-filter_complex")
            add("[1]scale=$width:$height[s1];[0][s1]overlay=$posX:$posY:shortest=1")
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    fun mergeAudios(inputAudioList: ArrayList<Paths>, duration: String, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            for (i in 0 until inputAudioList.size) {
                add("-i")
                add(inputAudioList[i].filePath)
            }
            add("-filter_complex")
            add("amix=inputs=${inputAudioList.size}:duration=$duration:dropout_transition=${inputAudioList.size}")
            add("-codec:a")
            add("libmp3lame")
            add("-q:a")
            add("0")
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    fun audioVolumeUpdate(inputFile: String, volume: Float, output: String): Array<String>
    {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-i")
            add(inputFile)
            add("-af")
            add("volume=$volume")
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    fun audioMotion(inputVideo: String, output: String, atempo: Double): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-i")
            add(inputVideo)
            add("-filter:a")
            add("atempo=$atempo")
            add("-vn")
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    fun cutAudio(inputVideoPath: String, startTime: String?, endTime: String?, output: String): Array<String> {
        Common.getFrameRate(inputVideoPath)
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-i")
            add(inputVideoPath)
            add("-ss")
            add(startTime.toString())
            add("-to")
            add(endTime.toString())
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    fun compressAudio(inputAudioPath: String, bitrate: String, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-i")
            add(inputAudioPath)
            add("-ab")
            add(bitrate)
            add("-preset")
            add("ultrafast")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }
}

