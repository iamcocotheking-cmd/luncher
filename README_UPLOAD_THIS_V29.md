# DURBIN Launcher V29 - Remote Modpack Links

This version removes the huge bundled DURBIN modpack zips from the APK source.

Removed from APK assets:
- app_pojavlauncher/src/main/assets/durbin_modpacks/1.20.1.zip
- app_pojavlauncher/src/main/assets/durbin_modpacks/1.21.11.zip

Now the launcher downloads the correct modpack from an online link only when launching a Fabric/DURBIN profile.

## Firebase paths to add

Use Firebase Realtime Database:

```json
{
  "durbin": {
    "modpacks": {
      "1_20_1": {
        "url": "PASTE_DIRECT_ZIP_LINK_FOR_1_20_1_HERE",
        "version": "1.20.1"
      },
      "1_21_11": {
        "url": "PASTE_DIRECT_ZIP_LINK_FOR_1_21_11_HERE",
        "version": "1.21.11"
      }
    }
  }
}
```

## Firebase rules

Add this under `durbin` rules:

```json
"modpacks": {
  ".read": true,
  ".write": "root.child('durbin/admins').child(auth.uid).val() === true"
}
```

## Direct link requirement

Use a direct downloadable zip link.
Good places:
- GitHub Releases asset link
- Netlify file link
- Firebase Storage download URL

The launcher caches the downloaded zip, so it does not redownload every launch unless the URL changes.
