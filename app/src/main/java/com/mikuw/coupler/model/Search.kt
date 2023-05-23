package com.mikuw.coupler.model

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

/*data class Affirmation(val stringResourceId: Int) {
}*/

data class Search(
    val title: String,
    val location: String? = null,
    val from: Date? = null,
    val to: Date? = null,
    val creator: String? = null,
    val desc: String? = null
) : Serializable {
    fun formattedDate(date: Date): String? {
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        return dateFormat.format(date)
    }
}