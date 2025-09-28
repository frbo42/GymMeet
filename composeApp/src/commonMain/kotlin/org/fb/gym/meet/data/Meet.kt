package org.fb.gym.meet.data

data class Meet(
    val id: String,
    val name: String,
    val date: String,
    val gymnasts: List<Gymnast> = emptyList()
)
