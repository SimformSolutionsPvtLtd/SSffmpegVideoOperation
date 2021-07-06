package com.simform.videoimageeditor

import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videooperations.FFmpegQueryExtension
import com.simform.videooperations.FileSelection

/**
 * Created by Ashvin Vavaliya on 29,December,2020
 * Simform Solutions Pvt Ltd.
 */
abstract class BaseActivity(view: Int, title: Int) : AppCompatActivity(), View.OnClickListener, FileSelection {
    private var layoutView = view
    var toolbarTitle: Int = title
    var height: Int? = 0
    var width: Int? = 0
    var mediaFiles: List<MediaFile>? = null
    var retriever: MediaMetadataRetriever? = null
    val utils = Utils()
    val ffmpegQueryExtension = FFmpegQueryExtension()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutView)
        utils.addSupportActionBar(this, toolbarTitle)
        initialization()
    }

    protected abstract fun initialization()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            mediaFiles = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)
            (this as FileSelection).selectedFiles(mediaFiles,requestCode)
        }
    }
}