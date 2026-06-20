package net.kdt.pojavlaunch.durbin;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.firebase.FirebaseApp;

import net.kdt.pojavlaunch.Logger;
import net.kdt.pojavlaunch.instances.Instance;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * DURBIN Firebase mod link installer.
 *
 * Admin-editable links are loaded from Firebase Realtime Database:
 *
 * durbin/modLinks/1_20_1/url
 * durbin/modLinks/1_21_11/url
 *
 * If Firebase is offline/blocked/empty, it uses fallback links.
 * If the Firebase link changes, the cached zip is invalidated and redownloaded.
 */
public final class DurbinModPackInstaller {
    private static final String TAG = "DurbinModPackInstaller";
    private static final String ASSET_ROOT = "durbin_modpacks";

    private static final String FALLBACK_URL_1201 = "https://cdn.discordapp.com/attachments/1474466632666583284/1517603294401531995/1.20.1.zip?ex=6a36e1b5&is=6a359035&hm=5d82043c535fa9533504f3f0ff480e6025f9840e07d60ff6f36e7c8b7b79dc8c&";
    private static final String FALLBACK_URL_12111 = "https://cdn.discordapp.com/attachments/1474466632666583284/1517603546093322400/1.21.11.zip?ex=6a36e1f1&is=6a359071&hm=75a02d225f04b95c9f2f1e609c4e6903de7dd85f43af885cfce69fd33489f40a&";

    private DurbinModPackInstaller() {}

    public static void installForVersion(Context context, Instance instance, String versionId) {
        if (context == null || instance == null || versionId == null) return;

        String lower = versionId.toLowerCase(Locale.US);
        boolean isFabricLike = lower.contains("fabric") || lower.contains("durbin");
        if (!isFabricLike) {
            Logger.appendToLog("Info: DURBIN mod pack skipped: selected profile is not Fabric/DURBIN");
            return;
        }

        String packVersion;
        String firebaseKey;
        String fallbackUrl;

        if (lower.contains("1.21.11")) {
            packVersion = "1.21.11";
            firebaseKey = "1_21_11";
            fallbackUrl = FALLBACK_URL_12111;
        } else if (lower.contains("1.20.1")) {
            packVersion = "1.20.1";
            firebaseKey = "1_20_1";
            fallbackUrl = FALLBACK_URL_1201;
        } else {
            Logger.appendToLog("Info: DURBIN mod pack skipped: unsupported version " + versionId);
            return;
        }

        File modsDir = new File(instance.getGameDirectory(), "mods");
        if (!modsDir.isDirectory() && !modsDir.mkdirs()) {
            Logger.appendToLog("Warn: DURBIN could not create mods folder: " + modsDir.getAbsolutePath());
            return;
        }

        String packUrl = getFirebaseModUrlOrFallback(context, firebaseKey, fallbackUrl);

        try {
            File zipFile = getCachedOrDownloadedZip(context, packVersion, packUrl);
            int copied = extractJarsFromZip(zipFile, modsDir);
            Logger.appendToLog("Info: DURBIN " + packVersion + " Firebase/online mods ready. Installed/updated: " + copied);
        } catch (Throwable onlineError) {
            Log.e(TAG, "Online DURBIN mod install failed", onlineError);
            Logger.appendToLog("Warn: DURBIN online mod install failed: " + onlineError.getClass().getSimpleName() + ": " + onlineError.getMessage());

            try {
                int copied = extractJarsFromAsset(context.getAssets(), ASSET_ROOT + "/" + packVersion + ".zip", modsDir);
                Logger.appendToLog("Info: DURBIN asset fallback mods ready. Installed/updated: " + copied);
            } catch (Throwable assetError) {
                Logger.appendToLog("Warn: DURBIN asset fallback failed: " + assetError.getMessage());
            }
        }
    }

    private static String getFirebaseModUrlOrFallback(Context context, String firebaseKey, String fallbackUrl) {
        try {
            FirebaseApp app = FirebaseApp.getApps(context).isEmpty() ? FirebaseApp.initializeApp(context) : FirebaseApp.getApps(context).get(0);
            if (app == null || app.getOptions() == null || app.getOptions().getDatabaseUrl() == null) {
                Logger.appendToLog("Info: DURBIN Firebase mod link skipped: database URL missing. Using fallback.");
                return fallbackUrl;
            }

            String databaseUrl = app.getOptions().getDatabaseUrl();
            if (databaseUrl.endsWith("/")) databaseUrl = databaseUrl.substring(0, databaseUrl.length() - 1);

            // Preferred structure: durbin/modLinks/1_20_1/url = "https://..."
            String urlEndpoint = databaseUrl + "/durbin/modLinks/" + firebaseKey + "/url.json";
            String firebaseUrl = readFirebaseString(urlEndpoint);
            if (isUsableUrl(firebaseUrl)) {
                Logger.appendToLog("Info: DURBIN Firebase mod link loaded for " + firebaseKey);
                return firebaseUrl;
            }

            // Also support structure: durbin/modLinks/1_20_1 = { "url": "https://..." }
            String objectEndpoint = databaseUrl + "/durbin/modLinks/" + firebaseKey + ".json";
            String objectResponse = httpGetText(objectEndpoint);
            Object parsed = new JSONTokener(objectResponse).nextValue();
            if (parsed instanceof JSONObject) {
                String url = ((JSONObject) parsed).optString("url", "");
                if (isUsableUrl(url)) {
                    Logger.appendToLog("Info: DURBIN Firebase mod object link loaded for " + firebaseKey);
                    return url;
                }
            }

            Logger.appendToLog("Info: DURBIN Firebase mod link empty for " + firebaseKey + ". Using fallback.");
        } catch (Throwable t) {
            Logger.appendToLog("Warn: DURBIN Firebase mod link failed: " + t.getMessage() + ". Using fallback.");
        }

        return fallbackUrl;
    }

