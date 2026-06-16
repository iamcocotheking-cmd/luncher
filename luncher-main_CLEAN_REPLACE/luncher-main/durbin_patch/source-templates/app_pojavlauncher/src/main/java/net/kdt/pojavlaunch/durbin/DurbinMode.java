package net.kdt.pojavlaunch.durbin;

public final class DurbinMode {
    private DurbinMode() {}

    public static final String NAME = "DURBIN";
    public static final String DISPLAY_SUFFIX = " DURBIN";

    public static boolean isDurbinProfile(String profileName) {
        if (profileName == null) return false;
        return profileName.toUpperCase().contains("DURBIN");
    }
}
