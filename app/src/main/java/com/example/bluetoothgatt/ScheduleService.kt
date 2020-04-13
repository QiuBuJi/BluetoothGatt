package com.example.bluetoothgatt

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class ScheduleService : Service() {

    companion object {
        var count: Int = 0
        var stopCount = false
    }

    private val NOTIFICATION_ID_Int = android.os.Process.myPid()
    private val NOTIFICATION_ID = NOTIFICATION_ID_Int.toString()

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate")

        startForegroundService()

        Thread {
            while (true) {
                count++
                Log.d(TAG, "schedule service: $count")
                Thread.sleep(1000)
            }
        }.start()
    }


    private fun startForegroundService() {
        val intent = Intent(this, ScheduleActivity::class.java)
        val mainIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(this, NOTIFICATION_ID)
            .setContentTitle("定时服务后台运行...")
            .setContentText("您的定时任务")
            .setTicker("定时服务启动...")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setWhen(System.currentTimeMillis())
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(mainIntent)
            .setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
            .build()
        notification.flags = Notification.FLAG_AUTO_CANCEL

        channel()

        startForeground(1, notification)
    }


    private fun channel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importanceLow = NotificationManager.IMPORTANCE_NONE
            val channel = NotificationChannel(NOTIFICATION_ID, "Schedule", importanceLow)
                .apply {
                    enableLights(true)
                    lightColor = Color.BLUE
                    enableVibration(true)
                    lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "Service onBind")


        return null
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service onStartCommand: ")

        val mainIntent = Intent(this, ScheduleActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(mainIntent)
            Log.d(TAG, "onReceive: start activity")
        } catch (e: Exception) {
            Log.d(TAG, "onReceive: startActivity error!")
            e.printStackTrace()
        }

        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service onDestroy")
    }
}