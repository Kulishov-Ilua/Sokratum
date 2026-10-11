package ru.kulishov.sokratum.di

import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.kulishov.sokratum.data.local.AppDatabase
import ru.kulishov.sokratum.data.local.getRoomDatabase

actual fun platformDatabaseModule(): Module = module {
    single<SQLiteDriver> { BundledSQLiteDriver() }
    single<AppDatabase> { getRoomDatabase(get()) }

    single { get<AppDatabase>().modelDao() }
    single { get<AppDatabase>().spaceDao() }
    single { get<AppDatabase>().chatDao() }
    single { get<AppDatabase>().messageDao() }
}