package net.kdt.pojavlaunch.durbin;

import java.util.ArrayList;
import java.util.List;

public class DurbinClientManifest {
    public String minecraftVersion = "1.21.1";
    public String loader = "fabric";
    public DurbinFile durbinClient = new DurbinFile();
    public List<DurbinFile> dependencies = new ArrayList<>();

    public static class DurbinFile {
        public String version = "1.0.0";
        public String fileName = "durbin-client.jar";
        public String downloadUrl = "";
        public String sha256 = "";
    }
}
