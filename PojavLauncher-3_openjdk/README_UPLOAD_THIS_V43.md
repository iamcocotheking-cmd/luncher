# DURBIN Launcher V43 - Java Source Merge Fixed

This version fixes the V42 build stage that reached Java compile then failed with missing original HyperLauncher Java classes/resources.

Changes:
- Restored missing Java source files from HyperLauncher base without overwriting DURBIN files.
- Restored missing XML/layout/drawable/font/menu resources.
- Restored missing libs from the HyperLauncher base.
- Added android.nonFinalResIds=false for Java switch-case R.id compatibility.
- Added fallback resources for remaining R references.

Upload only this version as a full repo replacement.
