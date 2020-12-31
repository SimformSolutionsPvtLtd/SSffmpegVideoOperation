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
import com.simform.videoimageeditor.utils.Paths
import java.io.File
import java.util.concurrent.CyclicBarrier
import kotlinx.android.synthetic.main.activity_combine_images.btnImagePath
import kotlinx.android.synthetic.main.activity_combine_images.edtSecond
import kotlinx.android.synthetic.main.activity_combine_images.tvInputPathImage
import kotlinx.android.synthetic.main.activity_combine_images.btnCombine
import kotlinx.android.synthetic.main.activity_combine_images.mProgressView
import kotlinx.android.synthetic.main.activity_combine_images.tvOutputPath

class CombineImagesActivity : BaseActivity(R.layout.activity_combine_images) {
    private var isImageSelected: Boolean = false
    override fun initialization() {
        btnImagePath.setOnClickListener(this)
        btnCombine.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnImagePath -> {
                Common.selectFile(this, maxSelection = 25, isImageSelection = true)
            }
            R.id.btnCombine -> {
                when {
                    !isImageSelected -> {
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
                        processStart()
                        val gate = CyclicBarrier(2)
                        val imageToVideo = object : Thread() {
                            override fun run() {
                                gate.await()
                                combineImagesProcess()
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
        when (requestCode) {
            Common.IMAGE_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles?.size != 0) {
                    val size: Int? = mediaFiles?.size
                    tvInputPathImage.text = "$size"+ (if (size == 1) " Image " else " Images ") + "selected"
                    isImageSelected = true
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.image_not_selected_toast_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun processStop() {
        runOnUiThread {
            btnImagePath.isEnabled = true
            btnCombine.isEnabled = true
            mProgressView.visibility = View.GONE
        }
    }

    private fun processStart() {
        runOnUiThread {
            btnImagePath.isEnabled = false
            btnCombine.isEnabled = false
            mProgressView.visibility = View.VISIBLE
        }
    }

    private fun combineImagesProcess() {
        val dir = File(getExternalFilesDir(OUT_PUT_DIR).toString())
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val dest = File(dir.path + File.separator + OUT_PUT_DIR + System.currentTimeMillis().div(1000L) + ".mp4")
        val outputPath = dest.absolutePath
        val pathsList = ArrayList<Paths>()
        mediaFiles?.let {
            for (element in it){
                val paths = Paths()
                paths.filePath = element.path
                paths.isImageFile = true
                pathsList.add(paths)
            }

            val combineQuery = Extension.combineImagesAndVideos(
                pathsList,
                640,
                480,
                edtSecond.text.toString(),
                outputPath
            )
            Config.enableLogCallback { log ->
                tvOutputPath.text = log?.text
            }
            when (FFmpeg.execute(combineQuery)) {
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

    }
}