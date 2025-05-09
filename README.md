# 🎨 Artist Search App (Android)

The **Artist Search App** is a Jetpack Compose-based Android application that allows users to search for artists using the Artsy API, view artist details, artworks, and similar artists, and manage their favorites list. The app supports both authenticated and guest users, with features dynamically adapting based on login state. This project was built as part of CSCI 571 Assignment 4 at USC.

---

## 🚀 Features

- 🔍 **Search Artists**: Search for artists with real-time API calls to Artsy.
- 📄 **Artist Details**: View artist bio, birth/death info, nationality, artworks, and similar artists.
- 🌟 **Favorites Management**: Add/remove artists to favorites (only for authenticated users).
- 🖼️ **Artworks Tab**: Explore an artist's artworks with images, dates, and category viewing.
- 🧠 **Similar Artists**: Authenticated users can view and navigate through similar artists.
- 👤 **Authentication**: Register and log in with email/password. JWT cookies handle session persistence.
- 🌗 **Dark/Light Theme**: UI adjusts for system theme using Material3 styling.
- ✅ **Responsive UI**: Designed to work on various Android devices (tested on Pixel 8 Pro).

---

## 📱 Screens

1. **Home**: Shows today's date, favorites section, login/register button or profile menu.
2. **Search**: Search input with debounced API queries and artist result cards.
3. **Artist Detail**: Tabs for Artist Info, Artworks, and Similar Artists (if logged in).
4. **Login/Register**: Validated input fields with UI feedback and success/error snackbars.

---

## 🛠️ Tech Stack

- **Frontend**: Jetpack Compose, Kotlin
- **State Management**: ViewModel + StateFlow
- **Networking**: Retrofit + OkHttp + Coil for image loading
- **Backend**: Node.js + Express
- **Database**: MongoDB Atlas (for user auth and favorites)
- **Authentication**: JWT + HttpOnly cookies

---

## 🧪 Prerequisites

- Android Studio Hedgehog or newer
- Android SDK 34 or higher
- A real or virtual Android device (tested on Pixel 8 Pro emulator)

---

## 📦 Installation

1. **Clone the repo:**
```bash
git clone https://github.com/yourusername/artist-search-android.git
cd artist-search-android
```

2. **Open in Android Studio:**
   - Open the project folder.
   - Let Gradle sync and download dependencies.

3. **Configure your backend URL (if needed):**
   - Update `ApiClient.kt` to point to your deployed backend server.

4. **Run the app:**
   - Choose a device or emulator.
   - Hit **Run ▶️** in Android Studio.

---

## 🔐 Backend Setup

The backend is hosted separately and includes routes for:

- `POST /auth/login`
- `POST /auth/register`
- `GET /search`
- `GET /artist/:id`
- `GET /artist/:id/artworks`
- `GET /artist/:id/similar`
- `GET /artwork/:id/categories`
- `GET/POST/DELETE /user/favorites`

> If you want to host your own, clone the Node.js backend from the Assignment 3 project and deploy to GCP or Render.

---

## 🧠 Learning Outcomes

- Built a full-featured Android app using Jetpack Compose
- Integrated with real-world APIs (Artsy) using Retrofit
- Managed state and UI feedback for authentication and data loading
- Worked with cookie-based JWT authentication in mobile environment
- Created responsive, theme-aware layouts with Compose

---

## 🙋‍♂️ Authors

- **Surin Shah**  
  Master's Student, University of Southern California  
  [LinkedIn](https://www.linkedin.com/in/surinshah)

---

## 📄 License

This project is for academic purposes as part of CSCI 571 and is not intended for commercial distribution.

---

## 📷 Screenshots

> *(Add emulator screenshots here for Home, Search, Artist Detail, Login, Register)*

---

## 💬 Feedback

Feel free to open issues or submit pull requests if you'd like to contribute or find bugs. Thanks for checking out the Artist Search App! 🎉
