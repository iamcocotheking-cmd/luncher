# DURBIN Launcher fixed source v3

Upload these files to your GitHub repo root.

Repo root should contain:
- `.github/workflows/build-durbin-launcher.yml`
- `PojavLauncher-3_openjdk/`

Important:
- Delete old repo files first.
- Do not merge with old files.
- This version fixes the missing `dnbglfw/src/main/cpp/glfw` native source issue.
- The workflow tries to download the real MojoLauncher GLFW source.
- If GitHub cannot download it, it falls back to a small compile-safe GLFW stub so the APK can still build.


## v4 UI fixes
- Made landscape dashboard scrollable.
- News and PvP Tier buttons are visible at the top of the dashboard.
- Restored base-style tools: Wiki, Discord, Custom controls, Execute .jar, Share log, Open game directory.
- Reduced heavy glow/background/elevation for less lag.
- Made top task bar buttons smaller and cleaner.


## v5 Better GUI
- Removed portrait/vertical dashboard branching.
- The DURBIN home now always uses the horizontal dashboard layout.
- Added horizontal + vertical scrolling so small screens do not hide cards.
- Improved visibility: text is white/gray instead of black-on-dark.
- Better action cards: News, PvP Tier List, Wiki, Discord, Controls, Execute .jar, Logs, Game Directory, YouTube, DURBIN Mode.
- Top task bar is smaller, darker, and cleaner.
- Removed heavy visual effects for less lag.


## v6 Banner + DURBIN Mode
- Added banner image switching by selected profile:
  - DURBIN profiles show `durbin_banner.png`
  - Normal Vanilla/Fabric/Forge profiles show `minecraft_banner.png`
- Added `Install DURBIN` button.
- `Install DURBIN` creates a `DURBIN <version>` profile.
- It installs Fabric for the selected Minecraft version, then downloads Sodium, Iris, and Lithium into `.minecraft/mods`.
- If the selected version is too old or unclear, it uses Minecraft 1.21.1.


## v8 UI corrected
- Removed main menu vertical/portrait behavior.
- Locked launcher activities to landscape/sensorLandscape.
- Removed the banner from the top.
- Moved the version banner above the Play button.
- Banner is rounded and cropped with ContentScale.Crop.
- News is now inline under Launcher Hub, not a separate page.
- PvP Tier List still opens its tier page.


## v9 Transparent background
- Main DURBIN launcher background is fully transparent.
- Glass panels are transparent with borders only.
- Action cards/buttons are transparent.
- Top task bar background is transparent.


## v10 Firebase fixed
- Filled Firebase Android config from google-services.json.
- Database URL set to `https://durbin-f317c-default-rtdb.asia-southeast1.firebasedatabase.app`.
- News and Tier List now use the exact Realtime Database URL.
- Google login still needs SHA1 + fresh google-services.json with Web Client ID.
- News/tier reading does not need Google login if your database rules allow public read.


## v11 SHA1 + Google login ready
- Replaced google-services.json with the new SHA1-enabled Firebase file.
- Added Web Client ID to `durbin_firebase_config.xml`.
- Android OAuth client is present.
- Package name remains `net.ashmeet.hyperlauncher`.
- Certificate hash/SHA1 without colons: `17d6f8a1a38eb2efb7b2c7a775999cf40d468410`


## v12 Reference GUI + Google fix
- Main menu rebuilt closer to the reference image:
  - left icon sidebar
  - big hero/banner launch card
  - latest news image cards
  - right friends/profile panel
  - tools section below news
- Main content is vertically scrollable on the Y-axis.
- The banner is now a large visible hero panel.
- Fixed Google sign-in error 10 cause:
  - debug APK no longer uses `.debug` package suffix
  - app package now matches Firebase: `net.ashmeet.hyperlauncher`
  - debug keystore SHA1 matches the SHA1 in the provided google-services.json
- After installing this version, uninstall the old `.debug` app if it is still on the phone.


## v13 Final polish before compile
- Safer Firebase news loader code.
- Bigger visible hero/banner.
- Main menu remains Y-axis scrollable.
- News tiles polished.
- Added final compile checklist.
- Workflow now checks:
  - no merge conflict markers
  - Firebase package matches app package
  - Web Client ID exists
  - SHA1 is printed before build


## v15 Workflow false-positive fixed
- Fixed the workflow conflict-marker check.
- It now ignores binary files like `.so`.
- It no longer mistakes normal source comment separator lines as merge conflicts.
- Real merge conflict markers are still detected.
