package org.fb.gym.meet.ui

import kotlinx.coroutines.flow.StateFlow
import org.fb.gym.meet.data.Category
import org.fb.gym.meet.data.Gender
import org.fb.gym.meet.data.GymnastRepository


actual fun createEditGymnastViewModel(
    gymnastId: String?,
    repository: GymnastRepository,
): EditGymnastContract {
    return AndroidEditGymnastViewModel(gymnastId, repository)
}

class AndroidEditGymnastViewModel(gymnastId: String?, repository: GymnastRepository) : EditGymnastContract {
    private val delegate = EditGymnastViewModel(gymnastId, repository)
    override fun onSave(onSaved: () -> Unit) = delegate.onSave(onSaved)

    override val uiState: StateFlow<EditGymnastUiState> = delegate.uiState

    override fun onFirstNameChanged(text: String) = delegate.onFirstNameChanged(text)

    override fun onLastNameChanged(text: String) = delegate.onLastNameChanged(text)

    override fun onGenderChanged(gender: Gender) = delegate.onGenderChanged(gender)

    override fun onCategoryChanged(category: Category) = delegate.onCategoryChanged(category)
}