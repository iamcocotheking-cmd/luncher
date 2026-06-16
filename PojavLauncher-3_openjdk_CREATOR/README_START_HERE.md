# DURBIN Full Hyper Base Creator

This pack creates a full replacement folder named:

```text
PojavLauncher-3_openjdk
```

Use this when you want to delete your old launcher source folder and replace it with a clean Hyper Launcher base rebranded for DURBIN.

## Fast way

On Linux / GitHub Codespace / Antigravity terminal:

```bash
bash make_full_source.sh
```

On Windows:

```bat
make_full_source.bat
```

The script will:

1. Move any old `PojavLauncher-3_openjdk` folder to a backup folder.
2. Clone Hyper Launcher branch `v3_openjdk`.
3. Rename the folder to `PojavLauncher-3_openjdk`.
4. Apply DURBIN branding.
5. Add the DURBIN Client Mode Java skeleton.
6. Add a DURBIN client manifest example.

## Build command after it finishes

```bash
cd PojavLauncher-3_openjdk
./gradlew :app_pojavlauncher:assembleDebug --stacktrace
```

Windows:

```bat
cd PojavLauncher-3_openjdk
gradlew.bat :app_pojavlauncher:assembleDebug --stacktrace
```

## Important

This uses the public Hyper Launcher source. It does not use decompiled APK code.
Keep upstream credits and license files when publishing.
