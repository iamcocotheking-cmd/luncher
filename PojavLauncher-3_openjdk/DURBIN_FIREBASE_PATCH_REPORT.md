# DURBIN Firebase Patch Report

Added Firebase support for:

- News screen
- PvP tier list screen
- Google login
- Personal My Rank screen
- Firebase Realtime Database sample JSON
- Firebase Realtime Database security rules
- Firebase setup instructions

Changed files:

- app_pojavlauncher/build.gradle
- app_pojavlauncher/src/main/AndroidManifest.xml
- app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/kotlin/ui/screens/MainMenuScreen.kt
- app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/durbin/firebase/DurbinFirebaseHubActivity.kt
- app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/durbin/firebase/DurbinFirebaseConfig.kt
- app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/durbin/firebase/DurbinFirebaseModels.kt
- app_pojavlauncher/src/main/res/values/durbin_firebase_config.xml
- firebase/durbin-realtime-database.sample.json
- firebase/database.rules.json
- DURBIN_FIREBASE_SETUP.md

Main menu now includes:

- News button
- PvP Tier List button

Firebase database paths:

- durbin/news
- durbin/pvpTierLists
- durbin/userRanks/{firebaseUid}/ranks
- durbin/users/{firebaseUid}
