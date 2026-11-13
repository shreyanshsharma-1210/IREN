# IREN User Manual

Welcome to **IREN** (formerly HybridMind), your intelligent hybrid AI assistant that works seamlessly both online and offline. This manual will guide you through setting up and using the app to its full potential.

## Table of Contents
1. [Introduction](#introduction)
2. [Getting Started](#getting-started)
    - [Installation](#installation)
    - [Authentication](#authentication)
    - [Initial Setup & Downloads](#initial-setup--downloads)
3. [Core Features](#core-features)
    - [Chat Interface](#chat-interface)
    - [Hybrid Intelligence (Online vs. Offline)](#hybrid-intelligence-online-vs-offline)
    - [Privacy & Security](#privacy--security)
4. [Settings & Customization](#settings--customization)
5. [Troubleshooting](#troubleshooting)

---

## Introduction

IREN is designed to provide you with a robust AI experience regardless of your internet connection.
- **Online Mode**: Uses Google's **Gemini 1.5 Flash** for high-speed, high-accuracy responses and complex reasoning when you have internet access.
- **Offline Mode**: Uses **Gemma 3n / 2B** models running locally on your device aka **LiteRT-LM**, ensuring you can chat even without a signal.

## Getting Started

### Installation
Ensure your device runs Android 8.0 (Oreo) or higher. Install the APK provided by your administrator or download it from the official release channel.

### Authentication
Upon opening the app, you will be greeted by the Welcome Screen.
1. **Sign Up**: Create a new account using your email and password.
2. **Log In**: Access your existing account.
   *Note: Authentication requires an internet connection.*

### Initial Setup & Downloads
To unlock Offline capabilities, you must download a local AI model.
1. After logging in, you will be prompted to download a model if you haven't already.
2. Select your preferred model version:
    - **Standard (Gemma 3n E2B)**: ~2GB. Best for older devices or saving space. Good speed.
    - **Advanced (Gemma 3n E4B)**: ~4GB. Higher accuracy and better reasoning. Requires more storage and RAM.
3. Tap **Download**. You can minimize the app; the download will continue in the background.

> **Tip**: We recommend downloading over Wi-Fi to avoid data charges.

---

## Core Features

### Chat Interface
The chat screen is your command center.
- **Text Input**: Type your query in the bottom text field.
- **Voice Input**: Tap the **Microphone** icon to speak your message.
- **Image Input**: Tap the **+** (Plus) icon to attach an image from your Gallery or Camera. (Multimodal support available in both online and offline modes for compatible models).

### Hybrid Intelligence (Online vs. Offline)
IREN automatically detects your network status.
- **Online Indicator**: A green "Online - Gemini" status at the top means you are connected. Your queries are processed by Google's powerful cloud AI.
- **Offline Indicator**: A grey "Offline - Local" status means you are disconnected. The app instantly switches to the on-device Gemma model.

### Privacy & Security
Your privacy is paramount.
- **Offline Privacy**: Messages sent while **offline** are processed entirely on your device. They are **NEVER** synced to the cloud.
- **Auto-Prune**: To manage local storage, offline messages are automatically deleted after **90 days**.
- **Secure Auth**: Your account is protected by industry-standard Firebase Authentication.

---

## Settings & Customization
Access settings by tapping the **Menu** icon (top-left) > **Settings**.

- **Theme**: Switch between **Light**, **Dark**, or **System Default** to match your device settings.
- **Model Management**: Re-download or switch between Standard and Advanced offline models.
- **Account**: Sign out of your current session.

---

## Troubleshooting

### Download Failed?
- Check your internet connection.
- Ensure you have enough free storage space (at least 5GB recommended).
- Tap "Try Again" on the download screen.

### App Running Slow?
- If using the "Advanced" model on an older device, try switching to the "Standard" model in Settings for better performance.

### "Model Not Initialized" Error?
- This may happen immediately after download. Restart the app to allow the AI engine to load the new model files fresh.
