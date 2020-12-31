package com.simform.videoimageeditor.processActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.utils.Common
import com.simform.videoimageeditor.utils.Common.OUT_PUT_DIR
import com.simform.videoimageeditor.utils.Extension
import com.simform.videoimageeditor.utils.ISize
import com.simform.videoimageeditor.utils.SizeFromImage
import java.io.File
import java.util.concurrent.CyclicBarrier
import kotlinx.android.synthetic.main.activity_image_to_video_convert.tvOutputPath
import kotlinx.android.synthetic.main.activity_image_to_video_convert.btnConvert
import kotlinx.android.synthetic.main.activity_image_to_video_convert.btnImagePath
import kotlinx.android.synthetic.main.activity_image_to_video_convert.edtSecond
import kotlinx.android.synthetic.main.activity_image_to_video_convert.mProgressView
import kotlinx.android.synthetic.main.activity_image_to_video_convert.tvInputPath
import kotlinx.android.synthetic.main.activity_image_to_video_convert.tvInputPath

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
                        Toast.makeText(
                            this,
                            getString(R.string.input_image_validate_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    TextUtils.isEmpty(edtSecond.text.toString().trim()) || edtSecond.text.toString()
                        .trim().toInt() == 0 -> {
                        Toast.makeText(
                            this,
                            getString(R.string.please_enter_second),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
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
                tvInputPath.text = mediaFiles[0]?.path
            } else {
                isFileSelected = false
                Toast.makeText(
                    this,
                    getString(R.string.image_not_selected_toast_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun createVideo() {
        processStart()
        val dir = File(getExternalFilesDir(OUT_PUT_DIR).toString())
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val dest = File(dir.path + File.separator + OUT_PUT_DIR + System.currentTimeMillis().div(1000L) + ".mp4")
        val outputPath = dest.absolutePath
        val size: ISize = SizeFromImage(tvInputPath.text.toString())
        val query = Extension.imageToVideo(
            tvInputPath.text.toString(),
            outputPath,
            edtSecond.text.toString().toInt(), size.width(), size.height()
        )

        Config.enableLogCallback { log ->
            tvOutputPath.text = log?.text
        }
        when (FFmpeg.execute(query)) {
            Config.RETURN_CODE_SUCCESS -> {
                runOnUiThread {
                    tvOutputPath.text = "Output Path : \n$outputPath"
                    processStop()
                }
            }
            Config.RETURN_CODE_CANCEL -> {
                processStop()
                FFmpeg.cancel()
            }
            else -> {
                processStop()
                Config.printLastCommandOutput(Log.INFO)
            }
        }
    }

    private fun processStop() {
        runOnUiThread {
            btnImagePath.isEnabled = true
            btnConvert.isEnabled = true
            mProgressView.visibility = View.GONE
        }
    }

    private fun processStart() {
        runOnUiThread {
            btnImagePath.isEnabled = false
            btnConvert.isEnabled = false
            mProgressView.visibility = View.VISIBLE
        }
    }
}