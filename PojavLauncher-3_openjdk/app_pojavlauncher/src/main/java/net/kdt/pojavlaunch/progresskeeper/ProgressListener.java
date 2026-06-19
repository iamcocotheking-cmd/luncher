package net.kdt.pojavlaunch.progresskeeper;

public interface ProgressListener {
    void onProgressStarted();
    void onProgressUpdated(int progress, int resId, Object... data);
    void onProgressEnded();
}
