package com.servirunplatomas.domain.model.contact

import kotlinx.serialization.Serializable

@Serializable
data class Contact(
    var images: ArrayList<String> = arrayListOf(""),
    var instagram: String = "",
    var email: String = "",
    var version: String = ""
)