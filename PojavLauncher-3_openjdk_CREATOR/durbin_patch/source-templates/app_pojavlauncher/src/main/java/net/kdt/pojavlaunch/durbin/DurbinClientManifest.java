package net.kdt.pojavlaunch.durbin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DurbinClientManifest {
    public final String minecraftVersion;
    public final String loader;
    public final ModFile durbinMod;
    public final List<ModFile> dependencies;

    private DurbinClientManifest(String minecraftVersion, String loader, ModFile durbinMod, List<ModFile> dependencies) {
        this.minecraftVersion = minecraftVersion;
        this.loader = loader;
        this.durbinMod = durbinMod;
        this.dependencies = Collections.unmodifiableList(dependencies);
    }

    public static DurbinClientManifest fromJson(String json) throws JSONException {
        JSONObject root = new JSONObject(json);
        String minecraftVersion = root.optString("minecraftVersion", "");
        String loader = root.optString("loader", "fabric");
        ModFile durbinMod = ModFile.fromJson(root.getJSONObject("durbinMod"));

        ArrayList<ModFile> dependencies = new ArrayList<>();
        JSONArray deps = root.optJSONArray("dependencies");
        if (deps != null) {
            for (int i = 0; i < deps.length(); i++) {
                dependencies.add(ModFile.fromJson(deps.getJSONObject(i)));
            }
        }

        return new DurbinClientManifest(minecraftVersion, loader, durbinMod, dependencies);
    }

    public static final class ModFile {
        public final String name;
        public final String version;
        public final String fileName;
        public final String downloadUrl;
        public final String sha256;

        private ModFile(String name, String version, String fileName, String downloadUrl, String sha256) {
            this.name = name;
            this.version = version;
            this.fileName = fileName;
            this.downloadUrl = downloadUrl;
            this.sha256 = sha256;
        }

        public static ModFile fromJson(JSONObject object) {
            return new ModFile(
                    object.optString("name", "Unknown"),
                    object.optString("version", ""),
                    object.optString("fileName", ""),
                    object.optString("downloadUrl", ""),
                    object.optString("sha256", "")
            );
        }
    }
}
