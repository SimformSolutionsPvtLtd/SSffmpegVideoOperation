package com.simform.videoimageeditor.otherFFMPEGProcessActivity

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.widget.Toast
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.databinding.ActivityAudiosMergeBinding
import com.simform.videoimageeditor.utils.enableEdgeToEdge
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.Common
import com.simform.videooperations.Common.DURATION_FIRST
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.LogMessage
import com.simform.videooperations.Paths

class AudiosMergeActivity : BaseActivity(R.layout.activity_audios_merge, R.string.merge_audios) {
    private lateinit var binding: ActivityAudiosMergeBinding
    private var isInputAudioSelected: Boolean = false
    
    override fun initialization() {
        binding = ActivityAudiosMergeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge(binding.toolbar.root)
        binding.toolbar.textTitle.text = getString(R.string.merge_audios)
        binding.btnAudioPath.setOnClickListener(this)
        binding.btnMerge.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAudioPath -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pickMultipleAudio.launch(arrayOf("audio/*"))
                } else {
                    Common.selectFile(
                        this,
                        maxSelection = 10,
                        isImageSelection = false,
                        isAudioSelection = true
                    )
                }
            }
            R.id.btnMerge -> {
                mediaFiles?.size?.let {
                    if (it < 2 || !isInputAudioSelected) {
                        Toast.makeText(this, getString(R.string.min_audio_selection_validation), Toast.LENGTH_SHORT).show()
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
        val pathsList = ArrayList<Paths>()
        mediaFiles?.let {
            for (element in it) {
                val paths = Paths()
                paths.filePath =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Common.saveFileToTempAndGetPath(this, element.uri) ?: ""
                    } else {
                        element.path ?: ""
                    }
                paths.isImageFile = true
                pathsList.add(paths)
            }

            val query = ffmpegQueryExtension.mergeAudios(pathsList, DURATION_FIRST, outputPath)

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
    }

    private fun processStop() {
        binding.apply {
            btnAudioPath.isEnabled = true
            btnMerge.isEnabled = true
            mProgressView.root.visibility = View.GONE
        }
    }

    private fun processStart() {
        binding.apply {
            btnAudioPath.isEnabled = false
            btnMerge.isEnabled = false
            mProgressView.root.visibility = View.VISIBLE
        }
    }

    @SuppressLint("NewApi")
    override fun selectedFiles(mediaFiles: List<MediaFile>?, requestCode: Int) {
        when (requestCode) {
            Common.AUDIO_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    val size: Int = mediaFiles.size
                    if (size > 1) {
                        binding.tvInputPathAudio.text = "$size Audio selected"
                        isInputAudioSelected = true
                    } else {
                        Toast.makeText(this, getString(R.string.min_audio_selection_validation), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, getString(R.string.min_audio_selection_validation), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}