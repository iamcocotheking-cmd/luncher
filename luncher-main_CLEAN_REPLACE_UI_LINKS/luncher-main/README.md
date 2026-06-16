# DURBIN Launcher Clean Replacement Repo

This repo is the clean GitHub Actions wrapper. It does not store the full cloned Hyper source in your repository.

## What it does

- Clones Hyper Launcher `v3_openjdk` during GitHub Actions
- Applies DURBIN branding
- Adds COSA credit
- Sets Discord button/link to `https://discord.gg/PqnbXNrtHR`
- Replaces/removes Wiki as a Wiki link and uses YouTube instead: `https://www.youtube.com/@Cosa_5023_YT`
- Adds DURBIN themes: Orange, Pink Black, Blue Black, Cyan Black, Purple Black, Red Black, Mono
- Adds vertical + horizontal orientation support patch
- Uses your spyglass logo as app icon
- Uses `default2.json` as default controls
- Uploads one APK artifact

## How to use

Delete the old `PojavLauncher-3_openjdk_CREATOR` folder from your GitHub repo.

Then upload only these clean files/folders:

```text
.github/
durbin_patch/
README.md
DELETE_OLD_FOLDER_FIRST.md
```

Run the workflow: **Build DURBIN Launcher Small APK**.
