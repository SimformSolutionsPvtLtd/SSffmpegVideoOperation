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
import kotlinx.android.synthetic.main.activity_merge_image_and_mp3.btnImagePath
import kotlinx.android.synthetic.main.activity_merge_image_and_mp3.btnMerge
import kotlinx.android.synthetic.main.activity_merge_image_and_mp3.btnMp3Path
import kotlinx.android.synthetic.main.activity_merge_image_and_mp3.mProgressView
import kotlinx.android.synthetic.main.activity_merge_image_and_mp3.tvInputPathAudio
import kotlinx.android.synthetic.main.activity_merge_image_and_mp3.tvInputPathImage
import kotlinx.android.synthetic.main.activity_merge_image_and_mp3.tvOutputPath

class MergeImageAndMP3Activity : BaseActivity(R.layout.activity_merge_image_and_mp3, R.string.merge_image_and_audio) {
    private var isInputImageSelected: Boolean = false
    private var isInputMP3Selected: Boolean = false
    override fun initialization() {
        btnImagePath.setOnClickListener(this)
        btnMp3Path.setOnClickListener(this)
        btnMerge.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnImagePath -> {
                Common.selectFile(this, maxSelection = 1, isImageSelection = true, isAudioSelection = false)
            }
            R.id.btnMp3Path -> {
                Common.selectFile(this, maxSelection = 1, isImageSelection = false, isAudioSelection = true)
            }
            R.id.btnMerge -> {
                when {
                    !isInputImageSelected -> {
                        Toast.makeText(this, getString(R.string.please_select_input_image), Toast.LENGTH_SHORT).show()
                    }
                    !isInputMP3Selected -> {
                        Toast.makeText(this, getString(R.string.please_select_input_audio), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        processStart()
                        mergeProcess()
                    }
                }
            }
        }
    }

    private fun mergeProcess() {
        val outputPath = Common.getFilePath(this, Common.VIDEO)
        val query = ffmpegQueryExtension.mergeImageAndAudio(tvInputPathImage.text.toString(), tvInputPathAudio.text.toString(), outputPath)

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
            Common.IMAGE_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    tvInputPathImage.text = mediaFiles[0].path
                    isInputImageSelected = true
                } else {
                    Toast.makeText(this, getString(R.string.video_not_selected_toast_message), Toast.LENGTH_SHORT).show()
                }
            }
            Common.AUDIO_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    tvInputPathAudio.text = mediaFiles[0].path
                    isInputMP3Selected = true
                } else {
                    Toast.makeText(this, getString(R.string.video_not_selected_toast_message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun processStop() {
        btnImagePath.isEnabled = true
        btnMp3Path.isEnabled = true
        btnMerge.isEnabled = true
        mProgressView.visibility = View.GONE
    }

    private fun processStart() {
        btnImagePath.isEnabled = false
        btnMp3Path.isEnabled = false
        btnMerge.isEnabled = false
        mProgressView.visibility = View.VISIBLE
    }
}