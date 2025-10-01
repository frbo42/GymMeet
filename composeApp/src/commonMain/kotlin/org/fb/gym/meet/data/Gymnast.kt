package org.fb.gym.meet.data

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Gymnast(
    val id: String = Uuid.random().toString(),   // auto‑generated if you use the default ctor
    val firstName: String = "",
    val lastName: String = "",
    val category: Category = Category.C5,
    val gender: Gender = Gender.M
)

enum class Gender {
    M, F
}

enum class Category {
    C1, C2, C3, C4, C5, C6, C7
}