# DURBIN Launcher V33 Resource Sweep Fix

Full source ZIP, not patch-only.

The previous build reached Android resource linking, then failed because more default resources were missing.

This version scans the app XML resources and generates safe default resources for missing:
- @dimen references
- @drawable references

Important fixes from the latest log:
- fragment_padding_medium
- profile_editor_image_padding
- ic_px_edit
- background_line
- ic_px_gamepad
- ic_px_java_run
- ic_px_sharelog
- ic_px_folder
- background_card

Generated missing dimens: 37
Generated/covered missing drawable defaults: 40
Still missing drawable refs after generation: []

Kept:
- V32 resource fixes
- V31 all fixes
- V29/V30 build fixes
- Google login
- real DURBIN 1.20.1 + 1.21.11 mod packs
- Fabric start profile
- launcher logo as profile/version logo
- side bar removed
- lightweight spiral loading animation
- banners + auto renderer
