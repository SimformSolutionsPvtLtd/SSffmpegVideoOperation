package com.simform.videoimageeditor.mainActivity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.processActivity.*
import com.simform.videoimageeditor.utils.Common.getPermission
import com.simform.videoimageeditor.utils.Common.openActivity
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), View.OnClickListener,
    EasyPermissions.PermissionCallbacks {
    var allPermissionGranted = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        allPermissionGranted = getPermission(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    override fun onClick(v: View?) {
        if (allPermissionGranted) {
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
                    openActivity(this, MotionActivity())
                }
                R.id.btnReverseVideo -> {
                    openActivity(this, ReverseVideoActivity())
                }
            }
        } else {
            getPermission(this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        allPermissionGranted = true
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        getPermission(this)
    }
}