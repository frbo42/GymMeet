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

/**
 * A lightweight, platform‑neutral ViewModel.
 *
 * - `scoreCardId` uniquely identifies the gymnast whose scores we edit.
 * - `repository` is injected (DI‑friendly) – you can pass a real DB impl later.
 */
class ScoreViewModel(
    private val scoreCardId: ScoreCardId,
    private val repository: MeetRepository,
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.Main + Job())
) {

    /** Public read‑only stream of the current ScoreCard. */
    val scoreCard: StateFlow<ScoreCard?> = repository
        .observeScoreCard(scoreCardId)
        .stateIn(
            scope = externalScope,
            started = SharingStarted.Eagerly,
            initialValue = ScoreCard()
        )

    /** Called by the UI whenever the user edits any field. */
    fun updateScoreCard(updated: ScoreCard) {
        // Fire‑and‑forget – repository decides whether to write to disk, network, etc.
        externalScope.launch {
            repository.saveScoreCard(scoreCardId, updated)
        }
    }
}