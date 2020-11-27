package edu.hkbu.comp.comp4097.comp4097project.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlaceInfo(
    @PrimaryKey
    val xid: String,
    val name: String?,
    val lat: Double?,
    val lon: Double?,
    val dist: Double?,
    val kinds: String?,
    var image_URL: String?,
    var district: String?,
    var description: String?
)