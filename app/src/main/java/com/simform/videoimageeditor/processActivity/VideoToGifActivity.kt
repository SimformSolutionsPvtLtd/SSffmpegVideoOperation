package com.simform.videoimageeditor.processActivity

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import com.arthenica.mobileffmpeg.LogMessage
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.utils.Common
import com.simform.videoimageeditor.utils.FFmpegCallBack
import com.simform.videoimageeditor.utils.FFmpegQueryExtension
import java.util.concurrent.CyclicBarrier
import kotlinx.android.synthetic.main.activity_video_to_gif.btnConvertIntoGif
import kotlinx.android.synthetic.main.activity_video_to_gif.btnVideoPath
import kotlinx.android.synthetic.main.activity_video_to_gif.mProgressView
import kotlinx.android.synthetic.main.activity_video_to_gif.tvInputPathVideo
import kotlinx.android.synthetic.main.activity_video_to_gif.tvOutputPath

class VideoToGifActivity : BaseActivity(R.layout.activity_video_to_gif) {
    private var isInputVideoSelected: Boolean = false
    override fun initialization() {
        btnVideoPath.setOnClickListener(this)
        btnConvertIntoGif.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnVideoPath -> {
                Common.selectFile(this, maxSelection = 1, isImageSelection = false, isAudioSelection = false)
            }
            R.id.btnConvertIntoGif -> {
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
                                convertProcess()
                            }
                        }
                        imageToVideo.start()
                        gate.await()
                    }
                }
            }
        }
    }

    private fun convertProcess() {
        val outputPath = Common.getFilePath(this, Common.VIDEO)
        val query = FFmpegQueryExtension.convertVideoToGIF(tvInputPathVideo.text.toString(), outputPath)

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
        runOnUiThread {
            btnVideoPath.isEnabled = true
            btnConvertIntoGif.isEnabled = true
            mProgressView.visibility = View.GONE
        }
    }

    private fun processStart() {
        btnVideoPath.isEnabled = false
        btnConvertIntoGif.isEnabled = false
        mProgressView.visibility = View.VISIBLE
    }
}