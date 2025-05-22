# 📸 Photo Search App

A modern Android application built as part of the ERA Singapore technical assignment.  
Users can search and explore photos using the [Pexels API](https://www.pexels.com/api/documentation/).

---

## 🧱 Architecture

The app follows **Clean Architecture** principles to maintain a scalable, modular, and testable codebase.  
In the **Presentation layer**, the UI logic is structured using **MVVM**, and further enhanced with **MVI** pattern for unidirectional data flow.

### Layered Structure:
📦 Data → API + Datasource + Repository implementations (remote + local)
📦 Domain → Use cases, models (pure Kotlin, no Android dependencies)
📦 Presentation → Jetpack Compose UI + ViewModel + MVI state management(State,Event,Effect)

---

## 🚀 Features Implemented

- 🔍 Search photos using Pexels API
- 💡 Keyword suggestions
- 🕘 Save & manage search history
- 🎛️ Filter by orientation, size, and color
- 📜 Infinite scroll with pagination
- 🔄 Pull-to-refresh
- 🖼️ Photo detail view
- ✌️ Pinch-to-zoom and double-tap zoom with pan support
- 💾 Image caching
- 🎨 Smooth UI animations
- ⚠️ Proper handling of loading, error, and empty states

---

## 🛠️ Tech Stack

- **Jetpack Compose** – Declarative UI toolkit
- **Hilt** – Dependency Injection
- **Retrofit** – Networking
- **Room** – Local database
- **Coil** – Image loading & caching
- **Kotlin Coroutines + Flow** – Async + reactive programming
- **Clean Architecture** – Layered and testable design
- **MVI + MVVM** – Robust state & event handling in UI

---

## ▶️ How to Run

1. Get a free API key from: https://www.pexels.com/api/
2. Add the key to your local `local.properties` file:
   PIXELS_API_KEY=your_api_key_here
3. Build and run the app using Android Studio (Electric Eel+ recommended).

---

## 📌 Possible Improvements (With More Time)

- 📥 Image download and save to gallery
- 🔥 Popular or trending images
- ⚡ Performance tuning for large image sets
- 🧹 Reduce boilerplate and hardcoded values
- 🧪 Add more tests (unit + UI)
  
---

## 📸 Screenshots

![Home](https://github.com/user-attachments/assets/fecbad12-c6a5-4ef6-a580-d1b9916cb57f)![Search](https://github.com/user-attachments/assets/63cbe38e-8188-4e8b-bdb3-91fb962c847c)![SearchResult](https://github.com/user-attachments/assets/fff27806-7f6c-41f6-aecd-01c5e495e882)
![Detail](https://github.com/user-attachments/assets/8ec7fd5f-39e4-44b6-a28c-23250c888b31)![Filter](https://github.com/user-attachments/assets/d01df96e-7892-4804-8342-214127fafedb)![NoData](https://github.com/user-attachments/assets/41f04d38-7a68-4bd3-af8b-f6f63fde617a)
![Error](https://github.com/user-attachments/assets/af3e520f-200e-4d15-8dde-5aba068dec08)

## ✍️ Author

[CÙ HUY HIẾU]  
