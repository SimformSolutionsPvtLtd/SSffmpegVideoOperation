package com.simform.videoimageeditor.otherFFMPEGProcessActivity

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.ikovac.timepickerwithseconds.MyTimePickerDialog
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.Common
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.FFmpegQueryExtension
import com.simform.videooperations.LogMessage
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.android.synthetic.main.activity_crop_audio.btnAudioPath
import kotlinx.android.synthetic.main.activity_crop_audio.btnConvert
import kotlinx.android.synthetic.main.activity_crop_audio.btnSelectEndTime
import kotlinx.android.synthetic.main.activity_crop_audio.btnSelectStartTime
import kotlinx.android.synthetic.main.activity_crop_audio.edtEndTime
import kotlinx.android.synthetic.main.activity_crop_audio.edtStartTime
import kotlinx.android.synthetic.main.activity_crop_audio.mProgressView
import kotlinx.android.synthetic.main.activity_crop_audio.tvInputPath
import kotlinx.android.synthetic.main.activity_crop_audio.tvMaxTime
import kotlinx.android.synthetic.main.activity_crop_audio.tvOutputPath

class CropAudioActivity : BaseActivity(R.layout.activity_crop_audio, R.string.crop_audio_using_time) {
    private var startTimeString: String? = null
    private var endTimeString: String? = null
    private var maxTimeString: String? = null

    override fun initialization() {
        btnAudioPath.setOnClickListener(this)
        btnSelectStartTime.setOnClickListener(this)
        btnSelectEndTime.setOnClickListener(this)
        btnConvert.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAudioPath -> {
                Common.selectFile(this, maxSelection = 1, isImageSelection = false, isAudioSelection = true)
            }
            R.id.btnSelectStartTime -> {
                if (!TextUtils.isEmpty(maxTimeString) && !TextUtils.equals(maxTimeString, getString(R.string.zero_time))) {
                    selectTime(edtStartTime, true)
                } else {
                    Toast.makeText(this, getString(R.string.input_audio_validate_message), Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btnSelectEndTime -> {
                if (!TextUtils.isEmpty(maxTimeString) && !TextUtils.equals(maxTimeString, getString(R.string.zero_time))) {
                    selectTime(edtEndTime, false)
                } else {
                    Toast.makeText(this, getString(R.string.input_audio_validate_message), Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btnConvert -> {
                when {
                    TextUtils.isEmpty(maxTimeString) -> {
                        Toast.makeText(this, getString(R.string.input_audio_validate_message), Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(startTimeString) -> {
                        Toast.makeText(this, getString(R.string.start_time_validate_message), Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(endTimeString) -> {
                        Toast.makeText(this, getString(R.string.end_time_validate_message), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        if (isValidation()) {
                            processStart()
                            cutProcess()
                        } else {
                            Toast.makeText(this, getString(R.string.start_time_end_time_validation_message), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("NewApi")
    override fun selectedFiles(mediaFiles: List<MediaFile>?, requestCode: Int) {
        if (requestCode == Common.AUDIO_FILE_REQUEST_CODE) {
            if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                tvInputPath.text = mediaFiles[0].path
                maxTimeString = Common.stringForTime(mediaFiles[0].duration)
                tvMaxTime.text = "Selected audio max time : $maxTimeString"
            } else {
                Toast.makeText(this, getString(R.string.audio_not_selected_toast_message), Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun selectTime(tvTime: TextView, isStartTime: Boolean) {
        MyTimePickerDialog(this, { _, hourOfDay, minute, seconds ->
            val selectedTime = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", seconds)
            if (isSelectedTimeValid(selectedTime)) {
                tvTime.text = selectedTime
                if (isStartTime) {
                    startTimeString = selectedTime
                } else {
                    endTimeString = selectedTime
                }
            } else {
                Toast.makeText(this, getString(R.string.time_range_validate_message), Toast.LENGTH_SHORT).show()
            }
        }, 0, 0, 0, true).show()
    }

    private fun isSelectedTimeValid(selectedTime: String?): Boolean {
        var isBetween = false
        try {
            val time1: Date = SimpleDateFormat(Common.TIME_FORMAT, Locale.ENGLISH).parse(getString(R.string.zero_time))
            val time2: Date = SimpleDateFormat(Common.TIME_FORMAT, Locale.ENGLISH).parse(maxTimeString)
            val sTime: Date = SimpleDateFormat(Common.TIME_FORMAT, Locale.ENGLISH).parse(selectedTime)
            if (time1.before(sTime) && time2.after(sTime)) {
                isBetween = true
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return isBetween
    }

    private fun isValidation(): Boolean {
        var isBetween = false
        try {
            val time1: Date = SimpleDateFormat(Common.TIME_FORMAT, Locale.ENGLISH).parse(startTimeString)
            val time2: Date = SimpleDateFormat(Common.TIME_FORMAT, Locale.ENGLISH).parse(endTimeString)
            if (time1.before(time2)) {
                isBetween = true
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return isBetween
    }

    @SuppressLint("SetTextI18n")
    private fun cutProcess() {
        val outputPath = Common.getFilePath(this, Common.MP3)
        val query = ffmpegQueryExtension.cutAudio(tvInputPath.text.toString(), startTimeString, endTimeString, outputPath)
        CallBackOfQuery().callQuery(query, object : FFmpegCallBack {
            override fun process(logMessage: LogMessage) {
                tvOutputPath.text = logMessage.text
            }

            override fun success() {
                tvOutputPath.text = String.format(getString(R.string.output_path), outputPath)
                processStop()
            }

            override fun cancel() {
                processStop()
            }

            override fun failed() {
                processStop()
            }
        })
    }

    private fun processStop() {
        btnAudioPath.isEnabled = true
        btnSelectStartTime.isEnabled = true
        btnSelectEndTime.isEnabled = true
        btnConvert.isEnabled = true
        mProgressView.visibility = View.GONE
    }

    private fun processStart() {
        btnAudioPath.isEnabled = false
        btnSelectStartTime.isEnabled = false
        btnSelectEndTime.isEnabled = false
        btnConvert.isEnabled = false
        mProgressView.visibility = View.VISIBLE
    }
}