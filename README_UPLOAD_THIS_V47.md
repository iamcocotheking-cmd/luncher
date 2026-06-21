# DURBIN Launcher V47 - Duplicate Dependency Classes Fixed

Use this as a full replacement.

## What changed from V46

V46 reached `checkFullDebugDuplicateClasses` and failed because the project included two copies of the same libraries:

- Gson from Maven: `com.google.code.gson:gson:2.11.0`
- Gson local jar: `app_pojavlauncher/libs/gson-2.8.6.jar`
- exp4j from Maven: `net.objecthunter:exp4j:0.4.8`
- exp4j local jar: `app_pojavlauncher/libs/exp4j-0.4.9-SNAPSHOT.jar`

V47 keeps the local libs and removes the duplicate Maven dependencies.

Also removed duplicate source fallback stubs for exp4j and Apache Commons Compress because the local jars already provide those classes.

## Upload steps

1. Delete old repo files.
2. Upload only the files from this V47 zip.
3. Run GitHub Actions.
4. Download the APK artifact if the workflow passes.

Do not mix with V46/V45 files.
