package com.example.to_dolist.utils

import java.text.SimpleDateFormat
import java.util.*

object TimeToStringConverter {
    fun TimeToString(time: Long): String {
        val dateFormat = SimpleDateFormat(Constants.TIME_PATTERN)
        return dateFormat.format(Date(time))
    }
}