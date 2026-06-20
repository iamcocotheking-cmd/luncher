# DURBIN Launcher V30 - Forge Installer Compile Fixed

This version fixes this build error:

- forge_installer/src/main/java/git/artdeell/installer_agent/Agent.java could not find:
  - ComponentFilter
  - ComponentTimeoutTask
  - MainWindowFilter
  - DialogFilter

Fix included:
- Added those 4 missing Java helper files.
- Added a workflow compile guard that recreates them before build if GitHub source is missing them.
- V29 remote modpack links, banners, Google login, auto renderer, no-sidebar UI, and Firebase features are kept.

Upload this full source to GitHub and run:
Actions -> Build DURBIN Launcher APK -> Run workflow
