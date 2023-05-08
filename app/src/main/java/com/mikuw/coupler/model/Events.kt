package com.mikuw.coupler.model

import java.text.SimpleDateFormat
import java.util.*

/*data class Affirmation(val stringResourceId: Int) {
}*/

data class Event(
    val name: String,
    val location: String? = null,
    val from: Date? = null,
    val to: Date? = null
) {
    fun formattedDate(date: Date): String? {
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        return dateFormat.format(date)
    }
}