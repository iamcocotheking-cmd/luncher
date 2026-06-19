package net.kdt.pojavlaunch.modloaders.modpacks.models;

import androidx.annotation.Nullable;

public class ModItem {
    public int apiSource;
    public boolean isModpack;
    public String id;
    public String title;
    public String description;
    @Nullable public String imageUrl;

    public ModItem() {}

    public ModItem(int apiSource, boolean isModpack, String id, String title, String description, @Nullable String imageUrl) {
        this.apiSource = apiSource;
        this.isModpack = isModpack;
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getIconCacheTag() {
        return apiSource + "_" + (id == null ? "unknown" : id.replaceAll("[^A-Za-z0-9_.-]", "_"));
    }
}
