package org.fb.gym.meet.data


data class Score(
    val floor: Double = 0.0,
    val ring: Double = 0.0,
    val vault: VaultScore = VaultScore(),
    val parallel: Double = 0.0,
    val bar: Double = 0.0
)

data class VaultScore(
    val firstJump: Double = 0.0,
    val secondJump: Double = 0.0
)
