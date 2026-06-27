package net.kdt.pojavlaunch.durbin

import android.content.Context
import android.widget.Toast
import com.kdt.mcgui.ProgressLayout
import net.kdt.pojavlaunch.PojavApplication
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.extra.ExtraConstants
import net.kdt.pojavlaunch.extra.ExtraCore
import net.kdt.pojavlaunch.instances.Instance
import net.kdt.pojavlaunch.instances.Instances
import net.kdt.pojavlaunch.modloaders.FabriclikeUtils
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper
import net.kdt.pojavlaunch.utils.DownloadUtils
import java.io.File
import org.json.JSONObject
import java.io.IOException
import java.util.Locale
import java.util.zip.ZipFile

object DurbinClientInstaller {
    private const val FABRIC_LOADER_VERSION = "0.19.3"
    private const val VERSION_MANIFEST_V2 = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"


    private fun updateInstallTask(progress: Int, message: String, onStatus: (String) -> Unit) {
        ProgressKeeper.submitProgress(ProgressLayout.INSTALL_MODPACK, progress, -1, message)
        Tools.runOnUiThread { onStatus(message) }
    }

    private fun clearInstallTask() {
        ProgressKeeper.clearProgress(ProgressLayout.INSTALL_MODPACK)
    }

    fun installAndLaunch(
        context: Context,
        minecraftVersion: String,
        zipUrl: String,
        onStatus: (String) -> Unit
    ) {
        updateInstallTask(3, "Preparing DURBIN Client $minecraftVersion...", onStatus)
        PojavApplication.sExecutorService.execute {
            try {
                val instance = installBlocking(context, minecraftVersion, zipUrl) { progress, message ->
                    updateInstallTask(progress, message, onStatus)
                }

                Tools.runOnUiThread {
                    Instances.setSelectedInstance(instance)
                    updateInstallTask(100, "Installed DURBIN Client $minecraftVersion. Launching Minecraft...", onStatus)
                    Toast.makeText(context, "Launching DURBIN Client $minecraftVersion", Toast.LENGTH_LONG).show()
                    ExtraCore.setValue(ExtraConstants.REFRESH_VERSION_SPINNER, null)
                    ExtraCore.setValue(ExtraConstants.LAUNCH_GAME, true)
                    Tools.runOnUiThread({ clearInstallTask() }, 2500)
                }
            } catch (t: Throwable) {
                Tools.runOnUiThread {
                    updateInstallTask(-1, "Install failed: ${t.message ?: "unknown error"}", onStatus)
                    Tools.showError(context, t)
                    Tools.runOnUiThread({ clearInstallTask() }, 4000)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun installBlocking(
        context: Context,
        minecraftVersion: String,
        zipUrl: String,
        onStatus: (Int, String) -> Unit
    ): Instance {
        val cleanVersion = minecraftVersion.trim()
        if (cleanVersion != "1.21.11") {
            throw IOException("Only DURBIN Client 1.21.11 is supported.")
        }

        ensureBaseVersionJson(cleanVersion) { onStatus(12, it) }

        onStatus(28, "Installing Fabric loader $FABRIC_LOADER_VERSION for $cleanVersion...")
        val fabricVersionId = installFabricProfile(cleanVersion)

        onStatus(42, "Creating DURBIN Client profile...")
        val instance = findOrCreateDurbinInstance(cleanVersion, fabricVersionId)

        val gameDir = instance.gameDirectory
        val modsDir = File(gameDir, "mods")
        if (!modsDir.exists() && !modsDir.mkdirs()) {
            throw IOException("Could not create mods folder: ${modsDir.absolutePath}")
        }

        onStatus(58, "Downloading DURBIN Client mod ZIP...")
        val zipFile = File(Tools.DIR_CACHE, "durbin-client-$cleanVersion.zip")
        if (zipFile.exists()) zipFile.delete()
        DownloadUtils.downloadFile(zipUrl, zipFile)

        onStatus(78, "Extracting mods from ZIP...")
        val extracted = extractModJars(zipFile, modsDir)
        if (extracted <= 0) {
            throw IOException("No .jar mods were found inside the DURBIN ZIP.")
        }

        instance.versionId = fabricVersionId
        instance.renderer = "opengles3_ltw"
        instance.sharedData = false
        instance.name = "DURBIN Client $cleanVersion"
        instance.maybeWrite()

        onStatus(96, "Ready: DURBIN Client $cleanVersion with $extracted mods.")
        return instance
    }


    @Throws(IOException::class)
    private fun ensureBaseVersionJson(minecraftVersion: String, onStatus: (String) -> Unit) {
        val versionDir = File(Tools.DIR_HOME_VERSION, minecraftVersion)
        val versionJson = File(versionDir, "$minecraftVersion.json")

        if (versionJson.isFile && versionJson.length() > 128) {
            return
        }

        onStatus("Downloading Minecraft $minecraftVersion metadata...")

        val manifestText = DownloadUtils.downloadString(VERSION_MANIFEST_V2)
        val manifest = JSONObject(manifestText)
        val versions = manifest.getJSONArray("versions")

        var metadataUrl: String? = null
        for (i in 0 until versions.length()) {
            val item = versions.getJSONObject(i)
            if (item.optString("id") == minecraftVersion) {
                metadataUrl = item.optString("url")
                break
            }
        }

        val url = metadataUrl
            ?: throw IOException(
                "Minecraft $minecraftVersion was not found in Mojang's official version manifest. " +
                    "Use 1.20.1, or confirm that $minecraftVersion is a real Java version supported by Fabric."
            )

        val json = DownloadUtils.downloadString(url)
        if (!versionDir.exists() && !versionDir.mkdirs()) {
            throw IOException("Could not create version folder: ${versionDir.absolutePath}")
        }

        Tools.write(versionJson, json)

        if (!versionJson.isFile || versionJson.length() <= 128) {
            throw IOException("Downloaded Minecraft $minecraftVersion version JSON is invalid.")
        }
    }

    @Throws(IOException::class)
    private fun installFabricProfile(minecraftVersion: String): String {
        val versionId = try {
            FabriclikeUtils.FABRIC_UTILS.install(minecraftVersion, FABRIC_LOADER_VERSION)
        } catch (t: Throwable) {
            null
        }

        if (!versionId.isNullOrBlank() && fabricVersionJsonExists(versionId)) {
            return versionId
        }

        val fallbackVersionId = "fabric-loader-$FABRIC_LOADER_VERSION-$minecraftVersion"
        if (fabricVersionJsonExists(fallbackVersionId)) {
            return fallbackVersionId
        }

        throw IOException(
            "Could not install Fabric $FABRIC_LOADER_VERSION for Minecraft $minecraftVersion. " +
                "Check internet and Fabric support for this version."
        )
    }

    private fun fabricVersionJsonExists(versionId: String): Boolean {
        val versionDir = File(Tools.DIR_HOME_VERSION, versionId)
        val versionJson = File(versionDir, "$versionId.json")
        return versionJson.isFile && versionJson.length() > 0
    }

    @Throws(IOException::class)
    private fun findOrCreateDurbinInstance(minecraftVersion: String, versionId: String): Instance {
        val targetName = "DURBIN Client $minecraftVersion"

        val existing = runCatching {
            Instances.loadAllInstances().firstOrNull {
                it.name.equals(targetName, ignoreCase = true)
            }
        }.getOrNull()

        if (existing != null) {
            existing.versionId = versionId
            existing.renderer = "opengles3_ltw"
            existing.sharedData = false
            existing.maybeWrite()
            return existing
        }

        return Instances.createInstance({ instance ->
            instance.name = targetName
            instance.icon = "icon"
            instance.versionId = versionId
            instance.renderer = "opengles3_ltw"
            instance.sharedData = false
        }, "durbin-client-$minecraftVersion")
    }

    @Throws(IOException::class)
    private fun extractModJars(zipFile: File, modsDir: File): Int {
        clearDurbinManagedMods(modsDir)

        var count = 0
        ZipFile(zipFile).use { zip ->
            val entries = zip.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                if (entry.isDirectory) continue

                val fileName = entry.name.substringAfterLast('/').trim()
                val lower = fileName.lowercase(Locale.ROOT)
                if (!lower.endsWith(".jar")) continue
                if (lower.contains("sources") || lower.contains("javadoc")) continue

                val safeName = "durbinclient_" + fileName.replace(Regex("[^A-Za-z0-9._-]"), "_")
                val outFile = File(modsDir, safeName)
                zip.getInputStream(entry).use { input ->
                    outFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                count++
            }
        }

        return count
    }

    private fun clearDurbinManagedMods(modsDir: File) {
        val old = modsDir.listFiles() ?: return
        old.forEach { file ->
            if (file.isFile && (file.name.startsWith("durbin_") || file.name.startsWith("cosa_") || file.name.startsWith("durbinclient_")) && file.name.endsWith(".jar")) {
                runCatching { file.delete() }
            }
        }
    }
}
