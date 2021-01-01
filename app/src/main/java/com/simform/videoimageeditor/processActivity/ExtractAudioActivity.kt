package com.simform.videoimageeditor.processActivity

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
import kotlinx.android.synthetic.main.activity_extract_audio.btnExtract
import kotlinx.android.synthetic.main.activity_extract_audio.btnVideoPath
import kotlinx.android.synthetic.main.activity_extract_audio.mProgressView
import kotlinx.android.synthetic.main.activity_extract_audio.tvInputPathVideo
import kotlinx.android.synthetic.main.activity_extract_audio.tvOutputPath

class ExtractAudioActivity : BaseActivity(R.layout.activity_extract_audio) {
    private var isInputVideoSelected: Boolean = false
    override fun initialization() {
        btnVideoPath.setOnClickListener(this)
        btnExtract.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnVideoPath -> {
                Common.selectFile(this, maxSelection = 1, isImageSelection = false)
            }
            R.id.btnExtract -> {
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
                                extractProcess()
                            }
                        }
                        imageToVideo.start()
                        gate.await()
                    }
                }
            }
        }
    }

    private fun extractProcess() {
        val outputPath = Common.getFilePath(this, Common.MP3)
        val query = FFmpegQueryExtension.extractAudio(tvInputPathVideo.text.toString(), outputPath)

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
            btnExtract.isEnabled = true
            mProgressView.visibility = View.GONE
        }
    }

    private fun processStart() {
        btnVideoPath.isEnabled = false
        btnExtract.isEnabled = false
        mProgressView.visibility = View.VISIBLE
    }
}