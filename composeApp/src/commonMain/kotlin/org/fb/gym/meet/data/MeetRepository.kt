package org.fb.gym.meet.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.fb.gym.meet.db.AppDatabase

class MeetRepository(
    private val db: AppDatabase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
) {

    private val _meets: StateFlow<List<MeetOverview>> = db.meetQueries.selectOverviews()
        .asFlow()
        .mapToList(Dispatchers.Default)
        .map { it.toOverviews() }
        .distinctUntilChanged()
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,   // start listening as soon as the repo is created
            initialValue = emptyList()
        )

    fun observeMeets(): Flow<List<MeetOverview>> = _meets


    private fun List<org.fb.gym.meet.db.Meet>.toOverviews(): List<MeetOverview> {
        return this.map { it.toOverview() }
    }

    private fun selectParticipantsByMeetId(meetId: String): List<Participant> =
        db.meetQueries.selectParticipantsByMeetId(meetId)
            .executeAsList()
            .map { participant -> participant.toParticipant() }
            .sortedBy { it.gymnastId }

    private fun org.fb.gym.meet.db.Participant.toParticipant(): Participant {
        return Participant(
            this.gymnast_id,
            this.score_card.toScoreCard()
        )
    }

    private fun org.fb.gym.meet.db.Meet.toOverview(): MeetOverview {
        return MeetOverview(
            id = id,
            name = name,
            date = date
        )
    }

    private fun org.fb.gym.meet.db.Meet.toMeet(): Meet {
        val participants = selectParticipantsByMeetId(this.id)
        return Meet(
            MeetOverview(
                id = id,
                name = name,
                date = date
            ),
            participants = participants
        )
    }

    private fun ScoreCard.toJson(): String =
        Json.encodeToString(this)

    private fun String.toScoreCard(): ScoreCard =
        Json.decodeFromString(this)

    fun saveMeet(meet: Meet) {
        db.transaction {
            db.meetQueries.upsertOverview(
                meet.overview.id,
                meet.overview.name,
                meet.overview.date,
            )

            // Delete all existing participants for this meet
            db.meetQueries.deleteParticipantsByMeetId(meet.overview.id)

            // Insert all current participants
            meet.participants.forEach { participant ->
                db.meetQueries.insertParticipant(
                    meet_id = meet.overview.id,
                    gymnast_id = participant.gymnastId,
                    score_card = participant.scoreCard.toJson()
                )
            }
        }
    }

    fun observeMeet(meetId: String): Flow<Meet?> {
        return db.meetQueries.selectMeetById(meetId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { it?.toMeet() }
    }

    fun observeScoreCard(scoreCardId: ScoreCardId): Flow<ScoreCard?> {
        return db.meetQueries.selectScoreCardByMeetIdGymnastId(scoreCardId.meetId, scoreCardId.gymnastId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { it?.toScoreCard() }
    }

    fun saveScoreCard(scoreCardId: ScoreCardId, scoreCard: ScoreCard) {
        db.meetQueries.updateScoreCard(scoreCard.toJson(), scoreCardId.meetId, scoreCardId.gymnastId)
    }
}