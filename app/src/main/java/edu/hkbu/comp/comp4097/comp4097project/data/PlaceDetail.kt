package edu.hkbu.comp.comp4097.comp4097project.data

data class PlaceDetail (
    val xid: String,
    val name: String,
    val address: Map<String, String>,
    val image: String?,
    val info: Map<String, String>?
)