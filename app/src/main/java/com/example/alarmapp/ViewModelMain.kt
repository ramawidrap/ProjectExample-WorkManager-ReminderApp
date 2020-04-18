package com.example.alarmapp

import android.app.Application
import androidx.annotation.NonNull
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.PeriodicWorkRequest

class ViewModelMain(@NonNull app : Application) : AndroidViewModel(app) {

    private val periodicWorkRequestData = MutableLiveData<PeriodicWorkRequest>()


    fun post(periodicWorkRequest: PeriodicWorkRequest) {
        periodicWorkRequestData.postValue(periodicWorkRequest)
    }

    fun get() : LiveData<PeriodicWorkRequest> {
        return periodicWorkRequestData
    }

}