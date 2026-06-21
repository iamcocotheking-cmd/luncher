# DURBIN Launcher V40

Fixes the V39 native build failure where CMake/Ninja stopped because `jvm_hooks.h` was missing.

## Fixed
- Added `app_pojavlauncher/src/main/jni/jvm_hooks/jvm_hooks.h`
- Added missing native helper headers: `log.h`, `stdio_is.h`, `native_hooks/native_hooks.h`, `bytehook.h`
- Added workflow guard to recreate/check native headers before build
- Keeps V39 fixes: ImageReceiver, hook.c, xawt_fake.c, V38 duplicate-resource fix, V37 preflight, V33 memory fix, new banner

## Upload
Delete old repo files first, then upload only this V40 source.
