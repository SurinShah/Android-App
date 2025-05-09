# 🎨 Artist Search App (Android)

The **Artist Search App** is a Jetpack Compose-based Android application that allows users to search for artists using the Artsy API, view artist details, artworks, and similar artists, and manage their favorites list. The app supports both authenticated and guest users, with features dynamically adapting based on login state.

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

## 📦 Installation

1. **Clone the repo:**
```bash
git clone https://github.com/SurinShah/Android-App.git
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

## 🧠 Learning Outcomes

- Built a full-featured Android app using Jetpack Compose
- Integrated with real-world APIs (Artsy) using Retrofit
- Managed state and UI feedback for authentication and data loading
- Worked with cookie-based JWT authentication in mobile environment
- Created responsive, theme-aware layouts with Compose

---

## 🙋‍♂️ Authors

- **Surin Shah**  
  [LinkedIn](https://www.linkedin.com/in/surinshah)

---

## 💬 Feedback

Feel free to open issues or submit pull requests if you'd like to contribute or find bugs. Thanks for checking out the Artist Search App! 🎉
