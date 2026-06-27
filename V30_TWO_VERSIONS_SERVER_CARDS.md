# DURBIN v30 - Two Versions + Server Banner Cards

Changed:
- Fabric/Minecraft version picker now shows only:
  - 1.21.11
  - 1.20.1
- If a different version was selected, it auto-selects the first available DURBIN-supported version.
- Server list UI now uses banner cards like a client launcher.
- Server card shows:
  - banner image
  - server name
  - IP
  - MOTD
  - PLAY button
- Backend dashboard server form now supports:
  - bannerUrl
- Launcher reads:
  durbin/servers/SERVER_ID/bannerUrl
