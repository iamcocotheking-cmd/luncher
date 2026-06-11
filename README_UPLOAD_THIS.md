# DURBIN GitHub Actions Fix

Upload the `.github` folder from this ZIP to the ROOT of your GitHub repo.

Your current repo root has:

- `PojavLauncher-3_openjdk/`
- `durbin-ui-extract/`
- `.gitattributes`

This workflow is made for that nested structure. It builds from inside:

`PojavLauncher-3_openjdk/`

After uploading:

1. Go to GitHub repo.
2. Open the Actions tab.
3. Click **DURBIN Launcher APK Build**.
4. Click **Run workflow**.
5. After it finishes, download the artifact named `durbin-launcher-debug-apk`.

If it fails, send the red error log to ChatGPT.
