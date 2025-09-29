package org.fb.gym.meet.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GymnastRepository {
    private val _gymnasts = MutableStateFlow<List<Gymnast>>(
        listOf(
            Gymnast("g-11", "Marie", "May", Category.C7),
            Gymnast("g-22", "Markus", "Huber", Category.C3),
        )
    )

    fun observeGymnasts(): Flow<List<Gymnast>> = _gymnasts.asStateFlow()

    suspend fun saveGymnast(gymnast: Gymnast) {
        // Append – duplicate‑id handling omitted for brevity
        _gymnasts.value = _gymnasts.value + gymnast
    }
}