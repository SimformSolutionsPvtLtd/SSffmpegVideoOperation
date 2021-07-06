package com.simform.videoimageeditor.middlewareActivity

import android.view.View
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.videoProcessActivity.*
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
                utils.openActivity(this, CutVideoUsingTimeActivity())
            }
            R.id.btnImageToVideo -> {
                utils.openActivity(this, ImageToVideoConvertActivity())
            }
            R.id.btnAddWaterMarkOnVideo -> {
                utils.openActivity(this, AddWaterMarkOnVideoActivity())
            }
            R.id.btnCombineImageVideo -> {
                utils.openActivity(this, CombineImageAndVideoActivity())
            }
            R.id.btnCombineImages -> {
                utils.openActivity(this, CombineImagesActivity())
            }
            R.id.btnCombineVideos -> {
                utils.openActivity(this, CombineVideosActivity())
            }
            R.id.btnCompressVideo -> {
                utils.openActivity(this, CompressVideoActivity())
            }
            R.id.btnExtractVideo -> {
                utils.openActivity(this, ExtractImagesActivity())
            }
            R.id.btnExtractAudio -> {
                utils.openActivity(this, ExtractAudioActivity())
            }
            R.id.btnMotion -> {
                utils.openActivity(this, FastAndSlowVideoMotionActivity())
            }
            R.id.btnReverseVideo -> {
                utils.openActivity(this, ReverseVideoActivity())
            }
            R.id.btnFadeInFadeOutVideo -> {
                utils.openActivity(this, VideoFadeInFadeOutActivity())
            }
            R.id.btnVideoConvertIntoGIF -> {
                utils.openActivity(this, VideoToGifActivity())
            }
            R.id.btnVideoRotateFlip -> {
                utils.openActivity(this, VideoRotateFlipActivity())
            }
            R.id.btnMergeVideoAndAudio -> {
                utils.openActivity(this, MergeAudioVideoActivity())
            }
            R.id.btnAddTextOnVideo -> {
                utils.openActivity(this, AddTextOnVideoActivity())
            }
            R.id.btnRemoveAudioFromVideo -> {
                utils.openActivity(this, RemoveAudioFromVideoActivity())
            }
            R.id.btnMergeImageAndAudio -> {
                utils.openActivity(this, MergeImageAndMP3Activity())
            }
            R.id.btnSetAspectRatio -> {
                utils.openActivity(this, AspectRatioActivity())
            }
        }
    }
}