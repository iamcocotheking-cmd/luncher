DURBIN V36 - Missing class/source bridge fix

Use this as a full source replacement. Do not mix with older V35/V34 files.

Fixes the current compileFullDebugKotlin errors:
- Missing ModloaderDownloadListener / ModloaderListenerProxy
- Missing FabricVersion
- Missing ExtraListener / ProgressListener
- Missing VersionSelectorListener
- Missing FileListView / FileSelectedListener
- Missing ModItem / SearchFilters / SearchResult / Constants / CurseManifest
- Missing ControlJoystickData
- Missing gson and commons-io dependencies
- Missing drawable/dimen/id resources from the current log
