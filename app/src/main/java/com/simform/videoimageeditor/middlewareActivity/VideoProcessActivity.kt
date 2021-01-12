package com.simform.videoimageeditor.middlewareActivity

import android.view.View
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.videoProcessActivity.*
import com.simform.videoimageeditor.utility.Common.openActivity
import kotlinx.android.synthetic.main.activity_video_process.*

/**
 * Created by Ashvin Vavaliya on 29,December,2020
 * Simform Solutions Pvt Ltd.
 */
class VideoProcessActivity : BaseActivity(R.layout.activity_video_process, R.string.video_operations) {
    override fun initialization() {
        supportActionBar?.title = getString(R.string.video_operations)
        btnCutVideo.setOnClickListener(this)
        btnImageToVideo.setOnClickListener(this)
        btnAddWaterMarkOnVideo.setOnClickListener(this)
        btnCombineImageVideo.setOnClickListener(this)
        btnCombineImages.setOnClickListener(this)
        btnCombineVideos.setOnClickListener(this)
        btnCompressVideo.setOnClickListener(this)
        btnExtractVideo.setOnClickListener(this)
        btnExtractAudio.setOnClickListener(this)
        btnMotion.setOnClickListener(this)
        btnReverseVideo.setOnClickListener(this)
        btnFadeInFadeOutVideo.setOnClickListener(this)
        btnVideoConvertIntoGIF.setOnClickListener(this)
        btnVideoRotateFlip.setOnClickListener(this)
        btnMergeVideoAndAudio.setOnClickListener(this)
        btnAddTextOnVideo.setOnClickListener(this)
        btnRemoveAudioFromVideo.setOnClickListener(this)
        btnMergeImageAndAudio.setOnClickListener(this)
        btnSetAspectRatio.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnCutVideo -> {
                openActivity(this, CutVideoUsingTimeActivity())
            }
            R.id.btnImageToVideo -> {
                openActivity(this, ImageToVideoConvertActivity())
            }
            R.id.btnAddWaterMarkOnVideo -> {
                openActivity(this, AddWaterMarkOnVideoActivity())
            }
            R.id.btnCombineImageVideo -> {
                openActivity(this, CombineImageAndVideoActivity())
            }
            R.id.btnCombineImages -> {
                openActivity(this, CombineImagesActivity())
            }
            R.id.btnCombineVideos -> {
                openActivity(this, CombineVideosActivity())
            }
            R.id.btnCompressVideo -> {
                openActivity(this, CompressVideoActivity())
            }
            R.id.btnExtractVideo -> {
                openActivity(this, ExtractImagesActivity())
            }
            R.id.btnExtractAudio -> {
                openActivity(this, ExtractAudioActivity())
            }
            R.id.btnMotion -> {
                openActivity(this, FastAndSlowVideoMotionActivity())
            }
            R.id.btnReverseVideo -> {
                openActivity(this, ReverseVideoActivity())
            }
            R.id.btnFadeInFadeOutVideo -> {
                openActivity(this, VideoFadeInFadeOutActivity())
            }
            R.id.btnVideoConvertIntoGIF -> {
                openActivity(this, VideoToGifActivity())
            }
            R.id.btnVideoRotateFlip -> {
                openActivity(this, VideoRotateFlipActivity())
            }
            R.id.btnMergeVideoAndAudio -> {
                openActivity(this, MergeAudioVideoActivity())
            }
            R.id.btnAddTextOnVideo -> {
                openActivity(this, AddTextOnVideoActivity())
            }
            R.id.btnRemoveAudioFromVideo -> {
                openActivity(this, RemoveAudioFromVideoActivity())
            }
            R.id.btnMergeImageAndAudio -> {
                openActivity(this, MergeImageAndMP3Activity())
            }
            R.id.btnSetAspectRatio -> {
                openActivity(this, AspectRatioActivity())
            }
        }
    }
}