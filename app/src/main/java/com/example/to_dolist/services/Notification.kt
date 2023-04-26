package com.example.to_dolist.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Build
import com.example.to_dolist.data.localDatabase.TodoListItem
import com.example.to_dolist.data.repository.Repository
import com.example.to_dolist.utils.BroadcastMaker
import com.example.to_dolist.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class Notification : JobService() {
    @Inject
    lateinit var repo: Repository
    lateinit var job: Job
    lateinit var serviceIntent: Intent

    override fun onStartJob(params: JobParameters): Boolean {
        val jsonItem = params.getExtras().get(Constants.BUNDLE_NAME) as String
        val type = object : TypeToken<TodoListItem>() {}.type
        work(Gson().fromJson(jsonItem, type))
        return true
    }

    private fun work(todo: TodoListItem) {
        serviceIntent = Intent(this, ForegroundService::class.java)
        serviceIntent.putExtra(Constants.TODO_ITEM_KEY, Gson().toJson(todo))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        job = CoroutineScope(Dispatchers.Default).launch {
            delay(Constants.FIVE_MIN_DELAY_TIME)
            stopService(serviceIntent)
            repo.deleteById(todo.id)
            BroadcastMaker.sendBroadcast(
                applicationContext,
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        stopService(serviceIntent)
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return false
    }
}