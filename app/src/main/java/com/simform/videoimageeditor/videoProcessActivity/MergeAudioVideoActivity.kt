package com.simform.videoimageeditor.videoProcessActivity

import android.view.View
import android.widget.Toast
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.databinding.ActivityMergeAudioVideoBinding
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.Common
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.FFmpegQueryExtension
import com.simform.videooperations.LogMessage

class MergeAudioVideoActivity : BaseActivity(R.layout.activity_merge_audio_video, R.string.merge_video_and_audio) {
    private lateinit var binding: ActivityMergeAudioVideoBinding
    private var isInputVideoSelected: Boolean = false
    private var isInputAudioSelected: Boolean = false
    
    override fun initialization() {
        binding = ActivityMergeAudioVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.apply {
            btnVideoPath.setOnClickListener(this@MergeAudioVideoActivity)
            btnMp3Path.setOnClickListener(this@MergeAudioVideoActivity)
            btnMerge.setOnClickListener(this@MergeAudioVideoActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnVideoPath -> {
                Common.selectFile(this, maxSelection = 1, isImageSelection = false, isAudioSelection = false)
            }
            R.id.btnMp3Path -> {
                Common.selectFile(this, maxSelection = 1, isImageSelection = false, isAudioSelection = true)
            }
            R.id.btnMerge -> {
                when {
                    !isInputVideoSelected -> {
                        Toast.makeText(this, getString(R.string.input_video_validate_message), Toast.LENGTH_SHORT).show()
                    }
                    !isInputAudioSelected -> {
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
        val query = ffmpegQueryExtension.mergeAudioVideo(binding.tvInputPathVideo.text.toString(), binding.tvInputPathAudio.text.toString(), outputPath)

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
            Common.VIDEO_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    binding.tvInputPathVideo.text = mediaFiles[0].path
                    isInputVideoSelected = true
                } else {
                    Toast.makeText(this, getString(R.string.video_not_selected_toast_message), Toast.LENGTH_SHORT).show()
                }
            }
            Common.AUDIO_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    binding.tvInputPathAudio.text = mediaFiles[0].path
                    isInputAudioSelected = true
                } else {
                    Toast.makeText(this, getString(R.string.video_not_selected_toast_message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun processStop() {
        binding.apply {
            btnVideoPath.isEnabled = true
            btnMp3Path.isEnabled = true
            btnMerge.isEnabled = true
            mProgressView.root.visibility = View.GONE
        }
    }

    private fun processStart() {
        binding.apply {
            btnVideoPath.isEnabled = false
            btnMp3Path.isEnabled = false
            btnMerge.isEnabled = false
            mProgressView.root.visibility = View.VISIBLE
        }
    }
}