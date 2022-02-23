package com.simform.videoimageeditor.videoProcessActivity

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.Common
import com.simform.videooperations.Common.RATIO_1
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.FFmpegQueryExtension
import com.simform.videooperations.LogMessage
import kotlinx.android.synthetic.main.activity_aspect_ratio.btnAspectRatio
import kotlinx.android.synthetic.main.activity_aspect_ratio.btnVideoPath
import kotlinx.android.synthetic.main.activity_aspect_ratio.mProgressView
import kotlinx.android.synthetic.main.activity_aspect_ratio.tvInputPathVideo
import kotlinx.android.synthetic.main.activity_aspect_ratio.tvOutputPath

class AspectRatioActivity : BaseActivity(R.layout.activity_aspect_ratio, R.string.apply_aspect_ratio) {
    private var isInputVideoSelected: Boolean = false
    override fun initialization() {
        btnVideoPath.setOnClickListener(this)
        btnAspectRatio.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnVideoPath -> {
                Common.selectFile(this, maxSelection = 1, isImageSelection = false, isAudioSelection = false)
            }
            R.id.btnAspectRatio -> {
                when {
                    !isInputVideoSelected -> {
                        Toast.makeText(this, getString(R.string.input_video_validate_message), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        processStart()
                        applyRatioProcess()
                    }
                }
            }
        }
    }

    private fun applyRatioProcess() {
        val outputPath = Common.getFilePath(this, Common.VIDEO)
        val query = ffmpegQueryExtension.applyRatio(tvInputPathVideo.text.toString(), RATIO_1, outputPath)

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
        btnAspectRatio.isEnabled = true
        mProgressView.visibility = View.GONE
    }

    private fun processStart() {
        btnVideoPath.isEnabled = false
        btnAspectRatio.isEnabled = false
        mProgressView.visibility = View.VISIBLE
    }

}