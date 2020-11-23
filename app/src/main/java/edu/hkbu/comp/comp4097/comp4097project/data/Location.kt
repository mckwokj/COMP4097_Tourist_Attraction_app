package edu.hkbu.comp.comp4097.comp4097project.data

data class Location(
    val place_id: Int,
    val address: Address
) {}

data class Address(
    val county: String
){}