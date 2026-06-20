# DURBIN Launcher V36 Workflow max-workers Fix

Full source ZIP.

Fixes the new workflow error:
- Multiple arguments were provided for command-line option '--max-workers'

What changed:
- Removed --max-workers from Gradle command lines.
- Kept worker limit in gradle.properties only:
  org.gradle.workers.max=1
- Removed duplicate DURBIN V35 memory safety lines.
- Kept Kotlin OOM memory settings.
- Kept all V35/V34/V33 fixes.

Upload this full ZIP contents to GitHub and run Actions again.
