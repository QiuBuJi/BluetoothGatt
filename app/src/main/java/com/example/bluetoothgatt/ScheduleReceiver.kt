package com.example.bluetoothgatt

import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class ScheduleReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d(TAG, "bootComplete")

                val launchIntentForPackage =
                    context.packageManager.getLaunchIntentForPackage("com.example.bluetoothgatt")
                context.startActivity(launchIntentForPackage)
                context.startService(Intent(context, ScheduleService::class.java))

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    Log.d(TAG, "onReceive: sdk>=O")
//                    val service =
////                        context.startService(Intent(context, ScheduleService::class.java))
//                        context.startForegroundService(Intent(context, ScheduleService::class.java))
//                    Log.d(TAG, "onReceive: service: $service")
//                } else {
//                    Log.d(TAG, "onReceive: sdk<O")
//                    context.startService(Intent(context, ScheduleService::class.java))
//                }


                context.startActivity(Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    Log.d(TAG, "onReceive: start activity")
                })
//
//                val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//                val intent = Intent(context, ScheduleService::class.java)
//                val pendingIntent =
//                    PendingIntent.getService(
//                        context,
//                        0,
//                        intent,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                    )
//                alarm.set(AlarmManager.RTC_WAKEUP, 1000, pendingIntent)

                Log.d(TAG, "onReceive: receive complete")
            }
            else -> {
                Log.d(TAG, "nothingElse")
                context.startService(Intent(context, ScheduleService::class.java))
            }
        }
    }
}