package com.simform.videoimageeditor.otherFFMPEGProcessActivity

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.Common
import com.simform.videooperations.Common.DURATION_FIRST
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.FFmpegQueryExtension
import com.simform.videooperations.LogMessage
import com.simform.videooperations.Paths
import kotlinx.android.synthetic.main.activity_audios_merge.btnAudioPath
import kotlinx.android.synthetic.main.activity_audios_merge.btnMerge
import kotlinx.android.synthetic.main.activity_audios_merge.mProgressView
import kotlinx.android.synthetic.main.activity_audios_merge.tvInputPathAudio
import kotlinx.android.synthetic.main.activity_audios_merge.tvOutputPath

class AudiosMergeActivity : BaseActivity(R.layout.activity_audios_merge, R.string.merge_audios) {
    private var isInputAudioSelected: Boolean = false
    override fun initialization() {
        btnAudioPath.setOnClickListener(this)
        btnMerge.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAudioPath -> {
                Common.selectFile(this, maxSelection = 10, isImageSelection = false, isAudioSelection = true)
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
                paths.filePath = element.path
                paths.isImageFile = true
                pathsList.add(paths)
            }

            val query = ffmpegQueryExtension.mergeAudios(pathsList, DURATION_FIRST, outputPath)

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
    }

    private fun processStop() {
        btnAudioPath.isEnabled = true
        btnMerge.isEnabled = true
        mProgressView.visibility = View.GONE
    }

    private fun processStart() {
        btnAudioPath.isEnabled = false
        btnMerge.isEnabled = false
        mProgressView.visibility = View.VISIBLE
    }

    @SuppressLint("NewApi")
    override fun selectedFiles(mediaFiles: List<MediaFile>?, requestCode: Int) {
        when (requestCode) {
            Common.AUDIO_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    val size: Int = mediaFiles.size
                    if (size > 1) {
                        tvInputPathAudio.text = "$size Audio selected"
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