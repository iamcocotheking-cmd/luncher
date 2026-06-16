@echo off
setlocal enabledelayedexpansion
set TARGET=PojavLauncher-3_openjdk
set REPO=https://github.com/hollowlauncher/HyperLauncher.git
set BRANCH=v3_openjdk

where git >nul 2>nul
if errorlevel 1 (
  echo Git is missing. Install Git first.
  exit /b 1
)

if exist "%TARGET%" (
  set BACKUP=%TARGET%_backup
  echo Old %TARGET% found. Moving it to !BACKUP!
  ren "%TARGET%" "!BACKUP!"
)

echo Cloning Hyper Launcher %BRANCH% into %TARGET%...
git clone --depth 1 --recurse-submodules --branch "%BRANCH%" "%REPO%" "%TARGET%"
if errorlevel 1 exit /b 1

cd "%TARGET%"
echo Applying DURBIN patch...
bash "..\durbin_patch\apply_durbin_changes.sh"
if errorlevel 1 exit /b 1

echo.
echo DONE. Full source created: %TARGET%
echo Build now with:
echo cd %TARGET%
echo gradlew.bat :app_pojavlauncher:assembleDebug --stacktrace
