package com.mikuw.coupler.model

import java.io.Serializable

data class Pet(
    val name: String = "",
    val desc: String = "",
    val ownerId: String = "",
    val type: String = "",
    ) : Serializable
