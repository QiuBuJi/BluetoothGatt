package com.example.bluetoothgatt

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class ScheduleReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d(TAG, "onReceive: Boot Complete")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Log.d(TAG, "onReceive: sdk>=O")
                    val service =
                        context.startForegroundService(Intent(context, ScheduleService::class.java))
                    Log.d(TAG, "onReceive: service: $service")
                } else {
                    Log.d(TAG, "onReceive: sdk<O")
                    context.startService(Intent(context, ScheduleService::class.java))
                }

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

                Log.d(TAG, "onReceive: receive end...")
            }
            else -> {
                Log.d(TAG, "nothingElse")
                context.startService(Intent(context, ScheduleService::class.java))
            }
        }
    }
}