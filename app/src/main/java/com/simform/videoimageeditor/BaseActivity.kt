package com.simform.videoimageeditor

import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videooperations.Common
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
    val pickSingleMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val mediaType = when (contentResolver.getType(uri)?.substringBefore('/')) {
                    "image" -> MediaFile.TYPE_IMAGE
                    "video" -> MediaFile.TYPE_VIDEO
                    "audio" -> MediaFile.TYPE_AUDIO
                    else -> MediaFile.TYPE_FILE
                }

                val mediaFile = MediaFile().apply {
                    setUri(uri)
                    setMediaType(mediaType)
                    setMimeType(contentResolver.getType(uri))
                    setName(uri.lastPathSegment ?: "Unknown")
                }

                val requestCode = when (mediaType) {
                    MediaFile.TYPE_IMAGE -> Common.IMAGE_FILE_REQUEST_CODE
                    MediaFile.TYPE_VIDEO -> Common.VIDEO_FILE_REQUEST_CODE
                    MediaFile.TYPE_AUDIO -> Common.AUDIO_FILE_REQUEST_CODE
                    else -> Common.VIDEO_FILE_REQUEST_CODE // Default to video for unknown types
                }

                val mediaFiles = listOf(mediaFile)
                this.mediaFiles = mediaFiles
                (this as FileSelection).selectedFiles(mediaFiles, requestCode)
            } else {
                Toast.makeText(this, "User cancelled the selection", Toast.LENGTH_SHORT).show()
            }
        }

    val pickMultipleMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        if (uris.isNotEmpty()) {
            val mediaFiles = uris.map { uri ->
                val mediaType = when (contentResolver.getType(uri)?.substringBefore('/')) {
                    "image" -> MediaFile.TYPE_IMAGE
                    "video" -> MediaFile.TYPE_VIDEO
                    "audio" -> MediaFile.TYPE_AUDIO
                    else -> MediaFile.TYPE_FILE
                }

                MediaFile().apply {
                    setUri(uri)
                    setMediaType(mediaType)
                    setMimeType(contentResolver.getType(uri))
                    setName(uri.lastPathSegment ?: "Unknown")
                }
            }
            val requestCode = when (contentResolver.getType(mediaFiles.first().uri)?.substringBefore('/')) {
                "image" -> Common.IMAGE_FILE_REQUEST_CODE
                "video" -> Common.VIDEO_FILE_REQUEST_CODE
                "audio" -> Common.AUDIO_FILE_REQUEST_CODE
                else -> Common.VIDEO_FILE_REQUEST_CODE// Default to video for unknown types
            }
            this.mediaFiles = mediaFiles
            (this as FileSelection).selectedFiles(mediaFiles, requestCode)
        } else {
            Toast.makeText(this, "User cancelled the selection", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Content view will be set by individual activities using view binding
        initialization()
        utils.addSupportActionBar(this, toolbarTitle)
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
        if (resultCode == RESULT_OK && data != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            mediaFiles = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)
            (this as FileSelection).selectedFiles(mediaFiles,requestCode)
        }
    }
}