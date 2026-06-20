package net.kdt.pojavlaunch.durbin;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.kdt.pojavlaunch.Logger;
import net.kdt.pojavlaunch.durbin.firebase.DurbinFirebaseConfig;
import net.kdt.pojavlaunch.instances.Instance;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * DURBIN remote mod installer.
 *
 * The real DURBIN Fabric mod packs are NOT bundled in the APK anymore.
 * This keeps the APK much smaller. The launcher downloads the correct pack
 * from Firebase/remote URL only when a supported DURBIN Fabric profile launches.
 *
 * Firebase paths:
 * durbin/modpacks/1_20_1/url
 * durbin/modpacks/1_21_11/url
 */
public final class DurbinModPackInstaller {
    private static final String TAG = "DurbinModPackInstaller";
    private static final String REMOTE_LINKS_ASSET = "durbin_modpacks/remote_links.properties";
    private static final int FIREBASE_WAIT_MS = 3500;
    private static final int CONNECT_TIMEOUT_MS = 15000;
    private static final int READ_TIMEOUT_MS = 45000;

    private DurbinModPackInstaller() {
    }

    public static void installForVersion(Context context, Instance instance, String versionId) {
        if (context == null || instance == null || versionId == null) return;

        String lower = versionId.toLowerCase(Locale.US);
        boolean isFabricLike = lower.contains("fabric") || lower.contains("durbin");
        if (!isFabricLike) {
            Logger.appendToLog("Info: DURBIN remote mod pack skipped: selected profile is not Fabric/DURBIN");
            return;
        }

        String packVersion = null;
        if (lower.contains("1.21.11")) {
            packVersion = "1.21.11";
        } else if (lower.contains("1.20.1")) {
            packVersion = "1.20.1";
        }

        if (packVersion == null) {
            Logger.appendToLog("Info: DURBIN remote mod pack skipped: unsupported version " + versionId);
            return;
        }

        File gameDir = instance.getGameDirectory();
        File modsDir = new File(gameDir, "mods");
        if (!modsDir.isDirectory() && !modsDir.mkdirs()) {
            Logger.appendToLog("Warn: DURBIN could not create mods folder: " + modsDir.getAbsolutePath());
            return;
        }

        try {
            String remoteUrl = resolveRemoteUrl(context, packVersion);
            if (remoteUrl == null || remoteUrl.trim().isEmpty() || remoteUrl.startsWith("PASTE_")) {
                Logger.appendToLog("Warn: DURBIN " + packVersion + " remote modpack URL is not configured.");
                Logger.appendToLog("Warn: Set Firebase: durbin/modpacks/" + firebaseKey(packVersion) + "/url");
                return;
            }

            File packZip = downloadOrUseCache(context, packVersion, remoteUrl.trim());
            int copied = extractJars(packZip, modsDir);
            Logger.appendToLog("Info: DURBIN " + packVersion + " remote mods ready. Installed/updated: " + copied);
        } catch (Throwable t) {
            Log.e(TAG, "Failed to install DURBIN remote mods", t);
            Logger.appendToLog("Warn: DURBIN remote mods failed: " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    private static String resolveRemoteUrl(Context context, String packVersion) {
        String firebaseUrl = readUrlFromFirebase(context, packVersion);
        if (firebaseUrl != null && !firebaseUrl.trim().isEmpty() && !firebaseUrl.startsWith("PASTE_")) {
            return firebaseUrl.trim();
        }

        return readUrlFromAssetConfig(context.getAssets(), packVersion);
    }

    private static String readUrlFromFirebase(Context context, String packVersion) {
        try {
            if (!DurbinFirebaseConfig.INSTANCE.ensureInitialized(context)) return null;

            AtomicReference<String> result = new AtomicReference<>(null);
            CountDownLatch latch = new CountDownLatch(1);

            FirebaseDatabase.getInstance()
                    .getReference("durbin/modpacks/" + firebaseKey(packVersion) + "/url")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            String value = snapshot.getValue(String.class);
                            result.set(value);
                            latch.countDown();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            Logger.appendToLog("Warn: DURBIN Firebase modpack URL read failed: " + error.getMessage());
                            latch.countDown();
                        }
                    });

            latch.await(FIREBASE_WAIT_MS, TimeUnit.MILLISECONDS);
            return result.get();
        } catch (Throwable t) {
            Logger.appendToLog("Warn: DURBIN Firebase modpack URL unavailable: " + t.getMessage());
            return null;
        }
    }

