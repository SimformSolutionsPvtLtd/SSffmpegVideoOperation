package com.simform.videoimageeditor.videoProcessActivity

import android.annotation.SuppressLint
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.databinding.ActivityCombineImagesBinding
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.Common
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.FFmpegQueryExtension
import com.simform.videooperations.LogMessage
import com.simform.videooperations.Paths

class CombineImagesActivity : BaseActivity(R.layout.activity_combine_images, R.string.merge_images) {
    private lateinit var binding: ActivityCombineImagesBinding
    private var isImageSelected: Boolean = false
    
    override fun initialization() {
        binding = ActivityCombineImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            btnImagePath.setOnClickListener(this@CombineImagesActivity)
            btnCombine.setOnClickListener(this@CombineImagesActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnImagePath -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                } else {
                    Common.selectFile(
                        this,
                        maxSelection = 25,
                        isImageSelection = true,
                        isAudioSelection = false
                    )
                }
            }
            R.id.btnCombine -> {
                when {
                    !isImageSelected -> {
                        Toast.makeText(this, getString(R.string.input_image_validate_message), Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(binding.edtSecond.text.toString().trim()) || binding.edtSecond.text.toString().trim().toInt() == 0 -> {
                        Toast.makeText(this, getString(R.string.please_enter_second), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        processStart()
                        combineImagesProcess()
                    }
                }
            }
        }
    }

    @SuppressLint("NewApi", "SetTextI18n")
    override fun selectedFiles(mediaFiles: List<MediaFile>?, requestCode: Int) {
        when (requestCode) {
            Common.IMAGE_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    val size: Int = mediaFiles.size
                    binding.tvInputPathImage.text = "$size" + (if (size == 1) " Image " else " Images ") + "selected"
                    isImageSelected = true
                } else {
                    Toast.makeText(this, getString(R.string.image_not_selected_toast_message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun processStop() {
        binding.apply {
            btnImagePath.isEnabled = true
            btnCombine.isEnabled = true
            mProgressView.root.visibility = View.GONE
        }
    }

    private fun processStart() {
        binding.apply {
            binding.apply {
                btnImagePath.isEnabled = false
                btnCombine.isEnabled = false
                mProgressView.root.visibility = View.VISIBLE
            }
        }
    }

    private fun combineImagesProcess() {
        val outputPath = Common.getFilePath(this, Common.VIDEO)
        val pathsList = ArrayList<Paths>()
        mediaFiles?.let {
            for (element in it) {
                val paths = Paths()
                paths.filePath =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Common.saveFileToTempAndGetPath(this, element.uri) ?: ""
                    } else {
                        element.path
                    }
                paths.isImageFile = true
                pathsList.add(paths)
            }

            val query = ffmpegQueryExtension.combineImagesAndVideos(
                pathsList,
                640,
                480,
                binding.edtSecond.text.toString(),
                outputPath
            )

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
}