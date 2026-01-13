# 🛡️ CyberPulse - Privacy-First Cybersecurity Companion

![Android](https://img.shields.io/badge/Android-14+-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)

## 📖 Overview

**CyberPulse** is an ultra-smooth, minimalist, and privacy-focused Android application designed for cybersecurity professionals and students. It aggregates real-time cyber news, breach alerts, learning resources, and security events into one sleek interface.

## ✨ Key Features

### 🔐 Privacy-First Architecture
- **Google One-Tap Sign-In Only** - No email/password storage
- **Data Sovereignty Modal** - Users control exactly what data is stored
- **Stateless Mode** - Full functionality without any data persistence
- **Zero Tracking** - No analytics unless explicitly allowed

### 📰 Real-Time Cyber Intelligence
- Curated news from top security sources
- Smart auto-tagging: #Ransomware, #ZeroDay, #DataBreach, #PatchTuesday
- Breach Radar with dedicated data leak feed
- CVE Quick Lookup

### 🎓 Learning Hub
- Latest courses from Udemy, Coursera, SANS
- Free certification resources
- Skill path recommendations

### 🏆 Events & Community
- Hackathon calendar
- CTF event tracker
- Webinar schedule with reminders

### 🔔 Smart Notifications
- Granular FCM-based alerts
- Topic subscriptions: Critical Only | All News | Hackathon Alerts
- Daily Cyber Tips

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         PRESENTATION                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │
│  │   Screens   │  │  ViewModels │  │   States    │              │
│  │  (Compose)  │◄─┤    (Hilt)   │◄─┤   (Flow)    │              │
│  └─────────────┘  └─────────────┘  └─────────────┘              │
└───────────────────────────┬─────────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────────┐
│                          DOMAIN                                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │
│  │  Use Cases  │  │   Models    │  │ Repositories│              │
│  │             │  │   (Clean)   │  │ (Interfaces)│              │
│  └─────────────┘  └─────────────┘  └─────────────┘              │
└───────────────────────────┬─────────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────────┐
│                           DATA                                   │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │
│  │   Remote    │  │    Local    │  │   Firebase  │              │
│  │  (Retrofit) │  │   (Room)    │  │ (Auth/FCM)  │              │
│  └─────────────┘  └─────────────┘  └─────────────┘              │
└─────────────────────────────────────────────────────────────────┘
```

## 🛠️ Tech Stack

| Category | Technology |
|----------|------------|
| Language | Kotlin 1.9+ |
| UI | Jetpack Compose (Material 3) |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| Networking | Retrofit + OkHttp |
| Local DB | Room |
| Auth | Firebase Auth (Google Sign-In) |
| Push | Firebase Cloud Messaging |
| Async | Kotlin Coroutines + Flow |

## 📁 Project Structure

```
app/
├── src/main/
│   ├── java/com/cyberpulse/
│   │   ├── CyberPulseApp.kt
│   │   ├── MainActivity.kt
│   │   │
│   │   ├── core/
│   │   │   ├── di/                    # Hilt modules
│   │   │   ├── navigation/            # NavHost & routes
│   │   │   ├── network/               # Retrofit setup
│   │   │   └── util/                  # Extensions, constants
│   │   │
│   │   ├── data/
│   │   │   ├── local/
│   │   │   │   ├── dao/               # Room DAOs
│   │   │   │   ├── entity/            # Room entities
│   │   │   │   └── CyberPulseDatabase.kt
│   │   │   ├── remote/
│   │   │   │   ├── api/               # Retrofit services
│   │   │   │   └── dto/               # API response models
│   │   │   └── repository/            # Repository implementations
│   │   │
│   │   ├── domain/
│   │   │   ├── model/                 # Domain models
│   │   │   ├── repository/            # Repository interfaces
│   │   │   └── usecase/               # Business logic
│   │   │
│   │   ├── presentation/
│   │   │   ├── auth/                  # Login & data consent
│   │   │   ├── home/                  # Dashboard & news feed
│   │   │   ├── breach/                # Breach Radar
│   │   │   ├── academy/               # Learning hub
│   │   │   ├── events/                # Hackathons & CTFs
│   │   │   ├── tools/                 # HIBP, CVE lookup
│   │   │   └── components/            # Reusable UI components
│   │   │
│   │   └── ui/
│   │       └── theme/                 # CyberPulse design system
│   │
│   └── res/
│       ├── values/
│       ├── drawable/
│       └── font/
│
├── build.gradle.kts (app)
└── build.gradle.kts (project)
```

## 🚀 Getting Started

1. Clone the repository
2. Add your `google-services.json` to `/app`
3. Configure API keys in `local.properties`:
   ```
   NEWS_API_KEY=your_key
   HIBP_API_KEY=your_key
   ```
4. Build and run!

## 📜 License

MIT License - See LICENSE file for details.

---

**Built with 💚 for the Cybersecurity Community**
