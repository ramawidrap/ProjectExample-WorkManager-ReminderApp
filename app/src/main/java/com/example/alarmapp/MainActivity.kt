package com.example.alarmapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.work.*
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var taskTwo: OneTimeWorkRequest




    private lateinit var workManager: WorkManager

    private lateinit var periodicTask: PeriodicWorkRequest

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        workManager = WorkManager.getInstance()

        button.setOnClickListener {
            val setTime = setTimeNotif(timePcker.hour,timePcker.minute)
            periodicTask = PeriodicWorkRequestBuilder<TaskWorker>(setTime, TimeUnit.MINUTES).setInputData(workDataOf("input" to setTextReminder.text)).addTag("periodicTask").build()
            workManager.enqueueUniquePeriodicWork("periodicTask", ExistingPeriodicWorkPolicy.KEEP, periodicTask)
            setTextReminder.text.clear()
            flagText.visibility = View.VISIBLE
        }


        workManager.getWorkInfoByIdLiveData(periodicTask.id).observe(this,
            Observer<WorkInfo> { t ->
                if (t!!.state.isFinished) {
                    flagText.text = ""
                    flagText.visibility = View.INVISIBLE
                    workManager.cancelUniqueWork("periodicTask")
                }
            })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setTimeNotif(hourPicker: Int, minutePicker : Int): Long {


        val currentDateTime = LocalDateTime.now()
        val time = LocalDateTime.parse(currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm")))
        var hourSet = hourPicker - time.hour
        var minuteSet = minutePicker - time.minute
        if (minuteSet < 0) {
            minuteSet = 60 - (Math.abs(minuteSet))
        }

        if (hourSet < 0) {
            hourSet = 12 - (Math.abs(hourSet))
        }

        return  (60 * hourSet + minuteSet).toLong()


    }


}
