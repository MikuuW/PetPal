package com.mikuw.coupler.model

import java.text.SimpleDateFormat
import java.util.*

/*data class Affirmation(val stringResourceId: Int) {
}*/

data class Petsitter(
    val firstname: String,
    val lastname: String,
    val email: String,
    val imageUri: String?,
    val postalcode: String?,
    val street: String?,
    val streetNr: String?,
    val city: String?
) {
}