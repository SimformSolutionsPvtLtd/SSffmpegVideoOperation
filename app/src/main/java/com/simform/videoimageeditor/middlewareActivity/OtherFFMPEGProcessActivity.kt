package com.simform.videoimageeditor.middlewareActivity

import android.view.View
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.otherFFMPEGProcessActivity.AudiosMergeActivity
import com.simform.videoimageeditor.otherFFMPEGProcessActivity.ChangeAudioVolumeActivity
import com.simform.videoimageeditor.otherFFMPEGProcessActivity.CropAudioActivity
import com.simform.videoimageeditor.otherFFMPEGProcessActivity.FastAndSlowAudioActivity
import com.simform.videoimageeditor.otherFFMPEGProcessActivity.MergeGIFActivity
import com.simform.videoimageeditor.utils.utils.openActivity
import kotlinx.android.synthetic.main.activity_other_ffmpeg_process.btnAudiosVolumeUpdate
import kotlinx.android.synthetic.main.activity_other_ffmpeg_process.btnCutAudio
import kotlinx.android.synthetic.main.activity_other_ffmpeg_process.btnFastAndSlowAudio
import kotlinx.android.synthetic.main.activity_other_ffmpeg_process.btnMergeAudios
import kotlinx.android.synthetic.main.activity_other_ffmpeg_process.btnMergeGIF

class OtherFFMPEGProcessActivity : BaseActivity(R.layout.activity_other_ffmpeg_process, R.string.other_ffmpeg_operations) {
    override fun initialization() {
        supportActionBar?.title = getString(R.string.other_ffmpeg_operations)
        btnMergeGIF.setOnClickListener(this)
        btnMergeAudios.setOnClickListener(this)
        btnAudiosVolumeUpdate.setOnClickListener(this)
        btnFastAndSlowAudio.setOnClickListener(this)
        btnCutAudio.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnMergeGIF -> {
                openActivity(this, MergeGIFActivity())
            }
            R.id.btnMergeAudios -> {
                openActivity(this, AudiosMergeActivity())
            }
            R.id.btnAudiosVolumeUpdate -> {
                openActivity(this, ChangeAudioVolumeActivity())
            }
            R.id.btnFastAndSlowAudio -> {
                openActivity(this, FastAndSlowAudioActivity())
            }
            R.id.btnCutAudio -> {
                openActivity(this, CropAudioActivity())
            }
        }
    }

}