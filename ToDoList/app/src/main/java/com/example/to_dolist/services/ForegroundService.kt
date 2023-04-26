package com.example.to_dolist.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.to_dolist.R
import com.example.to_dolist.data.localDatabase.TodoListItem
import com.example.to_dolist.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ForegroundService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val jsonItem = intent?.extras?.get(Constants.TODO_ITEM_KEY) as String
        val type = object : TypeToken<TodoListItem>() {}.type
        val todo: TodoListItem = Gson().fromJson(jsonItem, type)
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        //configure notification channel

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationChannel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            mNotificationChannel.description = Constants.NOTIFICATION_CHANNEL_DESC
            mNotificationChannel.enableLights(true)
            mNotificationChannel.lightColor = Color.WHITE
            mNotificationManager.createNotificationChannel(mNotificationChannel)
            mNotificationChannel.canBypassDnd()
        }

        val notification =
            NotificationCompat.Builder(applicationContext, Constants.NOTIFICATION_CHANNEL_ID)
                .setContentTitle(todo.title)
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText(todo.todo.joinToString("\n")))
                .setSmallIcon(R.drawable.ic_alarm)
                .build()
        startForeground(Constants.FOREGROUND_SERVICE_ID, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}