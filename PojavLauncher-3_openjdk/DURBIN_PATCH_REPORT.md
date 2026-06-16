# DURBIN real source patch

This ZIP is patched from the real `HyperLauncher-3_openjdk` source, not the old creator script.

Changes applied:

- New DURBIN Compose home UI for landscape and portrait.
- Launcher activities support `fullSensor`, so the launcher UI can rotate vertical/horizontal.
- Wiki button removed from the Compose UI.
- YouTube link added: https://www.youtube.com/@Cosa_5023_YT
- Discord link changed to: https://discord.gg/PqnbXNrtHR
- Owner credit added: COSA.
- App name changed to DURBIN Launcher.
- Spyglass PNG used as `@drawable/icon` and mipmap launcher icons.
- `default2.json` copied to `app_pojavlauncher/src/main/assets/default.json`.
- Theme selector now includes orange, pink, blue, cyan, purple, red, mono, and Crynoix legacy.
- Default theme mode is DURBIN Orange.

Build command:

```bash
./gradlew :app_pojavlauncher:assembleFullDebug --stacktrace
```

or:

```bash
./gradlew :app_pojavlauncher:assembleDebug --stacktrace
```
