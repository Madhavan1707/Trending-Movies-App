# Trending Movies App

An Android application that displays trending movies using [The Movie Database (TMDB) API](https://developers.themoviedb.org/3/getting-started/introduction). This project demonstrates a modern Android architecture using MVVM, dependency injection with Dagger, local persistence with Room, reactive programming with RxJava2, and image loading with Glide.


## Overview

The Trending Movies App retrieves movie data from TMDB and displays it in a visually appealing grid. Users can:
- View trending and now playing movies.
- Tap on a movie to see detailed information including release date, rating, cast, trailer, and similar movies.

The project follows a clean architecture pattern with separation of concerns: 
- **Presentation layer:** Uses MVVM with LiveData and ViewModel.
- **Data layer:** Communicates with the TMDB API via Retrofit and uses RxJava2 for asynchronous calls.
- **Local storage:** Persists movie data using Room for offline support.
- **Dependency Injection:** Managed by Dagger for modular, testable code.


## Features

- **Trending Movies:** Home screen displays trending movies in a 2-column grid.
- **Movie Details:** Detailed view with a collapsing toolbar that shows movie poster, title, overview, release date, rating (both numeric and via RatingBar), cast list with profile images, a trailer button that launches YouTube, and similar movies.
- **Offline Support:** Caches API responses using Room so that movies can be loaded even without an active internet connection.
- **Search**: An animated search bar in the search screen that provides debounced search results.
- **Offline Support**: Caches API responses using Room so that movies can be loaded even without an active internet connection.


## Libraries Used

- **Retrofit:** Used for making network calls to the TMDB API. 
- **RxJava2:** Manages asynchronous API calls and handles threading.
- **Glide:** Loads and caches images (e.g., movie posters and cast profile images).
- **Room:** Provides local database storage and ORM for caching movies.
- **Dagger 2:** Handles dependency injection for improved modularity and testing.
- **Material Components:** Implements Material Design UI elements such as MaterialToolbar, MaterialButton, and CardViews.
- **ConstraintLayout & CoordinatorLayout:** For flexible and responsive UI design.


## Setup

### Prerequisites

- Android Studio 4.0 or higher.
- Android API Level 31+ (target).
- A valid TMDB API key. Replace the placeholder in `MainApplication.java` with your key:
  ```java
  public static final String TMDB_API_KEY = "YOUR_API_KEY_HERE";
  ```

### Installation

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/Madhavan1707/Trending-Movies-App.git
   ```
2. **Open the project** in Android Studio.
3. **Sync Gradle** to download dependencies.
4. **Run the App** on your emulator or physical device.


## Capabilities and Limitations

### Capabilities

- **Real-Time Data:** Fetches up-to-date movie data from TMDB.
- **Responsive UI:** Implements a modern, responsive interface with Material Design components.
- **Offline Caching:** Stores movie data locally using Room, enabling basic offline support.
- **Modular Architecture:** Uses MVVM with Dagger and RxJava2 for maintainable, scalable code.
- **Interactive Details:** Detailed movie view includes cast, similar movies, and trailer playback via YouTube.

### Limitations

- **API Key Exposure:** The TMDB API key is stored in the source code; consider securing it using more advanced methods (e.g., gradle properties, server-side proxy) for production apps.
- **Basic Error Handling:** Current error handling is minimal (e.g., simple Toast messages). Consider expanding error management for a more robust user experience.
- **Limited Offline Functionality:** While movies are cached, the app may not update or support all interactions in offline mode.
- **Cast and Similar Movies:** These features depend on TMDB’s available data. If a movie has missing cast or similar movies information, those sections might appear empty.
- **Trailer URL:** The trailer button uses a placeholder URL. Integration with TMDB’s trailer endpoint (or YouTube API) can further enhance this feature.
- **Bookmarking**.


## Contributing

Feel free to fork this repository and submit pull requests. When contributing, please follow standard code style guidelines and write tests where appropriate.

![image](https://github.com/user-attachments/assets/9e84833a-2642-4970-8a58-6fd687604f7e)

![image](https://github.com/user-attachments/assets/134ac94b-2b27-4496-956d-7f9664c96f2f)

![image](https://github.com/user-attachments/assets/5ad81474-8dae-459d-a5e9-4c6a24a3359f)

![image](https://github.com/user-attachments/assets/1fd9accb-b023-4491-80af-1eb84933a9af)

![image](https://github.com/user-attachments/assets/cae2b958-d93b-4f73-ba7d-25a06842f309)

![image](https://github.com/user-attachments/assets/2220efd1-5ca8-42d2-b60d-ac51452dda7c)

![image](https://github.com/user-attachments/assets/11aa6f00-bd80-4728-842a-4fe243582d20)
