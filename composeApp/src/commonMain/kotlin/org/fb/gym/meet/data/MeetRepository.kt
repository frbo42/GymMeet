package org.fb.gym.meet.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MeetRepository {

    private val meets: List<Meet> = mutableListOf(
        Meet(
            "1",
            "Geräteturnen Seelandmeisterschaft",
            "20.9.25",
            Location(
                "Aarestr.",
                "3270",
                "Aarberg",
            )
        ),
        Meet(
            "2",
            "Schweizer Meisterschaft",
            "22.10.25",
            Location(
                "Harderggerstr.",
                "3008",
                "Bern",
            )
        ),
    )

    fun findMeets(): List<Meet> {
        return meets
    }

    companion object {
        var gymnasts =
            setOf(
                Gymnast("g-11", "Anna Müller"),
                Gymnast("g-12", "Peter Schmid"),
                Gymnast("g-13", "Laura Keller"),
                Gymnast("g-21", "Markus Huber"),
                Gymnast("g-22", "Sofia Weber"),
            )
    }

    fun findGymnastsForMeet(meetId: String): List<Gymnast> {
        return when (meetId) {
            "1" -> listOf(
                Gymnast("g-11", "Anna Müller"),
                Gymnast("g-12", "Peter Schmid"),
                Gymnast("g-13", "Laura Keller")
            )

            "2" -> listOf(
                Gymnast("g-21", "Markus Huber"),
                Gymnast("g-22", "Sofia Weber"),
            )

            else -> emptyList()
        }
    }

    fun getResults(meetId: String, gymnastId: String): ScoreCard {
        return ScoreCard(
            floor = Score(8.4)
        )
    }

    fun getGymnast(gymnastId: String): Gymnast {
        return gymnasts.find { it.id == gymnastId }!!
    }

    private val storage = mutableMapOf<ScoreCardId, MutableStateFlow<ScoreCard>>()

    fun observeScoreCard(scoreCardId: ScoreCardId): Flow<ScoreCard> {
        return storage.getOrPut(scoreCardId) { MutableStateFlow(ScoreCard()) }.asStateFlow()
    }

    suspend fun saveScoreCard(scoreCardId: ScoreCardId, scoreCard: ScoreCard) {
        val flow = storage.getOrPut(scoreCardId) { MutableStateFlow(ScoreCard()) }
        flow.value = scoreCard
        println("saved scoreCard: $scoreCardId - $scoreCard")
    }
}