package ru.kulishov.sokratum.data.local

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteDriver

import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import org.w3c.dom.Worker

fun getDriver(): SQLiteDriver =
    WebWorkerSQLiteDriver(Worker(js("""new URL("sqlite-wasm-worker/worker.js", import.meta.url)""")))

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