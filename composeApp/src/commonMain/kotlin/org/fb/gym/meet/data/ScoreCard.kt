package org.fb.gym.meet.data

import kotlin.jvm.JvmInline
import kotlin.math.pow
import kotlin.math.round


data class ScoreCard(
    val floor: Score = Score(),
    val rings: Score = Score(),
    val vault: VaultScore = VaultScore(),
    val parallel: Score = Score(),
    val bar: Score = Score()
)

data class VaultScore(
    val firstJump: Score = Score(),
    val secondJump: Score = Score()
)

@JvmInline
value class Score(val value: Double = -1.0) {
    override fun toString(): String = value.format2()


    fun Double.format2(): String {
        if (this < 0) return ""
        val decimals = 2
        // 1️⃣  Scale the number so we can round it to the desired precision
        val factor = 10.0.pow(decimals.toDouble())

        // 2️⃣  Round half‑up (the same behaviour as java.util.Formatter)
        val rounded = round(this * factor) / factor

        // 3️⃣  Build a string that always shows *exactly* `decimals` fraction digits.
        //     We cannot use String.format, so we construct it manually.
        val integerPart = rounded.toLong()
        val fractionPart = ((rounded - integerPart) * factor).toLong()

        // Pad the fraction with leading zeros if necessary (e.g. 9.5 → "500")
        val fractionString = fractionPart
            .toString()
            .padStart(decimals, '0')

        return "$integerPart.$fractionString"
    }
}