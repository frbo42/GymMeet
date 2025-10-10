package org.fb.gym.meet.ui

import kotlinx.coroutines.flow.StateFlow
import org.fb.gym.meet.data.GymnastRepository
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

actual fun createEditMeetViewModel(
    meetId: String,
    meetRepo: MeetRepository,
    gymnastRepo: GymnastRepository,
): EditMeetContract {
    return AndroidEditMeetViewModel(meetId, meetRepo, gymnastRepo)
}

class AndroidEditMeetViewModel(meetId: String, meetRepo: MeetRepository, gymnastRepo: GymnastRepository) :
    EditMeetContract {

    private val delegate = EditMeetViewModel(meetId, meetRepo, gymnastRepo)

    override val uiState: StateFlow<EditMeetUiState> = delegate.uiState

    override fun onSave() = delegate.onSave()

    override fun onNameChanged(newName: String) = delegate.onNameChanged(newName)

    override fun toggleGymnastSelection(gymnastId: String) = delegate.toggleGymnastSelection(gymnastId)

    override fun onDateChanged(newDate: String) = delegate.onDateChanged(newDate)
    override fun onDelete() = delegate.onDelete()
}