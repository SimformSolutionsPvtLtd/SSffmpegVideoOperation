package com.simform.videoimageeditor.middlewareActivity

import android.view.View
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.otherFFMPEGProcessActivity.MergeGIFActivity
import com.simform.videoimageeditor.utility.Common
import kotlinx.android.synthetic.main.activity_other_ffmpeg_process.btnMergeGIF

class OtherFFMPEGProcessActivity : BaseActivity(R.layout.activity_other_ffmpeg_process, R.string.other_ffmpeg_operations) {
    override fun initialization() {
        supportActionBar?.title = getString(R.string.other_ffmpeg_operations)
        btnMergeGIF.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnMergeGIF -> {
                Common.openActivity(this, MergeGIFActivity())
            }
        }
    }

}