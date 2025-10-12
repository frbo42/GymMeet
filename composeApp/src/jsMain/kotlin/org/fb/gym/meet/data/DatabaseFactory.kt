package org.fb.gym.meet.data

import app.cash.sqldelight.db.SqlDriver

actual object DatabaseFactory {
    actual fun createDriver(): SqlDriver {
        throw UnsupportedOperationException("Not yet implemented")
    }
}

