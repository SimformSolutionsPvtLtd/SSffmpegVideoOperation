package com.simform.videoimageeditor.otherFFMPEGProcessActivity

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.Common
import com.simform.videooperations.Common.BITRATE_128
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.FFmpegQueryExtension
import com.simform.videooperations.LogMessage
import kotlinx.android.synthetic.main.activity_compress_audio.btnAudioPath
import kotlinx.android.synthetic.main.activity_compress_audio.btnChange
import kotlinx.android.synthetic.main.activity_compress_audio.mProgressView
import kotlinx.android.synthetic.main.activity_compress_audio.tvInputPathAudio
import kotlinx.android.synthetic.main.activity_compress_audio.tvOutputPath

class CompressAudioActivity : BaseActivity(R.layout.activity_compress_audio, R.string.compress_audio) {
    private var isInputAudioSelected: Boolean = false
    override fun initialization() {
        btnAudioPath.setOnClickListener(this)
        btnChange.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAudioPath -> {
                Common.selectFile(this, maxSelection = 1, isImageSelection = false, isAudioSelection = true)
            }
            R.id.btnChange -> {
                mediaFiles?.size?.let {
                    if (!isInputAudioSelected) {
                        Toast.makeText(this, getString(R.string.validation_of_audio), Toast.LENGTH_SHORT).show()
                        return
                    }
                }
                processStart()
                compressAudioProcess()
            }
        }
    }

    private fun compressAudioProcess() {
        val outputPath = Common.getFilePath(this, Common.MP3)
        val query = ffmpegQueryExtension.compressAudio(inputAudioPath = tvInputPathAudio.text.toString(), bitrate = BITRATE_128, output = outputPath)
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

    private fun processStop() {
        btnAudioPath.isEnabled = true
        btnChange.isEnabled = true
        mProgressView.visibility = View.GONE
    }

    private fun processStart() {
        btnAudioPath.isEnabled = false
        btnChange.isEnabled = false
        mProgressView.visibility = View.VISIBLE
    }

    @SuppressLint("NewApi")
    override fun selectedFiles(mediaFiles: List<MediaFile>?, requestCode: Int) {
        when (requestCode) {
            Common.AUDIO_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    tvInputPathAudio.text = mediaFiles[0].path
                    isInputAudioSelected = true
                } else {
                    Toast.makeText(this, getString(R.string.min_audio_selection_validation), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}