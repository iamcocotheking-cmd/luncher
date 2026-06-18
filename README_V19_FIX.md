# DURBIN v19 Workflow Compile Fix

This is a workflow-only fix for the current v18 source in your GitHub repo.

Do NOT delete your project source.
Only replace:
.github/workflows/build-durbin-launcher.yml

What it fixes:
- MainMenuScreen.kt missing DurbinInlineNewsPanel
- MainMenuScreen.kt missing DurbinActionCard

The workflow patches those missing functions automatically before building.
