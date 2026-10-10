package com.example.aiinterviewcoach.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InterviewDao {
    @Query("SELECT * FROM interview_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<InterviewHistory>>

    @Insert
    suspend fun insertHistory(history: InterviewHistory)
}