# DURBIN Launcher V45 - Color Fallback Duplicate Fixed

Fixes the V44 failure at mergeFullDebugResources:

- Duplicate color/checkbox_text_color between res/color/checkbox_text_color.xml and values/durbin_v43_java_fallbacks.xml
- Adds universal fallback duplicate cleanup for colors, strings, dimens, ids, layouts, drawables, fonts, menus, and other generated fallback resources
- Keeps V43 Java missing-class fixes
- Keeps V42 debug keystore fix
- Keeps V41/V40 native fixes

Upload instructions:
1. Delete old repo files.
2. Upload only this V45 source.
3. Run GitHub Actions.
