package org.fb.gym.meet.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.fb.gym.meet.data.MeetRepository
import org.fb.gym.meet.data.ScoreCardId

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