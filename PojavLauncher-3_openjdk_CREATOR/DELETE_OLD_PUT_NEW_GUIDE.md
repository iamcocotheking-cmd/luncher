# Delete Old, Put New Guide

1. Extract this ZIP anywhere.
2. Run:

```bash
bash make_full_source.sh
```

3. The script creates:

```text
PojavLauncher-3_openjdk/
```

4. Delete your old folder from your project.
5. Put this new `PojavLauncher-3_openjdk` folder there.
6. Open it in Android Studio or push to GitHub.
7. Build:

```bash
./gradlew :app_pojavlauncher:assembleDebug --stacktrace
```

If you use Windows, run:

```bat
make_full_source.bat
```

Then:

```bat
cd PojavLauncher-3_openjdk
gradlew.bat :app_pojavlauncher:assembleDebug --stacktrace
```
