package com.symbolsense.data.local

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val IndonesianLocale = Locale("id", "ID")

fun formatTimestampLabel(timestamp: Long, now: Long = System.currentTimeMillis()): String {
    val target = Calendar.getInstance().apply { timeInMillis = timestamp }
    val today = Calendar.getInstance().apply { timeInMillis = now }

    val time = SimpleDateFormat("HH:mm", IndonesianLocale).format(Date(timestamp))

    return when {
        isSameDay(target, today) -> "Hari ini, $time"
        isYesterday(target, today) -> "Kemarin, $time"
        target.get(Calendar.YEAR) == today.get(Calendar.YEAR) -> {
            val date = SimpleDateFormat("dd MMM", IndonesianLocale).format(Date(timestamp))
            "$date, $time"
        }
        else -> {
            val date = SimpleDateFormat("dd MMM yyyy", IndonesianLocale).format(Date(timestamp))
            "$date, $time"
        }
    }
}

private fun isSameDay(a: Calendar, b: Calendar): Boolean {
    return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
        a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
}

private fun isYesterday(target: Calendar, today: Calendar): Boolean {
    val yesterday = today.clone() as Calendar
    yesterday.add(Calendar.DAY_OF_YEAR, -1)
    return isSameDay(target, yesterday)
}
