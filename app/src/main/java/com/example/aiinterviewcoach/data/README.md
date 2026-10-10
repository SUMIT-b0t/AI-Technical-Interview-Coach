# AI Technical Interview Coach 🚀
A production-ready native Android application built with **Jetpack Compose** and **Google Gemini AI SDK** that acts as an interactive technical interview coach, evaluating candidate answers and generating dynamic questions in real-time.

## 🛠️ Tech Stack & Architecture
* **UI Framework:** Jetpack Compose, Material Design 3
* **Architecture:** MVVM (Model-View-ViewModel) with Unidirectional Data Flow using Kotlin `StateFlow`
* **AI Integration:** Google AI Client SDK (`gemini-3.8-flash`) with structured JSON parsing via Gson
* **Security & Resilience:** Zero-config architecture leveraging secure `local.properties` + `BuildConfig` injection, coupled with automated exponential backoff retry logic for server-overload (503) states.
* **Asynchronous Handling:** Kotlin Coroutines & Lifecycle ViewModel scopes.

## 📱 Features
* **Dynamic Question Generation:** Instantly generates fresh, role-specific technical interview questions on demand using Gemini.
* **Real-time AI Evaluation:** Evaluates software engineering answers and returns precise scoring (1-10), strengths, and areas for improvement.
* **Resilient Network Handling:** Gracefully handles high-demand server states with automated retry mechanisms and user-friendly states.
* **Zero-Configuration UX:** Secure compile-time key injection ensures out-of-the-box readiness for reviewers without manual API key entry.

## 🚀 Getting Started
1. Clone the repository.
2. Add your `GEMINI_API_KEY=your_key` inside `local.properties`.
3. Open the project in Android Studio and run it on an emulator or physical device.