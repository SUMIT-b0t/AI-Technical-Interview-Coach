# 🤖 AI Technical Interview Coach | Android

A production-ready, feature-rich Android application built to help developers and job seekers master technical interviews. Powered by **Google Gemini AI**, this app delivers dynamic question generation, instant AI-driven answer evaluations, and robust local session tracking using **Room Database**.

---

## ✨ Key Features

* **Dynamic Question Generation**: Automatically generates challenging technical interview questions tailored to your chosen topic or role using `gemini-3.8-flash`.
* **Real-Time AI Evaluation**: Submit your answers and receive immediate, structured feedback—including scores, key strengths, and areas for improvement.
* **Resilient API Handling**: Implements exponential backoff retry logic to gracefully handle network fluctuations and rate limits.
* **Offline History Tracking**: Uses **Room Database** to securely store your interview history, allowing you to review past scores and topics anytime.
* **Modern Dual-Tab UI**: Built entirely with **Jetpack Compose** featuring a seamless tabbed layout (Practice vs. History) powered by `LazyColumn` and reactive state flows.
* **Zero-Config Security**: Protects sensitive credentials by injecting the Gemini API key securely through `local.properties` and `BuildConfig`.

---

## 🛠️ Tech Stack & Architecture

* **Language**: [Kotlin](https://kotlinlang.org/)
* **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) & Material 3
* **Architecture**: **MVVM (Model-View-ViewModel)** with lifecycle-aware `AndroidViewModel` and unidirectional data flow using `StateFlow`.
* **Local Persistence**: [Room Database](https://developer.android.com/training/data-storage/room) for lightweight, offline-first data caching.
* **AI Integration**: [Google AI SDK for Android](https://github.com/google/generative-ai-android) (`gemini-3.8-flash`).

---

## 📱 App Architecture & Flow

```text
[ User Input / UI ] 
       │ (StateFlow)
       ▼
[ InterviewViewModel ] ──(Coroutines)──> [ Gemini AI SDK ]
       │                                         │
       ├──(Saves Session)                        ▼
       ▼                                  [ AI Evaluation & Score ]
[ Room Database ] <────────────────────────┘
