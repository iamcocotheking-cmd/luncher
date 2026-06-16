#!/usr/bin/env bash
set -euo pipefail

APP_MODULE="app_pojavlauncher"
APP_ID="com.durbin.launcher"
APP_NAME="DURBIN Launcher"
PATCH_DIR="$(cd "$(dirname "$0")" && pwd)"

if [ ! -d "$APP_MODULE" ]; then
  echo "ERROR: app_pojavlauncher folder not found. Run this inside the Hyper/Pojav source root."
  exit 1
fi

mkdir -p .durbin-backup
cp -f "$APP_MODULE/build.gradle" .durbin-backup/app_build.gradle 2>/dev/null || true
cp -f "$APP_MODULE/build.gradle.kts" .durbin-backup/app_build.gradle.kts 2>/dev/null || true
cp -f "$APP_MODULE/src/main/res/values/strings.xml" .durbin-backup/strings.xml 2>/dev/null || true

# Visible rebrand only. Keep Java package namespace to avoid breaking Pojav internals.
echo "Rebranding visible strings to DURBIN Launcher..."
if [ -d "$APP_MODULE/src/main/res" ]; then
  find "$APP_MODULE/src/main/res" -type f \( -name "*.xml" -o -name "*.json" -o -name "*.txt" \) -print0 | \
    xargs -0 perl -0777 -pi -e 's/HyperX Launcher/DURBIN Launcher/g; s/Hyper Launcher/DURBIN Launcher/g; s/HyperX/DURBIN/g; s/Hyper Launcher/DURBIN Launcher/g; s/Hyper/DURBIN/g; s/PojavLauncher/DURBIN Launcher/g; s/Pojav Launcher/DURBIN Launcher/g' || true
fi

# Application id only. Keep source packages unchanged.
echo "Setting Android applicationId to $APP_ID..."
for f in "$APP_MODULE/build.gradle" "$APP_MODULE/build.gradle.kts"; do
  if [ -f "$f" ]; then
    perl -0777 -pi -e "s/applicationId\s+[\"'][^\"']+[\"']/applicationId '$APP_ID'/g" "$f" || true
    perl -0777 -pi -e "s/applicationId\s*=\s*[\"'][^\"']+[\"']/applicationId = \"$APP_ID\"/g" "$f" || true
  fi
done

# Add DURBIN client mode skeleton.
echo "Adding DURBIN Client Mode skeleton..."
TARGET_DIR="$APP_MODULE/src/main/java/net/kdt/pojavlaunch/durbin"
mkdir -p "$TARGET_DIR"
cp -f "$PATCH_DIR/source-templates/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/durbin/"*.java "$TARGET_DIR/"

mkdir -p "$APP_MODULE/src/main/assets"
cp -f "$PATCH_DIR/manifests/durbin-client-manifest.example.json" "$APP_MODULE/src/main/assets/durbin-client-manifest.example.json"
cat > "$APP_MODULE/src/main/assets/durbin_branding.txt" <<TXT
DURBIN Launcher
Based on Hyper Launcher / MojoLauncher / PojavLauncher open-source components.
Project by COSA.
Minecraft is owned by Mojang/Microsoft. DURBIN Launcher is not affiliated with Mojang/Microsoft.
TXT

cat > DURBIN_NEXT_STEPS.md <<'MD'
# DURBIN Next Steps

This full source folder is now based on Hyper Launcher `v3_openjdk` and patched for DURBIN.

## Build

```bash
./gradlew :app_pojavlauncher:assembleDebug --stacktrace
```

## What has been added

- Visible app name replacement toward `DURBIN Launcher`
- Application ID set to `com.durbin.launcher`
- DURBIN branding asset
- DURBIN Client Mode Java skeleton:

```text
app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/durbin/
├── DurbinClientManager.java
├── DurbinClientManifest.java
├── DurbinInstallResult.java
└── DurbinMode.java
```

- Example manifest:

```text
app_pojavlauncher/src/main/assets/durbin-client-manifest.example.json
```

## Where to connect DURBIN mode

Search in Android Studio for:

```text
launchGame
LauncherActivity
Profile
versionId
fabric
```

Right before the final Minecraft launch starts, add:

```java
if (selectedProfileName != null && selectedProfileName.toUpperCase().contains("DURBIN")) {
    File minecraftDir = new File(Tools.DIR_GAME_HOME); // adjust if needed
    DurbinClientManager manager = new DurbinClientManager(minecraftDir);
    DurbinInstallResult result = manager.ensureFromUrl(DurbinClientManager.DEFAULT_MANIFEST_URL, message -> {
        // show message in progress UI/logcat
    });
    if (!result.success) {
        // show error dialog and stop launch
        return;
    }
}
```

Then let the normal Fabric launch continue.
MD

echo "DURBIN patch complete."
