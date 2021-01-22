package com.simform.videoimageeditor

import android.view.View
import com.simform.videoimageeditor.middlewareActivity.OtherFFMPEGProcessActivity
import com.simform.videoimageeditor.middlewareActivity.VideoProcessActivity
import com.simform.videoimageeditor.utils.utils.openActivity
import kotlinx.android.synthetic.main.activity_main.imageGifOperation
import kotlinx.android.synthetic.main.activity_main.videoOperation

class MainActivity : BaseActivity(R.layout.activity_main, R.string.ffpmeg_title) {
    override fun initialization() {
        supportActionBar?.title = getString(R.string.ffpmeg_title)
        videoOperation.setOnClickListener(this)
        imageGifOperation.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.videoOperation -> {
                openActivity(this, VideoProcessActivity())
            }
            R.id.imageGifOperation -> {
                openActivity(this, OtherFFMPEGProcessActivity())
            }
        }
    }

}