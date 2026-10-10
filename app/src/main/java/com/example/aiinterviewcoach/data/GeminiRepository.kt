package com.example.aiinterviewcoach.data

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.Gson
import kotlinx.coroutines.delay

class GeminiRepository {

    suspend fun generateRandomQuestion(apiKey: String, topic: String): String {
        val config = generationConfig {
            temperature = 0.7f
        }

        val generativeModel = GenerativeModel(
            modelName = "gemini-3.8-flash",
            apiKey = apiKey.trim(),
            generationConfig = config
        )

        val prompt = """
            You are an expert technical interviewer. 
            Generate a single, high-quality, professional technical interview question for the following topic or role: "$topic".
            Return ONLY the question text itself, without any introductory phrases, numbering, or extra quotes.
        """.trimIndent()

        val response = generativeModel.generateContent(
            content { text(prompt) }
        )

        return response.text?.trim() ?: "Explain a complex technical challenge you solved recently in $topic."
    }

    suspend fun evaluateAnswerWithRetry(
        apiKey: String,
        topic: String,
        question: String,
        userAnswer: String,
        retries: Int = 2
    ): EvaluationResult {
        var currentAttempt = 0
        while (true) {
            try {
                return evaluateAnswer(apiKey, topic, question, userAnswer)
            } catch (e: Exception) {
                currentAttempt++
                val isServerBusy = e.message?.contains("503") == true ||
                        e.message?.contains("UNAVAILABLE") == true ||
                        e.message?.contains("high demand") == true

                // If it's a server overload and we still have retries left, wait and loop again
                if (isServerBusy && currentAttempt <= retries) {
                    delay(2000L * currentAttempt) // Wait 2s, then 4s before retrying
                } else {
                    throw e // Otherwise throw the error up to the ViewModel
                }
            }
        }
    }

    private suspend fun evaluateAnswer(
        apiKey: String,
        topic: String,
        question: String,
        userAnswer: String
    ): EvaluationResult {

        val config = generationConfig {
            responseMimeType = "application/json"
            temperature = 0.4f
        }

        val generativeModel = GenerativeModel(
            modelName = "gemini-3.8-flash",
            apiKey = apiKey.trim(),
            generationConfig = config
        )

        val prompt = """
            You are an expert technical interviewer evaluating a Software Engineering candidate.
            Topic: $topic
            Question: $question
            Candidate Answer: $userAnswer

            Evaluate the candidate's response and return a strictly valid JSON object with this EXACT structure:
            {
              "score": <Integer from 1 to 10>,
              "strengths": ["<strength 1>", "<strength 2>"],
              "improvements": ["<improvement 1>", "<improvement 2>"],
              "sampleAnswer": "<Concise, high-scoring ideal answer>"
            }
        """.trimIndent()

        val response = generativeModel.generateContent(
            content {
                text(prompt)
            }
        )

        val rawJson = response.text ?: throw Exception("Empty response from Gemini AI")
        return Gson().fromJson(rawJson, EvaluationResult::class.java)
    }
}