package com.simform.videoimageeditor.videoProcessActivity

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
import com.simform.videooperations.Common.TIME_FORMAT
import com.simform.videooperations.Common.VIDEO_FILE_REQUEST_CODE
import com.simform.videooperations.Common.stringForTime
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.LogMessage
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.android.synthetic.main.activity_cut_video_using_time.btnConvert
import kotlinx.android.synthetic.main.activity_cut_video_using_time.btnSelectEndTime
import kotlinx.android.synthetic.main.activity_cut_video_using_time.btnSelectStartTime
import kotlinx.android.synthetic.main.activity_cut_video_using_time.btnVideoPath
import kotlinx.android.synthetic.main.activity_cut_video_using_time.edtEndTime
import kotlinx.android.synthetic.main.activity_cut_video_using_time.edtStartTime
import kotlinx.android.synthetic.main.activity_cut_video_using_time.mProgressView
import kotlinx.android.synthetic.main.activity_cut_video_using_time.tvInputPath
import kotlinx.android.synthetic.main.activity_cut_video_using_time.tvMaxTime
import kotlinx.android.synthetic.main.activity_cut_video_using_time.tvOutputPath


class CutVideoUsingTimeActivity : BaseActivity(R.layout.activity_cut_video_using_time, R.string.cut_video_using_time) {
    private var startTimeString: String? = null
    private var endTimeString: String? = null
    private var maxTimeString: String? = null

    override fun initialization() {
        btnVideoPath.setOnClickListener(this)
        btnSelectStartTime.setOnClickListener(this)
        btnSelectEndTime.setOnClickListener(this)
        btnConvert.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnVideoPath -> {
                Common.selectFile(this, maxSelection = 1, isImageSelection = false, isAudioSelection = false)
            }
            R.id.btnSelectStartTime -> {
                if (!TextUtils.isEmpty(maxTimeString) && !TextUtils.equals(maxTimeString, getString(R.string.zero_time))) {
                    selectTime(edtStartTime, true)
                } else {
                    Toast.makeText(this, getString(R.string.input_video_validate_message), Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btnSelectEndTime -> {
                if (!TextUtils.isEmpty(maxTimeString) && !TextUtils.equals(maxTimeString, getString(R.string.zero_time))) {
                    selectTime(edtEndTime, false)
                } else {
                    Toast.makeText(this, getString(R.string.input_video_validate_message), Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btnConvert -> {
                when {
                    TextUtils.isEmpty(maxTimeString) -> {
                        Toast.makeText(this, getString(R.string.input_video_validate_message), Toast.LENGTH_SHORT).show()
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
        if (requestCode == VIDEO_FILE_REQUEST_CODE) {
            if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                tvInputPath.text = mediaFiles[0].path
                maxTimeString = stringForTime(mediaFiles[0].duration)
                tvMaxTime.text = "Selected video max time : $maxTimeString"
            } else {
                Toast.makeText(this, getString(R.string.video_not_selected_toast_message), Toast.LENGTH_SHORT).show()
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
            val time1: Date = SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH).parse(getString(R.string.zero_time))
            val time2: Date = SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH).parse(maxTimeString)
            val sTime: Date = SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH).parse(selectedTime)
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
            val time1: Date = SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH).parse(startTimeString)
            val time2: Date = SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH).parse(endTimeString)
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
        val outputPath = Common.getFilePath(this, Common.VIDEO)
        val query = ffmpegQueryExtension.cutVideo(tvInputPath.text.toString(), startTimeString, endTimeString, outputPath)
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
        btnVideoPath.isEnabled = true
        btnSelectStartTime.isEnabled = true
        btnSelectEndTime.isEnabled = true
        btnConvert.isEnabled = true
        mProgressView.visibility = View.GONE
    }

    private fun processStart() {
        btnVideoPath.isEnabled = false
        btnSelectStartTime.isEnabled = false
        btnSelectEndTime.isEnabled = false
        btnConvert.isEnabled = false
        mProgressView.visibility = View.VISIBLE
    }
}