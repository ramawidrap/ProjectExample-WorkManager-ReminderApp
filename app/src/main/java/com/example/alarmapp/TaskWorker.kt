package com.example.alarmapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.*
import io.reactivex.Scheduler

const val CHANNEL_ID = "123"
const val CHANNEL_NAME = "MyChannel"

class TaskWorker (context : Context, parameter : WorkerParameters) : Worker(context,parameter){
    companion object {
        private const val TAG = "TaskWorker2"
    }


    override fun doWork(): Result {
        taskNotification(inputData.getString("input"))
        return Result.success()

    }

    private fun taskNotification(content : String?) {
        val notifManager : NotificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT)
            notifManager.createNotificationChannel(notificationChannel)
        }

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID).setContentTitle("REMINDER !").setContentText(content).setSmallIcon(R.mipmap.ic_launcher)

        notifManager.notify(1,builder.build())


    }


}

//class TaskWorkerFirst (context : Context,parameter : WorkerParameters) : Worker(context,parameter){
//    companion object {
//        private const val TAG = "TaskWorker2"
//    }
//
//    override fun doWork(): Result {
//        val inputData = getInputData().getInt("jumlah",0)
//        Log.i(TAG, "input: ${inputData}");
//
//
//
//        val output = workDataOf("jumlah" to inputData+100)
//        return Result.success(output)
//
//    }
//
//
//
//
//}