    private static String readUrlFromAssetConfig(AssetManager assets, String packVersion) {
        try (InputStream input = assets.open(REMOTE_LINKS_ASSET)) {
            Properties properties = new Properties();
            properties.load(input);
            String value = properties.getProperty(packVersion);
            if (value == null) value = properties.getProperty(firebaseKey(packVersion));
            return value;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static File downloadOrUseCache(Context context, String packVersion, String remoteUrl) throws Exception {
        File cacheDir = new File(context.getCacheDir(), "durbin_modpacks");
        if (!cacheDir.isDirectory() && !cacheDir.mkdirs()) {
            throw new IllegalStateException("Could not create cache folder");
        }

        File zipFile = new File(cacheDir, packVersion + ".zip");
        File urlFile = new File(cacheDir, packVersion + ".url");

        if (zipFile.isFile() && zipFile.length() > 1024 && urlFile.isFile()) {
            String oldUrl;
            try (FileInputStream in = new FileInputStream(urlFile)) {
                byte[] bytes = new byte[(int) Math.min(urlFile.length(), 4096)];
                int read = in.read(bytes);
                oldUrl = read > 0 ? new String(bytes, 0, read).trim() : "";
            }
            if (remoteUrl.equals(oldUrl)) {
                Logger.appendToLog("Info: DURBIN using cached modpack " + packVersion + " (" + zipFile.length() + " bytes)");
                return zipFile;
            }
        }

        Logger.appendToLog("Info: DURBIN downloading modpack " + packVersion + " from online link...");

        File tempFile = new File(cacheDir, packVersion + ".download");
        if (tempFile.exists() && !tempFile.delete()) {
            throw new IllegalStateException("Could not clear old temporary download");
        }

        HttpURLConnection connection = (HttpURLConnection) new URL(remoteUrl).openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
        connection.setReadTimeout(READ_TIMEOUT_MS);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("User-Agent", "DURBIN-Launcher");

        int code = connection.getResponseCode();
        if (code < 200 || code >= 300) {
            throw new IllegalStateException("Download HTTP " + code);
        }

        long written = 0;
        byte[] buffer = new byte[128 * 1024];
        try (InputStream input = new BufferedInputStream(connection.getInputStream());
             BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(tempFile))) {
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
                written += read;
            }
        } finally {
            connection.disconnect();
        }

        if (written < 1024) {
            throw new IllegalStateException("Downloaded file is too small");
        }

        if (zipFile.exists() && !zipFile.delete()) {
            throw new IllegalStateException("Could not replace cached modpack");
        }
        if (!tempFile.renameTo(zipFile)) {
            throw new IllegalStateException("Could not save downloaded modpack");
        }

        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(urlFile))) {
            out.write(remoteUrl.getBytes());
        }

        Logger.appendToLog("Info: DURBIN downloaded modpack " + packVersion + " (" + written + " bytes)");
        return zipFile;
    }

    private static int extractJars(File zipFile, File modsDir) throws Exception {
        try (InputStream raw = new FileInputStream(zipFile)) {
            return extractJars(raw, modsDir);
        }
    }

    private static int extractJars(InputStream raw, File modsDir) throws Exception {
        int copied = 0;

        try (ZipInputStream zip = new ZipInputStream(new BufferedInputStream(raw))) {
            ZipEntry entry;
            byte[] buffer = new byte[64 * 1024];

            while ((entry = zip.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;

                String name = entry.getName();
                if (!name.toLowerCase(Locale.US).endsWith(".jar")) continue;

                int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
                String fileName = slash >= 0 ? name.substring(slash + 1) : name;
                if (fileName.trim().isEmpty()) continue;

                File outFile = new File(modsDir, fileName);
                long expectedSize = entry.getSize();

                if (outFile.isFile() && expectedSize > 0 && outFile.length() == expectedSize) {
                    continue;
                }

                File tempFile = new File(modsDir, fileName + ".durbin_tmp");
                long written = 0;

                try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile))) {
                    int read;
                    while ((read = zip.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                        written += read;
                    }
                }

                if (outFile.exists() && !outFile.delete()) {
                    throw new IllegalStateException("Could not replace old mod: " + outFile.getName());
                }

                if (!tempFile.renameTo(outFile)) {
                    throw new IllegalStateException("Could not save mod: " + outFile.getName());
                }

                copied++;
                Logger.appendToLog("Info: DURBIN mod ready: " + fileName + " (" + written + " bytes)");
            }
        }

        return copied;
    }

    private static String firebaseKey(String packVersion) {
        return packVersion.replace('.', '_');
    }
}
