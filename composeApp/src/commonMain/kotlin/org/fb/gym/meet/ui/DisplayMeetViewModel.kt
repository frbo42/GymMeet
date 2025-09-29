package org.fb.gym.meet.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.fb.gym.meet.data.Meet
import org.fb.gym.meet.data.MeetRepository

class DisplayMeetViewModel(
    private val repository: MeetRepository,
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.Main + Job())
) {

    /** Public stream of the current meet list. */
    val meets: StateFlow<List<Meet>> = repository
        .observeMeets()
        .stateIn(
            scope = externalScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    /** Optional – delete a meet */
    fun deleteMeet(meetId: String) {
        externalScope.launch {
            repository.deleteMeet(meetId)
        }
    }
}