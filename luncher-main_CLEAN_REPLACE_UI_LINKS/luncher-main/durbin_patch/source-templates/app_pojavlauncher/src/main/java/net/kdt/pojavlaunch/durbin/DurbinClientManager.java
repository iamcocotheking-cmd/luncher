package net.kdt.pojavlaunch.durbin;

import java.io.File;

public class DurbinClientManager {
    private final File minecraftDir;

    public DurbinClientManager(File minecraftDir) {
        this.minecraftDir = minecraftDir;
    }

    public DurbinInstallResult prepareDurbinClient(String minecraftVersion) {
        if (minecraftDir == null) {
            return DurbinInstallResult.fail("Minecraft folder was not found.");
        }

        File modsDir = new File(minecraftDir, "mods");
        if (!modsDir.exists() && !modsDir.mkdirs()) {
            return DurbinInstallResult.fail("Could not create mods folder.");
        }

        return DurbinInstallResult.ok("DURBIN Client Mode is ready for " + minecraftVersion);
    }
}
