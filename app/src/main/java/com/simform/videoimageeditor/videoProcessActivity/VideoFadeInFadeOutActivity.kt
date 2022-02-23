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
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlinx.android.synthetic.main.activity_video_fade_in_fade_out.btnApplyFadeInFadeOut
import kotlinx.android.synthetic.main.activity_video_fade_in_fade_out.btnVideoPath
import kotlinx.android.synthetic.main.activity_video_fade_in_fade_out.mProgressView
import kotlinx.android.synthetic.main.activity_video_fade_in_fade_out.tvInputPathVideo
import kotlinx.android.synthetic.main.activity_video_fade_in_fade_out.tvOutputPath

class VideoFadeInFadeOutActivity : BaseActivity(R.layout.activity_video_fade_in_fade_out, R.string.video_fade_in_and_fade_out) {
    private var isInputVideoSelected: Boolean = false
    private var selectedVideoDurationInSecond = 0L
    override fun initialization() {
        btnVideoPath.setOnClickListener(this)
        btnApplyFadeInFadeOut.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnVideoPath -> {
                Common.selectFile(this, maxSelection = 1, isImageSelection = false, isAudioSelection = false)
            }
            R.id.btnApplyFadeInFadeOut -> {
                when {
                    !isInputVideoSelected -> {
                        Toast.makeText(this, getString(R.string.input_video_validate_message), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        processStart()
                        fadeInFadeOutProcess()
                    }
                }
            }
        }
    }

    private fun fadeInFadeOutProcess() {
        val outputPath = Common.getFilePath(this, Common.VIDEO)
        val query = ffmpegQueryExtension.videoFadeInFadeOut(tvInputPathVideo.text.toString(), selectedVideoDurationInSecond, fadeInEndSeconds = 3, fadeOutStartSeconds = 3, output = outputPath)

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

    @SuppressLint("NewApi")
    override fun selectedFiles(mediaFiles: List<MediaFile>?, requestCode: Int) {
        when (requestCode) {
            Common.VIDEO_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    tvInputPathVideo.text = mediaFiles[0].path
                    isInputVideoSelected = true
                    CompletableFuture.runAsync {
                        retriever = MediaMetadataRetriever()
                        retriever?.setDataSource(tvInputPathVideo.text.toString())
                        val time = retriever?.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        time?.toLong()?.let {
                            selectedVideoDurationInSecond = TimeUnit.MILLISECONDS.toSeconds(it)
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
        btnApplyFadeInFadeOut.isEnabled = true
        mProgressView.visibility = View.GONE
    }

    private fun processStart() {
        btnVideoPath.isEnabled = false
        btnApplyFadeInFadeOut.isEnabled = false
        mProgressView.visibility = View.VISIBLE
    }
}