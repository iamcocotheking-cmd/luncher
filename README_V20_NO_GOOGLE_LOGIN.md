# DURBIN v20 No Google Login

This is a workflow-only patch.

Do NOT delete your source.
Only replace:
.github/workflows/build-durbin-launcher.yml

What it does:
- Removes Firebase Auth dependency from app_pojavlauncher/build.gradle
- Removes Google Play Services Auth dependency
- Replaces DurbinFirebaseHubActivity.kt with a no-login Firebase hub
- Keeps News and PvP Tier List working from Realtime Database
- Removes Google button, Sign out button, and My Rank login system

Firebase paths still used:
- durbin/news
- durbin/pvpTierLists
