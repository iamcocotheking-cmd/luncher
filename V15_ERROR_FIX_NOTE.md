# v15 Error Fix

The previous workflow failed before the real build started.

Cause:
- the checker searched binary native libraries
- the checker also matched normal comment separator lines in GLFW source

Fix:
- the checker now ignores binary files and build folders
- it only checks real merge-conflict marker lines
- it keeps the Firebase package and Web Client ID checks
