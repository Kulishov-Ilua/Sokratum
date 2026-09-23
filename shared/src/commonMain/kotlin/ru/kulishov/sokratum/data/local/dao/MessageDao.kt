package ru.kulishov.sokratum.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.data.local.data.entity.MessageEntity
import ru.kulishov.sokratum.domain.model.Message

@Dao
interface MessageDao {
    @Query("select * from messages where idChat = :chatId order by sendTime")
    suspend fun getChatMessages(chatId: Long): List<MessageEntity>

    @Query("select * from messages where id = :id")
    suspend fun getMessage(id: Long): MessageEntity?

    @Query("SELECT * FROM messages WHERE idChat = :chatId AND sendTime > :updateTime ORDER BY sendTime")
    fun updateMessages(chatId: Long, updateTime: Long): Flow<List<MessageEntity>>


    @Insert
    suspend fun addMessage(message: Message): Long


}