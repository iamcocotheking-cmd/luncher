# DURBIN Launcher Clean Repo

This repo is intentionally small.

## Delete old folder
Delete this old folder from your GitHub repo if it exists:

```text
PojavLauncher-3_openjdk_CREATOR/
```

## Keep only these in the root

```text
.github/workflows/build-durbin-launcher.yml
durbin_patch/
README.md
```

The GitHub Action will clone Hyper Launcher `v3_openjdk` into a temporary `PojavLauncher-3_openjdk/` folder during the build, apply DURBIN patches, and upload only one APK.
