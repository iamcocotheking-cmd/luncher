package net.kdt.pojavlaunch.modloaders.modpacks.models;

import androidx.annotation.NonNull;

public class ModItem {
    public int apiSource;
    public boolean isModpack;
    public String id;
    public String title;
    public String description;
    public String imageUrl;

    public ModItem() {}

    public ModItem(int apiSource, boolean isModpack, String id, String title, String description, String imageUrl) {
        this.apiSource = apiSource;
        this.isModpack = isModpack;
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getIconCacheTag() {
        String safeId = id == null ? "unknown" : id;
        return apiSource + "_" + safeId;
    }

    @NonNull
    @Override
    public String toString() {
        return title == null ? super.toString() : title;
    }
}
