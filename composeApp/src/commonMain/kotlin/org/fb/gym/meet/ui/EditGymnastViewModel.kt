package org.fb.gym.meet.ui

import gymmeet.composeapp.generated.resources.Res
import gymmeet.composeapp.generated.resources.error_gymnast_first_name_required
import gymmeet.composeapp.generated.resources.error_gymnast_last_name_required
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.fb.gym.meet.data.Category
import org.fb.gym.meet.data.Gender
import org.fb.gym.meet.data.GymnastRepository
import org.jetbrains.compose.resources.StringResource


interface EditGymnastContract {
    fun onSave(onSaved: () -> Unit)

    val uiState: StateFlow<EditGymnastUiState>
    fun onFirstNameChanged(text: String)
    fun onLastNameChanged(text: String)
    fun onGenderChanged(gender: Gender)
    fun onCategoryChanged(category: Category)
    fun delete()
}

class EditGymnastViewModel(
    /** If null → we are creating a new gymnast, otherwise we are editing. */
    private val gymnastId: String? = null,
    private val repository: GymnastRepository,
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.Main + Job())
) : EditGymnastContract {

    // -----------------------------------------------------------------
    // 1️⃣  Load the existing gymnast (if we are editing) and map it
    //     to the UI state.  If we are creating, start with the default state.
    // -----------------------------------------------------------------
    private val _uiState: MutableStateFlow<EditGymnastUiState> = MutableStateFlow(EditGymnastUiState())
    override val uiState: StateFlow<EditGymnastUiState> = _uiState.asStateFlow()

    init {
        if (gymnastId != null) {
            // Pull the gymnast from the repository once and populate the UI.
            externalScope.launch {
                repository.observeGymnasts()
                    .map { list -> list.find { it.id == gymnastId } }
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
    override fun onFirstNameChanged(text: String) {
        _uiState.update { it.copy(firstName = text, firstNameError = null) }
    }

    override fun onLastNameChanged(text: String) {
        _uiState.update { it.copy(lastName = text, lastNameError = null) }
    }

    override fun onGenderChanged(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
    }

    override fun onCategoryChanged(category: Category) {
        _uiState.update { it.copy(category = category) }
    }

    override fun delete() {
        repository.delete(gymnastId)
    }

    // -----------------------------------------------------------------
    // 3️⃣  Save – validates, persists via the repository, and signals success.
    // -----------------------------------------------------------------
    override fun onSave(onSaved: () -> Unit) {
        val current = _uiState.value
        var ok = true
        var fnError: StringResource? = null
        var lnError: StringResource? = null

        if (current.firstName.isBlank()) {
            fnError = Res.string.error_gymnast_first_name_required
            ok = false
        }
        if (current.lastName.isBlank()) {
            lnError = Res.string.error_gymnast_last_name_required
            ok = false
        }

        if (!ok) {
            _uiState.update { it.copy(firstNameError = fnError, lastNameError = lnError) }
            return
        }

        // Build the Gymnast entity (preserve the original id if editing)
        val gymnast = current.toGymnast(gymnastId)

        externalScope.launch {
            repository.saveGymnast(gymnast)
            onSaved()   // tell the UI to navigate back / show a toast, etc.
        }
    }
}