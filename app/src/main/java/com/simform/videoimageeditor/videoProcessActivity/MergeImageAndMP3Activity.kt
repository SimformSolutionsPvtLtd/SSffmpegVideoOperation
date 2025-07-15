package com.simform.videoimageeditor.videoProcessActivity

import android.view.View
import android.widget.Toast
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.databinding.ActivityMergeImageAndMp3Binding
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.Common
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.LogMessage

class MergeImageAndMP3Activity : BaseActivity(R.layout.activity_merge_image_and_mp3, R.string.merge_image_and_audio) {
    private lateinit var binding: ActivityMergeImageAndMp3Binding
    private var isInputImageSelected: Boolean = false
    private var isInputMP3Selected: Boolean = false
    
    override fun initialization() {
        binding = ActivityMergeImageAndMp3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.apply {
            btnImagePath.setOnClickListener(this@MergeImageAndMP3Activity)
            btnMp3Path.setOnClickListener(this@MergeImageAndMP3Activity)
            btnMerge.setOnClickListener(this@MergeImageAndMP3Activity)
        }
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
        val query = ffmpegQueryExtension.mergeImageAndAudio(binding.tvInputPathImage.text.toString(), binding.tvInputPathAudio.text.toString(), outputPath)

        CallBackOfQuery().callQuery(query, object : FFmpegCallBack {
            override fun process(logMessage: LogMessage) {
                binding.tvOutputPath.text = logMessage.text
            }

            override fun success() {
                binding.tvOutputPath.text = String.format(getString(R.string.output_path), outputPath)
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
                    binding.tvInputPathImage.text = mediaFiles[0].path
                    isInputImageSelected = true
                } else {
                    Toast.makeText(this, getString(R.string.video_not_selected_toast_message), Toast.LENGTH_SHORT).show()
                }
            }
            Common.AUDIO_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    binding.tvInputPathAudio.text = mediaFiles[0].path
                    isInputMP3Selected = true
                } else {
                    Toast.makeText(this, getString(R.string.video_not_selected_toast_message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun processStop() {
        binding.apply {
            btnImagePath.isEnabled = true
            btnMp3Path.isEnabled = true
            btnMerge.isEnabled = true
            mProgressView.root.visibility = View.GONE
        }
    }

    private fun processStart() {
        binding.apply {
            btnImagePath.isEnabled = false
            btnMp3Path.isEnabled = false
            btnMerge.isEnabled = false
            mProgressView.root.visibility = View.VISIBLE
        }
    }
}