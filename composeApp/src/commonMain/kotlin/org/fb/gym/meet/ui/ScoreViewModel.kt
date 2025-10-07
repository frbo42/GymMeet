// shared/src/commonMain/kotlin/viewmodel/ScoreViewModel.kt
package org.fb.gym.meet.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.fb.gym.meet.data.MeetRepository
import org.fb.gym.meet.data.ScoreCard
import org.fb.gym.meet.data.ScoreCardId


interface ScoreContract {
    val scoreCard: StateFlow<ScoreCard?>

    fun updateScoreCard(updated: ScoreCard)
}

class ScoreViewModel(
    private val scoreCardId: ScoreCardId,
    private val repository: MeetRepository,
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.Main + Job())
) : ScoreContract {

    /** Public read‑only stream of the current ScoreCard. */
    override val scoreCard: StateFlow<ScoreCard?> = repository
        .observeScoreCard(scoreCardId)
        .stateIn(
            scope = externalScope,
            started = SharingStarted.Eagerly,
            initialValue = ScoreCard()
        )

    /** Called by the UI whenever the user edits any field. */
    override fun updateScoreCard(updated: ScoreCard) {
        // Fire‑and‑forget – repository decides whether to write to disk, network, etc.
        externalScope.launch {
            repository.saveScoreCard(scoreCardId, updated)
        }
    }
}