package com.example.proyecto_final_eventia.util

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

fun formatEventDate(isoDate: String): String {
    return try {
        val odt = OffsetDateTime.parse(isoDate)
        val formatter = DateTimeFormatter.ofPattern("EEE d MMM · HH:mm", Locale("es", "ES"))
        odt.format(formatter)
    } catch (e: Exception) {
        isoDate
    }
}

fun formatCacheAge(cachedAt: Long?): String {
    if (cachedAt == null) return ""
    val diff = System.currentTimeMillis() - cachedAt
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    return when {
        hours >= 1 -> "datos de hace ${hours}h"
        minutes >= 1 -> "datos de hace ${minutes}min"
        else -> "datos recientes"
    }
}
