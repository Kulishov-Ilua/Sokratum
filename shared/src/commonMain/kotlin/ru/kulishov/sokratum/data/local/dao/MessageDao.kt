package ru.kulishov.sokratum.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.data.local.data.entity.ChatEntity
import ru.kulishov.sokratum.data.local.data.entity.MessageEntity
import ru.kulishov.sokratum.domain.model.Message

@Dao
interface MessageDao {
    @Query("select * from messages where idChat = :chatId order by sendTime")
    suspend fun getChatMessages(chatId: Long): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE idChat = :chatId ORDER BY id ASC")
    suspend fun getMessages(chatId: Long): List<MessageEntity>

//    @Query("SELECT * FROM messages WHERE idChat = :chatId AND sendTime > :updateTime ORDER BY sendTime")
//    suspend fun updateMessages(chatId: Long, updateTime: Long): List<MessageEntity>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMessage(message: MessageEntity): Long

    @Query("DELETE FROM messages WHERE idChat = :chatId")
    suspend fun deleteByChat(chatId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MessageEntity>)

    @Transaction
    suspend fun replaceAll(chatId:Long, items: List<MessageEntity>) {
        deleteByChat(chatId)
        insertAll(items)
    }


}