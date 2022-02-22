package com.simform.videoimageeditor.videoProcessActivity

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.Common
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.FFmpegQueryExtension
import com.simform.videooperations.LogMessage
import kotlinx.android.synthetic.main.activity_video_rotate_flip.btn90Clockwise
import kotlinx.android.synthetic.main.activity_video_rotate_flip.btn90ClockwiseVerticalFlip
import kotlinx.android.synthetic.main.activity_video_rotate_flip.btn90CounterClockwise
import kotlinx.android.synthetic.main.activity_video_rotate_flip.btn90CounterClockwiseVerticalFlip
import kotlinx.android.synthetic.main.activity_video_rotate_flip.btnRotate180
import kotlinx.android.synthetic.main.activity_video_rotate_flip.btnRotate270
import kotlinx.android.synthetic.main.activity_video_rotate_flip.btnRotate90
import kotlinx.android.synthetic.main.activity_video_rotate_flip.btnVideoPath
import kotlinx.android.synthetic.main.activity_video_rotate_flip.mProgressView
import kotlinx.android.synthetic.main.activity_video_rotate_flip.tvInputPathVideo
import kotlinx.android.synthetic.main.activity_video_rotate_flip.tvOutputPath

class VideoRotateFlipActivity : BaseActivity(R.layout.activity_video_rotate_flip, R.string.video_rotate) {
    private var isInputVideoSelected: Boolean = false
    override fun initialization() {
        btnVideoPath.setOnClickListener(this)
        btnRotate90.setOnClickListener(this)
        btnRotate180.setOnClickListener(this)
        btnRotate270.setOnClickListener(this)
        btn90CounterClockwiseVerticalFlip.setOnClickListener(this)
        btn90Clockwise.setOnClickListener(this)
        btn90CounterClockwise.setOnClickListener(this)
        btn90ClockwiseVerticalFlip.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnVideoPath -> {
                Common.selectFile(this, maxSelection = 1, isImageSelection = false, isAudioSelection = false)
            }
            R.id.btnRotate90 -> {
                rotateDegree(90, true)
            }
            R.id.btnRotate180 -> {
                rotateDegree(180, true)
            }
            R.id.btnRotate270 -> {
                rotateDegree(270, true)
            }
            R.id.btn90CounterClockwiseVerticalFlip -> {
                rotateDegree(0, false)
            }
            R.id.btn90Clockwise -> {
                rotateDegree(1, false)
            }
            R.id.btn90CounterClockwise -> {
                rotateDegree(2, false)
            }
            R.id.btn90ClockwiseVerticalFlip -> {
                rotateDegree(3, false)
            }
        }
    }

    private fun rotateDegree(degree: Int, isRotate: Boolean) {
        when {
            !isInputVideoSelected -> {
                Toast.makeText(this, getString(R.string.input_video_validate_message), Toast.LENGTH_SHORT).show()
            }
            else -> {
                processStart()
                rotateProcess(degree, isRotate)
            }
        }
    }

    private fun rotateProcess(degree: Int, isRotate: Boolean) {
        val outputPath = Common.getFilePath(this, Common.VIDEO)
        val query = if (isRotate) {
            ffmpegQueryExtension.rotateVideo(tvInputPathVideo.text.toString(), degree, outputPath)
        } else {
            ffmpegQueryExtension.flipVideo(tvInputPathVideo.text.toString(), degree, outputPath)
        }

        CallBackOfQuery().callQuery(query, object : FFmpegCallBack {
            override fun process(logMessage: LogMessage) {
                tvOutputPath.text = logMessage.text
            }

            override fun success() {
                tvOutputPath.text = String.format(getString(R.string.output_path), outputPath)
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

    @SuppressLint("NewApi")
    override fun selectedFiles(mediaFiles: List<MediaFile>?, requestCode: Int) {
        when (requestCode) {
            Common.VIDEO_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    tvInputPathVideo.text = mediaFiles[0].path
                    isInputVideoSelected = true
                } else {
                    Toast.makeText(this, getString(R.string.video_not_selected_toast_message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun processStop() {
        btnVideoPath.isEnabled = true
        btnRotate90.isEnabled = true
        btnRotate180.isEnabled = true
        btnRotate270.isEnabled = true
        mProgressView.visibility = View.GONE
    }

    private fun processStart() {
        btnVideoPath.isEnabled = false
        btnRotate90.isEnabled = false
        btnRotate180.isEnabled = false
        btnRotate270.isEnabled = false
        mProgressView.visibility = View.VISIBLE
    }
}