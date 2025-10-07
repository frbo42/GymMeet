package org.fb.gym.meet.ui

import org.fb.gym.meet.data.GymnastRepository
import org.fb.gym.meet.data.MeetRepository
import org.fb.gym.meet.data.ScoreCardId

expect fun createScoreCardViewModel(
    scoreCardId: ScoreCardId,
    repository: MeetRepository
): ScoreContract

expect fun createMeetViewModel(
    repository: MeetRepository
): MeetContract

expect fun createEditGymnastViewModel(
    gymnastId: String?,
    repository: GymnastRepository,
): EditGymnastContract