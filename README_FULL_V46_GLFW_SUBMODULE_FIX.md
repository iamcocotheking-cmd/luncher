# DURBIN Launcher V46 GLFW Submodule Fix

Full source ZIP.

Exploration result from the latest log:
- Kotlin compile passed.
- Java compile passed.
- app_pojavlauncher native CMake passed for all ABIs.
- The actual failure is dnbglfw native CMake.
- dnbglfw/src/main/cpp/CMakeLists.txt uses add_subdirectory(glfw).
- But dnbglfw/src/main/cpp/glfw is an empty/missing Git submodule.

Fix:
- .gitmodules points dnbglfw/src/main/cpp/glfw to https://github.com/MojoLauncher/glfw
- GitHub Actions checkout now uses submodules: recursive.
- Added fallback workflow step: if glfw/CMakeLists.txt is missing, it runs:
  git clone --depth 1 https://github.com/MojoLauncher/glfw dnbglfw/src/main/cpp/glfw

No Java/Kotlin/UI/Firebase changes were made in V46.
This is only the missing GLFW native source fix.

Important upload instruction:
Delete old PojavLauncher-3_openjdk from the repo, upload this V46 fresh, then run Actions.
