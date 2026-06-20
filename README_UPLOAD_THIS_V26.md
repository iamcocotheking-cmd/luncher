# DURBIN Launcher V26

Changes:
- Added your new banners:
  - Forge banner
  - Fabric banner
  - OptiFine/OF banner
- Main menu banner auto changes based on selected version name/version id.
- Google login is enabled again.
- Firebase dependency versions are pinned to avoid Kotlin metadata errors.
- Firebase news images are shown from imageUrl.
- PvP tier buttons use grid rows instead of one long trapped row.
- Hub animations were reduced for smoother performance.
- Auto renderer chooses a safer renderer:
  - mobileglues first on 32-bit if available
  - opengles3_ltw if available
  - otherwise fallback to first compatible renderer
- Default renderer changed from old `ltw` to `opengles3_ltw`.

Important:
- For Google login, Firebase Android app must have your SHA1 fingerprint.
- If game still crashes with `glfwInit`, it is native renderer/runtime, not Firebase UI.


# DURBIN Launcher V27

Changes:
- Added real DURBIN bundled mod packs for 1.20.1 and 1.21.11.
- Mods are packaged in APK assets:
  app_pojavlauncher/src/main/assets/durbin_modpacks/1.20.1.zip
  app_pojavlauncher/src/main/assets/durbin_modpacks/1.21.11.zip
- Before game launch, DURBIN auto-copies the correct Fabric mods to the selected profile's mods folder.
- Only Fabric/DURBIN profiles trigger the mod copy.
- First launch now starts with a DURBIN Fabric 1.21.11 profile.
- All profile/version icons use the DURBIN launcher logo.
- Fabric/Forge/OptiFine created profiles use the DURBIN launcher logo too.
- Google login and V26 banner/renderer work are kept.

Important:
For the bundled mods to run, the selected version must be Fabric:
- fabric-loader-0.19.3-1.20.1
- fabric-loader-0.19.3-1.21.11

If you launch plain vanilla 1.20.1 / 1.21.11, Fabric mods will not load.
