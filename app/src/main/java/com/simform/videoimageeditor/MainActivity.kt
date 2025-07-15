package com.simform.videoimageeditor

import android.view.View
import androidx.activity.enableEdgeToEdge
import com.simform.videoimageeditor.databinding.ActivityMainBinding
import com.simform.videoimageeditor.middlewareActivity.OtherFFMPEGProcessActivity
import com.simform.videoimageeditor.middlewareActivity.VideoProcessActivity
import com.simform.videoimageeditor.utils.enableEdgeToEdge

class MainActivity : BaseActivity(R.layout.activity_main, R.string.ffpmeg_title) {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun initialization() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge(binding.toolbar.root)
        binding.toolbar.textTitle.text = getString(R.string.ffpmeg_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        binding.apply {
            videoOperation.setOnClickListener(this@MainActivity)
            imageGifOperation.setOnClickListener(this@MainActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.videoOperation -> {
                utils.openActivity(this, VideoProcessActivity())
            }
            R.id.imageGifOperation -> {
                utils.openActivity(this, OtherFFMPEGProcessActivity())
            }
        }
    }

}