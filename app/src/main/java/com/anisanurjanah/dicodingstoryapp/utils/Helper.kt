package com.anisanurjanah.dicodingstoryapp.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.withDateFormat(): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

    val date = inputFormat.parse(this) as Date
    return DateFormat.getDateInstance(DateFormat.MEDIUM).format(date)
}