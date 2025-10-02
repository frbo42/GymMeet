package org.fb.gym.meet.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.w3c.dom.Worker

actual object DatabaseFactory {
    actual fun createDriver(): SqlDriver {
        // Create a Web Worker that will run the SQLite database
        val worker = Worker(
            scriptURL = "./sqldriver.worker.js"
        )
        return WebWorkerDriver(worker)
    }
}