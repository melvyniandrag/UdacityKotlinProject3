package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if(Build.VERSION.SDK_INT < 33) {
            registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        } else{
            registerReceiver(
                receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                RECEIVER_EXPORTED
            )
        }

        // TODO: Implement code below
        binding.includedContent.customButton.setOnClickListener{
            download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i("onReceive", intent?.action.toString() ?: "null intent")
            if(intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if(id != null) {
                    val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    val query = DownloadManager.Query()
                    query.setFilterById(id)
                    val cursor = downloadManager.query(query)
                    if(cursor.moveToFirst()) {
                        val statusColIdx = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        if(statusColIdx < 0){
                            Log.i("Download" , "Bad col idx")
                        }else {
                            when (cursor.getInt(statusColIdx)) {
                                DownloadManager.STATUS_SUCCESSFUL -> Log.i(
                                    "Download",
                                    "Download Success"
                                )

                                DownloadManager.STATUS_FAILED -> Log.i("Download", "Download Failed")
                                else -> Log.i(
                                    "Download",
                                    "Download status is neither success nor fail: " + cursor.getInt(statusColIdx)
                                    )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun download() {
        Log.i("downloadCalled", "downloadCalled")
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        Log.i("Enqueue", "Enqueued " + downloadID.toString())
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }
}