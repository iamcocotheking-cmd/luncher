#!/usr/bin/env bash
set -euo pipefail

TARGET="${1:-PojavLauncher-3_openjdk}"
PATCH_DIR="$(cd "$(dirname "$0")" && pwd)"

if [ ! -d "$TARGET" ]; then
  echo "Target folder not found: $TARGET"
  exit 1
fi

cd "$TARGET"

echo "Applying DURBIN branding..."
find app_pojavlauncher/src/main/res -type f \( -name "*.xml" -o -name "*.json" \) \
  -exec sed -i \
    -e 's/Hyper Launcher/DURBIN Launcher/g' \
    -e 's/HyperX Launcher/DURBIN Launcher/g' \
    -e 's/HYPER LAUNCHER/DURBIN LAUNCHER/g' \
    -e 's/Crynoix Launcher(Collab)/DURBIN Blue/g' \
    -e 's/Hyper Launcher (default)/DURBIN Orange/g' {} + || true

# Add DURBIN color/theme resources. These are safe extra resources.
mkdir -p app_pojavlauncher/src/main/res/values
cat > app_pojavlauncher/src/main/res/values/durbin_themes.xml <<'XML'
<resources>
    <color name="durbin_orange">#FF7A00</color>
    <color name="durbin_pink">#FF2DAA</color>
    <color name="durbin_blue">#2D7DFF</color>
    <color name="durbin_cyan">#00D9FF</color>
    <color name="durbin_purple">#9B5CFF</color>
    <color name="durbin_red">#FF3B3B</color>
    <color name="durbin_black">#050505</color>
    <color name="durbin_card">#181818</color>
    <string name="durbin_theme_orange">DURBIN Orange</string>
    <string name="durbin_theme_pink">DURBIN Pink</string>
    <string name="durbin_theme_blue">DURBIN Blue</string>
    <string name="durbin_theme_cyan">DURBIN Cyan</string>
    <string name="durbin_theme_purple">DURBIN Purple</string>
    <string name="durbin_theme_red">DURBIN Red</string>
    <string name="durbin_theme_mono">DURBIN Mono</string>
</resources>
XML

mkdir -p app_pojavlauncher/src/main/res/drawable
cat > app_pojavlauncher/src/main/res/drawable/durbin_card_bg.xml <<'XML'
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <solid android:color="#151515" />
    <stroke android:width="1dp" android:color="#33FF7A00" />
    <corners android:radius="22dp" />
    <padding android:left="12dp" android:right="12dp" android:top="10dp" android:bottom="10dp" />
</shape>
XML

cat > app_pojavlauncher/src/main/res/drawable/durbin_button_bg.xml <<'XML'
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <solid android:color="#FF7A00" />
    <corners android:radius="999dp" />
    <padding android:left="18dp" android:right="18dp" android:top="10dp" android:bottom="10dp" />
</shape>
XML

# Add Java skeleton files.
mkdir -p app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/durbin
cp -R "$PATCH_DIR/source-templates/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/durbin/"*.java \
  app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/durbin/

# Add default controls.
mkdir -p app_pojavlauncher/src/main/assets
cp "$PATCH_DIR/controls/default2.json" app_pojavlauncher/src/main/assets/default.json
cp "$PATCH_DIR/controls/default2.json" app_pojavlauncher/src/main/assets/default2.json

# App icon: copy to common mipmap folders if present, create if missing.
for d in mipmap-mdpi mipmap-hdpi mipmap-xhdpi mipmap-xxhdpi mipmap-xxxhdpi drawable; do
  mkdir -p "app_pojavlauncher/src/main/res/$d"
  cp "$PATCH_DIR/assets/durbin_spyglass_logo.png" "app_pojavlauncher/src/main/res/$d/ic_launcher.png" || true
  cp "$PATCH_DIR/assets/durbin_spyglass_logo.png" "app_pojavlauncher/src/main/res/$d/ic_launcher_round.png" || true
done

# Try changing common app labels.
find app_pojavlauncher/src/main/res/values -type f -name "*.xml" -exec sed -i \
  -e 's/>Hyper Launcher</>DURBIN Launcher</g' \
  -e 's/>Hyper</>DURBIN</g' \
  -e 's/>PojavLauncher</>DURBIN Launcher</g' {} + || true

cat > DURBIN_PATCH_REPORT.txt <<TXT
DURBIN patch applied.

Included:
- Spyglass icon copied to common icon folders
- default2 controls copied as default.json and default2.json
- DURBIN theme resources added
- DURBIN card/button drawables added
- DURBIN Client Mode skeleton Java files added
TXT

echo "DURBIN patch done."
