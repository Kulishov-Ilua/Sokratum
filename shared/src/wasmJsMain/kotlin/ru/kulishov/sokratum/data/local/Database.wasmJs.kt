package ru.kulishov.sokratum.data.local

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import org.w3c.dom.Worker
@OptIn(ExperimentalWasmJsInterop::class)
private fun jsWorker(): Worker =
    js("""new Worker(new URL("sqlite-wasm-worker/worker.js", import.meta.url))""")

fun getDriver(): androidx.sqlite.SQLiteDriver {
    return WebWorkerSQLiteDriver(jsWorker())
}

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    return Room.databaseBuilder<AppDatabase>(
        name = "my_room.db"
    )
}
fun getRoomDatabase(
    driver: SQLiteDriver
): AppDatabase {
    return getDatabaseBuilder()
        .setDriver(driver)
        .build()
}