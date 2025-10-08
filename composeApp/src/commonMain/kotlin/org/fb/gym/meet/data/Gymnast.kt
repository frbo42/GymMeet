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

enum class Category(val vaultScoring: VaultScoring) {
    C1(VaultScoring.MAX),
    C2(VaultScoring.MAX),
    C3(VaultScoring.MAX),
    C4(VaultScoring.MAX),
    C5(VaultScoring.MAX),
    C6(VaultScoring.AVERAGE),
    C7(VaultScoring.AVERAGE),
    WOMEN_MEN(VaultScoring.AVERAGE),
}

enum class VaultScoring {
    MAX, AVERAGE
}