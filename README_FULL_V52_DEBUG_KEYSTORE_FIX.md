# DURBIN Launcher V52 Debug Keystore Fix

I checked the latest short log first.

The build now passes Java/Kotlin/native steps and reaches signing.
Real error:
- Task: :app_pojavlauncher:validateSigningFullDebug
- Keystore file app_pojavlauncher/debug.keystore not found for signing config customDebug.

V52 fix:
- Before Gradle build, workflow checks app_pojavlauncher/debug.keystore.
- If missing, it creates a standard Android debug keystore using keytool.
- Store password: android
- Key alias: androiddebugkey
- Key password: android

Also fixed logging bug:
- Previous workflow used echo "```text" in bash.
- Bash treated backticks as command substitution and printed weird command-not-found lines.
- V52 uses printf/single quotes instead.

No launcher UI/Firebase/Java/Kotlin source changes were made.
