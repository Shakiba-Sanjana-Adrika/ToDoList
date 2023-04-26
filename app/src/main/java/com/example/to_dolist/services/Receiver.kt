package com.example.to_dolist.services

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.PersistableBundle
import com.example.to_dolist.data.localDatabase.TodoListItem
import com.example.to_dolist.data.repository.Repository
import com.example.to_dolist.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class Receiver : BroadcastReceiver() {
    @Inject
    lateinit var repo: Repository

    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.Default).launch {

            //  last scheduled job is cancelled
            stopJob(context)

            // returning latest TodoListItem
            val todolist = repo.getAboveTodo()

            // checking if there is any TodoListItem exists
            if (todolist != null) {

                // calculating estimated time
                val diff = todolist.time - Calendar.getInstance().time.time
                val type = object : TypeToken<TodoListItem>() {}.type
                startJob(context,
                    diff - Constants.FIVE_MIN_DELAY_TIME,
                    Gson().toJson(todolist, type))
            }
        }
    }

    private fun stopJob(context: Context) {
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.cancel(Constants.NEXT_NOTIFICATION_JOB_ID)
    }

    private fun startJob(context: Context, time: Long, jsonItem: String) {
        val componentName = ComponentName(context, Notification::class.java)
        val persistentBundle = PersistableBundle()
        persistentBundle.putString(Constants.BUNDLE_NAME, jsonItem)
        val jobInfo = JobInfo.Builder(Constants.NEXT_NOTIFICATION_JOB_ID, componentName)
            .setMinimumLatency(time)
            .setExtras(persistentBundle)
            .setPersisted(true)
            .build()
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val result = jobScheduler.schedule(jobInfo)
        if (result == JobScheduler.RESULT_SUCCESS) {
        }
    }
}