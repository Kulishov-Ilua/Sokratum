package ru.kulishov.sokratum.data.local.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.data.local.data.entity.ChatEntity

@Dao
interface ChatDao {
    @Query("Select * from chats where idSpace = :spaceId")
    suspend fun getChatsData(spaceId: Long): List<ChatEntity>

    @Query("select * FROM chats ORDER BY lastOpen DESC limit 3")
    suspend fun getLastChats(): List<ChatEntity>


    @Query("DELETE FROM chats")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ChatEntity>)

    @Transaction
    suspend fun replaceAll(items: List<ChatEntity>) {
        deleteAll()
        insertAll(items)
    }

    @Query("Select * from chats where id = :id")
    suspend fun getChat(id: Long): ChatEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addChat(chat: ChatEntity): Long

    @Update
    suspend fun updateChat(chat: ChatEntity)

    @Delete
    suspend fun deleteChat(chat: ChatEntity)

}