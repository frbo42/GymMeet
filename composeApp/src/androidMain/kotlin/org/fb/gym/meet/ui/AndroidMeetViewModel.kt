package org.fb.gym.meet.ui

import kotlinx.coroutines.flow.StateFlow
import org.fb.gym.meet.data.MeetOverview
import org.fb.gym.meet.data.MeetRepository


actual fun createMeetViewModel(
    repository: MeetRepository
): MeetContract {
    return AndroidMeetViewModel(repository)
}

class AndroidMeetViewModel(repository: MeetRepository) : MeetContract {
    private val delegate = DisplayMeetViewModel(repository)

    override val meets: StateFlow<List<MeetOverview>> = delegate.meets
}