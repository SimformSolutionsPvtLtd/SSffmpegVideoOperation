package com.simform.videoimageeditor.videoProcessActivity

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.Common
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.FFmpegQueryExtension
import com.simform.videooperations.Statistics
import java.io.File
import kotlinx.android.synthetic.main.activity_extract_images.btnExtract
import kotlinx.android.synthetic.main.activity_extract_images.btnVideoPath
import kotlinx.android.synthetic.main.activity_extract_images.mProgressView
import kotlinx.android.synthetic.main.activity_extract_images.tvInputPathVideo
import kotlinx.android.synthetic.main.activity_extract_images.tvOutputPath

class ExtractImagesActivity : BaseActivity(R.layout.activity_extract_images, R.string.extract_frame_from_video) {
    private var isInputVideoSelected: Boolean = false
    override fun initialization() {
        btnVideoPath.setOnClickListener(this)
        btnExtract.setOnClickListener(this)
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
                    tvInputPathVideo.text = mediaFiles[0].path
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
        val query = ffmpegQueryExtension.extractImages(tvInputPathVideo.text.toString(), outputPath, spaceOfFrame = 4f)
        var totalFramesExtracted = 0
        CallBackOfQuery().callQuery(query, object : FFmpegCallBack {
            override fun statisticsProcess(statistics: Statistics) {
                totalFramesExtracted = statistics.videoFrameNumber
                tvOutputPath.text = "Frames : ${statistics.videoFrameNumber}"
            }

            override fun success() {
                tvOutputPath.text = "Output Directory : \n${File(getExternalFilesDir(Common.OUT_PUT_DIR).toString()).absolutePath} \n\nTotal Frames Extracted: $totalFramesExtracted"
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
        btnVideoPath.isEnabled = true
        btnExtract.isEnabled = true
        mProgressView.visibility = View.GONE
    }

    private fun processStart() {
        btnVideoPath.isEnabled = false
        btnExtract.isEnabled = false
        mProgressView.visibility = View.VISIBLE
    }
}