package com.simform.videoimageeditor.videoProcessActivity

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import android.view.View
import android.widget.Toast
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.Common
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.FFmpegQueryExtension
import com.simform.videooperations.LogMessage
import com.simform.videooperations.Paths
import java.util.concurrent.CompletableFuture
import kotlinx.android.synthetic.main.activity_combine_videos.btnCombine
import kotlinx.android.synthetic.main.activity_combine_videos.btnVideoPath
import kotlinx.android.synthetic.main.activity_combine_videos.mProgressView
import kotlinx.android.synthetic.main.activity_combine_videos.tvInputPathImage
import kotlinx.android.synthetic.main.activity_combine_videos.tvOutputPath
import kotlinx.android.synthetic.main.activity_merge_image_and_video.tvInputPathVideo

class CombineVideosActivity : BaseActivity(R.layout.activity_combine_videos, R.string.merge_videos) {
    private var isVideoSelected: Boolean = false

    override fun initialization() {
        btnVideoPath.setOnClickListener(this)
        btnCombine.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnVideoPath -> {
                Common.selectFile(this, maxSelection = 5, isImageSelection = false, isAudioSelection = false)
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
                    tvInputPathImage.text = "$size" + (if (size == 1) " Video " else " Videos ") + "selected"
                    isVideoSelected = true
                    CompletableFuture.runAsync {
                        retriever = MediaMetadataRetriever()
                        retriever?.setDataSource(tvInputPathVideo.text.toString())
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
        btnVideoPath.isEnabled = true
        btnCombine.isEnabled = true
        mProgressView.visibility = View.GONE
    }

    private fun processStart() {
        btnVideoPath.isEnabled = false
        btnCombine.isEnabled = false
        mProgressView.visibility = View.VISIBLE
    }

    private fun combineVideosProcess() {
        val outputPath = Common.getFilePath(this, Common.VIDEO)
        val pathsList = ArrayList<Paths>()
        mediaFiles?.let {
            for (element in it) {
                val paths = Paths()
                paths.filePath = element.path
                paths.isImageFile = false
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
}