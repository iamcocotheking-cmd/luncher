# DURBIN Launcher V37 Online Mods + JDK Fix

Full source ZIP.

Added online Durbin mod downloads:
- 1.20.1: https://cdn.discordapp.com/attachments/1474466632666583284/1517603294401531995/1.20.1.zip?ex=6a36e1b5&is=6a359035&hm=5d82043c535fa9533504f3f0ff480e6025f9840e07d60ff6f36e7c8b7b79dc8c&
- 1.21.11: https://cdn.discordapp.com/attachments/1474466632666583284/1517603546093322400/1.21.11.zip?ex=6a36e1f1&is=6a359071&hm=75a02d225f04b95c9f2f1e609c4e6903de7dd85f43af885cfce69fd33489f40a&

Fixes:
- Kotlin `JDK_HOME path is not specified` error.
- Uses Kotlin daemon instead of in-process.
- Exports JAVA_HOME/JDK_HOME in workflow.
- Removes global JAVA_TOOL_OPTIONS Xmx pollution.
- Keeps V36/V35/V34/V33 fixes.
- Keeps APK small by not bundling mod zips inside APK.

Note: Discord CDN links can expire. If they stop working, replace constants in:
app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/durbin/DurbinModPackInstaller.java
