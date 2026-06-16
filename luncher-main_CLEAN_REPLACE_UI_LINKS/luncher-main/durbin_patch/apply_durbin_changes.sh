#!/usr/bin/env bash
set -euo pipefail

TARGET="${1:-PojavLauncher-3_openjdk}"
PATCH_DIR="$(cd "$(dirname "$0")" && pwd)"
COSA_NAME="COSA"
COSA_YOUTUBE="https://www.youtube.com/@Cosa_5023_YT"
COSA_DISCORD="https://discord.gg/PqnbXNrtHR"

if [ ! -d "$TARGET" ]; then
  echo "Target folder not found: $TARGET"
  exit 1
fi

cd "$TARGET"

echo "Applying DURBIN branding, links, icon, controls, themes, and UI patch..."

# -----------------------------------------------------------------------------
# 1) Branding + owner links
# -----------------------------------------------------------------------------
# Replace visible names and common old links. Keep this broad so it works even if
# Hyper moves its UI files later.
find app_pojavlauncher/src/main -type f \( -name "*.xml" -o -name "*.json" -o -name "*.java" -o -name "*.kt" \) \
  -not -path "*/build/*" \
  -exec sed -i \
    -e 's/Hyper Launcher/DURBIN Launcher/g' \
    -e 's/HyperX Launcher/DURBIN Launcher/g' \
    -e 's/HYPER LAUNCHER/DURBIN LAUNCHER/g' \
    -e 's/Crynoix Launcher(Collab)/DURBIN Blue/g' \
    -e 's/Hyper Launcher (default)/DURBIN Orange/g' \
    -e 's/>Wiki</>YouTube</g' \
    -e 's/"Wiki"/"YouTube"/g' \
    -e 's/>wiki</>youtube</g' \
    -e 's/github\.com\/hollowlauncher\/HyperLauncher\/wiki/www.youtube.com\/@Cosa_5023_YT/g' \
    -e 's/github\.com\/hollowlauncher\/HyperLauncher/www.youtube.com\/@Cosa_5023_YT/g' \
    -e 's#https://discord\.gg/[A-Za-z0-9_-]*#https://discord.gg/PqnbXNrtHR#g' \
    -e 's#https://discord\.com/invite/[A-Za-z0-9_-]*#https://discord.gg/PqnbXNrtHR#g' \
    -e 's#discord\.gg/[A-Za-z0-9_-]*#discord.gg/PqnbXNrtHR#g' \
    {} + || true

# A second Python patch does smarter replacements for Kotlin/Java constants,
# URL strings, and theme arrays without breaking XML syntax.
python3 - <<'PYSUB'
from pathlib import Path
import re

ROOT = Path('app_pojavlauncher/src/main')
YOUTUBE = 'https://www.youtube.com/@Cosa_5023_YT'
DISCORD = 'https://discord.gg/PqnbXNrtHR'
OWNER = 'COSA'

text_ext = {'.xml', '.java', '.kt', '.json', '.gradle', '.kts', '.properties'}
for p in ROOT.rglob('*'):
    if not p.is_file() or p.suffix not in text_ext or '/build/' in str(p):
        continue
    try:
        s = p.read_text(encoding='utf-8')
    except Exception:
        continue
    old = s

    # Remove/replace Wiki UI with YouTube UI. This keeps the slot but it no
    # longer opens wiki. The user asked to remove Wiki and add YouTube.
    replacements = {
        'Wiki': 'YouTube',
        'wiki': 'youtube',
        'WIKI': 'YOUTUBE',
        'Hyper Launcher': 'DURBIN Launcher',
        'HyperX Launcher': 'DURBIN Launcher',
        'HYPER LAUNCHER': 'DURBIN LAUNCHER',
        'Hyper Launcher (default)': 'DURBIN Orange',
        'Crynoix Launcher(Collab)': 'DURBIN Blue',
    }
    for a,b in replacements.items():
        s = s.replace(a,b)

    # URL fixes.
    s = re.sub(r'https?://(?:www\.)?github\.com/hollowlauncher/HyperLauncher(?:/youtube)?', YOUTUBE, s)
    s = re.sub(r'https?://(?:www\.)?github\.com/[^"\'<>\s)]+(?:youtube|wiki)[^"\'<>\s)]*', YOUTUBE, s, flags=re.I)
    s = re.sub(r'https?://(?:www\.)?youtube\.com/[^"\'<>\s)]*', YOUTUBE, s)
    s = re.sub(r'https?://(?:www\.)?discord(?:\.gg|\.com/invite)/[A-Za-z0-9_-]+', DISCORD, s)
    s = re.sub(r'discord(?:\.gg|\.com/invite)/[A-Za-z0-9_-]+', 'discord.gg/PqnbXNrtHR', s)

    # Add owner wording into obvious about/credit texts.
    s = s.replace('DURBIN Launcher is based on', 'DURBIN Launcher by COSA is based on')

    # If a theme list already contains DURBIN Orange and DURBIN Blue, inject
    # extra visible choices after DURBIN Blue once.
    if 'DURBIN Orange' in s and 'DURBIN Blue' in s and 'DURBIN Pink' not in s:
        s = s.replace('DURBIN Blue', 'DURBIN Blue\nDURBIN Pink Black\nDURBIN Blue Black\nDURBIN Cyan Black\nDURBIN Purple Black\nDURBIN Red Black\nDURBIN Mono', 1)

    if s != old:
        p.write_text(s, encoding='utf-8')
