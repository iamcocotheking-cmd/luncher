# DURBIN Web Dashboard - Netlify Ready

This is a static web dashboard for DURBIN Launcher.

## What it can do

- Google login
- Add/edit/delete launcher news
- Add/delete PvP tier entries
- Add a user-specific rank for "My Rank"
- Works with Firebase Realtime Database

## Firebase project included

Project ID:

```text
durbin-f317c
```

Database URL:

```text
https://durbin-f317c-default-rtdb.asia-southeast1.firebasedatabase.app
```

## Deploy to Netlify

Easy method:

1. Go to Netlify.
2. Add new site.
3. Deploy manually.
4. Drag this whole folder or the ZIP into Netlify.
5. After deploy, copy your site domain, example:

```text
durbin-dashboard.netlify.app
```

## Important Firebase login setup

Go to:

```text
Firebase Console → Authentication → Settings → Authorized domains
```

Add your Netlify domain:

```text
your-site-name.netlify.app
```

Without this, Google login will fail.

## Make yourself admin

1. Open the dashboard.
2. Sign in with Google.
3. Click "Copy UID".
4. Go to Firebase Realtime Database → Data.
5. Add this:

```text
durbin/admins/YOUR_UID = true
```

## Database rules

Copy the rules from:

```text
firebase-rules.json
```

Paste them here:

```text
Firebase Console → Realtime Database → Rules
```

## News path

```text
durbin/news/news_001
```

## Tier path

```text
durbin/pvpTierLists/axe/entries/cosa
```

## My Rank path

```text
durbin/userRanks/USER_UID/ranks/axe
```


## Dashboard admin login

This version has a simple admin lock before the dashboard opens.

Admin users:

```text
COSA
MOD
```

Password:

```text
catslikecosa
```

Important:
This is a simple static-site lock. For real security, keep Firebase Realtime Database admin UID rules enabled.


## Gmail rank support

Use the "My Rank" tab.

Fill:

```text
Player Gmail: the same email the player uses in the launcher
Minecraft IGN
Category
Tier
Score
Region
```

The dashboard writes to:

```text
durbin/userRanksByEmail/email_key/ranks/category
```

It also writes to the old UID path if you paste UID:

```text
durbin/userRanks/uid/ranks/category
```

Email key example:

```text
cosa@gmail.com → cosa_at_gmail_dot_com
```


## Loading forever fix

This version stops infinite loading.

If Firebase blocks the request, the dashboard now shows the exact error and database path.

Common fixes:
- Add your UID in `durbin/admins/YOUR_UID = true`
- Paste `firebase-rules.json` into Realtime Database rules
- Make sure your Netlify domain is in Firebase Auth authorized domains
- Make sure Realtime Database is created in the same Firebase project


## Ad Adder

This version has an Ads tab.

It writes to:

```text
durbin/ads/main
```

Use a direct image URL ending in:

```text
.png
.jpg
.jpeg
.gif
.webp
```

Important:
The dashboard can save ads now. The current launcher must also be patched to read `durbin/ads/main` if you want the ad to change live from Firebase.


## Clean UI update

This version redesigns the admin dashboard UI:
- cleaner topbar
- cleaner sidebar
- cleaner cards/forms
- better mobile layout
- cleaner error cards
- updated `firebase-rules.json` with `.indexOn: ["timestamp"]` for news
