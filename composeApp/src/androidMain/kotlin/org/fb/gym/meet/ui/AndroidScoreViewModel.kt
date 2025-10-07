package org.fb.gym.meet.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.fb.gym.meet.data.MeetRepository
import org.fb.gym.meet.data.ScoreCard
import org.fb.gym.meet.data.ScoreCardId


actual fun createScoreCardViewModel(
    scoreCardId: ScoreCardId,
    repository: MeetRepository
): ScoreContract {
    return AndroidScoreViewModel(scoreCardId, repository)
}


class AndroidScoreViewModel(
    scoreCardId: ScoreCardId,
    repository: MeetRepository
) : ViewModel(), ScoreContract {

    private val delegate = ScoreViewModel(
        scoreCardId = scoreCardId,
        repository = repository,
        externalScope = viewModelScope
    )
    override val scoreCard = delegate.scoreCard

    override fun updateScoreCard(updated: ScoreCard) {
        delegate.updateScoreCard(updated)
    }
}