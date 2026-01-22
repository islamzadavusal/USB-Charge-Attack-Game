# ⚡ USB Charge Attack

<div align="center">

![Platform](https://img.shields.io/badge/platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-purple.svg)
![Compose](https://img.shields.io/badge/Compose-Latest-blue.svg)
![License](https://img.shields.io/badge/license-MIT-orange.svg)

**Tilt your phone to dodge enemies. Plug in your charger to shoot. That simple.**

</div>

---

## 🎮 What is this?

A space shooter game with a twist - you control your ship by tilting your phone (gyroscope), and fire weapons by plugging in your USB charger. Turn a boring charging routine into an epic battle!

### Demo
> Add gameplay GIF here

---

## ✨ Features

- 📱 **Gyroscope Controls** - Tilt to move
- 🔌 **USB Fire System** - Plug charger = Triple shot
- 👾 **Smart Enemies** - They hunt you down
- 💥 **60 FPS Gameplay** - Smooth as butter
- 🎨 **Beautiful Animations** - Custom Canvas graphics

---

## 🏗️ Architecture

Built with **Clean Architecture** + **MVI** pattern for production-grade code quality.

```
┌─────────────────────────────────────────┐
│         Presentation (MVI)              │
│   GameScreen → ViewModel → Contract     │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│         Domain (Business Logic)         │
│   UseCases → Models → Repositories      │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│         Data (Implementation)           │
│   Repositories → DataSources            │
└─────────────────────────────────────────┘
```

### MVI Flow
```
User Action (Intent) → ViewModel → UseCase → New State → UI Update
```

**Why this matters:**
- ✅ Testable - Pure business logic
- ✅ Scalable - Easy to add features
- ✅ Maintainable - Clear separation of concerns
- ✅ Professional - Enterprise-level code quality

---

## 📁 Project Structure

```
com.islamzada.usbchargeattack/
│
├── 📂 data/
│   ├── repository/          # Repository implementations
│   └── source/              # Gyroscope & USB charging data sources
│
├── 📂 domain/
│   ├── model/               # Game entities (Enemy, Projectile, Position)
│   ├── repository/          # Repository interfaces
│   └── usecase/             # Business logic
│       ├── FireWeaponUseCase
│       ├── ObserveGyroscopeUseCase
│       ├── ObserveChargingStateUseCase
│       └── UpdateGameStateUseCase
│
└── 📂 presentation/
    └── game/
        ├── GameContract.kt  # Intent, State, Effect
        ├── GameViewModel.kt # State management
        ├── GameScreen.kt    # UI (Jetpack Compose)
        └── components/      # Player, Enemy, Projectile views
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- Android device with gyroscope (recommended)
- Min SDK 24

### Installation

```bash
git clone https://github.com/islamzada/usb-charge-attack.git
cd usb-charge-attack
./gradlew installDebug
```

That's it! Open the app and start the mission.

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Architecture | Clean Architecture + MVI |
| DI | Koin |
| Async | Coroutines + Flow |
| Sensors | Gyroscope API |
| System Events | BroadcastReceiver |

---

## 🎯 How It Works

### Gyroscope Control
```kotlin
// SensorDataSource converts gyroscope data to position offsets
fun observeGyroscope(): Flow<Position> = callbackFlow {
    val x = -event.values[1] * SENSITIVITY
    val y = event.values[0] * SENSITIVITY
    trySend(Position(x, y))
}
```

### USB Charging Detection
```kotlin
// ChargingDataSource listens for USB events
when (intent.action) {
    Intent.ACTION_POWER_CONNECTED -> trySend(true)  // Fire!
    Intent.ACTION_POWER_DISCONNECTED -> trySend(false)
}
```

### Game Loop
- 60 FPS (16ms tick rate)
- Enemy spawns every 2 seconds
- Real-time collision detection
- Delta-time based physics

---

## 🎨 Game Mechanics

| Element | Behavior |
|---------|----------|
| **Player** | Controlled by gyroscope, stays in bottom 30% of screen |
| **Enemies** | Spawn randomly, move toward player, game over on collision |
| **Projectiles** | Triple-shot spread pattern, destroy enemies on hit |
| **Scoring** | +1 point per enemy destroyed |

---

## 🔮 Roadmap

- [ ] Sound effects & music
- [ ] Multiple enemy types
- [ ] Power-ups
- [ ] High score leaderboard
- [ ] Boss battles
- [ ] Difficulty levels

---

## 🤝 Contributing

Contributions welcome! Please:
1. Follow existing architecture patterns
2. Write clean, testable code
3. Follow Kotlin conventions
4. Add comments for complex logic

---

## 📄 License

MIT License - feel free to use this project for learning or building upon.

---

## 👨‍💻 Author

**Islamzada**
- GitHub: [@islamzada](https://github.com/islamzada)

---

<div align="center">

### ⚡ Plug in and shoot! ⚡

**Star ⭐ this repo if you found it interesting!**

</div>
