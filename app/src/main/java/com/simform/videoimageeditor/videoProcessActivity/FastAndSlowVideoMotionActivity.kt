package com.simform.videoimageeditor.videoProcessActivity

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
import kotlinx.android.synthetic.main.activity_fast_and_slow_video_motion.btnMotion
import kotlinx.android.synthetic.main.activity_fast_and_slow_video_motion.btnVideoPath
import kotlinx.android.synthetic.main.activity_fast_and_slow_video_motion.mProgressView
import kotlinx.android.synthetic.main.activity_fast_and_slow_video_motion.motionType
import kotlinx.android.synthetic.main.activity_fast_and_slow_video_motion.tvInputPathVideo
import kotlinx.android.synthetic.main.activity_fast_and_slow_video_motion.tvOutputPath

class FastAndSlowVideoMotionActivity : BaseActivity(R.layout.activity_fast_and_slow_video_motion, R.string.fast_slow_motion_video) {
    private var isInputVideoSelected: Boolean = false
    override fun initialization() {
        btnVideoPath.setOnClickListener(this)
        btnMotion.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnVideoPath -> {
                Common.selectFile(this, maxSelection = 1, isImageSelection = false, isAudioSelection = false)
            }
            R.id.btnMotion -> {
                when {
                    !isInputVideoSelected -> {
                        Toast.makeText(this, getString(R.string.input_video_validate_message), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        processStart()
                        motionProcess()
                    }
                }
            }
        }
    }

    private fun motionProcess() {
        val outputPath = Common.getFilePath(this, Common.VIDEO)
        var setpts = 0.5
        var atempo = 2.0
        if (!motionType.isChecked) {
            setpts = 2.0
            atempo = 0.5
        }
        val query = ffmpegQueryExtension.videoMotion(tvInputPathVideo.text.toString(), outputPath, setpts, atempo)
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
        btnMotion.isEnabled = true
        mProgressView.visibility = View.GONE
    }

    private fun processStart() {
        btnVideoPath.isEnabled = false
        btnMotion.isEnabled = false
        mProgressView.visibility = View.VISIBLE
    }
}