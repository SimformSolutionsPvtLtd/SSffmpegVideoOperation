package com.simform.videoimageeditor

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.utils.Common
import com.simform.videoimageeditor.utils.FileSelection
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.runAsync
import pub.devrel.easypermissions.EasyPermissions

abstract class BaseActivity(view: Int) : AppCompatActivity(), EasyPermissions.PermissionCallbacks, View.OnClickListener, FileSelection {
    var layoutView = view
    var allPermissionGranted = false
    var height: Int? = 0
    var width: Int? = 0
    var mediaFiles: List<MediaFile>? = null
    var retriever: MediaMetadataRetriever? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutView)
        Common.addSupportActionBar(this)
        initialization()
    }

    protected abstract fun initialization()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        allPermissionGranted = true
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Common.getPermission(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            mediaFiles = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)
            (this as FileSelection).selectedFiles(mediaFiles,requestCode)
        }
    }
}