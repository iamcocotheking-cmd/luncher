#!/usr/bin/env bash
set -euo pipefail

TARGET="PojavLauncher-3_openjdk"
REPO="https://github.com/hollowlauncher/HyperLauncher.git"
BRANCH="v3_openjdk"
PATCH_DIR="$(cd "$(dirname "$0")" && pwd)/durbin_patch"

command -v git >/dev/null 2>&1 || { echo "git is missing. Install Git first."; exit 1; }

if [ -d "$TARGET" ]; then
  BACKUP="${TARGET}_backup_$(date +%Y%m%d_%H%M%S)"
  echo "Old $TARGET found. Moving it to $BACKUP"
  mv "$TARGET" "$BACKUP"
fi

echo "Cloning Hyper Launcher $BRANCH into $TARGET..."
git clone --depth 1 --recurse-submodules --branch "$BRANCH" "$REPO" "$TARGET"

cd "$TARGET"

echo "Applying DURBIN patch..."
bash "$PATCH_DIR/apply_durbin_changes.sh"

echo ""
echo "DONE. Full source created: $TARGET"
echo "Build now with:"
echo "cd $TARGET"
echo "./gradlew :app_pojavlauncher:assembleDebug --stacktrace"
