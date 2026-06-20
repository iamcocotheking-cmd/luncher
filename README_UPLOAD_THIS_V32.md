# DURBIN Launcher V32 - AndroidX + Diagnostic Workflow

This version fixes the current AndroidX build error:

android.useAndroidX property is not enabled

Fixed in source:
- PojavLauncher-3_openjdk/gradle.properties now has android.useAndroidX=true
- android.enableJetifier=true is also enabled

Workflow improved:
- shows project path
- prints gradle.properties
- checks Forge helper files
- checks merge conflict markers
- prints Gradle projects/tasks
- saves durbin-build.log
- prints a short clear error summary when build fails

Upload this full source to GitHub and run Actions.
