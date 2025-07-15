package com.simform.videoimageeditor.videoProcessActivity

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.databinding.ActivityCombineVideosBinding
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.Common
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.LogMessage
import com.simform.videooperations.Paths
import java.util.concurrent.CompletableFuture

class CombineVideosActivity : BaseActivity(R.layout.activity_combine_videos, R.string.merge_videos) {
    private lateinit var binding: ActivityCombineVideosBinding
    private var isVideoSelected: Boolean = false

    override fun initialization() {
        binding = ActivityCombineVideosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            btnVideoPath.setOnClickListener(this@CombineVideosActivity)
            btnCombine.setOnClickListener(this@CombineVideosActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnVideoPath -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                } else {
                    // Fallback for devices below Android 14
                    Common.selectFile(this, maxSelection = 5, isImageSelection = false, isAudioSelection = false)
                }
            }
            R.id.btnCombine -> {
                when {
                    !isVideoSelected -> {
                        Toast.makeText(this, getString(R.string.input_video_validate_message), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        processStart()
                        combineVideosProcess()
                    }
                }
            }
        }
    }

    @SuppressLint("NewApi", "SetTextI18n")
    override fun selectedFiles(mediaFiles: List<MediaFile>?, requestCode: Int) {
        when (requestCode) {
            Common.VIDEO_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    val size: Int = mediaFiles.size
                    binding.tvInputPathImage.text = "$size" + (if (size == 1) " Video " else " Videos ") + "selected"
                    this.mediaFiles = mediaFiles
                    isVideoSelected = true
                    CompletableFuture.runAsync {
                        retriever = MediaMetadataRetriever()
                        retriever?.setDataSource(
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                Common.saveFileToTempAndGetPath(this, mediaFiles[0].uri) ?: ""
                            } else {
                                mediaFiles[0].path
                            }
                        )
                        val bit = retriever?.frameAtTime
                        if (bit != null) {
                            width = bit.width
                            height = bit.height
                        }
                    }
                } else {
                    Toast.makeText(this, getString(R.string.video_not_selected_toast_message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun processStop() {
        binding.apply {
            btnVideoPath.isEnabled = true
            btnCombine.isEnabled = true
            mProgressView.root.visibility = View.GONE
        }
    }

    private fun processStart() {
        binding.apply {
            btnVideoPath.isEnabled = false
            btnCombine.isEnabled = false
            mProgressView.root.visibility = View.VISIBLE
        }
    }

    private fun combineVideosProcess() {
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

                paths.isImageFile = false
                paths.hasAudio = hasAudio(paths.filePath)
                pathsList.add(paths)
            }

            val query = ffmpegQueryExtension.combineVideos(
                pathsList,
                width,
                height,
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

    private fun hasAudio(filePath: String): Boolean {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(filePath)
            val hasAudio = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO)
            hasAudio == "yes"
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            retriever.release()
        }
    }
}