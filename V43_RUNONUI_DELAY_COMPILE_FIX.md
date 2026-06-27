# v43 - runOnUiThread delayed compile fix

Fixed current Kotlin compile error:

DurbinClientInstaller.kt:
- Too many arguments for Tools.runOnUiThread(...)

Cause:
- The project only supports Tools.runOnUiThread(runnable)
- v40/v42 used Tools.runOnUiThread(runnable, delay)

Fix:
- Added Android Handler/Looper imports
- Replaced delayed runOnUiThread calls with:
  Handler(Looper.getMainLooper()).postDelayed(...)

Kept:
- v42 CSS loader animation
- v41 DURBIN Client naming
- v40 clean rank PFP, no selling, progress in Tasks, fade animation
