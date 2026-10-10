package com.example.aiinterviewcoach.data

data class EvaluationResult(
    val score: Int,
    val strengths: List<String>,
    val improvements: List<String>,
    val sampleAnswer: String
)
