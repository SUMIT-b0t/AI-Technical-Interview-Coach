package com.example.aiinterviewcoach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.aiinterviewcoach.ui.InterviewScreen
import com.example.aiinterviewcoach.ui.theme.AIInterviewCoachTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AIInterviewCoachTheme {
                InterviewScreen()
            }
        }
    }
}