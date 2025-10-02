package org.fb.gym.meet.data

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.fb.gym.meet.db.AppDatabase

actual object DatabaseFactory {
    // You need a Context – you can obtain it via ApplicationProvider or a DI container.
    private lateinit var context: Context

    fun init(appContext: Context) {
        context = appContext
    }

    actual fun createDriver(): SqlDriver {
        // The database file will be stored in the app's internal storage.
        return AndroidSqliteDriver(AppDatabase.Schema, context, "app.db")
    }
}