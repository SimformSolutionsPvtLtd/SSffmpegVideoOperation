package com.simform.videoimageeditor.processActivity

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.arthenica.mobileffmpeg.LogMessage
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.utils.Common
import com.simform.videoimageeditor.utils.Common.VIDEO
import com.simform.videoimageeditor.utils.Common.getFilePath
import com.simform.videoimageeditor.utils.Common.selectFile
import com.simform.videoimageeditor.utils.FFmpegQueryExtension.addVideoWaterMark
import com.simform.videoimageeditor.utils.FFmpegCallBack
import kotlinx.android.synthetic.main.activity_add_water_mark_on_video.*
import java.util.concurrent.CompletableFuture.runAsync
import java.util.concurrent.CyclicBarrier

class AddWaterMarkOnVideoActivity : BaseActivity(R.layout.activity_add_water_mark_on_video) {
    private var isInputVideoSelected = false
    private var isWaterMarkImageSelected = false
    override fun initialization() {
        btnVideoPath.setOnClickListener(this)
        btnImagePath.setOnClickListener(this)
        btnAdd.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnVideoPath -> {
                selectFile(this, maxSelection = 1, isImageSelection = false, isAudioSelection = false)
            }
            R.id.btnImagePath -> {
                selectFile(this, maxSelection = 1, isImageSelection = true, isAudioSelection = false)
            }
            R.id.btnAdd -> {
                when {
                    !isInputVideoSelected -> {
                        Toast.makeText(this, getString(R.string.input_video_validate_message), Toast.LENGTH_SHORT).show()
                    }
                    !isWaterMarkImageSelected -> {
                        Toast.makeText(this, getString(R.string.input_image_validate_message), Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(edtXPos.text.toString()) -> {
                        Toast.makeText(this, getString(R.string.x_position_validation), Toast.LENGTH_SHORT).show()
                    }
                    edtXPos.text.toString().toFloat() > 100 || edtXPos.text.toString().toFloat() <= 0 -> {
                        Toast.makeText(this, getString(R.string.x_validation_invalid), Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(edtYPos.text.toString()) -> {
                        Toast.makeText(this, getString(R.string.y_position_validation), Toast.LENGTH_SHORT).show()
                    }
                    edtYPos.text.toString().toFloat() > 100 || edtYPos.text.toString().toFloat() <= 0 -> {
                        Toast.makeText(this, getString(R.string.y_validation_invalid), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        processStart()
                        val gate = CyclicBarrier(2)
                        val imageToVideo = object : Thread() {
                            override fun run() {
                                gate.await()
                                addWaterMarkProcess()
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
    override fun selectedFiles(mediaFiles: List<MediaFile>?, fileRequestCode: Int) {
        when (fileRequestCode) {
            Common.VIDEO_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    tvInputPathVideo.text = mediaFiles[0].path
                    isInputVideoSelected = true
                    runAsync {
                        retriever = MediaMetadataRetriever()
                        retriever?.setDataSource(tvInputPathVideo.text.toString())
                        val bit = retriever?.frameAtTime
                        width = bit?.width
                        height = bit?.height
                    }
                } else {
                    Toast.makeText(this, getString(R.string.video_not_selected_toast_message), Toast.LENGTH_SHORT).show()
                }
            }
            Common.IMAGE_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    tvInputPathImage.text = mediaFiles[0].path
                    isWaterMarkImageSelected = true
                } else {
                    Toast.makeText(this, getString(R.string.image_not_selected_toast_message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addWaterMarkProcess() {
        val outputPath = getFilePath(this, VIDEO)
        val xPos = width?.let {
            (edtXPos.text.toString().toFloat().times(it)).div(100)
        }
        val yPos = height?.let {
            (edtYPos.text.toString().toFloat().times(it)).div(100)
        }
        val query = addVideoWaterMark(tvInputPathVideo.text.toString(), tvInputPathImage.text.toString(), xPos, yPos, outputPath)
        Common.callQuery(this, query, object : FFmpegCallBack {
            override fun process(logMessage: LogMessage) {
                tvOutputPath.text = logMessage.text
            }

            override fun success() {
                tvOutputPath.text = "Output Path : \n$outputPath"
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
            btnImagePath.isEnabled = true
            btnAdd.isEnabled = true
            mProgressView.visibility = View.GONE
        }
    }

    private fun processStart() {
        btnVideoPath.isEnabled = false
        btnImagePath.isEnabled = false
        btnAdd.isEnabled = false
        mProgressView.visibility = View.VISIBLE
    }
}