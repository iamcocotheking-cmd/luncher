# DURBIN Firebase Setup

## Important
Do NOT import google-services.json into Realtime Database.
That file belongs in your Android source app module.

Your Firebase app config:
- Project ID: durbin-f317c
- Package name: net.ashmeet.hyperlauncher
- App ID: 1:364017714123:android:857f20b9cad4a70085ac4e
- Database URL: https://durbin-f317c-default-rtdb.asia-southeast1.firebasedatabase.app

## Step 1: Fix current database
In Firebase Console > Realtime Database > Data:
Delete these wrong root nodes if you see them:
- client
- project_info
- configuration_version

Those came from importing google-services.json by mistake.

## Step 2: Import correct database JSON
In Realtime Database > Data:
1. Click the three-dot menu
2. Import JSON
3. Upload: durbin-realtime-database-IMPORT-THIS.json

## Step 3: Add rules
In Realtime Database > Rules:
Paste database.rules.json and Publish.

## Step 4: Android source
Put google-services.json here:
app_pojavlauncher/google-services.json

Also make sure Firebase Authentication > Sign-in method > Google is enabled.

## Step 5: Google login
Your uploaded google-services.json currently has an empty oauth_client list.
For Google sign-in, add your SHA-1 fingerprint in Firebase project settings, then download google-services.json again.
