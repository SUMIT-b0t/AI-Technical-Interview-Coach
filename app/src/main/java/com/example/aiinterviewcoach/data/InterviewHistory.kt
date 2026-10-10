package com.example.aiinterviewcoach.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interview_history")
data class InterviewHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topic: String,
    val question: String,
    val userAnswer: String,
    val score: Int,
    val strengths: String, // Stored as comma-separated or JSON
    val timestamp: Long = System.currentTimeMillis()
)