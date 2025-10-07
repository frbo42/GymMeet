package org.fb.gym.meet.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.fb.gym.meet.data.MeetRepository


actual fun createMeetViewModel(
    repository: MeetRepository
): MeetContract {
    val desktopScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    return DisplayMeetViewModel(repository, desktopScope)
}
