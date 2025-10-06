MATCHMATE ANDROID APP
(debug apk uploaded: https://github.com/YOGESH-PHOGAT/MatchMate/blob/master/app-debug.apk )

MatchMate is a simple Android application that showcases a list of potential user profiles.
Users can browse these profiles and mark them as either 'Accepted' or 'Declined'.
The app is designed with a modern Android architecture, usin mvvm pattern and separation of data, business and ui layers.

Built Using: Kotlin, Room, Glide, Retrofit, Coroutines, Livedata

Features of the app: 
- Separation of data, logic and ui using mvvm pattern
- Pagination support
- Offline persistence of data using room database
- Repo first data delivery 
- Clean loading screens with progress and empty response handling
- Reuse of single fragments and adapters with sealed classes and inheritance

Potential updates I would like to have in time (couldnt get around enough time to do): 
- Backend integration with batched syncing calls
- Smart checks in different fragments (currently only two usecases so used if else, would have used base classes instead for more maintainable codebase)
- Swipe to select (avoided due to time needed for fixing recycler view scrolling and swipe behaviour)
- Pull to refresh the card stock
- Pre-loading of user images during loading
