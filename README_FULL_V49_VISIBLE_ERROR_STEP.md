# DURBIN Launcher V49 visible error step

This version fixes the logging problem.

Problem:
- GitHub artifact quota is full, so artifact logs cannot upload.
- The previous failed step showed only annotations and was hard to read.

V49 workflow behavior:
- Build full debug APK captures the full build into durbin-build-full.log but exits 0.
- A new step named SHOW DURBIN REAL ERROR HERE always runs after the build.
- If Gradle failed, that step prints important error lines + last 220 lines, then fails.
- The GitHub annotation message also contains the short error.
- No artifact is needed.

How to read next failure:
1. Open failed Actions run.
2. Click the failed step named SHOW DURBIN REAL ERROR HERE.
3. Copy/screenshot the visible lines under DURBIN REAL ERROR HERE.
