package com.simform.videoimageeditor.utility

import android.Manifest
import android.content.Context
import android.content.Intent
import android.media.MediaExtractor
import android.media.MediaFormat
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.utility.FFmpegQueryExtension.FRAME_RATE
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.DecimalFormat
import java.util.*

/**
 * Created by Ashvin Vavaliya on 24,November,2020
 * Simform Solutions Pvt Ltd.
 */
object Common {
    const val PERM = 111
    const val VIDEO_FILE_REQUEST_CODE = 112
    const val IMAGE_FILE_REQUEST_CODE = 113
    const val AUDIO_FILE_REQUEST_CODE = 114
    const val TIME_FORMAT = "HH:mm:ss"
    const val OUT_PUT_DIR: String = "Output"
    private val format: DecimalFormat = DecimalFormat("#.##")
    private const val MB = (1024 * 1024).toLong()
    private const val KB: Long = 1024

    //Output Files
    const val IMAGE: String = "IMAGE"
    const val VIDEO: String = "VIDEO"
    const val GIF: String = "GIF"
    const val MP3: String = "MP3"

   // Standard Ratio
    const val RATIO_1: String = "16:9"
    const val RATIO_2: String = "4:3"
    const val RATIO_3: String = "16:10"
    const val RATIO_4: String = "5:4"
    const val RATIO_5: String = "2:21:1"
    const val RATIO_6: String = "2:35:1"
    const val RATIO_7: String = "2:39:1"

    fun callQuery(context: AppCompatActivity, query: Array<String>, fFmpegCallBack: FFmpegCallBack) {
        Config.enableLogCallback { logMessage ->
            fFmpegCallBack.process(logMessage)
        }
        Config.enableStatisticsCallback { statistics ->
            fFmpegCallBack.statisticsProcess(statistics)
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

    fun addSupportActionBar(context: AppCompatActivity,title: Int) {
        if (context.supportActionBar != null) {
            context.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            context.supportActionBar?.setDisplayShowHomeEnabled(true)
            context.supportActionBar?.title = context.getString(title)
        }
    }

    fun openActivity(context: Context, activity: AppCompatActivity) {
        context.startActivity(Intent(context, activity::class.java))
    }

    fun stringForTime(timeMs: Long?): String {
        val mFormatBuilder = StringBuilder()
        val mFormatter = Formatter(mFormatBuilder, Locale.getDefault())
        val totalSeconds = timeMs?.div(1000)
        val seconds = (totalSeconds?.rem(60))?.toInt()
        val minutes = ((totalSeconds?.div(60))?.rem(60))?.toInt()
        val hours = (totalSeconds?.div(3600))?.toInt()
        mFormatBuilder.setLength(0)
        return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString()
    }

    fun getFrameRate(fileString: String) {
        val extractor = MediaExtractor()
        val file = File(fileString)
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(file)
            val fd = fis.fd
            extractor.setDataSource(fd)
            val numTracks = extractor.trackCount
            for (i in 0 until numTracks) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)
                if (mime.startsWith("video/")) {
                    if (format.containsKey(MediaFormat.KEY_FRAME_RATE)) {
                        FRAME_RATE = format.getInteger(MediaFormat.KEY_FRAME_RATE)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            extractor.release()
            try {
                fis?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getFileSize(file: File): String {
        require(file.isFile) { "Expected a file" }
        val length = file.length().toDouble()
        if (length > MB) {
            return format.format(length / MB).toString() + " MB"
        }
        return if (length > KB) {
            format.format(length / KB).toString() + " KB"
        } else {
            format.format(length).toString() + " GB"
        }
    }

    fun selectFile(activity: AppCompatActivity, maxSelection: Int, isImageSelection: Boolean, isAudioSelection: Boolean) {
        val intent = Intent(activity, FilePickerActivity::class.java)
        when {
            isImageSelection -> {
                intent.putExtra(
                    FilePickerActivity.CONFIGS, Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowImages(true)
                        .setShowVideos(false)
                        .enableImageCapture(true)
                        .enableVideoCapture(false)
                        .setMaxSelection(maxSelection)
                        .setSkipZeroSizeFiles(true)
                        .build()
                )
                activity.startActivityForResult(intent, IMAGE_FILE_REQUEST_CODE)
            }
            isAudioSelection -> {
                intent.putExtra(
                    FilePickerActivity.CONFIGS, Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowImages(false)
                        .setShowVideos(false)
                        .setShowAudios(true)
                        .enableImageCapture(false)
                        .enableVideoCapture(false)
                        .setMaxSelection(maxSelection)
                        .setSkipZeroSizeFiles(true)
                        .build()
                )
                activity.startActivityForResult(intent, AUDIO_FILE_REQUEST_CODE)
            }
            else -> {
                intent.putExtra(
                    FilePickerActivity.CONFIGS, Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowImages(false)
                        .setShowVideos(true)
                        .enableImageCapture(false)
                        .enableVideoCapture(true)
                        .setMaxSelection(maxSelection)
                        .setSkipZeroSizeFiles(true)
                        .build()
                )
                activity.startActivityForResult(intent, VIDEO_FILE_REQUEST_CODE)
            }
        }
    }

    fun getFilePath(context: Context, fileExtension: String) : String {
        val dir = File(context.getExternalFilesDir(Common.OUT_PUT_DIR).toString())
        if (!dir.exists()) {
            dir.mkdirs()
        }
        var extension:String? = null
        when {
            TextUtils.equals(fileExtension, IMAGE) -> {
                extension = "%03d.jpg"
            }
            TextUtils.equals(fileExtension, VIDEO) -> {
                extension = ".mp4"
            }
            TextUtils.equals(fileExtension, GIF) -> {
                extension = ".gif"
            }
            TextUtils.equals(fileExtension, MP3) -> {
                extension = ".mp3"
            }
        }
        val dest = File(dir.path + File.separator + Common.OUT_PUT_DIR + System.currentTimeMillis().div(1000L) + extension)
        return dest.absolutePath
    }

    @Throws(IOException::class)
    fun getFileFromAssets(context: Context, fileName: String): File =
        File(context.cacheDir, fileName).also {
            if (!it.exists()) {
                it.outputStream().use { cache ->
                    context.assets.open(fileName).use { inputStream ->
                        inputStream.copyTo(cache)
                    }
                }
            }
        }
}