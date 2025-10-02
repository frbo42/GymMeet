package org.fb.gym.meet.data

import app.cash.sqldelight.db.SqlDriver

expect object DatabaseFactory {
    fun createDriver(): SqlDriver
}