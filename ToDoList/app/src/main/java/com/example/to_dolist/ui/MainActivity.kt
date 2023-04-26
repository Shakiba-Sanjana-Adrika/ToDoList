package com.example.to_dolist.ui

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.IntentFilter
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.example.to_dolist.R
import com.example.to_dolist.databinding.ActivityMainBinding
import com.example.to_dolist.utils.Constants
import com.example.to_dolist.services.Receiver
import com.example.to_dolist.services.Sync
import com.example.to_dolist.ui.viewModel.TodoListViewModel
import dagger.hilt.android.AndroidEntryPoint

private lateinit var sharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener
private lateinit var sharedPreferences: SharedPreferences

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: TodoListViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private val broadcastReceiver by lazy {
        Receiver()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Register the broadcast receiver
        registerReceiver(broadcastReceiver, IntentFilter(Constants.DATA_UPDATE_INTENT))
        handleSharedPreference()
        themeHandler()
        // sync with remote server when for the first time app is launched
        firstInit()
    }

    fun handleSharedPreference() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferenceChangeListener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences: SharedPreferences?, key: String? ->
                when (key) {
                    Constants.SYNC_KEY -> {
                        handleSyncJob()
                    }
                    Constants.SYNC_INTERVAL_KEY -> {
                        startSyncJob()
                    }
                    Constants.THEME_KEY -> {
                        themeHandler()
                    }
                }
            }
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    // theme settings change in the settings menu.

    fun themeHandler() {
        when (sharedPreferences.getString(Constants.THEME_KEY, Constants.LIGHT_MODE)) {
            Constants.DARK_MODE -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.bg.setBackgroundResource(R.drawable.background_night)
            }
            Constants.LIGHT_MODE -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.bg.setBackgroundResource(R.drawable.background)
            }
        }
    }

    private fun handleSyncJob() {
        val syncStatus = sharedPreferences.getBoolean(Constants.SYNC_KEY, false)
        if (!syncStatus) {
            val jobScheduler =
                application.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.cancel(Constants.SYNC_JOB_ID)
        } else {
            startSyncJob()
        }
    }

    fun startSyncJob() {
        val jobScheduler =
            application.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.cancel(Constants.SYNC_JOB_ID)
        val userSelectedTime =
            sharedPreferences.getString(Constants.SYNC_INTERVAL_KEY, "24")!!.toLong()
        val componentName = ComponentName(applicationContext, Sync::class.java)
        val jobInfo = JobInfo.Builder(Constants.SYNC_JOB_ID, componentName)
            .setPeriodic(Constants.ONE_HOUR_DELAY_TIME * userSelectedTime)
            .setPersisted(true)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .build()
        val result = jobScheduler.schedule(jobInfo)
        if (result == JobScheduler.RESULT_SUCCESS) {
        }
    }

    fun firstInit() {
        val isFirstTime = sharedPreferences.getBoolean(Constants.FIRST_LAUNCH_KEY, true)
        if (isFirstTime) {
            sharedPreferences.edit().putBoolean(Constants.FIRST_LAUNCH_KEY, false).apply()
            viewModel.syncToLocalDB()
        }
    }
}