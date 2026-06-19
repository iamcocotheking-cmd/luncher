package net.kdt.pojavlaunch.modloaders.modpacks.models;

import com.google.gson.annotations.SerializedName;

public class CurseManifest {
    public String manifestType;
    public int manifestVersion;
    public CurseMinecraft minecraft;
    public CurseFile[] files = new CurseFile[0];
    public String overrides;

    public static class CurseMinecraft {
        public String version;
        public CurseModLoader[] modLoaders = new CurseModLoader[0];
    }

    public static class CurseModLoader {
        public String id;
        public boolean primary;
    }

    public static class CurseFile {
        @SerializedName("projectID")
        public long projectID;
        @SerializedName("fileID")
        public long fileID;
    }
}
