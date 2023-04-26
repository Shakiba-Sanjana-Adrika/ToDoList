package com.example.to_dolist.utils

import android.content.Context
import android.content.Intent
import com.example.to_dolist.services.Receiver

object BroadcastMaker {
    fun sendBroadcast(context: Context) {
        val intent = Intent(context, Receiver::class.java)
        intent.action = Constants.PACKAGE_NAME
        context.sendBroadcast(intent)
    }
}