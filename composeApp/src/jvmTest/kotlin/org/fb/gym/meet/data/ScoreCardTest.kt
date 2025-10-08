package org.fb.gym.meet.data

import kotlin.test.Test
import kotlin.test.assertEquals

class ScoreCardTest {

    @Test
    fun `calculate total empty card`() {
        val scoreCard = ScoreCard()

        val total = scoreCard.total(Category.C1)

        assertEquals(0.0, total.value)
    }

    @Test
    fun `calculate total filled card`() {
        val scoreCard = ScoreCard(
            Score(1.0),
            Score(1.0),
            VaultScore(Score(1.0), Score(1.0)),
            Score(1.0),
            Score(1.0),
        )

        val total = scoreCard.total(Category.C1)

        assertEquals(5.0, total.value)
    }

    @Test
    fun `calculate total vault card for max categories`() {
        val scoreCard = ScoreCard(
            vault = VaultScore(Score(1.0), Score(3.0)),
        )

        val maxCategories = setOf(Category.C1, Category.C2, Category.C3, Category.C4, Category.C5)

        maxCategories.forEach {
            val total = scoreCard.total(it)
            assertEquals(3.0, total.value)
        }
    }

    @Test
    fun `calculate total vault card for average categories`() {
        val scoreCard = ScoreCard(
            vault = VaultScore(Score(1.0), Score(3.0)),
        )

        val averageCategories = setOf(Category.C6, Category.C7)

        averageCategories.forEach {
            val total = scoreCard.total(it)
            assertEquals(2.0, total.value)
        }
    }
}