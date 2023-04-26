package com.example.to_dolist.services

import android.app.job.JobParameters
import android.app.job.JobService
import com.example.to_dolist.data.repository.Repository
import com.example.to_dolist.utils.BroadcastMaker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class Sync : JobService() {
    @Inject
    lateinit var repo: Repository
    override fun onStartJob(p0: JobParameters?): Boolean {
        CoroutineScope(Dispatchers.Default).launch { repo.insertTodoFromApiToLocalDB() }
            .invokeOnCompletion {
                BroadcastMaker.sendBroadcast(
                    applicationContext
                )
            }
        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }
}