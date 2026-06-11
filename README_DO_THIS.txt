DURBIN compile fix - no .github workflow included

This ZIP is safe to upload/copy over your existing repo.
It does NOT include .github or workflow files.

It replaces only these 2 source files:
1) PojavLauncher-3_openjdk/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/fragments/MainMenuFragment.java
2) PojavLauncher-3_openjdk/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/ui/DurbinDashboard.kt

How to use on Windows:
1. Backup your current PojavLauncher-3_openjdk folder first.
2. Extract this ZIP.
3. Copy the PojavLauncher-3_openjdk folder from this ZIP.
4. Paste it into the SAME parent folder where your current PojavLauncher-3_openjdk is.
5. When Windows asks, choose Replace files.
6. Commit and push to GitHub.
7. Run GitHub Actions again.

Do NOT delete your old project folder first.
Windows folder copy will merge folders and replace only matching files.
