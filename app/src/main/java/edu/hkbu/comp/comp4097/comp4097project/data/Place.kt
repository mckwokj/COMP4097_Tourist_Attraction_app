package edu.hkbu.comp.comp4097.comp4097project.data

data class Place(
    val features: List<Features>
){
}

data class Features(
    val geometry: Geometry,
    val properties: Properties
){}

data class Geometry(
    val coordinates: List<Double>
){}

data class Properties(
    val xid: String,
    val dist: Double,
    val name: String,
    val kinds: String,
){}