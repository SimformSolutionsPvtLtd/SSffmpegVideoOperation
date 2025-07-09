package com.simform.videoimageeditor.otherFFMPEGProcessActivity

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.databinding.ActivityChangeAudioValumeBinding
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.Common
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.LogMessage

class ChangeAudioVolumeActivity : BaseActivity(R.layout.activity_change_audio_valume, R.string.change_audio_volume) {
    private lateinit var binding: ActivityChangeAudioValumeBinding
    private var isInputAudioSelected: Boolean = false
    
    override fun initialization() {
        binding = ActivityChangeAudioValumeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.apply {
            btnAudioPath.setOnClickListener(this@ChangeAudioVolumeActivity)
            btnChange.setOnClickListener(this@ChangeAudioVolumeActivity)
        }
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
                mergeAudioProcess()
            }
        }
    }

    private fun mergeAudioProcess() {
        val outputPath = Common.getFilePath(this, Common.MP3)
        val query = ffmpegQueryExtension.audioVolumeUpdate(binding.tvInputPathAudio.text.toString(), volume = 0.1f, output = outputPath)
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

    private fun processStop() {
        binding.apply {
            btnAudioPath.isEnabled = true
            btnChange.isEnabled = true
            mProgressView.root.visibility = View.GONE
        }
    }

    private fun processStart() {
        binding.apply {
            btnAudioPath.isEnabled = false
            btnChange.isEnabled = false
            mProgressView.root.visibility = View.VISIBLE
        }
    }

    @SuppressLint("NewApi")
    override fun selectedFiles(mediaFiles: List<MediaFile>?, requestCode: Int) {
        when (requestCode) {
            Common.AUDIO_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    binding.tvInputPathAudio.text = mediaFiles[0].path
                    isInputAudioSelected = true
                } else {
                    Toast.makeText(this, getString(R.string.min_audio_selection_validation), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}