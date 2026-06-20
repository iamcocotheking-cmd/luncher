# DURBIN Launcher V35 Kotlin OOM Fix

Full source ZIP.

The latest GitHub Actions log shows Kotlin compile reached:
- processFullDebugResources ✅
- compileFullDebugKotlin ❌

Real error:
Not enough memory to run Kotlin compilation.

V35 fixes:
- Adds strong gradle.properties memory settings.
- Forces Kotlin compile in-process.
- Limits Gradle workers to 1 to stop RAM spikes.
- Adds workflow memory env vars.
- Adds --max-workers=1 to the build command.
- Keeps V34 Kotlin missing-class fixes.
- Keeps all resource sweep fixes.
- Keeps Google login and Durbin UI.
- Keeps the lighter APK approach where bundled mod zips are not inside APK assets.

If this still OOMs on ubuntu-latest, use a bigger GitHub runner or split/remove heavy unused Compose screens.
