package com.simform.videoimageeditor.otherFFMPEGProcessActivity

import android.annotation.SuppressLint
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videoimageeditor.BaseActivity
import com.simform.videoimageeditor.R
import com.simform.videoimageeditor.databinding.ActivityCropAudioBinding
import com.simform.videoimageeditor.ikovac.timepickerwithseconds.MyTimePickerDialog
import com.simform.videooperations.CallBackOfQuery
import com.simform.videooperations.Common
import com.simform.videooperations.Common.stringForTime
import com.simform.videooperations.FFmpegCallBack
import com.simform.videooperations.LogMessage
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CropAudioActivity : BaseActivity(R.layout.activity_crop_audio, R.string.crop_audio_using_time) {
    private var startTimeString: String? = null
    private var endTimeString: String? = null
    private var maxTimeString: String? = null
    private lateinit var binding: ActivityCropAudioBinding

    override fun initialization() {
        binding = ActivityCropAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnAudioPath.setOnClickListener(this@CropAudioActivity)
            btnSelectStartTime.setOnClickListener(this@CropAudioActivity)
            btnSelectEndTime.setOnClickListener(this@CropAudioActivity)
            btnConvert.setOnClickListener(this@CropAudioActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAudioPath -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pickAudio.launch(arrayOf("audio/*"))
                } else {
                    Common.selectFile(
                        this,
                        maxSelection = 1,
                        isImageSelection = false,
                        isAudioSelection = true
                    )
                }
            }
            R.id.btnSelectStartTime -> {
                if (!TextUtils.isEmpty(maxTimeString) && !TextUtils.equals(maxTimeString, getString(R.string.zero_time))) {
                    selectTime(binding.edtStartTime, true)
                } else {
                    Toast.makeText(this, getString(R.string.input_audio_validate_message), Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btnSelectEndTime -> {
                if (!TextUtils.isEmpty(maxTimeString) && !TextUtils.equals(maxTimeString, getString(R.string.zero_time))) {
                    selectTime(binding.edtEndTime, false)
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
                binding.tvInputPath.text =
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Common.saveFileToTempAndGetPath(this, mediaFiles[0].uri)
                    } else {
                        mediaFiles[0].path ?: ""
                    }
                maxTimeString =
                    stringForTime(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Common.getDurationFromFile(File(binding.tvInputPath.text.toString()))
                    } else {
                        mediaFiles[0].duration
                    }
                )
                binding.tvMaxTime.text = "Selected audio max time : $maxTimeString"
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
        val query = ffmpegQueryExtension.cutAudio(binding.tvInputPath.text.toString(), startTimeString, endTimeString, outputPath)
        CallBackOfQuery().callQuery(query, object : FFmpegCallBack {
            override fun process(logMessage: LogMessage) {
                binding.tvOutputPath.text = logMessage.text
            }

            override fun success() {
                binding.tvOutputPath.text = String.format(getString(R.string.output_path), outputPath)
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
        binding.apply {
            btnAudioPath.isEnabled = true
            btnSelectStartTime.isEnabled = true
            btnSelectEndTime.isEnabled = true
            btnConvert.isEnabled = true
            mProgressView.root.visibility = View.GONE
        }
    }

    private fun processStart() {
        binding.apply {
            btnAudioPath.isEnabled = false
            btnSelectStartTime.isEnabled = false
            btnSelectEndTime.isEnabled = false
            btnConvert.isEnabled = false
            mProgressView.root.visibility = View.VISIBLE
        }
    }
}