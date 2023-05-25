package com.mikuw.coupler.model

import java.io.Serializable

data class Message(
    val sender: String = "",
    val receiver: String = "",
    val content: String = "",
    val timestamp: Long = 0L,
) : Serializable
