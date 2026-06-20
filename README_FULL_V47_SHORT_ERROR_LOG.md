# DURBIN Launcher V47 Short Error Log

Full source ZIP.

This does not change launcher code. It changes the GitHub Actions workflow so errors are easy to read.

What it does:
- Saves the full Gradle output to durbin-build-full.log
- Creates DURBIN_SHORT_ERROR.txt with only important error lines and the last 180 log lines
- Uploads both files as an artifact named DURBIN-short-error-log, even when build fails

How to use after a failed build:
1. Open the failed GitHub Actions run.
2. Scroll to Artifacts.
3. Download DURBIN-short-error-log.
4. Send DURBIN_SHORT_ERROR.txt here.

This keeps V46 GLFW submodule fix and all previous launcher fixes.
