package net.kdt.pojavlaunch.profiles;

@FunctionalInterface
public interface VersionSelectorListener {
    void onVersionSelected(String versionId, boolean isSnapshot);
}
