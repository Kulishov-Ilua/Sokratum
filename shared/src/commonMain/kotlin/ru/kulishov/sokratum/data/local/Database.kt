package ru.kulishov.sokratum.data.local

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import androidx.sqlite.SQLiteDriver
import ru.kulishov.sokratum.data.local.dao.ChatDao
import ru.kulishov.sokratum.data.local.dao.MessageDao
import ru.kulishov.sokratum.data.local.dao.ModelDao
import ru.kulishov.sokratum.data.local.dao.SpaceDao
import ru.kulishov.sokratum.data.local.data.entity.ChatEntity
import ru.kulishov.sokratum.data.local.data.entity.MessageEntity
import ru.kulishov.sokratum.data.local.data.entity.ModelEntity
import ru.kulishov.sokratum.data.local.data.entity.SpaceEntity

@Database(
    entities = [ModelEntity::class,
        SpaceEntity::class,
        ChatEntity::class,
        MessageEntity::class], version = 1
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun modelDao(): ModelDao
    abstract fun spaceDao(): SpaceDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

