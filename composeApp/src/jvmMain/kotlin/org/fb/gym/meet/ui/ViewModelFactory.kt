package org.fb.gym.meet.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.fb.gym.meet.data.GymnastRepository
import org.fb.gym.meet.data.MeetRepository
import org.fb.gym.meet.data.ScoreCardId


actual fun createMeetViewModel(
    repository: MeetRepository
): MeetContract {
    val desktopScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    return DisplayMeetViewModel(repository, desktopScope)
}

actual fun createScoreCardViewModel(
    scoreCardId: ScoreCardId,
    repository: MeetRepository
): ScoreContract {
    // Desktop apps usually run on the JVM main thread, so Dispatchers.Main works.
    // If you prefer a dedicated scope you can create one here.
    val desktopScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    return ScoreViewModel(
        scoreCardId = scoreCardId,
        repository = repository,
        externalScope = desktopScope
    )
}

actual fun createEditGymnastViewModel(
    gymnastId: String?,
    repository: GymnastRepository,
): EditGymnastContract {
    val desktopScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    return EditGymnastViewModel(gymnastId, repository, desktopScope)
}