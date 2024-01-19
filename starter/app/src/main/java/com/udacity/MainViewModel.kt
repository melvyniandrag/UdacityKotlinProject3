package com.udacity

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.udacity.receiver.AlarmReceiver

class MainViewModel(private val app: Application) : AndroidViewModel(app) {
    private val REQUEST_CODE = 0
    private val notifyPendingIntent: PendingIntent
    private val notifyIntent = Intent(app, AlarmReceiver::class.java)

    init{
        notifyPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            REQUEST_CODE,
            notifyIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

}