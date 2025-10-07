package org.fb.gym.meet.ui

import org.fb.gym.meet.data.MeetRepository
import org.fb.gym.meet.data.ScoreCardId

expect fun createScoreCardViewModel(
    scoreCardId: ScoreCardId,
    repository: MeetRepository
): ScoreContract