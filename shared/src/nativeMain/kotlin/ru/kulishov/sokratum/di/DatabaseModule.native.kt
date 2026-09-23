package ru.kulishov.sokratum.di

import androidx.sqlite.SQLiteDriver
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.kulishov.sokratum.data.local.AppDatabase
import ru.kulishov.sokratum.data.local.getDriver
import ru.kulishov.sokratum.data.local.getRoomDatabase

actual fun platformDatabaseModule(): Module = module {
    single<SQLiteDriver> { getDriver() }
    single<AppDatabase> { getRoomDatabase(get()) }

    single { get<AppDatabase>().modelDao() }
    single { get<AppDatabase>().spaceDao() }
    single { get<AppDatabase>().chatDao() }
    single { get<AppDatabase>().messageDao() }
}