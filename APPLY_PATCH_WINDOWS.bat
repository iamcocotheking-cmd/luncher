@echo off
setlocal

echo DURBIN compile fix patch
echo This script copies only the fixed Java/Kotlin source files.
echo It does NOT touch .github workflows.
echo.

set TARGET=PojavLauncher-3_openjdk
if not exist "%TARGET%" (
  echo ERROR: Please run this BAT from the repo root folder that contains PojavLauncher-3_openjdk.
  pause
  exit /b 1
)

copy /Y "PATCH_FILES\PojavLauncher-3_openjdk\app_pojavlauncher\src\main\java\net\kdt\pojavlaunch\fragments\MainMenuFragment.java" "%TARGET%\app_pojavlauncher\src\main\java\net\kdt\pojavlaunch\fragments\MainMenuFragment.java"
if errorlevel 1 goto fail

copy /Y "PATCH_FILES\PojavLauncher-3_openjdk\app_pojavlauncher\src\main\java\net\kdt\pojavlaunch\ui\DurbinDashboard.kt" "%TARGET%\app_pojavlauncher\src\main\java\net\kdt\pojavlaunch\ui\DurbinDashboard.kt"
if errorlevel 1 goto fail

echo.
echo Done. Now commit, push, and run GitHub Actions again.
pause
exit /b 0

:fail
echo.
echo Failed to copy files. Check that your folder names match.
pause
exit /b 1
