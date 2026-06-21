# DURBIN Launcher V42 - Debug Keystore Signing Fixed

This version fixes the current GitHub Actions failure:

```text
:app_pojavlauncher:validateSigningFullDebug FAILED
Keystore file app_pojavlauncher/debug.keystore not found for signing config customDebug
```

## Fixes

- Added `PojavLauncher-3_openjdk/app_pojavlauncher/debug.keystore`
- Added workflow step to generate the debug keystore before build if missing
- Prints SHA1/SHA256 in the workflow log for Firebase Google login setup
- Keeps V41 dnbglfw/glfw fix
- Keeps V40 native header fix
- Keeps V39/V38/V37 source/resource fixes

## Upload

Delete old repo files first, then upload only this V42 source.

## Important

For Firebase Google login, add the SHA1 printed by the workflow to Firebase Android app package `net.ashmeet.hyperlauncher.debug` if you use debug builds.
