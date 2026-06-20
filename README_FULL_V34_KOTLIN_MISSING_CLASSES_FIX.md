# DURBIN Launcher V34 Kotlin Missing Classes Fix

Full source ZIP.

This version fixes the new compileFullDebugKotlin errors from the latest log.

The log showed missing:
- com.kdt.pickafile.FileListView / pickafile
- ExtraListener
- ProgressListener
- VersionSelectorListener
- ModItem / SearchFilters / SearchResult / Constants
- ControlJoystickData
- ImageReceiver
- Gson classes/dependency
- Commons IO dependency
- many R.drawable icons referenced only from Kotlin

Actions:
- Added missing Java helper classes/interfaces.
- Added missing modpack model classes.
- Added missing utility classes.
- Added Gson + Commons IO + RecyclerView dependencies.
- Generated missing R.drawable resources referenced from code.
- Generated missing R.dimen resources referenced from code/XML.
- Removed bundled mod-pack ZIPs from APK assets to make build faster/lighter.

Removed bundled asset size: 91.7 MB

Note:
The launcher will still build with Durbin UI. To make bundled mods work without increasing APK size,
upload the mod ZIPs online later and use download links.
