# DURBIN v13 Final Compile Checklist

Before compiling:
1. Delete old repo files first.
2. Upload all files from this ZIP.
3. Do not merge with old workflow files.
4. Make sure this file exists:
   - PojavLauncher-3_openjdk/app_pojavlauncher/google-services.json
5. Make sure package name stays:
   - net.ashmeet.hyperlauncher
6. After installing the new APK on phone:
   - uninstall the old DURBIN/HyperLauncher app first
   - then install the new APK

Google Error 10 fix included:
- Debug APK no longer uses `.debug` package suffix.
- The included debug keystore SHA1 matches the new Firebase JSON.

Firebase news path:
Realtime Database:
durbin/news/news_001

Example:
{
  "title": "DURBIN First News",
  "body": "Welcome to DURBIN Launcher!",
  "tag": "Update",
  "timestamp": 1780000000000
}
