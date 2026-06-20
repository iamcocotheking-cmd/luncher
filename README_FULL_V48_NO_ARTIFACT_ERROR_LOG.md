# DURBIN Launcher V48 no-artifact error log

The screenshot showed GitHub artifact storage quota is full, so the short error log artifact cannot upload.

V48 changes the workflow only:
- No artifact upload is needed for the error log.
- The Gradle step prints DURBIN SHORT ERROR START directly in the GitHub log.
- The same short error is written into the GitHub job summary.
- APK artifact/release upload steps are continue-on-error so quota problems do not hide the real compile/build error.
- Adds a visible check for dnbglfw/src/main/cpp/glfw before building.

How to use:
1. Open failed Actions run.
2. Click Build full debug APK.
3. Ctrl+F search: DURBIN SHORT ERROR START
4. Send only that small section.

Launcher code is unchanged from V47/V46.
