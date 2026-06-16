package net.kdt.pojavlaunch.durbin;

public final class DurbinInstallResult {
    public final boolean success;
    public final String message;

    private DurbinInstallResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static DurbinInstallResult ok(String message) {
        return new DurbinInstallResult(true, message);
    }

    public static DurbinInstallResult fail(String message) {
        return new DurbinInstallResult(false, message);
    }
}
