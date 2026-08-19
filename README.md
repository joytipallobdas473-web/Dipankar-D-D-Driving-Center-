# 🚗 D&D Driving Center — Android App & Web Portal

[![Android CI & APK Build](https://github.com/your-username/dd-driving-center/actions/workflows/android.yml/badge.svg)](https://github.com/your-username/dd-driving-center/actions/workflows/android.yml)
[![Vercel Deployment](https://img.shields.io/badge/Deployed_on-Vercel-black?style=flat&logo=vercel)](https://vercel.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple.svg?logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=android)](https://developer.android.com/jetpack/compose)
[![Material Design 3](https://img.shields.io/badge/Design-Material%203-7C4DFF)](https://m3.material.io)

> **"Drive with Confidence. Learn from the Best."**  
> D&D Driving Center is a premium native Android application with interactive 3D driving simulator, official RTO mock exams, live GPS instructor radar, slot booking in INR ₹, EMI installment calculator, and roadside emergency assistance.

---

## 🌟 Key Features

- **🎮 3D Interactive Driving Simulator**:
  - Steerable wheel, accelerator & brake pedals with custom Canvas physics.
  - Clutch biting-point telemetry and Arcade Nitro Boost (NOS 🚀) with speed streaks.
  - Oncoming dynamic 3D vehicles with headlight beam projections and obstacle evasion.
- **🛑 Official RTO Exam Practice**:
  - Real Indian traffic signs, road safety rules, and instant scorecards.
- **📍 Live GPS Pickup Tracker**:
  - Real-time animated radar showing pickup vehicle location, instructor name, vehicle number, and live ETA.
- **💳 0% Interest EMI Fee Calculator**:
  - Flexible installments (3, 6, 9, 12 months) for all packages with instant UPI payment confirmation simulation.
- **🏆 Weekly Learner Leaderboard & Badges**:
  - City-wide XP rankings and unlockable badges (*Clutch King, Parking Pro, Night Navigator*).
- **🆘 24x7 Roadside Safety SOS**:
  - Direct 1-tap emergency patrol hotline (`+91 98765 43210` & `112`) and step-by-step troubleshooting guides (flat tires, jump-starts, overheating).
- **📂 Digital Document Wallet**:
  - Secure local storage for Learner's License, Aadhaar, PAN card, and Medical Certificates.

---

## 🏗️ Technical Architecture

- **Language & Runtime**: Kotlin (JVM 17)
- **UI Framework**: Jetpack Compose with Material Design 3 (M3)
- **Theme**: Eye-Comfort Nordic Warm Charcoal & Muted Amber Gold palette
- **Database / Persistence**: Room SQLite Database with modern KSP compiler
- **Asynchronous Flow**: Kotlin Coroutines & `StateFlow`
- **CI / CD**: GitHub Actions workflow for automated debug APK compilation
- **Web Portal / Hosting**: Vercel-ready static showcase (`index.html`, `vercel.json`)

---

## 🚀 Deployment Instructions

### 1. Push to GitHub

Initialize your repository and push to GitHub:

```bash
git init
git add .
git commit -m "feat: Initial commit of D&D Elite Driving Center"
git branch -M main
git remote add origin https://github.com/<YOUR-USERNAME>/<YOUR-REPO-NAME>.git
git push -u origin main
```

*(The included `.github/workflows/android.yml` will automatically build the Android APK upon every push!)*

---

### 2. Deploy to Vercel (Web Showcase & Portal)

1. Open [Vercel](https://vercel.com) and log in.
2. Click **"Add New..."** ➔ **"Project"**.
3. Import your GitHub repository (`<YOUR-REPO-NAME>`).
4. Keep the default settings (Framework Preset: **Other**, Root Directory: `./`).
5. Click **"Deploy"**.

Vercel will immediately deploy and host the luxury D&D Driving Center showcase web page with zero configuration required!

---

### 3. Open & Build in Android Studio

1. Open **Android Studio** (Ladybug / Koala / Hedgehog or newer).
2. Select **File ➔ Open** and choose the repository folder.
3. Allow Gradle to sync dependencies.
4. Run on an Android Emulator or physical device (API 26+ / Android 8.0+).

To build the APK locally via terminal:

```bash
./gradlew assembleDebug
```

The compiled APK will be output to:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📄 License
This project is licensed under the MIT License.
