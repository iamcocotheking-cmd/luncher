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


## v16 Kotlin import fixed
- Fixed `DurbinFirebaseHubActivity.kt`.
- Added missing Compose imports for `verticalScroll` and `rememberScrollState`.
- This fixes the compile error from v15.


## v17 Artifact quota fix
- Build succeeded before; upload failed because GitHub artifact quota was full.
- Added release fallback so APK can still be downloaded from GitHub Releases.


## v18 Final UI cleanup
- Smaller Play button so the banner image is visible.
- Removed subtitle under DURBIN Launcher title.
- Removed placeholder news cards.
- Quick tool cards are bigger so text is not cut.
- Replaced Friends panel with a clean 9:16 ad image area.
- Added `durbin_ad_9_16.png` for your ad image.
- Firebase tier area is brighter/readable.


## v20 banners + animation
- Added Fabric, Forge, OptiFine, and new Minecraft/vanilla banners.
- Banner changes automatically depending on selected version/profile.
- New banners are optimized to 1280x720 to reduce lag.
- Added lightweight 90ms button press animations.
- Fixed missing `DurbinInlineNewsPanel` and `DurbinActionCard` compile errors.


## v21 No DURBIN Mod + Backend + White UI
- Removed DURBIN mod installer from launcher UI.
- Removed `DurbinInstaller.kt`.
- Added backend dashboard folder: `DURBIN_BACKEND_DASHBOARD_NETLIFY`.
- Backend supports news, PvP ranks, user rank, Gmail rank, ads, and admin rules.
- Replaced Install DURBIN button with News + Rank Hub.
- Made launcher/Firebase hub default text white.
- Added extra lightweight button press animations.


## v22 Dialog compile fix
- Fixed MainMenuScreen.kt terminate dialog.
- Removed duplicate `containerColor`.
- Fixed Kotlin composable-context compile errors around lines 165–189.
- Keeps all v21 changes: no DURBIN mod installer, backend folder, white text, animations.


## v23 Backend all ranks + Auth UID
- Backend Tier List now shows current category entries and all entries across every category.
- Added Auth UID dashboard panel.
- Dashboard records signed-in users under `durbin/dashboardUsers`.
- Auth UID panel shows current UID, dashboard users, and rank users by UID/Gmail.


## v24 No Sidebar + UI refresh
- Removed home side bar.
- Updated account adder/login UI.
- Updated file manager buttons.
- Settings no longer uses the side rail.
- Made topbar/selection text white.
- Added lightweight button press animations.


## v25 More UI polish
- Cleaner top navigation with less orange.
- Account Center UI improved.
- File Manager UI improved.
- Settings remains no-side-rail compact UI.
- More white text fixes.


## v26 WhiteVariant compile fix
- Fixed `Unresolved reference 'WhiteVariant'`.
- Replaced `Color.WhiteVariant` with `Color.White.copy(alpha = 0.70f)`.
- Keeps all v25 UI changes.


## v27 Server List Dashboard
- Added top-nav `Servers` button.
- Added launcher server list screen.
- Backend dashboard now supports adding/editing/deleting servers.
- Launcher reads `durbin/servers` and syncs enabled servers into Minecraft `servers.dat`.
- Existing `servers.dat` is backed up to `servers.dat.durbin_backup`.


## v28 Only two DURBIN mod builds
- Top nav `Addons` is now `DURBIN`.
- Users only see DURBIN 1.20.1 and DURBIN 1.21.11.
- The old Modrinth/extra versions are not shown from the top nav page.
- Download buttons open the official GitHub release ZIP links.


## v29 Launcher imports fixed
- Fixed LauncherScreen.kt compile error.
- Added missing Compose imports for DURBIN button animation.
- Keeps v28: only DURBIN 1.20.1 and 1.21.11 visible.


## v30 Two versions + server banner cards
- Version picker locked to only 1.21.11 and 1.20.1.
- Server list redesigned as banner cards with name + PLAY button.
- Backend server form now supports `bannerUrl`.


## v31 Real Install + Play
- DURBIN page now uses `Install + Play` instead of just opening download links.
- It downloads the selected ZIP inside the launcher.
- It extracts the ZIP's `.jar` mods into the instance `mods` folder.
- It installs/selects Fabric and launches Minecraft.
- It only supports DURBIN 1.20.1 and DURBIN 1.21.11.


## v32 Server compile fix
- Fixed missing `border` import.
- Fixed missing `Bitmap` import.
- Added missing `ic_px_play.xml`.
- Keeps v31 real DURBIN Install + Play.


## v33 Exact Bitmap import fix
- Fixed `Unresolved reference 'Bitmap'`.
- Added exact `import android.graphics.Bitmap`.
- Made the remote banner bitmap state type explicit.
- Keeps v31 real Install + Play and v32 server fixes.


## v34 Launch + Google fallback + server UI fix
- Fixed Minecraft 1.20.1 version JSON error by downloading base Mojang metadata before Fabric install.
- Google login now has anonymous/guest UID fallback.
- Server list no longer has an inner Home button.
- Server refresh is on the top bar.
- DURBIN mod card uses the uploaded DURBIN banner.


## v35 Launcher border import fix
- Fixed `Unresolved reference 'border'` in `LauncherScreen.kt`.
- Added `import androidx.compose.foundation.border`.
- Keeps all v34 fixes.


## v36 Restore mod downloader + new DURBIN banner
- Restored the normal Addons/mod downloader.
- Added DURBIN as a separate top-nav page.
- DURBIN page still only has 1.20.1 and 1.21.11 with Install + Play.
- Replaced the DURBIN mod banner with the new uploaded banner.


## v37 Home uses DURBIN banner
- Home hero banner now uses your new DURBIN banner when selected profile is DURBIN.
- DURBIN mod page also keeps the same banner.
- Other profiles keep their normal banners.
