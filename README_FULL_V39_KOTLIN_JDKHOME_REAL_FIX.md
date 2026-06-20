# DURBIN Launcher V39 Kotlin JDK_HOME Real Fix

Full source ZIP.

The latest log still failed with:
JDK_HOME path is not specified in compiler configuration

Real fix:
- Removed the old V35 Kotlin option: -Xjdk-release=17
- That option can trigger the JDK_HOME compiler error on GitHub runners.
- Replaced it with safe Kotlin jvmTarget = 17.
- Keeps Kotlin daemon and explicit JAVA_HOME/JDK_HOME workflow exports.
- Keeps Firebase editable mod links from V38.
- Keeps online mod downloads.
- Keeps small APK approach.
