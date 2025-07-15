package com.simform.videoimageeditor.videoProcessActivity

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.databinding.ActivityVideoRotateFlipBinding
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.Common
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.FFmpegQueryExtension
import com.simform.videooperations.LogMessage

class VideoRotateFlipActivity : BaseActivity(R.layout.activity_video_rotate_flip, R.string.video_rotate) {
    private lateinit var binding: ActivityVideoRotateFlipBinding
    private var isInputVideoSelected: Boolean = false
    
    override fun initialization() {
        binding = ActivityVideoRotateFlipBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            btnVideoPath.setOnClickListener(this@VideoRotateFlipActivity)
            btnRotate90.setOnClickListener(this@VideoRotateFlipActivity)
            btnRotate180.setOnClickListener(this@VideoRotateFlipActivity)
            btnRotate270.setOnClickListener(this@VideoRotateFlipActivity)
            btn90CounterClockwiseVerticalFlip.setOnClickListener(this@VideoRotateFlipActivity)
            btn90Clockwise.setOnClickListener(this@VideoRotateFlipActivity)
            btn90CounterClockwise.setOnClickListener(this@VideoRotateFlipActivity)
            btn90ClockwiseVerticalFlip.setOnClickListener(this@VideoRotateFlipActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnVideoPath -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pickSingleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                } else {
                    // Fallback for devices below Android 14
                    Common.selectFile(this, maxSelection = 1, isImageSelection = false, isAudioSelection = false)
                }
            }
            R.id.btnRotate90 -> {
                rotateDegree(90, true)
            }
            R.id.btnRotate180 -> {
                rotateDegree(180, true)
            }
            R.id.btnRotate270 -> {
                rotateDegree(270, true)
            }
            R.id.btn90CounterClockwiseVerticalFlip -> {
                rotateDegree(0, false)
            }
            R.id.btn90Clockwise -> {
                rotateDegree(1, false)
            }
            R.id.btn90CounterClockwise -> {
                rotateDegree(2, false)
            }
            R.id.btn90ClockwiseVerticalFlip -> {
                rotateDegree(3, false)
            }
        }
    }

    private fun rotateDegree(degree: Int, isRotate: Boolean) {
        when {
            !isInputVideoSelected -> {
                Toast.makeText(this, getString(R.string.input_video_validate_message), Toast.LENGTH_SHORT).show()
            }
            else -> {
                processStart()
                rotateProcess(degree, isRotate)
            }
        }
    }

    private fun rotateProcess(degree: Int, isRotate: Boolean) {
        val outputPath = Common.getFilePath(this, Common.VIDEO)
        val query = if (isRotate) {
            ffmpegQueryExtension.rotateVideo(binding.tvInputPathVideo.text.toString(), degree, outputPath)
        } else {
            ffmpegQueryExtension.flipVideo(binding.tvInputPathVideo.text.toString(), degree, outputPath)
        }

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

    @SuppressLint("NewApi")
    override fun selectedFiles(mediaFiles: List<MediaFile>?, requestCode: Int) {
        when (requestCode) {
            Common.VIDEO_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    binding.tvInputPathVideo.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Common.saveFileToTempAndGetPath(this, mediaFiles[0].uri)
                    } else {
                        mediaFiles[0].path
                    }
                    isInputVideoSelected = true
                } else {
                    Toast.makeText(this, getString(R.string.video_not_selected_toast_message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun processStop() {
        binding.apply {
            btnVideoPath.isEnabled = true
            btnRotate90.isEnabled = true
            btnRotate180.isEnabled = true
            btnRotate270.isEnabled = true
            mProgressView.root.visibility = View.GONE
        }
    }

    private fun processStart() {
        binding.apply {
            btnVideoPath.isEnabled = false
            btnRotate90.isEnabled = false
            btnRotate180.isEnabled = false
            btnRotate270.isEnabled = false
            mProgressView.root.visibility = View.VISIBLE
        }
    }
}