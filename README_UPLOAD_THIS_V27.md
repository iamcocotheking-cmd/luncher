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


## V28 final lag-free polish
- Sidebar removed.
- Spiral loading animation fixed.
- Infinite glow disabled for lower lag.
