package com.simform.videoimageeditor.middlewareActivity

import android.view.View
import androidx.activity.enableEdgeToEdge
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.databinding.ActivityOtherFfmpegProcessBinding
import com.simform.videoimageeditor.otherFFMPEGProcessActivity.AudiosMergeActivity
import com.simform.videoimageeditor.otherFFMPEGProcessActivity.ChangeAudioVolumeActivity
import com.simform.videoimageeditor.otherFFMPEGProcessActivity.CompressAudioActivity
import com.simform.videoimageeditor.otherFFMPEGProcessActivity.CropAudioActivity
import com.simform.videoimageeditor.otherFFMPEGProcessActivity.FastAndSlowAudioActivity
import com.simform.videoimageeditor.otherFFMPEGProcessActivity.MergeGIFActivity
import com.simform.videoimageeditor.utils.enableEdgeToEdge

class OtherFFMPEGProcessActivity : BaseActivity(R.layout.activity_other_ffmpeg_process, R.string.other_ffmpeg_operations) {

    private lateinit var binding: ActivityOtherFfmpegProcessBinding

    override fun initialization() {
        binding = ActivityOtherFfmpegProcessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge(binding.toolbar.root)
        binding.toolbar.textTitle.text = getString(R.string.other_ffmpeg_operations)
        binding.apply {
            btnMergeGIF.setOnClickListener(this@OtherFFMPEGProcessActivity)
            btnMergeAudios.setOnClickListener(this@OtherFFMPEGProcessActivity)
            btnAudiosVolumeUpdate.setOnClickListener(this@OtherFFMPEGProcessActivity)
            btnFastAndSlowAudio.setOnClickListener(this@OtherFFMPEGProcessActivity)
            btnCutAudio.setOnClickListener(this@OtherFFMPEGProcessActivity)
            btnCompressAudio.setOnClickListener(this@OtherFFMPEGProcessActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnMergeGIF -> {
                utils.openActivity(this, MergeGIFActivity())
            }
            R.id.btnMergeAudios -> {
                utils.openActivity(this, AudiosMergeActivity())
            }
            R.id.btnAudiosVolumeUpdate -> {
                utils.openActivity(this, ChangeAudioVolumeActivity())
            }
            R.id.btnFastAndSlowAudio -> {
                utils.openActivity(this, FastAndSlowAudioActivity())
            }
            R.id.btnCutAudio -> {
                utils.openActivity(this, CropAudioActivity())
            }
            R.id.btnCompressAudio -> {
                utils.openActivity(this, CompressAudioActivity())
            }
        }
    }

}