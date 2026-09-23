package ru.kulishov.sokratum.data.local

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.koin.java.KoinJavaComponent

 fun getDriver(): SQLiteDriver = BundledSQLiteDriver()
fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("my_room.db")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}

 fun getRoomDatabase(
    driver: SQLiteDriver,
    context: Context
): AppDatabase {
    return getDatabaseBuilder(context)
        .setDriver(driver)
        .build()
}