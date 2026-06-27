# DURBIN v34 - Launch, Google fallback, server UI fix

Fixed/changed:
- DurbinClientInstaller now downloads the base Minecraft version JSON before installing Fabric.
  This fixes: Unable to read Version JSON for version 1.20.1
- Google login now falls back to Firebase anonymous/guest UID login if Google fails.
- Firebase Hub button says Login instead of Google.
- Server list redesigned:
  - no Home button inside server list
  - Refresh button is at the top
  - no instruction text about refreshing
  - clicking PLAY syncs that server
- DURBIN mod page uses the uploaded DURBIN banner.
