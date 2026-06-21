# DURBIN Backend Dashboard

This backend controls the launcher data in Firebase Realtime Database.

It supports:
- News
- PvP tier list
- User-specific My Rank
- User rank by Gmail
- Ads
- Admin UID rules

Deploy:
1. Go to Netlify.
2. Drag this whole `DURBIN_BACKEND_DASHBOARD_NETLIFY` folder into Netlify.
3. In Firebase Authentication, add your Netlify domain to Authorized domains.
4. In Firebase Realtime Database, paste rules from `firebase-rules.json`.
5. Open the dashboard, sign in with Google, copy UID.
6. Add this in Realtime Database:

durbin/admins/YOUR_UID = true

Launcher paths:
- News: durbin/news
- Tier list: durbin/pvpTierLists
- My Rank by UID: durbin/userRanks
- My Rank by Gmail: durbin/userRanksByEmail
- Ads: durbin/ads/main
