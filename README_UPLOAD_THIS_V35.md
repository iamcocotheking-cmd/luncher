# DURBIN Launcher V35

Fixes current build error from Pasted text(80).

Real issue: Android resource linking failed because old XML layouts referenced missing fallback resources.

V35 fixes:
- Resource guard now runs BEFORE the build, not after upload.
- Adds missing drawable fallbacks: ic_telegram, ic_mouse_pointer, spinner_arrow, spinner_arrow_right, ic_px_* icons, background_line, etc.
- Adds missing sdp/ssp dimension fallbacks and id/array fallbacks.
- Keeps V34 fixes, V33 memory fix, new Minecraft banner, Google login, remote modpacks.
- Workflow logs are less noisy and show AAPT resource errors clearly.

Upload all V35 files as a full source replacement. Do not mix old versions.
