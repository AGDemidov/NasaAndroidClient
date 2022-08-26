package com.agdemidov.nasaclient.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.DataFormatException

object DateGetter {
    fun getDateWithOffset(offsetInDays: Int = 0): String =
        try {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, offsetInDays)
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            formatter.timeZone = TimeZone.getTimeZone("GMT")
            val date = formatter.format(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, -offsetInDays)
            date
        } catch (ex: Exception) {
            throw DataFormatException("Can't get date for Http request from Calendar")
        }

    fun getToday(): String = getDateWithOffset(0)
    fun getTomorrow(): String = getDateWithOffset(1)
    fun getYesterday(): String = getDateWithOffset(-1)
}
