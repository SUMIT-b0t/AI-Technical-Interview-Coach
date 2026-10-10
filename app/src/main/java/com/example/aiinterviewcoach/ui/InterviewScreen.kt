package com.example.aiinterviewcoach.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aiinterviewcoach.viewmodel.InterviewViewModel
import com.example.aiinterviewcoach.viewmodel.UiState
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InterviewScreen(viewModel: InterviewViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val questionText by viewModel.currentQuestion.collectAsState()
    val historyList by viewModel.historyList.collectAsState(initial = emptyList())

    var selectedTab by remember { mutableStateOf(0) } // 0: Practice, 1: History

    var topic by remember { mutableStateOf("Android / Kotlin") }
    var question by remember { mutableStateOf(questionText) }
    var userAnswer by remember { mutableStateOf("") }

    LaunchedEffect(questionText) {
        question = questionText
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "AI Technical Interview Coach", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        // Tab Selector for Practice vs History
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Practice") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("History (${historyList.size})") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0) {
            // --- PRACTICE TAB ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("Interview Topic / Role") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Interview Question", style = MaterialTheme.typography.bodyMedium)
                    TextButton(
                        onClick = { viewModel.generateNewQuestion(topic) },
                        enabled = uiState !is UiState.Loading
                    ) {
                        Text("✨ Generate New Question")
                    }
                }

                OutlinedTextField(
                    value = question,
                    onValueChange = {
                        question = it
                        viewModel.updateQuestion(it)
                    },
                    label = { Text("Question") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = userAnswer,
                    onValueChange = { userAnswer = it },
                    label = { Text("Your Answer") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )

                Button(
                    onClick = {
                        viewModel.submitForEvaluation(topic, question, userAnswer)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState !is UiState.Loading
                ) {
                    if (uiState is UiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Submit for AI Evaluation")
                    }
                }

                when (val state = uiState) {
                    is UiState.Idle -> {}
                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("AI is working...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    is UiState.Success -> {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = "Score: ${state.evaluation.score}/10", style = MaterialTheme.typography.titleMedium)
                                Text(text = "Strengths:", style = MaterialTheme.typography.bodyMedium)
                                state.evaluation.strengths.forEach { strength ->
                                    Text(text = "• $strength", style = MaterialTheme.typography.bodySmall)
                                }
                                Text(text = "Areas for Improvement:", style = MaterialTheme.typography.bodyMedium)
                                state.evaluation.improvements.forEach { improvement ->
                                    Text(text = "• $improvement", style = MaterialTheme.typography.bodySmall)
                                }
                                Text(text = "Ideal Answer:", style = MaterialTheme.typography.bodyMedium)
                                Text(text = state.evaluation.sampleAnswer, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    is UiState.Error -> {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        } else {
            // --- HISTORY TAB ---
            if (historyList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No interview history yet. Submit a response to track your progress!", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(historyList) { history ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = history.topic, style = MaterialTheme.typography.titleSmall)
                                    Text(text = "Score: ${history.score}/10", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                                }
                                Text(text = "Q: ${history.question}", style = MaterialTheme.typography.bodySmall)
                                Text(text = "Strengths: ${history.strengths}", style = MaterialTheme.typography.bodySmall)

                                val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(history.timestamp))
                                Text(text = dateStr, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
            }
        }
    }
}