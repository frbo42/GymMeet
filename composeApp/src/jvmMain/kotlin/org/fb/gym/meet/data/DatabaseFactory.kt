package org.fb.gym.meet.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.fb.gym.meet.db.AppDatabase
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.exists

actual object DatabaseFactory {
    actual fun createDriver(): SqlDriver {
        val dir = Paths.get(System.getProperty("user.home"), ".gym-meet")
        val dbExists = dir.exists()
        Files.createDirectories(dir) // no-op if exists

        val dbPath = dir.resolve("gym-meet.db").toString()
        val url = "jdbc:sqlite:$dbPath"
        val driver = JdbcSqliteDriver(url)
        if (!dbExists) {
            AppDatabase.Schema.create(driver)
        }
        return driver
    }
}