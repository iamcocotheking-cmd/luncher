# DURBIN Launcher V45 JNI CMake Fix

Full source ZIP.

I checked the latest build log first. The Java/Kotlin compile passed.
The actual failure is native CMake configure:
- missing app_pojavlauncher/src/main/jni/driver_helper/hook.c
- missing app_pojavlauncher/src/main/jni/awt_xawt/xawt_fake.c

V45 fixes this by copying missing original JNI native files from the clean HyperLauncher source.

This version does NOT add a new workflow patch block. It only adds missing source files, so the workflow YAML stays stable.

Missing JNI files copied from original HyperLauncher: 7
Static CMake check: all .c/.cpp source paths referenced in CMakeLists.txt exist.

Important upload instruction:
Do not merge on top of old broken source. Delete old PojavLauncher-3_openjdk from the repo, then upload this V45 folder fresh.
