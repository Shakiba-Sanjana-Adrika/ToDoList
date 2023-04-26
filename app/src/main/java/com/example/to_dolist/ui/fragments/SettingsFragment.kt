package com.example.to_dolist.ui.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.to_dolist.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}