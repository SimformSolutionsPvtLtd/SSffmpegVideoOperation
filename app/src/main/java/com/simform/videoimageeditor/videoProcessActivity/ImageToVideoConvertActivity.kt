package com.simform.videoimageeditor.videoProcessActivity

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.databinding.ActivityImageToVideoConvertBinding
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.Common
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.FFmpegQueryExtension
import com.simform.videooperations.ISize
import com.simform.videooperations.LogMessage
import com.simform.videooperations.SizeOfImage

class ImageToVideoConvertActivity : BaseActivity(R.layout.activity_image_to_video_convert, R.string.image_to_video) {
    private lateinit var binding: ActivityImageToVideoConvertBinding
    private var isFileSelected: Boolean = false
    
    override fun initialization() {
        binding = ActivityImageToVideoConvertBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.apply {
            btnImagePath.setOnClickListener(this@ImageToVideoConvertActivity)
            btnConvert.setOnClickListener(this@ImageToVideoConvertActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnImagePath -> {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    pickSingleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                } else {
                    // Fallback for devices below Android 14
                    Common.selectFile(this, maxSelection = 1, isImageSelection = true, isAudioSelection = false)
                }
            }
            R.id.btnConvert -> {
                when {
                    !isFileSelected -> {
                        Toast.makeText(this, getString(R.string.input_image_validate_message), Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(binding.edtSecond.text.toString().trim()) || binding.edtSecond.text.toString().trim().toInt() == 0 -> {
                        Toast.makeText(this, getString(R.string.please_enter_second), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        processStart()
                        createVideo()
                    }
                }
            }
        }
    }

    @SuppressLint("NewApi")
    override fun selectedFiles(mediaFiles: List<MediaFile>?, requestCode: Int) {
        if (requestCode == Common.IMAGE_FILE_REQUEST_CODE) {
            if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                isFileSelected = true
                binding.tvInputPath.text =
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        Common.saveFileToTempAndGetPath(this, mediaFiles[0].uri)
                    } else {
                        mediaFiles[0].path
                    }
            } else {
                isFileSelected = false
                Toast.makeText(this, getString(R.string.image_not_selected_toast_message), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createVideo() {
        val outputPath = Common.getFilePath(this, Common.VIDEO)
        val size: ISize = SizeOfImage(binding.tvInputPath.text.toString())
        val query = ffmpegQueryExtension.imageToVideo(binding.tvInputPath.text.toString(), outputPath, binding.edtSecond.text.toString().toInt(), size.width(), size.height())

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
            btnImagePath.isEnabled = true
            btnConvert.isEnabled = true
            mProgressView.root.visibility = View.GONE
        }
    }

    private fun processStart() {
        binding.apply {
            btnImagePath.isEnabled = false
            btnConvert.isEnabled = false
            mProgressView.root.visibility = View.VISIBLE
        }
    }
}