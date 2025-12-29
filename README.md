# IREN - Offline/Online AI Chatbot

A hybrid Android app that seamlessly switches between cloud-based Gemini AI and on-device LiteRT-LM based on network connectivity.

## Features

- 🔄 **Hybrid Intelligence**: Automatically switches between Gemini (online) and Gemma (offline)
- 🔐 **Firebase Authentication**: Secure login with session persistence
- 💾 **Offline-First**: Local Room database with smart sync strategy
- 🔒 **Privacy Mode**: Offline messages never sync to cloud
- 🧹 **Auto-Cleanup**: Automatic pruning of old offline messages (90 days)
- 📱 **Premium UI**: Modern, glassmorphic interface with theme support (Light/Dark/System)

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose (Material3)
- **Online AI**: Google Generative AI SDK (gemini-1.5-flash-002)
- **Offline AI**: LiteRT-LM / MediaPipe LLM Inference (Gemma 3n / Gemma 2B/4B)
- **Database**: Room
- **Auth**: Firebase Authentication
- **Background**: WorkManager
- **Architecture**: Repository Pattern with Flow

## Setup Instructions

### 1. Prerequisites
- Android Studio Ladybug or later
- Android SDK 26+
- Firebase project

### 2. Firebase Setup
1. Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com)
2. Enable Firebase Authentication (Email/Password)
3. Download `google-services.json` and place it in `app/` directory

### 3. API Keys
1. Get a Gemini API key from [Google AI Studio](https://makersuite.google.com/app/apikey)
2. The app secure handles API keys; ensure yours is configured in the environment or directly if testing.

### 4. Model Files
The app provides a dedicated download screen for Gemma models:
- Gemma 3n E2B (Optimized for Efficiency)
- Gemma 3n E4B (Optimized for Performance)
- Legacy Gemma 2B support

### 5. Build & Run
```bash
./gradlew assembleDebug
```

## Project Structure

```
app/src/main/java/com/example/hybridmind/
├── core/
│   └── NetworkMonitor.kt          # Connectivity tracking
├── data/
│   ├── ChatRepository.kt          # Hybrid routing logic
│   ├── ModelDownloader.kt         # Model download manager
│   └── local/
│       └── AppDatabase.kt         # Room database
├── ui/
│   ├── auth/
│   │   └── AuthScreen.kt          # Firebase login
│   ├── download/
│   │   └── DownloadScreen.kt      # Model selection
│   ├── chat/
│   │   └── ChatScreen.kt          # Main chat interface
│   └── theme/
│       └── Theme.kt               # IREN Design System
├── workers/
│   └── AutoPruneWorker.kt         # Background cleanup
└── MainActivity.kt                # App entry point
```

## Key Concepts

### Hybrid Router
The `ChatRepository` acts as the single source of truth, routing messages to either:
- **Online**: Gemini API + save to Room
- **Offline**: LiteRT-LM + save to Room with `is_offline_only=true` flag

### Privacy Rules
- Messages created while offline are marked as private
- They are **never synced** to the cloud, even when connectivity is restored
- Auto-pruned after 90 days to save space

### Memory Safety
- Checks available RAM before allowing large model downloads
- Model initialized in background to avoid UI jank
- WakeLock during model downloads to prevent interruption

## Roadmap
- [x] Dark Mode support
- [x] Multi-model support (Gemma 3n)
- [ ] Firestore sync for online messages
- [ ] Multimodal support (image input)
- [ ] Conversation export

## License
MIT
