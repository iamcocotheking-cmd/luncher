# DURBIN Launcher V50 root log fix

This fixes the V49 logging issue.

Problem:
- The build log was saved in a different working directory.
- The error step looked for durbin-build-full.log inside PojavLauncher-3_openjdk, so it could not find it.

Fix:
- The build step now always saves the log to $GITHUB_WORKSPACE/durbin_logs/durbin-build-full.log.
- The error step looks there first, then searches the whole workspace if needed.
- No artifact upload is needed.
- If Gradle fails, the step named SHOW DURBIN REAL ERROR HERE prints the real error.

How to use:
1. Run GitHub Actions.
2. If it fails, open the step named SHOW DURBIN REAL ERROR HERE.
3. Send the text under IMPORTANT ERROR LINES or LAST 260 LINES.

