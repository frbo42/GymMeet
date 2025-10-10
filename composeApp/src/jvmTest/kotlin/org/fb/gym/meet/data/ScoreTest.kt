package org.fb.gym.meet.data

import kotlin.test.Test
import kotlin.test.assertEquals

class ScoreTest {

    @Test
    fun `calculate rounding`() {
        val score = Score(8.6)

        assertEquals("8.60", score.toString())
    }

}