PYSUB

# -----------------------------------------------------------------------------
# 2) Android orientation: allow both vertical and horizontal UI
# -----------------------------------------------------------------------------
# Many Pojav/Hyper style launchers lock main screens to landscape. This changes
# activity orientation to fullSensor so Android can use portrait and landscape.
MANIFEST="app_pojavlauncher/src/main/AndroidManifest.xml"
if [ -f "$MANIFEST" ]; then
  sed -i \
    -e 's/android:screenOrientation="landscape"/android:screenOrientation="fullSensor"/g' \
    -e 's/android:screenOrientation="sensorLandscape"/android:screenOrientation="fullSensor"/g' \
    -e 's/android:screenOrientation="userLandscape"/android:screenOrientation="fullSensor"/g' \
    -e 's/android:screenOrientation="reverseLandscape"/android:screenOrientation="fullSensor"/g' \
    "$MANIFEST" || true
fi

# -----------------------------------------------------------------------------
# 3) DURBIN theme resources: more colors and portrait/landscape dimensions
# -----------------------------------------------------------------------------
mkdir -p app_pojavlauncher/src/main/res/values app_pojavlauncher/src/main/res/values-land app_pojavlauncher/src/main/res/values-port
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
    <color name="durbin_card_stroke">#33FF7A00</color>

    <string name="durbin_owner_name">COSA</string>
    <string name="durbin_owner_credit">Made by COSA</string>
    <string name="durbin_discord_url">https://discord.gg/PqnbXNrtHR</string>
    <string name="durbin_youtube_url">https://www.youtube.com/@Cosa_5023_YT</string>
    <string name="durbin_discord_button">Discord</string>
    <string name="durbin_youtube_button">YouTube</string>

    <string name="durbin_theme_orange">DURBIN Orange</string>
    <string name="durbin_theme_pink">DURBIN Pink Black</string>
    <string name="durbin_theme_blue">DURBIN Blue Black</string>
    <string name="durbin_theme_cyan">DURBIN Cyan Black</string>
    <string name="durbin_theme_purple">DURBIN Purple Black</string>
    <string name="durbin_theme_red">DURBIN Red Black</string>
    <string name="durbin_theme_mono">DURBIN Mono</string>

    <string-array name="durbin_theme_names">
        <item>DURBIN Orange</item>
        <item>DURBIN Pink Black</item>
        <item>DURBIN Blue Black</item>
        <item>DURBIN Cyan Black</item>
        <item>DURBIN Purple Black</item>
        <item>DURBIN Red Black</item>
        <item>DURBIN Mono</item>
    </string-array>
</resources>
XML

cat > app_pojavlauncher/src/main/res/values-land/durbin_orientation.xml <<'XML'
<resources>
    <bool name="durbin_is_landscape">true</bool>
    <dimen name="durbin_page_padding">24dp</dimen>
    <dimen name="durbin_card_radius">22dp</dimen>
    <dimen name="durbin_nav_width">160dp</dimen>
</resources>
XML

cat > app_pojavlauncher/src/main/res/values-port/durbin_orientation.xml <<'XML'
<resources>
    <bool name="durbin_is_landscape">false</bool>
    <dimen name="durbin_page_padding">16dp</dimen>
    <dimen name="durbin_card_radius">22dp</dimen>
    <dimen name="durbin_nav_width">0dp</dimen>
</resources>
XML

# -----------------------------------------------------------------------------
# 4) DURBIN drawable resources for corrected horizontal + vertical UI
# -----------------------------------------------------------------------------
mkdir -p app_pojavlauncher/src/main/res/drawable app_pojavlauncher/src/main/res/drawable-nodpi
cp "$PATCH_DIR/assets/durbin_spyglass_logo.png" app_pojavlauncher/src/main/res/drawable/durbin_spyglass_logo.png
cp "$PATCH_DIR/assets/durbin_spyglass_logo.png" app_pojavlauncher/src/main/res/drawable-nodpi/durbin_spyglass_logo.png

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

