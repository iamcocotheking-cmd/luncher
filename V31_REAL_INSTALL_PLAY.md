# DURBIN v31 - Real Install + Play

This version changes the DURBIN page from a link page into a real installer.

Install + Play does:
1. Installs Fabric loader 0.19.3 for the selected version.
2. Creates or reuses a clean DURBIN instance:
   - DURBIN 1.20.1
   - DURBIN 1.21.11
3. Downloads the correct GitHub ZIP.
4. Extracts only .jar mod files from the ZIP into that instance's mods folder.
5. Selects the DURBIN instance.
6. Starts Minecraft launch.

Important:
- If Fabric metadata does not support a version, the app will show an error instead of pretending it can launch.
- The installer clears old DURBIN-managed mods only: files starting with durbin_.
- It does not delete user mods that do not start with durbin_.
