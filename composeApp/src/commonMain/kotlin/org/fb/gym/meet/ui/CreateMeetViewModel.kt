// shared/src/commonMain/kotlin/viewmodel/MeetListViewModel.kt
package org.fb.gym.meet.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.fb.gym.meet.data.GymnastRepository
import org.fb.gym.meet.data.Meet
import org.fb.gym.meet.data.MeetRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Holds the list of meets and a single‑shot “add” operation.
 *
 * The ViewModel is deliberately *platform‑agnostic* – you can instantiate it
 * from Android, Desktop, iOS, etc.  The only requirement is a CoroutineScope.
 */
class CreateMeetViewModel(
    private val meetRepo: MeetRepository,
    private val gymnastRepo: GymnastRepository,
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.Main + Job())
) {

    // -----------------------------------------------------------------
    // 1️⃣  UI state – combines the meet fields + the list of gymnasts
    // -----------------------------------------------------------------
    private val _uiState = MutableStateFlow(CreateMeetUiState())
    val uiState: StateFlow<CreateMeetUiState> = _uiState.asStateFlow()

    init {
        // Observe the global list of gymnasts and push it into the UI state
        externalScope.launch {
            gymnastRepo.observeGymnasts()
                .collect { list ->
                    _uiState.update { it.copy(allGymnasts = list) }
                }
        }
    }

    // -----------------------------------------------------------------
    // 2️⃣  Mutators for the text fields
    // -----------------------------------------------------------------
    fun onNameChanged(newName: String) {
        println("new name: ${_uiState.value}")
        _uiState.update { it.copy(name = newName, nameError = null) }
        println("new name: ${_uiState.value}")
    }


    fun onDateChanged(newDate: String) {
        _uiState.update { it.copy(date = newDate, dateError = null) }
    }

    fun toggleGymnastSelection(gymnastId: String) {
        _uiState.update { state ->
            val newSet = state.selectedGymnastIds.toMutableSet()
            if (!newSet.add(gymnastId)) {
                // already present → remove
                newSet.remove(gymnastId)
            }
            state.copy(selectedGymnastIds = newSet)
        }
    }

    // -----------------------------------------------------------------
    // 4️⃣  Save the meet (validation + repository call)
    // -----------------------------------------------------------------
    @OptIn(ExperimentalUuidApi::class)
    fun onSave() {

        println("on save: ${_uiState.value}")

        val current = _uiState.value
        var ok = true
        var nameErr: String? = null
        var dateErr: String? = null

        if (current.name.isBlank()) {
            nameErr = "Name cannot be empty"
            ok = false
        }

        val datePattern = Regex("""\d{2}\.\d{2}\.\d{4}""") // DD.MM.YYYY
        if (!datePattern.matches(current.date)) {
            dateErr = "Enter a date like 28.09.2025"
            ok = false
        }

        if (!ok) {
            _uiState.update { it.copy(nameError = nameErr, dateError = dateErr) }
            return
        }

        // Build the Meet object with the selected gymnasts
        val selectedGymnasts = current.allGymnasts.filter { it.id in current.selectedGymnastIds }

        val meet = Meet(
            id = Uuid.random().toString(),
            name = current.name.trim(),
            date = current.date.trim(),
            gymnasts = selectedGymnasts
        )

        // Persist
        externalScope.launch {
            meetRepo.saveMeet(meet)
        }

        // Reset UI (optional – you could also navigate away)
        _uiState.value = CreateMeetUiState()
    }
}