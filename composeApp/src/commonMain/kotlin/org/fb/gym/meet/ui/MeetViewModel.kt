// shared/src/commonMain/kotlin/viewmodel/MeetListViewModel.kt
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

/**
 * Holds the list of meets and a single‑shot “add” operation.
 *
 * The ViewModel is deliberately *platform‑agnostic* – you can instantiate it
 * from Android, Desktop, iOS, etc.  The only requirement is a CoroutineScope.
 */
class MeetViewModel(
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

    /** Called by the UI when a new meet is created. */
    fun addMeet(meet: Meet) {
        externalScope.launch {
            repository.saveMeet(meet)
        }
    }

    /** Optional – delete a meet */
    fun deleteMeet(meetId: String) {
        externalScope.launch {
            repository.deleteMeet(meetId)
        }
    }
}