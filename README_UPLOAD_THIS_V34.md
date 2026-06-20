# DURBIN Launcher V34

This version fixes the current resource linking build error.

Current fixed error:
- processFullDebugResources failed
- Android resource linking failed
- drawable/ic_social_media had no default resource
- dimen/padding_tiny was missing

Added:
- app_pojavlauncher/src/main/res/values/dimens.xml
- app_pojavlauncher/src/main/res/drawable/ic_social_media.xml
- workflow V34 resource linking guard before build

Use as full source replacement.
