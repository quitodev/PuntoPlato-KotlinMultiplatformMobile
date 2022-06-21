package com.servirunplatomas.domain.model.points

import kotlinx.serialization.Serializable

@Serializable
data class Points(
    var name: String = "",
    var address: String = "",
    var instagram: String = "",
    var schedule: String = "",
    var image: String = "",
    var latitude: String = "",
    var longitude: String = ""
)