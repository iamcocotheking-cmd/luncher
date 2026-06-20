# DURBIN Launcher V51 Fetch GLFW Before Build

I checked the V50 short log. The build still fails at dnbglfw CMake:
- Task: :dnbglfw:configureCMakeDebug[arm64-v8a]
- CMakeLists.txt line 16: add_subdirectory(glfw)
- Meaning: dnbglfw/src/main/cpp/glfw is still missing during GitHub Actions.

Why V50 still failed:
- V50 fixed logging only.
- The build body only checked the GLFW folder, but did not force-download it before Gradle.

V51 fix:
- Before Gradle starts, workflow force-checks:
  dnbglfw/src/main/cpp/glfw/CMakeLists.txt
- If missing, it runs:
  git clone --depth 1 https://github.com/MojoLauncher/glfw.git dnbglfw/src/main/cpp/glfw
- If MojoLauncher clone fails, it falls back to:
  git clone --depth 1 https://github.com/glfw/glfw.git dnbglfw/src/main/cpp/glfw
- If CMakeLists.txt is still missing, it stops early with a clear GLFW download failed error.

No launcher UI/Firebase/Java/Kotlin code changes were made.