cat > app_pojavlauncher/src/main/res/drawable/durbin_button_pink_bg.xml <<'XML'
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <solid android:color="#FF2DAA" />
    <corners android:radius="999dp" />
</shape>
XML

cat > app_pojavlauncher/src/main/res/drawable/durbin_button_blue_bg.xml <<'XML'
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <solid android:color="#2D7DFF" />
    <corners android:radius="999dp" />
</shape>
XML

cat > app_pojavlauncher/src/main/res/drawable/durbin_horizontal_panel_bg.xml <<'XML'
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <gradient android:startColor="#111111" android:centerColor="#050505" android:endColor="#220F00" android:angle="0" />
    <stroke android:width="1dp" android:color="#44FF7A00" />
    <corners android:radius="26dp" />
</shape>
XML

cat > app_pojavlauncher/src/main/res/drawable/durbin_vertical_panel_bg.xml <<'XML'
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <gradient android:startColor="#050505" android:centerColor="#111111" android:endColor="#220F00" android:angle="270" />
    <stroke android:width="1dp" android:color="#44FF7A00" />
    <corners android:radius="26dp" />
</shape>
XML

cat > app_pojavlauncher/src/main/res/drawable/durbin_dark_gradient_bg.xml <<'XML'
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <gradient android:startColor="#000000" android:centerColor="#080808" android:endColor="#1A0A00" android:angle="315" />
</shape>
XML

# -----------------------------------------------------------------------------
# 5) App icon/adaptive icon
# -----------------------------------------------------------------------------
for d in mipmap-mdpi mipmap-hdpi mipmap-xhdpi mipmap-xxhdpi mipmap-xxxhdpi; do
  mkdir -p "app_pojavlauncher/src/main/res/$d"
  cp "$PATCH_DIR/assets/durbin_spyglass_logo.png" "app_pojavlauncher/src/main/res/$d/ic_launcher.png" || true
  cp "$PATCH_DIR/assets/durbin_spyglass_logo.png" "app_pojavlauncher/src/main/res/$d/ic_launcher_round.png" || true
done

mkdir -p app_pojavlauncher/src/main/res/mipmap-anydpi-v26
cat > app_pojavlauncher/src/main/res/mipmap-anydpi-v26/ic_launcher.xml <<'XML'
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:color="#FF7A00" />
    <foreground android:drawable="@drawable/durbin_spyglass_logo" />
</adaptive-icon>
XML
cat > app_pojavlauncher/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml <<'XML'
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:color="#FF7A00" />
    <foreground android:drawable="@drawable/durbin_spyglass_logo" />
</adaptive-icon>
XML

# -----------------------------------------------------------------------------
# 6) Add Java skeleton files + default controls
# -----------------------------------------------------------------------------
mkdir -p app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/durbin
cp -R "$PATCH_DIR/source-templates/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/durbin/"*.java \
  app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/durbin/

mkdir -p app_pojavlauncher/src/main/assets
cp "$PATCH_DIR/controls/default2.json" app_pojavlauncher/src/main/assets/default.json
cp "$PATCH_DIR/controls/default2.json" app_pojavlauncher/src/main/assets/default2.json
cp "$PATCH_DIR/controls/default2.json" app_pojavlauncher/src/main/assets/durbin_default_controls.json

# -----------------------------------------------------------------------------
# 7) App labels + settings/about credit. Also keep Discord button correct.
# -----------------------------------------------------------------------------
find app_pojavlauncher/src/main/res/values -type f -name "*.xml" -exec sed -i \
  -e 's/>Hyper Launcher</>DURBIN Launcher</g' \
  -e 's/>Hyper</>DURBIN</g' \
  -e 's/>PojavLauncher</>DURBIN Launcher</g' \
  -e 's/>Wiki</>YouTube</g' \
  -e 's/>Discord</>Discord</g' \
  {} + || true

cat > DURBIN_PATCH_REPORT.txt <<TXT
DURBIN patch applied.

Included:
- Both vertical and horizontal orientation enabled by changing landscape locks to fullSensor
- Portrait and landscape DURBIN resource qualifiers added
- Wiki button converted/removed as a Wiki link and replaced with YouTube: $COSA_YOUTUBE
- Discord button/link forced to: $COSA_DISCORD
- Owner credit added: $COSA_NAME
- Spyglass logo copied as normal and adaptive app icon
- default2 controls copied as default.json, default2.json, and durbin_default_controls.json
- Extra theme names/resources added:
  DURBIN Orange, Pink Black, Blue Black, Cyan Black, Purple Black, Red Black, Mono
- DURBIN corrected UI drawables added for cards, buttons, horizontal panels, and vertical panels
TXT

echo "DURBIN patch done. Report written to DURBIN_PATCH_REPORT.txt"
