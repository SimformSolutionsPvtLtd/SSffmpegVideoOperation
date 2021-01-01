package com.simform.videoimageeditor.processActivity

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import android.view.View
import android.widget.Toast
import com.arthenica.mobileffmpeg.LogMessage
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.utils.Common
import com.simform.videoimageeditor.utils.FFmpegCallBack
import com.simform.videoimageeditor.utils.FFmpegQueryExtension
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
                        Toast.makeText(this, getString(R.string.input_video_validate_message), Toast.LENGTH_SHORT).show()
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
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    tvInputPathVideo.text = mediaFiles[0].path
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
                    Toast.makeText(this, getString(R.string.video_not_selected_toast_message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun compressProcess() {
        val outputPath = Common.getFilePath(this, Common.VIDEO)
        val query = FFmpegQueryExtension.compressor(tvInputPathVideo.text.toString(), width, height, outputPath)
        Common.callQuery(this, query, object : FFmpegCallBack {
            override fun process(logMessage: LogMessage) {
                tvOutputPath.text = logMessage.text
            }

            @SuppressLint("SetTextI18n")
            override fun success() {
                tvOutputPath.text = "Output Path : \n$outputPath \n Output file Size : ${Common.getFileSize(File(outputPath))}"
                processStop()
            }

            override fun cancel() {
                processStop()
            }

            override fun failed() {
                processStop()
            }
        })
    }

    private fun processStop() {
        runOnUiThread {
            btnVideoPath.isEnabled = true
            btnCompress.isEnabled = true
            mProgressView.visibility = View.GONE
        }
    }

    private fun processStart() {
        btnVideoPath.isEnabled = false
        btnCompress.isEnabled = false
        mProgressView.visibility = View.VISIBLE
    }
}