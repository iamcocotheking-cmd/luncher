package net.kdt.pojavlaunch.modloaders;

import androidx.annotation.Keep;

@Keep
public class FabricVersion {
    public String version;
    public boolean stable;

    public FabricVersion() {}

    public FabricVersion(String version, boolean stable) {
        this.version = version;
        this.stable = stable;
    }

    @Override
    public String toString() {
        return version == null ? "" : version;
    }
}
