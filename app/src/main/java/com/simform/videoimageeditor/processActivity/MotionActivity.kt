package com.simform.videoimageeditor.processActivity

import android.util.Log
import android.view.View
import android.widget.Toast
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.utils.Common
import com.simform.videoimageeditor.utils.Extension
import kotlinx.android.synthetic.main.activity_motion.*
import java.io.File
import java.util.concurrent.CyclicBarrier

class MotionActivity : BaseActivity(R.layout.activity_motion) {
    private var isInputVideoSelected: Boolean = false
    override fun initialization() {
        btnVideoPath.setOnClickListener(this)
        btnMotion.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnVideoPath -> {
                Common.selectFile(this, maxSelection = 1, isImageSelection = false)
            }
            R.id.btnMotion -> {
                when {
                    !isInputVideoSelected -> {
                        Toast.makeText(
                            this,
                            getString(R.string.input_video_validate_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        processStart()
                        val gate = CyclicBarrier(2)
                        val imageToVideo = object : Thread() {
                            override fun run() {
                                gate.await()
                                motionProcess()
                            }
                        }
                        imageToVideo.start()
                        gate.await()
                    }
                }
            }
        }
    }

    private fun motionProcess() {
        val dir = File(getExternalFilesDir(Common.OUT_PUT_DIR).toString())
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val dest = File(dir.path + File.separator + Common.OUT_PUT_DIR + System.currentTimeMillis().div(1000L) + ".mp4")
        val outputPath = dest.absolutePath
        var setpts=0.5
        var atempo=2.0
        if(!motionType.isChecked){
            setpts=0.5
            atempo=2.0
        }
        val query = Extension.videoMotion(tvInputPathVideo.text.toString(), outputPath, setpts, atempo)
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

    override fun selectedFiles(mediaFiles: List<MediaFile>?, requestCode: Int) {
        when (requestCode) {
            Common.VIDEO_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    tvInputPathVideo.text = mediaFiles[0].path
                    isInputVideoSelected = true
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.video_not_selected_toast_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun processStop() {
        runOnUiThread {
            btnVideoPath.isEnabled = true
            btnMotion.isEnabled = true
            mProgressView.visibility = View.GONE
        }
    }

    private fun processStart() {
        runOnUiThread {
            btnVideoPath.isEnabled = false
            btnMotion.isEnabled = false
            mProgressView.visibility = View.VISIBLE
        }
    }
}