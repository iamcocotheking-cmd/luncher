package net.kdt.pojavlaunch.modloaders;

import java.io.File;
import java.lang.ref.WeakReference;

public class ModloaderListenerProxy implements ModloaderDownloadListener {
    private WeakReference<ModloaderDownloadListener> listenerRef = new WeakReference<>(null);

    public void attachListener(ModloaderDownloadListener listener) {
        listenerRef = new WeakReference<>(listener);
    }

    public void detachListener() {
        listenerRef.clear();
    }

    private ModloaderDownloadListener listener() {
        return listenerRef.get();
    }

    @Override
    public void onDownloadFinished(File downloadedFile) {
        ModloaderDownloadListener listener = listener();
        if (listener != null) listener.onDownloadFinished(downloadedFile);
    }

    @Override
    public void onDataNotAvailable() {
        ModloaderDownloadListener listener = listener();
        if (listener != null) listener.onDataNotAvailable();
    }

    @Override
    public void onDownloadError(Exception e) {
        ModloaderDownloadListener listener = listener();
        if (listener != null) listener.onDownloadError(e);
    }
}
