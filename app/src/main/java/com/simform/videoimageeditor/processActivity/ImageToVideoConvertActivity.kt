package com.simform.videoimageeditor.processActivity

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.arthenica.mobileffmpeg.LogMessage
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.utils.Common
import com.simform.videoimageeditor.utils.FFmpegCallBack
import com.simform.videoimageeditor.utils.FFmpegQueryExtension
import com.simform.videoimageeditor.utils.ISize
import com.simform.videoimageeditor.utils.SizeOfImage
import java.util.concurrent.CyclicBarrier
import kotlinx.android.synthetic.main.activity_image_to_video_convert.btnConvert
import kotlinx.android.synthetic.main.activity_image_to_video_convert.btnImagePath
import kotlinx.android.synthetic.main.activity_image_to_video_convert.edtSecond
import kotlinx.android.synthetic.main.activity_image_to_video_convert.mProgressView
import kotlinx.android.synthetic.main.activity_image_to_video_convert.tvInputPath
import kotlinx.android.synthetic.main.activity_image_to_video_convert.tvOutputPath

class ImageToVideoConvertActivity : BaseActivity(R.layout.activity_image_to_video_convert) {
    private var isFileSelected: Boolean = false
    override fun initialization() {
        btnImagePath.setOnClickListener(this)
        btnConvert.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnImagePath -> {
                Common.selectFile(this, maxSelection = 1, isImageSelection = true)
            }
            R.id.btnConvert -> {
                when {
                    !isFileSelected -> {
                        Toast.makeText(this, getString(R.string.input_image_validate_message), Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(edtSecond.text.toString().trim()) || edtSecond.text.toString().trim().toInt() == 0 -> {
                        Toast.makeText(this, getString(R.string.please_enter_second), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        processStart()
                        val gate = CyclicBarrier(2)
                        val imageToVideo = object : Thread() {
                            override fun run() {
                                gate.await()
                                createVideo()
                            }
                        }
                        imageToVideo.start()
                        gate.await()
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
                tvInputPath.text = mediaFiles[0].path
            } else {
                isFileSelected = false
                Toast.makeText(this, getString(R.string.image_not_selected_toast_message), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createVideo() {
        val outputPath = Common.getFilePath(this, Common.VIDEO)
        val size: ISize = SizeOfImage(tvInputPath.text.toString())
        val query = FFmpegQueryExtension.imageToVideo(tvInputPath.text.toString(), outputPath, edtSecond.text.toString().toInt(), size.width(), size.height())

        Common.callQuery(this, query, object : FFmpegCallBack {
            override fun process(logMessage: LogMessage) {
                tvOutputPath.text = logMessage.text
            }

            override fun success() {
                tvOutputPath.text = "Output Path : \n$outputPath"
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
        runOnUiThread {
            btnImagePath.isEnabled = true
            btnConvert.isEnabled = true
            mProgressView.visibility = View.GONE
        }
    }

    private fun processStart() {
        btnImagePath.isEnabled = false
        btnConvert.isEnabled = false
        mProgressView.visibility = View.VISIBLE
    }
}