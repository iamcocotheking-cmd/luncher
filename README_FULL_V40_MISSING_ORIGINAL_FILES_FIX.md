# DURBIN Launcher V40 Missing Original Files Fix

Full source ZIP.

The latest build reached Java compile, then failed with about 100 missing original launcher files:
- DefocusableScrollView
- MinecraftLibraryArtifact
- EditorExitable
- ControlButtonMenuListener
- CropperBehaviour
- color selector classes
- pointer/gamepad classes
- SpeedCalculator
- JAssetInfo
- Java GUI layouts/ids
- CustomSeekbar attrs
- font resources
- exp4j dependency
- commons-compress dependency

V40 fixes this by merging missing original files/resources from the clean HyperLauncher base into the current Durbin source.

Important:
- Existing Durbin modified files were not overwritten.
- Old fake helper stubs were replaced with real HyperLauncher originals where available.
- Firebase editable mod links and UserRank support remain from V39/V38.
- Online mods remain; APK stays small.

Copied missing Java files: 31
Overwrote stub Java files with originals: 16
Copied missing resource files: 65
