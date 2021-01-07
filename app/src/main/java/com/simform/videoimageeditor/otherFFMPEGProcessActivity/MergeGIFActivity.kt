package com.simform.videoimageeditor.otherFFMPEGProcessActivity

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.arthenica.mobileffmpeg.LogMessage
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.utility.Common
import com.simform.videoimageeditor.utility.FFmpegCallBack
import com.simform.videoimageeditor.utility.FFmpegQueryExtension
import com.simform.videoimageeditor.utility.Paths
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CyclicBarrier
import kotlinx.android.synthetic.main.activity_add_water_mark_on_video.tvInputPathVideo
import kotlinx.android.synthetic.main.activity_merge_gif.edtXPos
import kotlinx.android.synthetic.main.activity_merge_gif.edtYPos
import kotlinx.android.synthetic.main.activity_merge_gif.tvOutputPath
import kotlinx.android.synthetic.main.activity_merge_gif.mProgressView
import kotlinx.android.synthetic.main.activity_merge_gif.btnGifPath
import kotlinx.android.synthetic.main.activity_merge_gif.btnMerge
import kotlinx.android.synthetic.main.activity_merge_gif.tvInputPathGif

class MergeGIFActivity : BaseActivity(R.layout.activity_merge_gif, R.string.merge_gif) {
    private var isInputGifSelected: Boolean = false
    override fun initialization() {
        btnGifPath.setOnClickListener(this)
        btnMerge.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnGifPath -> {
                Common.selectFile(this, maxSelection = 2, isImageSelection = true, isAudioSelection = false)
            }
            R.id.btnMerge -> {
                when {
                    !isInputGifSelected -> {
                        Toast.makeText(this, getString(R.string.input_gif_validate_message), Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(edtXPos.text.toString()) -> {
                        Toast.makeText(this, getString(R.string.x_position_validation), Toast.LENGTH_SHORT).show()
                    }
                    edtXPos.text.toString().toFloat() > 100 || edtXPos.text.toString().toFloat() <= 0 -> {
                        Toast.makeText(this, getString(R.string.x_validation_invalid), Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(edtYPos.text.toString()) -> {
                        Toast.makeText(this, getString(R.string.y_position_validation), Toast.LENGTH_SHORT).show()
                    }
                    edtYPos.text.toString().toFloat() > 100 || edtYPos.text.toString().toFloat() <= 0 -> {
                        Toast.makeText(this, getString(R.string.y_validation_invalid), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        processStart()
                        val gate = CyclicBarrier(2)
                        val imageToVideo = object : Thread() {
                            override fun run() {
                                gate.await()
                                combineGifProcess()
                            }
                        }
                        imageToVideo.start()
                        gate.await()
                    }
                }
            }
        }
    }

    private fun combineGifProcess() {
        val outputPath = Common.getFilePath(this, Common.GIF)
        val pathsList = ArrayList<Paths>()
        mediaFiles?.let {
            for (element in it) {
                val paths = Paths()
                paths.filePath = element.path
                paths.isImageFile = true
                pathsList.add(paths)
            }

            val xPos = width?.let { width ->
                (edtXPos.text.toString().toFloat().times(width)).div(100)
            }
            val yPos = height?.let { height ->
                (edtYPos.text.toString().toFloat().times(height)).div(100)
            }
            val query = FFmpegQueryExtension.mergeGIF(pathsList,xPos,yPos, outputPath)

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
    }

    private fun processStop() {
        runOnUiThread {
            btnGifPath.isEnabled = true
            btnMerge.isEnabled = true
            mProgressView.visibility = View.GONE
        }
    }

    private fun processStart() {
        btnGifPath.isEnabled = false
        btnMerge.isEnabled = false
        mProgressView.visibility = View.VISIBLE
    }

    @SuppressLint("NewApi")
    override fun selectedFiles(mediaFiles: List<MediaFile>?, requestCode: Int) {
        when (requestCode) {
            Common.IMAGE_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    val size: Int = mediaFiles.size
                    var isGifFile = true
                    for (i in 0 until size) {
                        if (File(mediaFiles[i].path).extension != "gif") {
                            isGifFile = false
                        }
                    }
                    if (size == 2 && isGifFile) {
                        width = 300
                        height = 300
                        tvInputPathGif.text = "$size GIF selected"
                        isInputGifSelected = true
                    } else if (size != 2) {
                        Toast.makeText(this, getString(R.string.please_selected_minimum_2_gif_file), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, getString(R.string.gif_extension_validation), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, getString(R.string.please_select_gif_file), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}