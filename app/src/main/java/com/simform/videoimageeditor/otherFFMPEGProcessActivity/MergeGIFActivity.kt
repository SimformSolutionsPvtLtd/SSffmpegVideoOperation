package com.simform.videoimageeditor.otherFFMPEGProcessActivity

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.databinding.ActivityMergeGifBinding
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.Common
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.LogMessage
import com.simform.videooperations.Paths
import java.io.File


class MergeGIFActivity : BaseActivity(R.layout.activity_merge_gif, R.string.merge_gif) {
    private lateinit var binding: ActivityMergeGifBinding
    private var isInputGifSelected: Boolean = false
    
    override fun initialization() {
        binding = ActivityMergeGifBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.apply {
            btnGifPath.setOnClickListener(this@MergeGIFActivity)
            btnMerge.setOnClickListener(this@MergeGIFActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnGifPath -> {
                Common.selectFile(this, maxSelection = 2, isImageSelection = true, isAudioSelection = false)
            }
            R.id.btnMerge -> {
                with(binding) {
                    when {
                        !isInputGifSelected -> {
                            Toast.makeText(this@MergeGIFActivity, getString(R.string.input_gif_validate_message), Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(edtXPos.text.toString()) -> {
                            Toast.makeText(this@MergeGIFActivity, getString(R.string.x_position_validation), Toast.LENGTH_SHORT).show()
                        }
                        edtXPos.text.toString().toFloat() > 100 || edtXPos.text.toString().toFloat() <= 0 -> {
                            Toast.makeText(this@MergeGIFActivity, getString(R.string.x_validation_invalid), Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(edtYPos.text.toString()) -> {
                            Toast.makeText(this@MergeGIFActivity, getString(R.string.y_position_validation), Toast.LENGTH_SHORT).show()
                        }
                        edtYPos.text.toString().toFloat() > 100 || edtYPos.text.toString().toFloat() <= 0 -> {
                            Toast.makeText(this@MergeGIFActivity, getString(R.string.y_validation_invalid), Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(edtXScale.text.toString()) -> {
                            Toast.makeText(this@MergeGIFActivity, getString(R.string.x_width_validation), Toast.LENGTH_SHORT).show()
                        }
                        edtXScale.text.toString().toFloat() > 100 || edtXScale.text.toString().toFloat() <= 0 -> {
                            Toast.makeText(this@MergeGIFActivity, getString(R.string.x_width_invalid), Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(edtYScale.text.toString()) -> {
                            Toast.makeText(this@MergeGIFActivity, getString(R.string.y_height_validation), Toast.LENGTH_SHORT).show()
                        }
                        edtYScale.text.toString().toFloat() > 100 || edtYScale.text.toString().toFloat() <= 0 -> {
                            Toast.makeText(this@MergeGIFActivity, getString(R.string.y_height_invalid), Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            processStart()
                            combineGifProcess()
                        }
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
                (binding.edtXPos.text.toString().toFloat().times(width)).div(100)
            }
            val yPos = height?.let { height ->
                (binding.edtYPos.text.toString().toFloat().times(height)).div(100)
            }

            val widthScale = width?.let { width ->
                (binding.edtXScale.text.toString().toFloat().times(width)).div(100)
            }
            val heightScale = height?.let { height ->
                (binding.edtYScale.text.toString().toFloat().times(height)).div(100)
            }
            val query = ffmpegQueryExtension.mergeGIF(pathsList, xPos, yPos, widthScale, heightScale, outputPath)

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

    private fun processStop() {
        binding.apply {
            btnGifPath.isEnabled = true
            btnMerge.isEnabled = true
            mProgressView.root.visibility = View.GONE
        }
    }

    private fun processStart() {
        binding.apply {
            btnGifPath.isEnabled = false
            btnMerge.isEnabled = false
            mProgressView.root.visibility = View.VISIBLE
        }
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
                        Glide.with(this)
                            .asGif()
                            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                            .load(mediaFiles[0].path)
                            .into(object : ViewTarget<ImageView?, GifDrawable?>(binding.mFirstGif) {
                                override fun onResourceReady(gifDrawable: GifDrawable, transition: Transition<in GifDrawable?>?) {
                                    width = gifDrawable.intrinsicWidth
                                    height = gifDrawable.intrinsicHeight
                                }
                            })
                        binding.tvInputPathGif.text = "$size GIF selected"
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