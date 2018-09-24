package com.foretree.praiserobot

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Process
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat

/**
 * Created by silen on 2018/9/24 1:14
 * Copyright (c) 2018 in FORETREE
 */
class PraiseNoticeService : Service() {
    private val mBinder: IPraiseRemoteService.Stub = object : IPraiseRemoteService.Stub() {
        override fun basicTypes(anInt: Int, aLong: Long, aBoolean: Boolean, aFloat: Float, aDouble: Double, aString: String?) {
            //do nothing
        }

        override fun getPid(): Int = Process.myPid()
    }

    override fun onCreate() {
        Log.d("==>", "onCreate")
        showNotification("好评君", "自动好评-点击打开设置页面")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("==>", "onStartCommand")
        showNotification("好评君", "自动好评-点击打开设置页面")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder = mBinder

    private fun showNotification(title: String, content: String) {
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "default"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(NotificationChannel(
                    channelId,
                    "android-praise-robot",
                    NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = ""
            })
        }
        //set notification
        val builder = NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(false)
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)
        notificationManager.notify(0, builder.build())
    }

}