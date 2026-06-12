package net.kdt.pojavlaunch.modloaders.modpacks.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kdt.mcgui.ProgressLayout;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.modloaders.modpacks.models.Constants;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModDetail;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModItem;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModrinthIndex;
import net.kdt.pojavlaunch.modloaders.modpacks.models.SearchFilters;
import net.kdt.pojavlaunch.modloaders.modpacks.models.SearchResult;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;
import net.kdt.pojavlaunch.progresskeeper.DownloaderProgressWrapper;
import net.kdt.pojavlaunch.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipFile;

public class ModrinthApi implements ModpackApi{
    private final ApiHandler mApiHandler;
    public ModrinthApi(){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "DurbinLauncher/1.0 (Minecraft Android Launcher; owner=COSA)");
        mApiHandler = new ApiHandler("https://api.modrinth.com/v2", headers);
    }

    @Override
    public SearchResult searchMod(SearchFilters searchFilters, SearchResult previousPageResult) {
        ModrinthSearchResult modrinthSearchResult = (ModrinthSearchResult) previousPageResult;

        if (modrinthSearchResult != null && modrinthSearchResult.previousOffset >= modrinthSearchResult.totalResultCount) {
            ModrinthSearchResult emptyResult = new ModrinthSearchResult();
            emptyResult.results = new ModItem[0];
            emptyResult.totalResultCount = modrinthSearchResult.totalResultCount;
            emptyResult.previousOffset = modrinthSearchResult.previousOffset;
            return emptyResult;
        }

        SearchResult strictResult = performModrinthSearch(searchFilters, modrinthSearchResult, true, true);
        if (hasUsefulResults(strictResult) || modrinthSearchResult != null) return strictResult;

        // If the current profile version was detected incorrectly, do not show a broken screen.
        // Fall back to loader-only search, then to all mods.
        if (!searchFilters.isModpack && searchFilters.mcVersion != null) {
            SearchResult loaderOnlyResult = performModrinthSearch(searchFilters, null, false, true);
            if (hasUsefulResults(loaderOnlyResult)) return loaderOnlyResult;
        }
        if (!searchFilters.isModpack && searchFilters.loader != null) {
            SearchResult allModsResult = performModrinthSearch(searchFilters, null, false, false);
            if (allModsResult != null) return allModsResult;
        }
        return strictResult;
    }

    private boolean hasUsefulResults(SearchResult result) {
        return result != null && result.results != null && result.results.length > 0;
    }

    private SearchResult performModrinthSearch(SearchFilters searchFilters, ModrinthSearchResult previousResult, boolean includeVersion, boolean includeLoader) {
        HashMap<String, Object> params = new HashMap<>();
        StringBuilder facetString = new StringBuilder();
        facetString.append("[");
        facetString.append(String.format("["project_type:%s"]", searchFilters.isModpack ? "modpack" : "mod"));
        if(includeVersion && searchFilters.mcVersion != null && !searchFilters.mcVersion.isEmpty()) {
            facetString.append(String.format(",["versions:%s"]", searchFilters.mcVersion));
        }
        if(!searchFilters.isModpack && includeLoader && searchFilters.loader != null && !searchFilters.loader.isEmpty()) {
            facetString.append(String.format(",["categories:%s"]", searchFilters.loader.toLowerCase(Locale.ROOT)));
        }
        facetString.append("]");
        params.put("facets", facetString.toString());
        params.put("query", searchFilters.name == null ? "" : searchFilters.name);
        params.put("limit", 50);
        params.put("index", searchFilters.name == null || searchFilters.name.trim().isEmpty() ? "downloads" : "relevance");
        if(previousResult != null) params.put("offset", previousResult.previousOffset);

        JsonObject response = mApiHandler.get("search", params, JsonObject.class);
        if(response == null) return null;
        JsonArray responseHits = response.getAsJsonArray("hits");
        if(responseHits == null) return null;

        ArrayList<ModItem> itemList = new ArrayList<>();
        for(int i=0; i<responseHits.size(); ++i){
            JsonObject hit = responseHits.get(i).getAsJsonObject();
            try {
                String projectType = safeString(hit, "project_type", searchFilters.isModpack ? "modpack" : "mod");
                String iconUrl = safeString(hit, "icon_url", "");
                itemList.add(new ModItem(
                        Constants.SOURCE_MODRINTH,
                        "modpack".equals(projectType),
                        safeString(hit, "project_id", ""),
                        safeString(hit, "title", "Unnamed Mod"),
                        safeString(hit, "description", "No description available."),
                        iconUrl
                ));
            } catch (Throwable ignored) {
                // Skip one bad Modrinth row instead of failing the whole downloader screen.
            }
        }

        ModrinthSearchResult result = previousResult == null ? new ModrinthSearchResult() : previousResult;
        result.previousOffset += responseHits.size();
        result.results = itemList.toArray(new ModItem[0]);
        result.totalResultCount = response.has("total_hits") ? response.get("total_hits").getAsInt() : result.results.length;
        return result;
    }

    private static String safeString(JsonObject object, String key, String fallback) {
        if(object == null || !object.has(key) || object.get(key) == null || object.get(key).isJsonNull()) return fallback;
        try {
            return object.get(key).getAsString();
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    @Override
    public ModDetail getModDetails(ModItem item) {

        JsonArray response = mApiHandler.get(String.format("project/%s/version", item.id), JsonArray.class);
        if(response == null) return null;
        System.out.println(response);
        String currentMcVersion = getCurrentMinecraftVersion();
        String currentLoader = getCurrentModLoader();
        ArrayList<String> namesList = new ArrayList<>();
        ArrayList<String> mcNamesList = new ArrayList<>();
        ArrayList<String> urlsList = new ArrayList<>();
        ArrayList<String> hashesList = new ArrayList<>();

        for (int i=0; i<response.size(); ++i) {
            JsonObject version = response.get(i).getAsJsonObject();
            if(!item.isModpack && currentMcVersion != null && version.has("game_versions")
                    && !version.get("game_versions").getAsJsonArray().toString().contains("\"" + currentMcVersion + "\"")) {
                continue;
            }
            if(!item.isModpack && currentLoader != null && version.has("loaders")
                    && !version.get("loaders").getAsJsonArray().toString().toLowerCase(Locale.ROOT).contains("\"" + currentLoader + "\"")) {
                continue;
            }
            JsonArray files = version.get("files").getAsJsonArray();
            if(files.size() == 0) continue;
            JsonObject primaryFile = files.get(0).getAsJsonObject();
            namesList.add(version.get("name").getAsString());
            mcNamesList.add(version.get("game_versions").getAsJsonArray().get(0).getAsString());
            urlsList.add(primaryFile.get("url").getAsString());
            JsonObject hashesMap = primaryFile.get("hashes") == null ? null : primaryFile.get("hashes").getAsJsonObject();
            hashesList.add(hashesMap == null || hashesMap.get("sha1") == null ? null : hashesMap.get("sha1").getAsString());
        }

        // If Modrinth has no exact loader/version match, fall back to the full list instead of showing a broken empty card.
        if(namesList.isEmpty()) {
            for (int i=0; i<response.size(); ++i) {
                JsonObject version = response.get(i).getAsJsonObject();
                JsonArray files = version.get("files").getAsJsonArray();
                if(files.size() == 0) continue;
                JsonObject primaryFile = files.get(0).getAsJsonObject();
                namesList.add(version.get("name").getAsString());
                mcNamesList.add(version.get("game_versions").getAsJsonArray().get(0).getAsString());
                urlsList.add(primaryFile.get("url").getAsString());
                JsonObject hashesMap = primaryFile.get("hashes") == null ? null : primaryFile.get("hashes").getAsJsonObject();
                hashesList.add(hashesMap == null || hashesMap.get("sha1") == null ? null : hashesMap.get("sha1").getAsString());
            }
        }

        return new ModDetail(
                item,
                namesList.toArray(new String[0]),
                mcNamesList.toArray(new String[0]),
                urlsList.toArray(new String[0]),
                hashesList.toArray(new String[0])
        );
    }

    @Override
    public ModLoader installMod(ModDetail modDetail, int selectedVersion) throws IOException{
        if(!modDetail.isModpack) {
            installSingleMod(modDetail, selectedVersion);
            return null;
        }
        return ModpackInstaller.installModpack(modDetail, selectedVersion, this::installMrpack);
    }

    private void installSingleMod(ModDetail modDetail, int selectedVersion) throws IOException {
        if (selectedVersion < 0 || selectedVersion >= modDetail.versionUrls.length) {
            throw new IOException("Invalid Modrinth version selection");
        }

        File modsDirectory = getCurrentModsDirectory();
        String url = modDetail.versionUrls[selectedVersion];
        String fileName = buildSafeFileName(modDetail, selectedVersion, url);

        ModDownloader modDownloader = new ModDownloader(modsDirectory, true);
        modDownloader.submitDownload(() -> new ModDownloader.FileInfo(
                url,
                fileName,
                modDetail.versionHashes[selectedVersion]
        ));
        modDownloader.awaitFinish(new DownloaderProgressWrapper(
                R.string.modpack_download_downloading_mods,
                ProgressLayout.INSTALL_MODPACK
        ));
    }

    private static File getCurrentModsDirectory() throws IOException {
        File gameDirectory = new File(Tools.DIR_GAME_NEW);
        try {
            LauncherProfiles.load();
            String currentProfile = LauncherPreferences.DEFAULT_PREF.getString(
                    LauncherPreferences.PREF_KEY_CURRENT_PROFILE,
                    null
            );
            if (Tools.isValidString(currentProfile) && LauncherProfiles.mainProfileJson != null) {
                MinecraftProfile profile = LauncherProfiles.mainProfileJson.profiles.get(currentProfile);
                if (profile != null) gameDirectory = Tools.getGameDirPath(profile);
            }
        } catch (Throwable ignored) {
            // Fall back to the default .minecraft directory.
        }

        File modsDirectory = new File(gameDirectory, "mods");
        if (!modsDirectory.exists() && !modsDirectory.mkdirs()) {
            throw new IOException("Could not create mods folder: " + modsDirectory.getAbsolutePath());
        }
        return modsDirectory;
    }

    private static String buildSafeFileName(ModDetail modDetail, int selectedVersion, String url) {
        String extension = ".jar";
        try {
            String path = new URI(url).getPath();
            int slash = path.lastIndexOf('/');
            String remoteName = slash >= 0 ? path.substring(slash + 1) : path;
            if (remoteName.endsWith(".jar") || remoteName.endsWith(".zip")) {
                return remoteName.replaceAll("[^a-zA-Z0-9._-]", "_");
            }
        } catch (Throwable ignored) {
            // Build fallback below.
        }

        String versionName = modDetail.versionNames[selectedVersion] == null ? "latest" : modDetail.versionNames[selectedVersion];
        return (modDetail.title + "-" + versionName + extension).replaceAll("[^a-zA-Z0-9._-]", "_");
    }


    private static String getCurrentMinecraftVersion() {
        try {
            LauncherProfiles.load();
            String currentProfile = LauncherPreferences.DEFAULT_PREF.getString(LauncherPreferences.PREF_KEY_CURRENT_PROFILE, null);
            if (!Tools.isValidString(currentProfile) || LauncherProfiles.mainProfileJson == null) return null;
            MinecraftProfile profile = LauncherProfiles.mainProfileJson.profiles.get(currentProfile);
            if (profile == null || !Tools.isValidString(profile.lastVersionId)) return null;
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(?<!\\d)1\\.\\d+(?:\\.\\d+)?(?!\\d)").matcher(profile.lastVersionId);
            if (matcher.find()) return matcher.group();
        } catch (Throwable ignored) { }
        return null;
    }

    private static String getCurrentModLoader() {
        try {
            LauncherProfiles.load();
            String currentProfile = LauncherPreferences.DEFAULT_PREF.getString(LauncherPreferences.PREF_KEY_CURRENT_PROFILE, null);
            if (!Tools.isValidString(currentProfile) || LauncherProfiles.mainProfileJson == null) return null;
            MinecraftProfile profile = LauncherProfiles.mainProfileJson.profiles.get(currentProfile);
            if (profile == null || profile.lastVersionId == null) return null;
            String id = profile.lastVersionId.toLowerCase(Locale.ROOT);
            if (id.contains("fabric") || id.contains("durbin")) return "fabric";
            if (id.contains("forge")) return "forge";
            if (id.contains("quilt")) return "quilt";
        } catch (Throwable ignored) { }
        return null;
    }

    private static ModLoader createInfo(ModrinthIndex modrinthIndex) {
        if(modrinthIndex == null) return null;
        Map<String, String> dependencies = modrinthIndex.dependencies;
        String mcVersion = dependencies.get("minecraft");
        if(mcVersion == null) return null;
        String modLoaderVersion;
        if((modLoaderVersion = dependencies.get("forge")) != null) {
            return new ModLoader(ModLoader.MOD_LOADER_FORGE, modLoaderVersion, mcVersion);
        }
        if((modLoaderVersion = dependencies.get("fabric-loader")) != null) {
            return new ModLoader(ModLoader.MOD_LOADER_FABRIC, modLoaderVersion, mcVersion);
        }
        if((modLoaderVersion = dependencies.get("quilt-loader")) != null) {
            return new ModLoader(ModLoader.MOD_LOADER_QUILT, modLoaderVersion, mcVersion);
        }
        return null;
    }

    private ModLoader installMrpack(File mrpackFile, File instanceDestination) throws IOException {
        try (ZipFile modpackZipFile = new ZipFile(mrpackFile)){
            ModrinthIndex modrinthIndex = Tools.GLOBAL_GSON.fromJson(
                    Tools.read(ZipUtils.getEntryStream(modpackZipFile, "modrinth.index.json")),
                    ModrinthIndex.class);
            
            ModDownloader modDownloader = new ModDownloader(instanceDestination);
            for(ModrinthIndex.ModrinthIndexFile indexFile : modrinthIndex.files) {
                modDownloader.submitDownload(indexFile.fileSize, indexFile.path, indexFile.hashes.sha1, indexFile.downloads);
            }
            modDownloader.awaitFinish(new DownloaderProgressWrapper(R.string.modpack_download_downloading_mods, ProgressLayout.INSTALL_MODPACK));
            ProgressLayout.setProgress(ProgressLayout.INSTALL_MODPACK, 0, R.string.modpack_download_applying_overrides, 1, 2);
            ZipUtils.zipExtract(modpackZipFile, "overrides/", instanceDestination);
            ProgressLayout.setProgress(ProgressLayout.INSTALL_MODPACK, 50, R.string.modpack_download_applying_overrides, 2, 2);
            ZipUtils.zipExtract(modpackZipFile, "client-overrides/", instanceDestination);
            return createInfo(modrinthIndex);
        }
    }

    class ModrinthSearchResult extends SearchResult {
        int previousOffset;
    }
}
