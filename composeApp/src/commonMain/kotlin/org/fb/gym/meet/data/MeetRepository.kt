package org.fb.gym.meet.data

import kotlinx.coroutines.flow.*

class MeetRepository {

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

    fun observeMeet(meetId: String): Flow<Meet?> {
        return _meets.map { meets -> meets.find { it.id == meetId } }
    }

    private val storage = mutableMapOf<ScoreCardId, MutableStateFlow<ScoreCard>>()

    fun observeScoreCard(scoreCardId: ScoreCardId): Flow<ScoreCard?> {
        return _meets
            .map { meets -> meets.find { it.id == scoreCardId.meetId } }
            .map { meet -> meet?.participants?.find { it.gymnastId == scoreCardId.gymnastId } }
            .map { participant -> participant?.scoreCard }
            .distinctUntilChanged()
    }

    suspend fun saveScoreCard(scoreCardId: ScoreCardId, scoreCard: ScoreCard) {
        val flow = storage.getOrPut(scoreCardId) { MutableStateFlow(ScoreCard()) }
        flow.value = scoreCard
        println("saved scoreCard: $scoreCardId - $scoreCard")
    }
}