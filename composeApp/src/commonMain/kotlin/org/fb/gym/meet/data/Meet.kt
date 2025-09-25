package org.fb.gym.meet.data

data class Meet(
    val id: String,
    val name: String,
    val date: String,
    val location: Location
)

data class Location(
    val street: String,
    val plz: String,
    val city: String,
)
