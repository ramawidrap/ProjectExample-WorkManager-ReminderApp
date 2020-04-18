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
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var viewModelMain: ViewModelMain


    private lateinit var workManager: WorkManager


    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModelMain = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)).get(ViewModelMain::class.java)

        workManager = WorkManager.getInstance()

        timePcker.setOnTimeChangedListener { view, hourOfDay, minute ->

            val setTime = setTimeNotif(hourOfDay, minute)
            Toast.makeText(this.applicationContext,setTime.toString(),Toast.LENGTH_LONG).show()
            val periodicTask = PeriodicWorkRequestBuilder<TaskWorker>(
                setTime,
                TimeUnit.MINUTES
            ).setInputData(workDataOf("input" to setTextReminder.text.toString())).addTag("periodicTask").build()
            workManager.enqueueUniquePeriodicWork("periodicTask", ExistingPeriodicWorkPolicy.KEEP, periodicTask)
            viewModelMain.post(periodicTask)
        }


        viewModelMain.get().observe(this, Observer {
            Log.i(TAG, "periodicwork: $it");
            workManager.getWorkInfoByIdLiveData(it.id).observe(this, Observer { workInfo ->
                Log.i(TAG, "cek work info: $workInfo");
                if (workInfo!!.state.isFinished) {
                    flagText.text = ""
                    flagText.visibility = View.INVISIBLE
                }
            })
        })


    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setTimeNotif(hourPicker: Int, minutePicker: Int): Long {


        val currentDateTime = LocalDateTime.now()
        val time = currentDateTime.format(DateTimeFormatter.ofPattern("hh:mm")).split(":")
        Log.i(TAG, "setTime: $time $hourPicker" );
        var hourSet = hourPicker - time[0].toInt()
        Log.i(TAG, "timenow: ${time[0]}");
        Log.i(TAG, "test: $hourSet");
        var minuteSet = minutePicker - time[1].toInt()
        if (minuteSet < 0) {
            minuteSet = 60 - (Math.abs(minuteSet))
        }

        if (hourSet < 0) {
            hourSet = 12 - (Math.abs(hourSet))
        }

        if(hourSet > 12 ) {
            hourSet -= 12
        }

        Log.i(TAG, "hourset: $hourSet");
        Log.i(TAG, "minuteset: $minuteSet ");

        return (60 * hourSet + minuteSet).toLong()


    }


}
