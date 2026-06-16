# DURBIN Launcher Firebase Setup

This source includes a Firebase Hub for:

- DURBIN news
- Minecraft PvP tier lists
- Google login
- Personal rank lookup, for example HT1 / LT1 / HT2 / LT2

The app reads from Firebase Realtime Database paths like:

```text
durbin/news
durbin/pvpTierLists
durbin/userRanks/{firebaseUid}/ranks
```

## 1. Create Firebase project

1. Go to Firebase Console.
2. Create a project named `DURBIN Launcher`.
3. Add an Android app.
4. Use this Android package name:

```text
net.ashmeet.hyperlauncher
```

If you later change the app package/applicationId, make the Firebase app package match it exactly.

## 2. Enable Firebase Authentication Google login

1. Firebase Console -> Build -> Authentication.
2. Open **Sign-in method**.
3. Enable **Google**.
4. Add your app SHA-1 fingerprint in Project settings -> Your apps -> Android app.
5. Copy the **Web client ID** from Google Cloud / Firebase config.

## 3. Create Realtime Database

1. Firebase Console -> Build -> Realtime Database.
2. Create database.
3. Choose a location.
4. Start in locked mode first.
5. Go to **Rules** and paste the rules from:

```text
firebase/database.rules.json
```

## 4. Import the database JSON

1. Open Realtime Database.
2. Go to the **Data** tab.
3. Click the three-dot menu.
4. Choose **Import JSON**.
5. Upload this file:

```text
firebase/durbin-realtime-database.sample.json
```

Then edit the example players/news inside Firebase Console.

## 5. Add your Firebase app config to the Android source

Open this file:

```text
app_pojavlauncher/src/main/res/values/durbin_firebase_config.xml
```

Replace the placeholders:

```xml
<string name="durbin_firebase_api_key">PASTE_API_KEY_HERE</string>
<string name="durbin_firebase_application_id">PASTE_MOBILE_SDK_APP_ID_HERE</string>
<string name="durbin_firebase_project_id">PASTE_PROJECT_ID_HERE</string>
<string name="durbin_firebase_database_url">PASTE_DATABASE_URL_HERE</string>
<string name="durbin_firebase_web_client_id">PASTE_WEB_CLIENT_ID_HERE</string>
```

You can find most values inside your downloaded `google-services.json` file. The database URL is shown in Firebase Realtime Database.

## 6. How to add a news post

Add or edit this path:

```text
durbin/news/news_003
```

Example:

```json
{
  "title": "New DURBIN update",
  "body": "Added PvP tier list and Firebase news.",
  "tag": "Update",
  "timestamp": 1797552000000,
  "pinned": false,
  "imageUrl": "",
  "linkUrl": "https://discord.gg/PqnbXNrtHR"
}
```

## 7. How to add a PvP tier list category

Add categories inside:

```text
durbin/pvpTierLists
```

Suggested category IDs:

```text
tank
nethpot
sword
crystal
uhc
smp
bedwars
```

Tier format supported:

```text
HT1, LT1, HT2, LT2, HT3, LT3, HT4, LT4, HT5, LT5
```

## 8. How to make a Google user see their own rank

After a player logs in once, the app writes their profile to:

```text
durbin/users/{firebaseUid}
```

Copy that Firebase UID and add their rank here:

```text
durbin/userRanks/{firebaseUid}/ranks/{categoryId}
```

Example:

```json
{
  "categoryName": "Tank",
  "tier": "HT1",
  "score": 1000,
  "ign": "COSA",
  "region": "AS",
  "updatedAt": 1797379200000
}
```

Then when that Google account logs in, the **My Rank** tab will show their ranks.

## 9. Build

```bash
./gradlew :app_pojavlauncher:assembleFullDebug --stacktrace
```

If Firebase login does not work, check:

- Web client ID is pasted correctly
- Google sign-in is enabled
- SHA-1 fingerprint is added
- Database rules are deployed
- Database URL is correct
