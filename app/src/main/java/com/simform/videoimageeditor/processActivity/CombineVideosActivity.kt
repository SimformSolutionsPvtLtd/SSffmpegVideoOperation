package com.simform.videoimageeditor.processActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.utils.Common
import com.simform.videoimageeditor.utils.Common.OUT_PUT_DIR
import com.simform.videoimageeditor.utils.Extension
import com.simform.videoimageeditor.utils.Paths
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CyclicBarrier
import kotlinx.android.synthetic.main.activity_combine_videos.btnCombine
import kotlinx.android.synthetic.main.activity_combine_videos.mProgressView
import kotlinx.android.synthetic.main.activity_combine_videos.tvInputPathImage
import kotlinx.android.synthetic.main.activity_combine_videos.tvOutputPath
import kotlinx.android.synthetic.main.activity_combine_videos.btnVideoPath
import kotlinx.android.synthetic.main.activity_merge_image_and_video.tvInputPathVideo

class CombineVideosActivity : BaseActivity(R.layout.activity_combine_videos) {
    private var isVideoSelected: Boolean = false

    override fun initialization() {
        btnVideoPath.setOnClickListener(this)
        btnCombine.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnVideoPath -> {
                Common.selectFile(this, maxSelection = 5, isImageSelection = false)
            }
            R.id.btnCombine -> {
                when {
                    !isVideoSelected -> {
                        Toast.makeText(
                            this,
                            getString(R.string.input_video_validate_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        processStart()
                        val gate = CyclicBarrier(2)
                        val imageToVideo = object : Thread() {
                            override fun run() {
                                gate.await()
                                combineVideosProcess()
                            }
                        }
                        imageToVideo.start()
                        gate.await()
                    }
                }
            }
        }
    }

    @SuppressLint("NewApi")
    override fun selectedFiles(mediaFiles: List<MediaFile>?, requestCode: Int) {
        when (requestCode) {
            Common.VIDEO_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    val size: Int = mediaFiles.size
                    tvInputPathImage.text = "$size"+ (if (size == 1) " Video " else " Videos ") + "selected"
                    isVideoSelected = true
                    CompletableFuture.runAsync {
                        retriever = MediaMetadataRetriever()
                        retriever?.setDataSource(tvInputPathVideo.text.toString())
                        val bit = retriever?.frameAtTime
                        if (bit != null) {
                            width = bit.width
                            height = bit.height
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.video_not_selected_toast_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun processStop() {
        runOnUiThread {
            btnVideoPath.isEnabled = true
            btnCombine.isEnabled = true
            mProgressView.visibility = View.GONE
        }
    }

    private fun processStart() {
        runOnUiThread {
            btnVideoPath.isEnabled = false
            btnCombine.isEnabled = false
            mProgressView.visibility = View.VISIBLE
        }
    }

    private fun combineVideosProcess() {
        val dir = File(getExternalFilesDir(OUT_PUT_DIR).toString())
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val dest = File(dir.path + File.separator + OUT_PUT_DIR + System.currentTimeMillis().div(1000L) + ".mp4")
        val outputPath = dest.absolutePath
        val pathsList = ArrayList<Paths>()
        mediaFiles?.let {
            for (element in it){
                val paths = Paths()
                paths.filePath = element.path
                paths.isImageFile = false
                pathsList.add(paths)
            }

            val combineQuery = Extension.combineVideos(
                pathsList,
                width,
                height,
                outputPath
            )
            Config.enableLogCallback { log ->
                tvOutputPath.text = log?.text
            }
            when (FFmpeg.execute(combineQuery)) {
                Config.RETURN_CODE_SUCCESS -> {
                    runOnUiThread {
                        tvOutputPath.text = "Output Path : \n$outputPath"
                        processStop()
                    }
                }
                Config.RETURN_CODE_CANCEL -> {
                    processStop()
                    FFmpeg.cancel()
                }
                else -> {
                    processStop()
                    Config.printLastCommandOutput(Log.INFO)
                }
            }
        }

    }
}