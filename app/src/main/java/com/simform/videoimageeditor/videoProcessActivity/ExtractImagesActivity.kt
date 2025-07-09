package com.simform.videoimageeditor.videoProcessActivity

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.databinding.ActivityExtractImagesBinding
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.Common
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.FFmpegQueryExtension
import com.simform.videooperations.Statistics
import java.io.File

class ExtractImagesActivity : BaseActivity(R.layout.activity_extract_images, R.string.extract_frame_from_video) {
    private lateinit var binding: ActivityExtractImagesBinding
    private var isInputVideoSelected: Boolean = false
    
    override fun initialization() {
        binding = ActivityExtractImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.apply {
            btnVideoPath.setOnClickListener(this@ExtractImagesActivity)
            btnExtract.setOnClickListener(this@ExtractImagesActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnVideoPath -> {
                Common.selectFile(this, maxSelection = 1, isImageSelection = false, isAudioSelection = false)
            }
            R.id.btnExtract -> {
                when {
                    !isInputVideoSelected -> {
                        Toast.makeText(this, getString(R.string.input_video_validate_message), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        processStart()
                        extractProcess()
                    }
                }
            }
        }
    }

    @SuppressLint("NewApi")
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
        }
    }

    @SuppressLint("SetTextI18n")
    private fun extractProcess() {
        val outputPath = Common.getFilePath(this, Common.IMAGE)
        val query = ffmpegQueryExtension.extractImages(binding.tvInputPathVideo.text.toString(), outputPath, spaceOfFrame = 4f)
        var totalFramesExtracted = 0
        CallBackOfQuery().callQuery(query, object : FFmpegCallBack {
            override fun statisticsProcess(statistics: Statistics) {
                totalFramesExtracted = statistics.videoFrameNumber
                binding.tvOutputPath.text = "Frames : ${statistics.videoFrameNumber}"
            }

            override fun success() {
                binding.tvOutputPath.text = "Output Directory : \n${File(getExternalFilesDir(Common.OUT_PUT_DIR).toString()).absolutePath} \n\nTotal Frames Extracted: $totalFramesExtracted"
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
            btnVideoPath.isEnabled = true
            btnExtract.isEnabled = true
            mProgressView.root.visibility = View.GONE
        }
    }

    private fun processStart() {
        binding.apply {
            btnVideoPath.isEnabled = false
            btnExtract.isEnabled = false
            mProgressView.root.visibility = View.VISIBLE
        }
    }
}