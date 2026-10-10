package com.example.aiinterviewcoach.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiinterviewcoach.BuildConfig
import com.example.aiinterviewcoach.data.AppDatabase
import com.example.aiinterviewcoach.data.EvaluationResult
import com.example.aiinterviewcoach.data.GeminiRepository
import com.example.aiinterviewcoach.data.InterviewHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface UiState {
    object Idle : UiState
    object Loading : UiState
    data class Success(val evaluation: EvaluationResult) : UiState
    data class Error(val message: String) : UiState
}

class InterviewViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GeminiRepository()
    private val dao = AppDatabase.getDatabase(application).interviewDao()

    // Expose past interview history as a reactive Flow
    val historyList = dao.getAllHistory()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val _currentQuestion = MutableStateFlow("What is the difference between StateFlow and SharedFlow in Kotlin Coroutines?")
    val currentQuestion: StateFlow<String> = _currentQuestion

    fun updateQuestion(newQuestion: String) {
        _currentQuestion.value = newQuestion
    }

    fun generateNewQuestion(topic: String) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank()) {
            _uiState.value = UiState.Error("API Key is missing. Please add GEMINI_API_KEY in local.properties.")
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val newQ = repository.generateRandomQuestion(apiKey, topic)
                _currentQuestion.value = newQ
                _uiState.value = UiState.Idle
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to generate question. Try typing one manually.")
            }
        }
    }

    fun submitForEvaluation(
        topic: String,
        question: String,
        userAnswer: String
    ) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        if (apiKey.isBlank()) {
            _uiState.value = UiState.Error("API Key is missing. Please add GEMINI_API_KEY in local.properties.")
            return
        }
        if (userAnswer.isBlank()) {
            _uiState.value = UiState.Error("Please write an answer before submitting.")
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val result = repository.evaluateAnswerWithRetry(
                    apiKey = apiKey,
                    topic = topic,
                    question = question,
                    userAnswer = userAnswer
                )

                // Save evaluation to Room database automatically
                dao.insertHistory(
                    InterviewHistory(
                        topic = topic,
                        question = question,
                        userAnswer = userAnswer,
                        score = result.score,
                        strengths = result.strengths.joinToString(", ")
                    )
                )

                _uiState.value = UiState.Success(result)
            } catch (e: Exception) {
                val errorMessage = if (e.message?.contains("503") == true || e.message?.contains("UNAVAILABLE") == true || e.message?.contains("high demand") == true) {
                    "The AI servers are busy right now. Please tap Submit again."
                } else {
                    e.localizedMessage ?: "Evaluation failed. Please try again."
                }
                _uiState.value = UiState.Error(errorMessage)
            }
        }
    }
}