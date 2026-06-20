# DURBIN V38 duplicate resource fixed

This version fixes the V37 mergeFullDebugResources duplicate resource crash.

Fixed exact errors:
- drawable/icon duplicated by icon.png + icon.xml
- drawable/ic_setting_sign_in_background duplicated by webp + xml
- id/dimension_tracker duplicated by fallback XML + styles.xml

Use as full source replacement. Do not mix old files.
