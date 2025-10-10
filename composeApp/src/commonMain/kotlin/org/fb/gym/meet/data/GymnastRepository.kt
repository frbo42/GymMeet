package org.fb.gym.meet.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import org.fb.gym.meet.db.AppDatabase

class GymnastRepository(
    private val db: AppDatabase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
) {
    private val _gymnasts: StateFlow<List<Gymnast>> = db.meetQueries.selectGymnasts()
        .asFlow()
        .mapToList(Dispatchers.Default)
        .map { it.toGymnasts() }
        .distinctUntilChanged()
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    fun observeGymnasts(): Flow<List<Gymnast>> = _gymnasts

    fun observeGymnast(gymnastId: String): Flow<Gymnast?> {
        return db.meetQueries.selectGymnastById(gymnastId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { it?.toGymnast() }
    }

    fun saveGymnast(gymnast: Gymnast) {
        db.meetQueries.upsertGymnast(
            gymnast.id,
            gymnast.firstName,
            gymnast.lastName,
            gymnast.gender.name,
            gymnast.category.name,
        )
    }

    fun delete(gymnastId: String?) {
        if (gymnastId == null) return
        db.meetQueries.deleteGymnast(gymnastId)
    }
}

private fun List<org.fb.gym.meet.db.Gymnast>.toGymnasts(): List<Gymnast> {
    return this.map { gymnast -> gymnast.toGymnast() }
}

private fun org.fb.gym.meet.db.Gymnast.toGymnast(): Gymnast {
    return Gymnast(
        this.id,
        this.first_name,
        this.last_name,
        Category.valueOf(this.category),
        Gender.valueOf(this.gender)
    )
}
