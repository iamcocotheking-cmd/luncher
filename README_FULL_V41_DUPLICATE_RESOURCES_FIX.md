# DURBIN Launcher V41 Duplicate Resources Fix

Full source ZIP.

Fixes latest build error:
- Duplicate drawable resources: ic_mouse_pointer, icon, ic_curseforge, icon_hyper, ic_modrinth
- Duplicate dimen resources: padding_tiny, padding_small, padding_medium, padding_moderate, etc.

What changed:
- Removed generated placeholder XML drawables when original PNG/WEBP assets exist.
- Removed old generated dimen XML files after real HyperLauncher dimens were merged.
- Added workflow cleanup so old duplicate files are removed on GitHub Actions too.
- Keeps V40 missing original file merge.
- Keeps Firebase editable mod links, PvP tier list, and userRank support.

Removed duplicate files: 8