    private static String readFirebaseString(String endpoint) throws Exception {
        String response = httpGetText(endpoint);
        Object parsed = new JSONTokener(response).nextValue();
        if (parsed instanceof String) return (String) parsed;
        return "";
    }

    private static String httpGetText(String endpoint) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(15000);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("User-Agent", "DURBIN-Launcher/1.0");

        int code = connection.getResponseCode();
        if (code < 200 || code >= 300) {
            throw new IllegalStateException("Firebase HTTP " + code);
        }

        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) builder.append(line);
        } finally {
            connection.disconnect();
        }

        return builder.toString();
    }

    private static boolean isUsableUrl(String url) {
        if (url == null) return false;
        String trimmed = url.trim();
        return trimmed.startsWith("https://") || trimmed.startsWith("http://");
    }

    private static File getCachedOrDownloadedZip(Context context, String packVersion, String packUrl) throws Exception {
        File cacheDir = new File(context.getFilesDir(), "durbin_modpacks_cache");
        if (!cacheDir.isDirectory() && !cacheDir.mkdirs()) {
            throw new IllegalStateException("Could not create DURBIN cache folder");
        }

        File zipFile = new File(cacheDir, packVersion + ".zip");
        File urlMarker = new File(cacheDir, packVersion + ".url.txt");

        String oldUrl = readSmallText(urlMarker);
        boolean cacheMatchesUrl = packUrl.equals(oldUrl);

        if (zipFile.isFile() && zipFile.length() > 1024 * 1024 && cacheMatchesUrl) {
            Logger.appendToLog("Info: DURBIN using cached mod pack: " + zipFile.getName());
            return zipFile;
        }

        if (zipFile.exists() && !cacheMatchesUrl) {
            Logger.appendToLog("Info: DURBIN mod link changed. Redownloading " + packVersion);
            zipFile.delete();
        }

        Logger.appendToLog("Info: DURBIN downloading mod pack " + packVersion);
        File temp = new File(cacheDir, packVersion + ".zip.tmp");
        if (temp.exists()) temp.delete();

        HttpURLConnection connection = (HttpURLConnection) new URL(packUrl).openConnection();
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(90000);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("User-Agent", "DURBIN-Launcher/1.0");

        int code = connection.getResponseCode();
        if (code < 200 || code >= 300) {
            throw new IllegalStateException("HTTP " + code);
        }

        byte[] buffer = new byte[64 * 1024];
        long total = 0;
        try (InputStream in = new BufferedInputStream(connection.getInputStream());
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(temp))) {
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                total += read;
            }
        } finally {
            connection.disconnect();
        }

        if (total < 1024) throw new IllegalStateException("Downloaded mod pack is too small: " + total);
        if (zipFile.exists() && !zipFile.delete()) throw new IllegalStateException("Could not replace old cached zip");
        if (!temp.renameTo(zipFile)) throw new IllegalStateException("Could not save downloaded mod pack");

        writeSmallText(urlMarker, packUrl);

        Logger.appendToLog("Info: DURBIN downloaded " + packVersion + " mod pack (" + total + " bytes)");
        return zipFile;
    }

    private static String readSmallText(File file) {
        try {
            if (!file.isFile()) return "";
            byte[] data = new byte[(int) Math.min(file.length(), 8192)];
            try (FileInputStream in = new FileInputStream(file)) {
                int read = in.read(data);
                if (read <= 0) return "";
                return new String(data, 0, read, StandardCharsets.UTF_8).trim();
            }
        } catch (Throwable ignored) {
            return "";
        }
    }

    private static void writeSmallText(File file, String text) {
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(text.getBytes(StandardCharsets.UTF_8));
        } catch (Throwable ignored) {
        }
    }

    private static int extractJarsFromAsset(AssetManager assets, String assetZip, File modsDir) throws Exception {
        try (InputStream raw = assets.open(assetZip);
             ZipInputStream zip = new ZipInputStream(new BufferedInputStream(raw))) {
            return extractJars(zip, modsDir);
        }
    }

    private static int extractJarsFromZip(File zipFile, File modsDir) throws Exception {
        try (InputStream raw = new FileInputStream(zipFile);
             ZipInputStream zip = new ZipInputStream(new BufferedInputStream(raw))) {
            return extractJars(zip, modsDir);
        }
    }

    private static int extractJars(ZipInputStream zip, File modsDir) throws Exception {
        int copied = 0;
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
            if (outFile.isFile() && expectedSize > 0 && outFile.length() == expectedSize) continue;

            File tempFile = new File(modsDir, fileName + ".durbin_tmp");
            long written = 0;
            try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile))) {
                int read;
                while ((read = zip.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                    written += read;
                }
            }

            if (outFile.exists() && !outFile.delete()) throw new IllegalStateException("Could not replace old mod: " + outFile.getName());
            if (!tempFile.renameTo(outFile)) throw new IllegalStateException("Could not save mod: " + outFile.getName());

            copied++;
            Logger.appendToLog("Info: DURBIN mod ready: " + fileName + " (" + written + " bytes)");
        }
        return copied;
    }
}
