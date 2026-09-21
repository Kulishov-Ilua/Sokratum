package ru.kulishov.sokratum.data.local.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.data.local.data.entity.ChatEntity

@Dao
interface ChatDao {
    @Query("Select * from chats where idSpace = :spaceID")
    fun getChatsData(spaceId: Long): Flow<List<ChatEntity>>

    @Query("select * FROM chats ORDER BY lastOpen DESC limit 10")
    fun getLastChats(): Flow<List<ChatEntity>>

    @Query("Select * from chats where id = :id")
    suspend fun getChat(id: Long): ChatEntity?

    @Insert
    suspend fun addChat(chat: ChatEntity): Long

    @Update
    suspend fun updateChat(chat: ChatEntity): Long

    @Delete
    suspend fun deleteChat(chat: ChatEntity)

}