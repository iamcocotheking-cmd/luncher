package net.kdt.pojavlaunch.durbin

import android.content.Context
import android.widget.Toast
import net.kdt.pojavlaunch.PojavApplication
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.extra.ExtraConstants
import net.kdt.pojavlaunch.extra.ExtraCore
import net.kdt.pojavlaunch.instances.Instances
import net.kdt.pojavlaunch.modloaders.FabriclikeUtils
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL
import java.util.Locale

object DurbinInstaller {
    private const val FALLBACK_MC_VERSION = "1.21.1"

    @JvmStatic
    fun installDurbin(context: Context, selectedVersionId: String?) {
        val appContext = context.applicationContext
        Toast.makeText(appContext, "DURBIN install started...", Toast.LENGTH_SHORT).show()

        PojavApplication.sExecutorService.execute {
            try {
                val mcVersion = extractMinecraftVersion(selectedVersionId)
                val loaderVersion = FabriclikeUtils.FABRIC_UTILS
                    .downloadLoaderVersions(mcVersion)
                    ?.firstOrNull { it.stable }
                    ?.version
                    ?: FabriclikeUtils.FABRIC_UTILS.downloadLoaderVersions(FALLBACK_MC_VERSION)
                        ?.firstOrNull { it.stable }
                        ?.version
                    ?: throw IllegalStateException("Could not find Fabric loader version")

                val chosenMcVersion = mcVersion
                val fabricVersionId = FabriclikeUtils.FABRIC_UTILS.install(chosenMcVersion, loaderVersion)
                    ?: throw IllegalStateException("Fabric install failed")

                val instance = Instances.createInstance({ i ->
                    i.name = "DURBIN $chosenMcVersion"
                    i.icon = "fabric"
                    i.versionId = fabricVersionId
                    i.sharedData = true
                }, "durbin-$chosenMcVersion")

                Instances.setSelectedInstance(instance)
                ExtraCore.setValue(ExtraConstants.REFRESH_VERSION_SPINNER, null)

                val modsDir = File(Tools.DIR_GAME_NEW, "mods")
                if (!modsDir.exists()) modsDir.mkdirs()

                downloadLatestModrinthJar("sodium", chosenMcVersion, modsDir)
                downloadLatestModrinthJar("iris", chosenMcVersion, modsDir)
                downloadLatestModrinthJar("lithium", chosenMcVersion, modsDir)

                Tools.runOnUiThread {
                    Toast.makeText(appContext, "DURBIN $chosenMcVersion ready! Press play.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Tools.showErrorRemote(e)
                Tools.runOnUiThread {
                    Toast.makeText(appContext, "DURBIN install failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun extractMinecraftVersion(versionId: String?): String {
        if (versionId.isNullOrBlank()) return FALLBACK_MC_VERSION

        // Examples:
        // 1.21.1
        // fabric-loader-0.16.10-1.21.1
        // forge-1.20.1-47.3.0
        val matches = Regex("""1\.\d+(?:\.\d+)?""").findAll(versionId).map { it.value }.toList()
        if (matches.isEmpty()) return FALLBACK_MC_VERSION

        // Avoid very old selected profiles, because Sodium/Iris/Lithium may not support them well.
        val latest = matches.last()
        val parts = latest.split(".").mapNotNull { it.toIntOrNull() }
        if (parts.size >= 2 && parts[1] < 18) return FALLBACK_MC_VERSION
        return latest
    }

    private fun downloadLatestModrinthJar(slug: String, mcVersion: String, modsDir: File) {
        val versionsUrl =
            "https://api.modrinth.com/v2/project/$slug/version?loaders=%5B%22fabric%22%5D&game_versions=%5B%22" +
                URLEncoder.encode(mcVersion, "UTF-8") +
                "%22%5D"

        val json = requestText(versionsUrl)
        val array = JSONArray(json)
        if (array.length() == 0) {
            // Try fallback version if selected version has no compatible file.
            if (mcVersion != FALLBACK_MC_VERSION) {
                downloadLatestModrinthJar(slug, FALLBACK_MC_VERSION, modsDir)
            }
            return
        }

        val files = array.getJSONObject(0).getJSONArray("files")
        var selectedUrl: String? = null
        var selectedFileName: String? = null

        for (i in 0 until files.length()) {
            val file = files.getJSONObject(i)
            val name = file.optString("filename", "$slug.jar")
            if (name.lowercase(Locale.ROOT).endsWith(".jar")) {
                selectedUrl = file.getString("url")
                selectedFileName = name
                break
            }
        }

        val url = selectedUrl ?: return
        val fileName = sanitizeFileName(selectedFileName ?: "$slug-$mcVersion.jar")

        // Remove old copies from the same mod to prevent duplicate mod versions.
        modsDir.listFiles()?.forEach {
            if (it.name.lowercase(Locale.ROOT).startsWith(slug.lowercase(Locale.ROOT) + "-") ||
                it.name.lowercase(Locale.ROOT).startsWith(slug.lowercase(Locale.ROOT) + "_")) {
                it.delete()
            }
        }

        downloadFile(url, File(modsDir, fileName))
    }

    private fun requestText(urlString: String): String {
        val connection = (URL(urlString).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("User-Agent", "DURBIN-Launcher/1.0 (Minecraft mod installer)")
            connectTimeout = 20000
            readTimeout = 30000
        }
        connection.inputStream.bufferedReader().use { return it.readText() }
    }

    private fun downloadFile(urlString: String, output: File) {
        val connection = (URL(urlString).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("User-Agent", "DURBIN-Launcher/1.0 (Minecraft mod installer)")
            connectTimeout = 20000
            readTimeout = 60000
        }

        connection.inputStream.use { input ->
            FileOutputStream(output).use { outputStream ->
                input.copyTo(outputStream)
            }
        }
    }

    private fun sanitizeFileName(name: String): String {
        return name.replace(Regex("""[\\/:*?"<>|]"""), "_")
    }
}
