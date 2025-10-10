package org.fb.gym.meet.data

import kotlin.test.Test
import kotlin.test.assertEquals

class ScoreTest {

    @Test
    fun `calculate rounding`() {
        val values = listOf(
            8.6 to "8.60",
            8.0 to "8.00",
            9.99 to "9.99",
            10.0 to "10.00",
            0.0 to "0.00",
            0.125 to "0.13",
            0.126 to "0.13",
        )

        values.forEach { (value, expected) ->
            val score = Score(value)
            assertEquals(expected, score.toString())
        }
    }
}