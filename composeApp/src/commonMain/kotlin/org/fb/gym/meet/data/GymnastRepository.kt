package org.fb.gym.meet.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class GymnastRepository {
    private val _gymnasts = MutableStateFlow<List<Gymnast>>(
        listOf(
            Gymnast("g-11", "Marie", "May", Category.C7),
            Gymnast("g-22", "Markus", "Huber", Category.C3),
        )
    )

    fun observeGymnasts(): Flow<List<Gymnast>> = _gymnasts.asStateFlow()

    fun observeGymnast(gymnastId: String): Flow<Gymnast?> {
        return _gymnasts.map { gymnasts -> gymnasts.find { it.id == gymnastId } }
    }

    suspend fun saveGymnast(gymnast: Gymnast) {
        // Append – duplicate‑id handling omitted for brevity
        _gymnasts.value = _gymnasts.value + gymnast
    }
}