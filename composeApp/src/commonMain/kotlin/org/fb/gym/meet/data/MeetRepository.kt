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
        ),
        Meet(
            "2",
            "Schweizer Meisterschaft",
            "22.10.25",
        ),
    )

    fun findMeets(): List<Meet> {
        return meets
    }

    // Internal mutable list that backs the public flow
    private val _meets = MutableStateFlow<List<Meet>>(emptyList())
    fun observeMeets(): Flow<List<Meet>> = _meets.asStateFlow()

    suspend fun saveMeet(meet: Meet) {
        // Replace an existing meet with the same id, otherwise append
        val updated = _meets.value.toMutableList()
        val index = updated.indexOfFirst { it.id == meet.id }
        if (index >= 0) {
            updated[index] = meet
        } else {
            updated.add(meet)
        }
        _meets.value = updated
    }

    suspend fun deleteMeet(meetId: String) {
        _meets.value = _meets.value.filterNot { it.id == meetId }
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