package com.simform.videoimageeditor.otherFFMPEGProcessActivity

import android.os.Build
import android.view.View
import android.widget.Toast
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.databinding.ActivityFastAndSlowAudioBinding
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.Common
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.FFmpegQueryExtension
import com.simform.videooperations.LogMessage

class FastAndSlowAudioActivity : BaseActivity(R.layout.activity_fast_and_slow_audio, R.string.fast_slow_motion_video) {
    private lateinit var binding: ActivityFastAndSlowAudioBinding
    private var isInputAudioSelected: Boolean = false
    
    override fun initialization() {
        binding = ActivityFastAndSlowAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.apply {
            btnAudioPath.setOnClickListener(this@FastAndSlowAudioActivity)
            btnMotion.setOnClickListener(this@FastAndSlowAudioActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAudioPath -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pickAudio.launch(arrayOf("audio/*"))
                } else {
                    Common.selectFile(
                        this,
                        maxSelection = 1,
                        isImageSelection = false,
                        isAudioSelection = true
                    )
                }
            }
            R.id.btnMotion -> {
                when {
                    !isInputAudioSelected -> {
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
        val outputPath = Common.getFilePath(this, Common.MP3)
        var atempo = 2.0
        if (!binding.motionType.isChecked) {
            atempo = 0.5
        }
        val query = ffmpegQueryExtension.audioMotion(binding.tvInputPathAudio.text.toString(), outputPath, atempo)
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
            Common.AUDIO_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    binding.tvInputPathAudio.text =
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Common.saveFileToTempAndGetPath(this, mediaFiles[0].uri)
                        } else {
                            mediaFiles[0].path ?: ""
                        }
                    isInputAudioSelected = true
                } else {
                    Toast.makeText(this, getString(R.string.audio_not_selected_toast_message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun processStop() {
        binding.apply {
            btnAudioPath.isEnabled = true
            btnMotion.isEnabled = true
            mProgressView.root.visibility = View.GONE
        }
    }

    private fun processStart() {
        binding.apply {
            btnAudioPath.isEnabled = false
            btnMotion.isEnabled = false
            mProgressView.root.visibility = View.VISIBLE
        }
    }
}