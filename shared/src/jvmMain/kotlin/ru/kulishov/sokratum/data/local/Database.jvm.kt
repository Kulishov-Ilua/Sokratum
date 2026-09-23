package ru.kulishov.sokratum.data.local

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File

fun getDriver(): SQLiteDriver = BundledSQLiteDriver()

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "my_room.db")
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    )
}

fun getRoomDatabase(
    driver: SQLiteDriver
): AppDatabase {
    return getDatabaseBuilder()
        .setDriver(driver)
        .build()
}