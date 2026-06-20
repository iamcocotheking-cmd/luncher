# DURBIN Launcher V31 - Workflow YAML Fixed

This version fixes the invalid GitHub Actions YAML from V30.

What changed:
- Removed the broken multi-line Python/Java heredoc from the workflow.
- Kept the real Java helper files directly in forge_installer source.
- Added a simple safe workflow check to verify helper files exist.
- Validated build-durbin-launcher.yml with PyYAML.

Use this as full source replacement.
