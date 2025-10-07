package org.fb.gym.meet.ui

import org.fb.gym.meet.data.GymnastRepository
import org.fb.gym.meet.data.MeetRepository
import org.fb.gym.meet.data.ScoreCardId

expect fun createMeetViewModel(
    repository: MeetRepository
): MeetContract

expect fun createEditMeetViewModel(
    meetId: String,
    meetRepo: MeetRepository,
    gymnastRepo: GymnastRepository,
): EditMeetContract

expect fun createEditGymnastViewModel(
    gymnastId: String?,
    repository: GymnastRepository,
): EditGymnastContract

expect fun createScoreCardViewModel(
    scoreCardId: ScoreCardId,
    repository: MeetRepository
): ScoreContract
