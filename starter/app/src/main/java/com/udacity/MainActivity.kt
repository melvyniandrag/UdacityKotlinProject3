package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.udacity.databinding.ActivityMainBinding
import com.udacity.receiver.AlarmReceiver
import android.Manifest
import java.lang.Exception
import java.util.LinkedList
import java.util.Queue

private val REQUEST_CODE = 0
private const val NOTIFICATION_ID = 0
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val downloadQueue: Queue<String> = LinkedList()

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var action: NotificationCompat.Action
    //private lateinit var notifyIntent : Intent

    //private var notifyPendingIntent: PendingIntent? = null

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
            if(binding.includedContent.radioGroup1.checkedRadioButtonId == -1){
                Toast.makeText(this, getString(R.string.nothing_selected), Toast.LENGTH_SHORT).show()
            } else {
                download(binding.includedContent.radioGroup1.checkedRadioButtonId)
            }
        }


        createChannel(
            getString(R.string.download_channel_id),
            getString(R.string.download_channel_name)
        )


    }


    private fun createChannel(channelId: String, channelName: String) {
        // TODO: Step 1.6 START create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description =
                getString(R.string.download_notification_channel_description)

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

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
                            try {
                                when (cursor.getInt(statusColIdx)) {

                                        DownloadManager.STATUS_SUCCESSFUL -> {
                                            Log.i("Download", "Download Success")

                                            val notifyIntent =
                                                Intent(context, DetailActivity::class.java).apply {
                                                    flags =
                                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                }.putExtra("Status", "Success")
                                                    .putExtra("Name", downloadQueue.remove())

                                            val notifyPendingIntent = PendingIntent.getActivity(
                                                context,
                                                REQUEST_CODE,
                                                notifyIntent,
                                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                            )

                                            val builder = NotificationCompat.Builder(
                                                context!!.applicationContext,
                                                getString(R.string.download_channel_id),
                                            )
                                                .setSmallIcon(R.drawable.download)
                                                .setContentTitle(getString(R.string.app_name))
                                                .setContentText("download successful")
                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                // Set the intent that fires when the user taps the notification.
                                                .setContentIntent(notifyPendingIntent)
                                                .setAutoCancel(true)

                                            with(NotificationManagerCompat.from(context)) {
                                                if (ActivityCompat.checkSelfPermission(
                                                        this@MainActivity,
                                                        Manifest.permission.POST_NOTIFICATIONS
                                                    ) != PackageManager.PERMISSION_GRANTED
                                                ) {
                                                    // TODO: Consider calling
                                                    // ActivityCompat#requestPermissions
                                                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)

                                                    return
                                                }
                                                notify(NOTIFICATION_ID, builder.build())
                                            }


                                        }
                                        DownloadManager.STATUS_FAILED -> {
                                            Log.i("Download", "Download Failed")

                                            val notifyIntent =
                                                Intent(context, DetailActivity::class.java).apply {
                                                    flags =
                                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                }.putExtra("Status", "Failed")
                                                    .putExtra("Name", downloadQueue.remove())

                                            val notifyPendingIntent = PendingIntent.getActivity(
                                                context,
                                                REQUEST_CODE,
                                                notifyIntent,
                                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                            )

                                            val builder = NotificationCompat.Builder(
                                                context!!.applicationContext,
                                                getString(R.string.download_channel_id),
                                            )
                                                .setSmallIcon(R.drawable.download)
                                                .setContentTitle(getString(R.string.app_name))
                                                .setContentText("Download failed")
                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                // Set the intent that fires when the user taps the notification.
                                                .setContentIntent(notifyPendingIntent)
                                                .setAutoCancel(true)

                                            with(NotificationManagerCompat.from(context)) {
                                                if (ActivityCompat.checkSelfPermission(
                                                        this@MainActivity,
                                                        Manifest.permission.POST_NOTIFICATIONS
                                                    ) != PackageManager.PERMISSION_GRANTED
                                                ) {
                                                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
                                                    return
                                                }
                                                // notificationId is a unique int for each notification that you must define.
                                                notify(NOTIFICATION_ID, builder.build())
                                            }

                                        }
                                        else -> Log.i(
                                                "Download",
                                                "Download status is neither success nor fail: " + cursor.getInt(
                                                    statusColIdx
                                                )
                                            )
                                }
                            } catch (e: Exception){
                                Log.e("Error", "download error")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun download(checked: Int) {
        Log.i("downloadCalled", "downloadCalled")

        downloadQueue.add(
            when (checked){
                R.id.radio1 -> getString(R.string.glide_radio_string)
                R.id.radio2 -> getString(R.string.udacity_radio_String)
                else -> getString(R.string.retrofit_radio_string)
            }
        )

        val toDownload = when (checked) {
            R.id.radio1 -> GLIDE_URL
            R.id.radio2 -> UDACITY_URL
            else -> RETROFIT_URL
        }
        val request =
            DownloadManager.Request(Uri.parse(toDownload))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        Log.i("Enqueue", "Enqueued " + downloadID.toString())
    }

    companion object {
        private const val GLIDE_URL = "https://github.com/bumptech/glide"

        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"

        private const val RETROFIT_URL = "https://github.com/square/retrofit"

    }
}