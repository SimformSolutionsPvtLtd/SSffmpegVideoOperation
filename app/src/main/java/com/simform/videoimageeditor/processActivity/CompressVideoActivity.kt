package com.simform.videoimageeditor.processActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaMetadataRetriever
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
import com.simform.videoimageeditor.utils.Extension
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CyclicBarrier
import kotlinx.android.synthetic.main.activity_compress_video.btnCompress
import kotlinx.android.synthetic.main.activity_compress_video.btnVideoPath
import kotlinx.android.synthetic.main.activity_compress_video.inputFileSize
import kotlinx.android.synthetic.main.activity_compress_video.mProgressView
import kotlinx.android.synthetic.main.activity_compress_video.tvInputPathVideo
import kotlinx.android.synthetic.main.activity_compress_video.tvOutputPath

class CompressVideoActivity : BaseActivity(R.layout.activity_compress_video) {
    private var isInputVideoSelected: Boolean = false

    override fun initialization() {
        btnVideoPath.setOnClickListener(this)
        btnCompress.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnVideoPath -> {
                Common.selectFile(this, maxSelection = 1, isImageSelection = false)
            }
            R.id.btnCompress -> {
                when {
                    !isInputVideoSelected -> {
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
                                compressProcess()
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
                if (mediaFiles != null && mediaFiles?.size != 0) {
                    tvInputPathVideo.text = mediaFiles?.get(0)?.path
                    isInputVideoSelected = true
                    CompletableFuture.runAsync {
                        retriever = MediaMetadataRetriever()
                        retriever?.setDataSource(tvInputPathVideo.text.toString())
                        val bit = retriever?.frameAtTime
                        width = bit?.width
                        height = bit?.height
                    }
                    inputFileSize.text = "Input file Size : ${Common.getFileSize(File(tvInputPathVideo.text.toString()))}"
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

    private fun compressProcess() {
        val dir = File(getExternalFilesDir(Common.OUT_PUT_DIR).toString())
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val dest = File(dir.path + File.separator + Common.OUT_PUT_DIR + System.currentTimeMillis().div(1000L) + ".mp4")
        val outputPath = dest.absolutePath

        val query = Extension.compressor(tvInputPathVideo.text.toString(), width, height, outputPath)

        Config.enableLogCallback { log ->
            tvOutputPath.text = log?.text
        }
        when (FFmpeg.execute(query)) {
            Config.RETURN_CODE_SUCCESS -> {
                runOnUiThread {
                    tvOutputPath.text = "Output Path : \n$outputPath \n Output file Size : ${Common.getFileSize(File(outputPath))}"
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

    private fun processStop() {
        runOnUiThread {
            btnVideoPath.isEnabled = true
            btnCompress.isEnabled = true
            mProgressView.visibility = View.GONE
        }
    }

    private fun processStart() {
        runOnUiThread {
            btnVideoPath.isEnabled = false
            btnCompress.isEnabled = false
            mProgressView.visibility = View.VISIBLE
        }
    }
}