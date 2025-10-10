package org.fb.gym.meet.data

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.math.pow
import kotlin.math.roundToLong

data class ScoreCardId(
    val meetId: String,
    val gymnastId: String
)

@Serializable
data class ScoreCard(
    val floor: Score = Score(),
    val rings: Score = Score(),
    val vault: VaultScore = VaultScore(),
    val parallel: Score = Score(),
    val bar: Score = Score()
) {
    fun total(category: Category): Score = floor + rings + vault.score(category) + parallel + bar

}

@Serializable
data class VaultScore(
    val firstJump: Score = Score(),
    val secondJump: Score = Score()
) {
    fun score(category: Category): Score =
        when (category.vaultScoring) {
            VaultScoring.AVERAGE -> {
                val avg = listOf<Double>(this.firstJump.value, this.secondJump.value).average()
                Score(avg)
            }

            else -> maxOf(firstJump, secondJump)
        }

}

@JvmInline
@Serializable
value class Score(val value: Double = 0.0) : Comparable<Score> {
    override fun toString(): String = value.format()

    operator fun plus(other: Score): Score = Score(value + other.value)

    operator fun div(divider: Int): Score = Score(value / divider)

    override operator fun compareTo(other: Score): Int = value.compareTo(other.value)

    private fun Double.format(): String {
        if (this < 0) return ""
        val decimals = 2
        // 1️⃣  Scale the number so we can round it to the desired precision
        val factor = 10.0.pow(decimals.toDouble())

        // 2️⃣  Round half‑up (the same behaviour as java.util.Formatter)
        val scaled = (this * factor).roundToLong()

        // 3️⃣  Build a string that always shows *exactly* `decimals` fraction digits.
        //     We cannot use String.format, so we construct it manually.
        val integerPart = scaled / factor.toLong()
        val fractionPart = scaled % factor.toLong()

        // Pad the fraction with leading zeros if necessary (e.g. 9.5 → "500")
        val fractionString = fractionPart
            .toString()
            .padStart(decimals, '0')

        return "$integerPart.$fractionString"
    }
}