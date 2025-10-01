package org.fb.gym.meet.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.fb.gym.meet.data.Category
import org.fb.gym.meet.data.Gender
import org.fb.gym.meet.data.GymnastRepository

class EditGymnastViewModel(
    private val repository: GymnastRepository,
    /** If null → we are creating a new gymnast, otherwise we are editing. */
    private val existingGymnastId: String? = null,
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.Main + Job())
) {

    // -----------------------------------------------------------------
    // 1️⃣  Load the existing gymnast (if we are editing) and map it
    //     to the UI state.  If we are creating, start with the default state.
    // -----------------------------------------------------------------
    private val _uiState: MutableStateFlow<EditGymnastUiState> = MutableStateFlow(EditGymnastUiState())
    val uiState: StateFlow<EditGymnastUiState> = _uiState.asStateFlow()

    init {
        if (existingGymnastId != null) {
            // Pull the gymnast from the repository once and populate the UI.
            externalScope.launch {
                repository.observeGymnasts()
                    .map { list -> list.find { it.id == existingGymnastId } }
                    .filterNotNull()
                    .first()                     // we only need the first emission
                    .let { gymnast ->
                        _uiState.value = EditGymnastUiState(
                            firstName = gymnast.firstName,
                            lastName = gymnast.lastName,
                            gender = gymnast.gender,
                            category = gymnast.category
                        )
                    }
            }
        }
    }

    // -----------------------------------------------------------------
    // 2️⃣  Mutators – called from the UI when the user types / selects.
    // -----------------------------------------------------------------
    fun onFirstNameChanged(text: String) {
        _uiState.update { it.copy(firstName = text, firstNameError = null) }
    }

    fun onLastNameChanged(text: String) {
        _uiState.update { it.copy(lastName = text, lastNameError = null) }
    }

    fun onGenderChanged(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun onCategoryChanged(category: Category) {
        _uiState.update { it.copy(category = category) }
    }

    // -----------------------------------------------------------------
    // 3️⃣  Save – validates, persists via the repository, and signals success.
    // -----------------------------------------------------------------
    fun onSave(onSaved: () -> Unit) {
        val current = _uiState.value
        var ok = true
        var fnError: String? = null
        var lnError: String? = null

        if (current.firstName.isBlank()) {
            fnError = "First name required"
            ok = false
        }
        if (current.lastName.isBlank()) {
            lnError = "Last name required"
            ok = false
        }

        if (!ok) {
            _uiState.update { it.copy(firstNameError = fnError, lastNameError = lnError) }
            return
        }

        // Build the Gymnast entity (preserve the original id if editing)
        val gymnast = current.toGymnast(existingGymnastId)

        externalScope.launch {
            repository.saveGymnast(gymnast)
            onSaved()   // tell the UI to navigate back / show a toast, etc.
        }
    }
}