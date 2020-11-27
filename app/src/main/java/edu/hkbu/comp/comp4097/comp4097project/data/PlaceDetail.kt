package edu.hkbu.comp.comp4097.comp4097project.data

data class PlaceDetail (
    val xid: String?,
    val name: String?,
    val address: Map<String, String>?,
    val preview: Map<String, String>?,
    val wikipedia_extracts: Map<String, String>?,
    val info: Map<String, String>?,
    val point: Map<String, Double>?
)