package net.kdt.pojavlaunch.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.Games
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SettingsSuggest
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.ashmeet.hyperlauncher.R
import net.kdt.pojavlaunch.BaseActivity
import net.kdt.pojavlaunch.ui.theme.PojavTheme
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Speed

private val DurbinOrange = Color(0xFFFF7A00)
private val DurbinGreen = Color(0xFF28D56A)
private val DurbinPanel = Color(0xFF0F1115)
private val DurbinPanelSoft = Color(0xFF191D24)

private enum class VersionCategory(val label: String) {
    ALL("All"),
    VANILLA("Vanilla"),
    FABRIC("Fabric"),
    FORGE("Forge"),
    NEOFORGE("NeoForge"),
    QUILT("Quilt"),
    PVP("PvP"),
    PERFORMANCE("FPS"),
    PACKS("Packs"),
    OTHER("Other"),
    DURBIN("DURBIN")
}

private data class DurbinVersionOption(
    val id: String,
    val title: String,
    val version: String,
    val category: VersionCategory,
    val subtitle: String,
    val description: String,
    val imageRes: Int,
    val icon: ImageVector,
    val status: String,
    val installed: Boolean,
    val enabled: Boolean = true,
    val addons: List<String>,
    val actionText: String,
    val action: () -> Unit
)

@Composable
fun ProfileTypeSelectScreen(
    onBack: () -> Unit,
    onVanillaClick: () -> Unit,
    onOptifineClick: () -> Unit,
    onFabricClick: () -> Unit,
    onForgeClick: () -> Unit,
    onQuiltClick: () -> Unit,
    onNeoForgeClick: () -> Unit,
    onLegacyFabricClick: () -> Unit,
    onModpackClick: () -> Unit,
    onBTAClick: () -> Unit
) {
    val isPreview = LocalInspectionMode.current
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    val backgroundBitmap = if (isPreview) {
        try { BaseActivity.getBackgroundBitmap() } catch (_: Throwable) { null }
    } else null

    val options = remember {
        listOf(
            DurbinVersionOption(
                id = "vanilla_latest",
                title = "Vanilla",
                version = "1.21.11",
                category = VersionCategory.VANILLA,
                subtitle = "Latest official Minecraft",
                description = "Best for normal survival, servers, and clean Minecraft gameplay.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Home,
                status = "Recommended",
                installed = false,
                addons = listOf("Official", "Stable", "No mods"),
                actionText = "CREATE VANILLA",
                action = onVanillaClick
            ),
            DurbinVersionOption(
                id = "vanilla_1211",
                title = "Vanilla",
                version = "1.21.1",
                category = VersionCategory.VANILLA,
                subtitle = "Popular stable release",
                description = "Good for servers and modern Minecraft with better compatibility.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Home,
                status = "Stable",
                installed = false,
                addons = listOf("Official", "Servers", "Clean"),
                actionText = "CREATE VANILLA",
                action = onVanillaClick
            ),
            DurbinVersionOption(
                id = "vanilla_1201",
                title = "Vanilla",
                version = "1.20.1",
                category = VersionCategory.VANILLA,
                subtitle = "Classic modern version",
                description = "Very common version for modpacks and older servers.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Home,
                status = "Popular",
                installed = false,
                addons = listOf("Official", "Stable", "Popular"),
                actionText = "CREATE VANILLA",
                action = onVanillaClick
            ),

            DurbinVersionOption(
                id = "fabric_latest",
                title = "Fabric",
                version = "1.21.11",
                category = VersionCategory.FABRIC,
                subtitle = "Fast lightweight mod loader",
                description = "Best for performance mods and modern client mods. Great for Sodium and Iris.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Build,
                status = "Recommended",
                installed = false,
                addons = listOf("Fabric API", "Sodium", "Iris", "Lithium"),
                actionText = "INSTALL FABRIC",
                action = onFabricClick
            ),
            DurbinVersionOption(
                id = "fabric_1211",
                title = "Fabric",
                version = "1.21.1",
                category = VersionCategory.FABRIC,
                subtitle = "Best compatibility choice",
                description = "A strong choice for DURBIN Client later, performance mods, and PvP setups.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Build,
                status = "Ready",
                installed = false,
                addons = listOf("Fabric API", "Sodium", "Iris"),
                actionText = "INSTALL FABRIC",
                action = onFabricClick
            ),
            DurbinVersionOption(
                id = "fabric_1201",
                title = "Fabric",
                version = "1.20.1",
                category = VersionCategory.FABRIC,
                subtitle = "Popular modded version",
                description = "Good for many Fabric mods and lightweight modded gameplay.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Build,
                status = "Popular",
                installed = false,
                addons = listOf("Fabric API", "Sodium", "Mods"),
                actionText = "INSTALL FABRIC",
                action = onFabricClick
            ),

            DurbinVersionOption(
                id = "forge_1211",
                title = "Forge",
                version = "1.21.1",
                category = VersionCategory.FORGE,
                subtitle = "Forge for modern mods",
                description = "Use Forge when your modpack or server specifically needs Forge.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Settings,
                status = "Ready",
                installed = false,
                addons = listOf("Forge", "Mods", "Servers"),
                actionText = "INSTALL FORGE",
                action = onForgeClick
            ),
            DurbinVersionOption(
                id = "forge_1201",
                title = "Forge",
                version = "1.20.1",
                category = VersionCategory.FORGE,
                subtitle = "Most popular Forge version",
                description = "Best option for many modpacks. Very common and stable.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Settings,
                status = "Recommended",
                installed = false,
                addons = listOf("Forge", "Modpacks", "Popular"),
                actionText = "INSTALL FORGE",
                action = onForgeClick
            ),
            DurbinVersionOption(
                id = "forge_1192",
                title = "Forge",
                version = "1.19.2",
                category = VersionCategory.FORGE,
                subtitle = "Older modpack support",
                description = "Good for older Forge packs and server compatibility.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Settings,
                status = "Stable",
                installed = false,
                addons = listOf("Forge", "Older mods", "Stable"),
                actionText = "INSTALL FORGE",
                action = onForgeClick
            ),
            DurbinVersionOption(
                id = "forge_1182",
                title = "Forge",
                version = "1.18.2",
                category = VersionCategory.FORGE,
                subtitle = "Legacy popular Forge",
                description = "For legacy Forge modpacks that still use 1.18.2.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Settings,
                status = "Legacy",
                installed = false,
                addons = listOf("Forge", "Legacy", "Modpacks"),
                actionText = "INSTALL FORGE",
                action = onForgeClick
            ),
            DurbinVersionOption(
                id = "forge_1165",
                title = "Forge",
                version = "1.16.5",
                category = VersionCategory.FORGE,
                subtitle = "Classic modpack version",
                description = "One of the most used old Forge versions for classic modpacks.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Settings,
                status = "Classic",
                installed = false,
                addons = listOf("Forge", "Classic", "Old packs"),
                actionText = "INSTALL FORGE",
                action = onForgeClick
            ),

            DurbinVersionOption(
                id = "neoforge_1211",
                title = "NeoForge",
                version = "1.21.1",
                category = VersionCategory.NEOFORGE,
                subtitle = "New generation Forge-like loader",
                description = "Modern Forge-family loader for newer modded Minecraft setups.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.SettingsSuggest,
                status = "Ready",
                installed = false,
                addons = listOf("NeoForge", "Modern", "Mods"),
                actionText = "INSTALL NEOFORGE",
                action = onNeoForgeClick
            ),
            DurbinVersionOption(
                id = "neoforge_1206",
                title = "NeoForge",
                version = "1.20.6",
                category = VersionCategory.NEOFORGE,
                subtitle = "Modern modded option",
                description = "Good for mods built for NeoForge instead of classic Forge.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.SettingsSuggest,
                status = "Ready",
                installed = false,
                addons = listOf("NeoForge", "Mods", "Modern"),
                actionText = "INSTALL NEOFORGE",
                action = onNeoForgeClick
            ),

            DurbinVersionOption(
                id = "quilt_1211",
                title = "Quilt",
                version = "1.21.1",
                category = VersionCategory.QUILT,
                subtitle = "Community mod loader",
                description = "Clean modded loader option. Use it if the mod requires Quilt.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Extension,
                status = "Ready",
                installed = false,
                addons = listOf("Quilt", "Community", "Mods"),
                actionText = "INSTALL QUILT",
                action = onQuiltClick
            ),
            DurbinVersionOption(
                id = "quilt_1206",
                title = "Quilt",
                version = "1.20.6",
                category = VersionCategory.QUILT,
                subtitle = "Stable Quilt option",
                description = "Simple Quilt profile creation for supported mods.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Extension,
                status = "Stable",
                installed = false,
                addons = listOf("Quilt", "Mods", "Stable"),
                actionText = "INSTALL QUILT",
                action = onQuiltClick
            ),

            DurbinVersionOption(
                id = "optifine",
                title = "OptiFine",
                version = "1.20.1",
                category = VersionCategory.OTHER,
                subtitle = "FPS and shaders classic",
                description = "Classic performance and shader option for supported versions.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Star,
                status = "Ready",
                installed = false,
                addons = listOf("FPS", "Shaders", "Classic"),
                actionText = "INSTALL OPTIFINE",
                action = onOptifineClick
            ),
            DurbinVersionOption(
                id = "legacy_fabric",
                title = "Legacy Fabric",
                version = "Old",
                category = VersionCategory.OTHER,
                subtitle = "Older Fabric versions",
                description = "Use this for older Fabric-supported Minecraft versions.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.History,
                status = "Legacy",
                installed = false,
                addons = listOf("Legacy", "Fabric", "Old mods"),
                actionText = "INSTALL LEGACY FABRIC",
                action = onLegacyFabricClick
            ),
            DurbinVersionOption(
                id = "modpacks",
                title = "Modpacks",
                version = "Browse",
                category = VersionCategory.OTHER,
                subtitle = "Install ready-made packs",
                description = "Browse and install modpacks instead of manually choosing every mod.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Download,
                status = "Browse",
                installed = false,
                addons = listOf("Packs", "Fast install", "Browse"),
                actionText = "BROWSE MODPACKS",
                action = onModpackClick
            ),
            DurbinVersionOption(
                id = "bta",
                title = "Better Than Adventure",
                version = "BTA",
                category = VersionCategory.OTHER,
                subtitle = "Special profile type",
                description = "Create a Better Than Adventure profile if your launcher setup supports it.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Games,
                status = "Special",
                installed = false,
                addons = listOf("BTA", "Special", "Classic"),
                actionText = "CREATE BTA",
                action = onBTAClick
            ),


            DurbinVersionOption(
                id = "vanilla_1206",
                title = "Vanilla",
                version = "1.20.6",
                category = VersionCategory.VANILLA,
                subtitle = "Stable 1.20 release",
                description = "Good for normal play with newer fixes and good server support.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Home,
                status = "Stable",
                installed = false,
                addons = listOf("Official", "Stable", "Servers"),
                actionText = "CREATE VANILLA",
                action = onVanillaClick
            ),
            DurbinVersionOption(
                id = "vanilla_1194",
                title = "Vanilla",
                version = "1.19.4",
                category = VersionCategory.VANILLA,
                subtitle = "Older server support",
                description = "Useful for older servers that still run Minecraft 1.19.4.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Home,
                status = "Old",
                installed = false,
                addons = listOf("Official", "Servers", "Old"),
                actionText = "CREATE VANILLA",
                action = onVanillaClick
            ),
            DurbinVersionOption(
                id = "vanilla_1182",
                title = "Vanilla",
                version = "1.18.2",
                category = VersionCategory.VANILLA,
                subtitle = "Caves and Cliffs classic",
                description = "A stable older version with many server and modpack uses.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Home,
                status = "Classic",
                installed = false,
                addons = listOf("Official", "Classic", "Stable"),
                actionText = "CREATE VANILLA",
                action = onVanillaClick
            ),
            DurbinVersionOption(
                id = "vanilla_1171",
                title = "Vanilla",
                version = "1.17.1",
                category = VersionCategory.VANILLA,
                subtitle = "Legacy modern",
                description = "For older servers and nostalgia gameplay.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Home,
                status = "Legacy",
                installed = false,
                addons = listOf("Official", "Legacy", "Servers"),
                actionText = "CREATE VANILLA",
                action = onVanillaClick
            ),
            DurbinVersionOption(
                id = "vanilla_1165",
                title = "Vanilla",
                version = "1.16.5",
                category = VersionCategory.VANILLA,
                subtitle = "Nether update classic",
                description = "One of the most popular older Minecraft versions.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Home,
                status = "Classic",
                installed = false,
                addons = listOf("Official", "Classic", "Nether"),
                actionText = "CREATE VANILLA",
                action = onVanillaClick
            ),
            DurbinVersionOption(
                id = "vanilla_1122",
                title = "Vanilla",
                version = "1.12.2",
                category = VersionCategory.VANILLA,
                subtitle = "Old modpack base",
                description = "A legendary old version used by many classic modpacks.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Home,
                status = "Legacy",
                installed = false,
                addons = listOf("Official", "Old", "Classic"),
                actionText = "CREATE VANILLA",
                action = onVanillaClick
            ),
            DurbinVersionOption(
                id = "vanilla_189",
                title = "Vanilla",
                version = "1.8.9",
                category = VersionCategory.VANILLA,
                subtitle = "Classic PvP version",
                description = "Old-school Minecraft PvP version used by many PvP players.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Home,
                status = "PvP",
                installed = false,
                addons = listOf("Official", "PvP", "Classic"),
                actionText = "CREATE VANILLA",
                action = onVanillaClick
            ),

            DurbinVersionOption(
                id = "fabric_1206",
                title = "Fabric",
                version = "1.20.6",
                category = VersionCategory.FABRIC,
                subtitle = "Modern Fabric support",
                description = "Good Fabric version for lightweight mods and performance setups.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Build,
                status = "Stable",
                installed = false,
                addons = listOf("Fabric API", "Sodium", "Iris"),
                actionText = "INSTALL FABRIC",
                action = onFabricClick
            ),
            DurbinVersionOption(
                id = "fabric_1194",
                title = "Fabric",
                version = "1.19.4",
                category = VersionCategory.FABRIC,
                subtitle = "Older Fabric mods",
                description = "Useful for older Fabric mods and server compatibility.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Build,
                status = "Old",
                installed = false,
                addons = listOf("Fabric API", "Sodium", "Mods"),
                actionText = "INSTALL FABRIC",
                action = onFabricClick
            ),
            DurbinVersionOption(
                id = "fabric_1182",
                title = "Fabric",
                version = "1.18.2",
                category = VersionCategory.FABRIC,
                subtitle = "Legacy Fabric mods",
                description = "Legacy Fabric setup for older performance mods.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Build,
                status = "Legacy",
                installed = false,
                addons = listOf("Fabric API", "Sodium", "Legacy"),
                actionText = "INSTALL FABRIC",
                action = onFabricClick
            ),
            DurbinVersionOption(
                id = "fabric_1165",
                title = "Fabric",
                version = "1.16.5",
                category = VersionCategory.FABRIC,
                subtitle = "Old Fabric setup",
                description = "Use if your old Fabric mods need Minecraft 1.16.5.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Build,
                status = "Legacy",
                installed = false,
                addons = listOf("Fabric API", "Old mods", "Legacy"),
                actionText = "INSTALL FABRIC",
                action = onFabricClick
            ),

            DurbinVersionOption(
                id = "forge_1122",
                title = "Forge",
                version = "1.12.2",
                category = VersionCategory.FORGE,
                subtitle = "Legendary modpack version",
                description = "Very popular for classic Forge modpacks and older mods.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Settings,
                status = "Classic",
                installed = false,
                addons = listOf("Forge", "Classic packs", "Old mods"),
                actionText = "INSTALL FORGE",
                action = onForgeClick
            ),
            DurbinVersionOption(
                id = "forge_189",
                title = "Forge",
                version = "1.8.9",
                category = VersionCategory.FORGE,
                subtitle = "Old PvP mod support",
                description = "For classic PvP mods and old-school Forge setups.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Settings,
                status = "PvP",
                installed = false,
                addons = listOf("Forge", "PvP mods", "Classic"),
                actionText = "INSTALL FORGE",
                action = onForgeClick
            ),

            DurbinVersionOption(
                id = "pvp_1211",
                title = "PvP Setup",
                version = "1.21.1",
                category = VersionCategory.PVP,
                subtitle = "Modern PvP profile",
                description = "UI preset for PvP players. Later this can auto-add useful PvP mods.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Games,
                status = "Preset",
                installed = false,
                addons = listOf("Fabric", "Sodium", "Low lag", "PvP"),
                actionText = "CREATE PVP SETUP",
                action = onFabricClick
            ),
            DurbinVersionOption(
                id = "pvp_189",
                title = "PvP Setup",
                version = "1.8.9",
                category = VersionCategory.PVP,
                subtitle = "Classic PvP profile",
                description = "Old-school PvP setup for 1.8.9 gameplay.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Games,
                status = "Classic",
                installed = false,
                addons = listOf("1.8.9", "PvP", "Classic"),
                actionText = "CREATE PVP SETUP",
                action = onVanillaClick
            ),
            DurbinVersionOption(
                id = "performance_1211",
                title = "FPS Boost",
                version = "1.21.1",
                category = VersionCategory.PERFORMANCE,
                subtitle = "Performance focused profile",
                description = "UI preset for Sodium, Lithium, and Iris performance setup later.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Star,
                status = "Preset",
                installed = false,
                addons = listOf("Sodium", "Lithium", "Iris", "FPS"),
                actionText = "CREATE FPS SETUP",
                action = onFabricClick
            ),
            DurbinVersionOption(
                id = "performance_1201",
                title = "FPS Boost",
                version = "1.20.1",
                category = VersionCategory.PERFORMANCE,
                subtitle = "Low-end device friendly",
                description = "Good preset idea for phones with low RAM and weaker GPUs.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Star,
                status = "Preset",
                installed = false,
                addons = listOf("Sodium", "Low RAM", "FPS"),
                actionText = "CREATE FPS SETUP",
                action = onFabricClick
            ),
            DurbinVersionOption(
                id = "shader_1201",
                title = "Shader Ready",
                version = "1.20.1",
                category = VersionCategory.PERFORMANCE,
                subtitle = "Iris shader profile",
                description = "UI preset for Iris and shader-ready Fabric profiles.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Star,
                status = "Preset",
                installed = false,
                addons = listOf("Iris", "Sodium", "Shaders"),
                actionText = "CREATE SHADER SETUP",
                action = onFabricClick
            ),

            DurbinVersionOption(
                id = "curseforge_packs",
                title = "CurseForge Packs",
                version = "Browse",
                category = VersionCategory.PACKS,
                subtitle = "Browse modpacks",
                description = "Open the modpack browser for CurseForge-style packs.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Download,
                status = "Browse",
                installed = false,
                addons = listOf("Modpacks", "Browse", "Install"),
                actionText = "BROWSE PACKS",
                action = onModpackClick
            ),
            DurbinVersionOption(
                id = "vanilla_plus_pack",
                title = "Vanilla+ Pack",
                version = "Preset",
                category = VersionCategory.PACKS,
                subtitle = "Simple improved Minecraft",
                description = "UI preset for a simple Vanilla+ pack idea. Logic can come later.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Download,
                status = "Preset",
                installed = false,
                addons = listOf("Quality", "Simple", "Vanilla+"),
                actionText = "CREATE PRESET",
                action = onModpackClick
            ),
            DurbinVersionOption(
                id = "survival_pack",
                title = "Survival Pack",
                version = "Preset",
                category = VersionCategory.PACKS,
                subtitle = "Survival-focused profile",
                description = "UI preset for survival mods or server-ready gameplay.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Download,
                status = "Preset",
                installed = false,
                addons = listOf("Survival", "Mods", "Worlds"),
                actionText = "CREATE PRESET",
                action = onModpackClick
            ),
            DurbinVersionOption(
                id = "skyblock_pack",
                title = "Skyblock Pack",
                version = "Preset",
                category = VersionCategory.PACKS,
                subtitle = "Skyblock-ready profile",
                description = "A clean preset card for skyblock-style servers and modpacks.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Download,
                status = "Preset",
                installed = false,
                addons = listOf("Skyblock", "Servers", "Mods"),
                actionText = "CREATE PRESET",
                action = onModpackClick
            ),

            DurbinVersionOption(
                id = "durbin",
                title = "DURBIN",
                version = "Coming soon",
                category = VersionCategory.DURBIN,
                subtitle = "Client mode UI placeholder",
                description = "DURBIN mod install is not ready yet. This card is kept for the new UI first.",
                imageRes = R.drawable.durbin_banner,
                icon = Icons.Rounded.PlayArrow,
                status = "Soon",
                installed = false,
                enabled = false,
                addons = listOf("Fabric", "Sodium", "Iris", "DURBIN Mod later"),
                actionText = "COMING SOON",
                action = {}
            )
        )
    }

    var selectedCategory by remember { mutableStateOf(VersionCategory.ALL) }
    var selectedId by remember { mutableStateOf("vanilla") }
    val visibleOptions = remember(selectedCategory, options) {
        if (selectedCategory == VersionCategory.ALL) options else options.filter { it.category == selectedCategory }
    }
    val selected = options.firstOrNull { it.id == selectedId } ?: options.first()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .background(
                Brush.radialGradient(
                    colors = listOf(DurbinOrange.copy(alpha = 0.18f), Color.Transparent),
                    radius = 980f
                )
            )
    ) {
        if (isPreview && backgroundBitmap != null) {
            Image(
                bitmap = backgroundBitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.68f)))
        }

        if (isPortrait) {
            DurbinVersionPortrait(
                onBack = onBack,
                options = visibleOptions,
                selected = selected,
                selectedCategory = selectedCategory,
                onCategoryChange = { selectedCategory = it },
                onSelect = { selectedId = it.id }
            )
        } else {
            DurbinVersionLandscape(
                onBack = onBack,
                options = visibleOptions,
                selected = selected,
                selectedCategory = selectedCategory,
                onCategoryChange = { selectedCategory = it },
                onSelect = { selectedId = it.id }
            )
        }
    }
}


@Composable
private fun BoxScope.DurbinVersionLandscape(
    onBack: () -> Unit,
    options: List<DurbinVersionOption>,
    selected: DurbinVersionOption,
    selectedCategory: VersionCategory,
    onCategoryChange: (VersionCategory) -> Unit,
    onSelect: (DurbinVersionOption) -> Unit
) {
    val pageScroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(pageScroll)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DurbinEasyTopBar(onBack = onBack)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 620.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DurbinVersionListPanel(
                options = options,
                selected = selected,
                selectedCategory = selectedCategory,
                onCategoryChange = onCategoryChange,
                onSelect = onSelect,
                modifier = Modifier
                    .weight(0.92f)
                    .heightIn(min = 600.dp)
            )

            DurbinSelectedVersionPanel(
                selected = selected,
                modifier = Modifier
                    .weight(1.08f)
                    .heightIn(min = 600.dp)
            )
        }

        Spacer(Modifier.height(18.dp))
    }
}



@Composable
private fun DurbinEasyTopBar(onBack: () -> Unit) {
    DurbinGlass(
        modifier = Modifier.fillMaxWidth(),
        corner = 24,
        borderAlpha = 0.13f
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.height(46.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, DurbinOrange.copy(alpha = 0.34f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = DurbinOrange.copy(alpha = 0.12f),
                    contentColor = DurbinOrange
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(19.dp))
                Spacer(Modifier.width(8.dp))
                Text("Back", fontWeight = FontWeight.Black)
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DurbinOrange.copy(alpha = 0.12f))
                    .border(BorderStroke(1.dp, DurbinOrange.copy(alpha = 0.28f)), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = null,
                    modifier = Modifier.size(38.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text("DURBIN Version Center", color = Color.White, fontWeight = FontWeight.Black, fontSize = 23.sp, maxLines = 1)
                Text("Choose loader → pick version → press one big button.", color = Color.White.copy(alpha = 0.62f), fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            DurbinTopInfoPill("UI First", DurbinOrange)
            DurbinTopInfoPill("Mod Later", DurbinGreen)
        }
    }
}

@Composable
private fun DurbinTopInfoPill(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(15.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.32f))
    ) {
        Text(
            text,
            color = color,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
            maxLines = 1
        )
    }
}

@Composable
private fun BoxScope.DurbinVersionPortrait(
    onBack: () -> Unit,
    options: List<DurbinVersionOption>,
    selected: DurbinVersionOption,
    selectedCategory: VersionCategory,
    onCategoryChange: (VersionCategory) -> Unit,
    onSelect: (DurbinVersionOption) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DurbinTopCompact(onBack = onBack)
        DurbinCategoryTabs(
            selectedCategory = selectedCategory,
            onCategoryChange = onCategoryChange
        )
        options.forEach {
            DurbinVersionCard(
                option = it,
                selected = selected.id == it.id,
                onClick = { onSelect(it) }
            )
        }
        DurbinSelectedVersionPanel(
            selected = selected,
            modifier = Modifier.fillMaxWidth().heightIn(min = 520.dp)
        )
    }
}

@Composable
private fun DurbinSideBar(
    onBack: () -> Unit,
    modifier: Modifier
) {
    DurbinGlass(
        modifier = modifier,
        corner = 28,
        borderAlpha = 0.16f
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "DURBIN",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1
            )
            Text(
                text = "LAUNCHER",
                color = DurbinOrange,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )

            Spacer(Modifier.height(8.dp))

            DurbinNavButton("Back", Icons.AutoMirrored.Filled.ArrowBack, true, onBack)
            DurbinNavButton("Versions", Icons.Rounded.Home, false) {}
            DurbinNavButton("Modded", Icons.Rounded.Build, false) {}
            DurbinNavButton("DURBIN", Icons.Rounded.PlayArrow, false) {}

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(DurbinOrange.copy(alpha = 0.12f))
                    .border(BorderStroke(1.dp, DurbinOrange.copy(alpha = 0.22f)), RoundedCornerShape(24.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.icon),
                        contentDescription = null,
                        modifier = Modifier.size(62.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.height(10.dp))
                    Text("BUILT TO PLAY", color = DurbinOrange, fontWeight = FontWeight.Black, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun DurbinTopCompact(onBack: () -> Unit) {
    DurbinGlass(modifier = Modifier.fillMaxWidth(), corner = 24, borderAlpha = 0.18f) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Image(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = null,
                modifier = Modifier.size(42.dp),
                contentScale = ContentScale.Fit
            )
            Column(modifier = Modifier.weight(1f)) {
                Text("Versions", color = Color.White, fontWeight = FontWeight.Black, fontSize = 21.sp)
                Text("Choose what you want to install", color = Color.White.copy(alpha = 0.62f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun DurbinNavButton(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(18.dp),
        color = if (selected) DurbinOrange.copy(alpha = 0.18f) else Color.Transparent,
        border = if (selected) BorderStroke(1.dp, DurbinOrange.copy(alpha = 0.35f)) else BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(icon, contentDescription = label, tint = if (selected) DurbinOrange else Color.White.copy(alpha = 0.72f), modifier = Modifier.size(22.dp))
            Text(label, color = if (selected) DurbinOrange else Color.White.copy(alpha = 0.72f), fontWeight = FontWeight.Black)
        }
    }
}



@Composable
private fun DurbinVersionListPanel(
    options: List<DurbinVersionOption>,
    selected: DurbinVersionOption,
    selectedCategory: VersionCategory,
    onCategoryChange: (VersionCategory) -> Unit,
    onSelect: (DurbinVersionOption) -> Unit,
    modifier: Modifier
) {
    DurbinGlass(
        modifier = modifier,
        corner = 26,
        borderAlpha = 0.16f
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Choose Version", color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp)
                        Surface(
                            shape = CircleShape,
                            color = DurbinOrange.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, DurbinOrange.copy(alpha = 0.26f))
                        ) {
                            Text(
                                "${options.size} shown",
                                color = DurbinOrange,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Text("Scroll down for Forge, PvP, FPS and Packs.", color = Color.White.copy(alpha = 0.62f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Surface(
                    modifier = Modifier.size(42.dp),
                    shape = RoundedCornerShape(15.dp),
                    color = DurbinOrange.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, DurbinOrange.copy(alpha = 0.35f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Search, contentDescription = null, tint = DurbinOrange)
                    }
                }
            }

            DurbinCategoryTabs(
                selectedCategory = selectedCategory,
                onCategoryChange = onCategoryChange
            )

            Row(horizontalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                DurbinMiniInfo("Best", "Fabric 1.21.1")
                DurbinMiniInfo("PvP", "1.8.9 / 1.21.1")
                DurbinMiniInfo("Forge", "1.20.1 / 1.12.2")
                DurbinMiniInfo("FPS", "Sodium setup")
                DurbinMiniInfo("Packs", "Browse presets")
            }

            DurbinHelperStrip(
                icon = Icons.Rounded.Info,
                text = "Tip: choose Fabric for FPS mods, Forge for big modpacks, Vanilla for normal play."
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                options.forEach { option ->
                    DurbinVersionCard(
                        option = option,
                        selected = selected.id == option.id,
                        onClick = { onSelect(option) }
                    )
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}


@Composable
private fun DurbinHelperStrip(icon: ImageVector, text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.045f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.09f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = DurbinOrange, modifier = Modifier.size(17.dp))
            Text(
                text,
                color = Color.White.copy(alpha = 0.68f),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
private fun DurbinCategoryTabs(
    selectedCategory: VersionCategory,
    onCategoryChange: (VersionCategory) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        VersionCategory.values().forEach { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategoryChange(category) },
                label = {
                    Text(
                        category.label,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        fontSize = 12.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = DurbinPanelSoft.copy(alpha = 0.74f),
                    selectedContainerColor = DurbinOrange.copy(alpha = 0.22f),
                    labelColor = Color.White.copy(alpha = 0.78f),
                    selectedLabelColor = DurbinOrange
                ),
                border = FilterChipDefaults.filterChipBorder(
                    selected = selectedCategory == category,
                    enabled = true,
                    borderColor = Color.White.copy(alpha = 0.12f),
                    selectedBorderColor = DurbinOrange.copy(alpha = 0.48f)
                )
            )
        }
    }
}


@Composable
private fun DurbinMiniInfo(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.055f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(label, color = DurbinOrange, fontWeight = FontWeight.Black, fontSize = 10.sp)
            Text(value, color = Color.White.copy(alpha = 0.78f), fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }
    }
}



@Composable
private fun DurbinVersionCard(
    option: DurbinVersionOption,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(96.dp),
        shape = RoundedCornerShape(20.dp),
        color = DurbinPanel.copy(alpha = 0.92f),
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) DurbinOrange else Color.White.copy(alpha = 0.12f))
    ) {
        Box(Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = option.imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                Modifier.fillMaxSize().background(
                    Brush.horizontalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.78f), Color.Black.copy(alpha = 0.34f), Color.Black.copy(alpha = 0.74f))
                    )
                )
            )

            Row(
                modifier = Modifier.fillMaxSize().padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(if (option.id == "durbin") DurbinOrange.copy(alpha = 0.20f) else Color.Black.copy(alpha = 0.42f))
                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)), RoundedCornerShape(15.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (option.id == "durbin") {
                        Image(
                            painter = painterResource(id = R.drawable.icon),
                            contentDescription = null,
                            modifier = Modifier.size(39.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Icon(option.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                    }
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        DurbinSmallPill(option.category.label)
                        Text(
                            text = option.status,
                            color = if (option.installed) DurbinGreen else DurbinOrange,
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp,
                            maxLines = 1
                        )
                    }
                    Text(
                        text = "${option.title} ${option.version}",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = option.subtitle,
                        color = Color.White.copy(alpha = 0.72f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = if (selected) DurbinOrange else Color.White.copy(alpha = 0.46f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun DurbinSmallPill(text: String) {
    Surface(
        shape = RoundedCornerShape(9.dp),
        color = Color.Black.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Text(
            text,
            color = Color.White.copy(alpha = 0.74f),
            fontWeight = FontWeight.Black,
            fontSize = 8.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            maxLines = 1
        )
    }
}

@Composable
private fun DurbinStatusBadge(text: String, installed: Boolean) {
    Surface(
        shape = CircleShape,
        color = if (installed) DurbinGreen.copy(alpha = 0.16f) else DurbinOrange.copy(alpha = 0.16f),
        border = BorderStroke(1.dp, if (installed) DurbinGreen.copy(alpha = 0.55f) else DurbinOrange.copy(alpha = 0.55f))
    ) {
        Text(
            text = text,
            color = if (installed) DurbinGreen else DurbinOrange,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp),
            maxLines = 1
        )
    }
}



@Composable
private fun DurbinSelectedVersionPanel(
    selected: DurbinVersionOption,
    modifier: Modifier
) {
    DurbinGlass(
        modifier = modifier,
        corner = 26,
        borderAlpha = 0.16f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 7.6f)
                    .clip(RoundedCornerShape(22.dp))
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)), RoundedCornerShape(22.dp))
            ) {
                Image(
                    painter = painterResource(id = selected.imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.60f)))))
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(10.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.Black.copy(alpha = 0.50f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f))
                ) {
                    Text(
                        selected.version,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    modifier = Modifier.align(Alignment.BottomStart).padding(10.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = DurbinOrange.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, DurbinOrange.copy(alpha = 0.34f))
                ) {
                    Text(
                        selected.category.label.uppercase(),
                        color = DurbinOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(DurbinOrange.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (selected.id == "durbin") {
                        Image(
                            painter = painterResource(id = R.drawable.icon),
                            contentDescription = null,
                            modifier = Modifier.size(38.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Icon(selected.icon, contentDescription = null, tint = DurbinOrange, modifier = Modifier.size(27.dp))
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${selected.title} ${selected.version}",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = selected.subtitle,
                        color = Color.White.copy(alpha = 0.70f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                DurbinStatusBadge(selected.status, selected.installed)
            }

            Text(
                text = selected.description,
                color = Color.White.copy(alpha = 0.76f),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                DurbinInfoCard(
                    icon = Icons.Rounded.CheckCircle,
                    title = "Good for",
                    body = selected.category.label
                )
                DurbinInfoCard(
                    icon = Icons.Rounded.Speed,
                    title = "Setup",
                    body = if (selected.enabled) "Ready" else "Later"
                )
            }

            DurbinVersionDropDown(selected)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Included / Suggested", color = Color.White.copy(alpha = 0.62f), fontWeight = FontWeight.Black, fontSize = 11.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    selected.addons.forEach { addon ->
                        AssistChip(
                            onClick = {},
                            label = { Text(addon, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 11.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = DurbinPanelSoft.copy(alpha = 0.78f),
                                labelColor = Color.White.copy(alpha = 0.82f)
                            ),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                        )
                    }
                }
            }

            ElevatedButton(
                onClick = { selected.action() },
                enabled = selected.enabled,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = if (selected.enabled) DurbinOrange else Color.White.copy(alpha = 0.10f),
                    contentColor = if (selected.enabled) Color.White else Color.White.copy(alpha = 0.45f),
                    disabledContainerColor = Color.White.copy(alpha = 0.10f),
                    disabledContentColor = Color.White.copy(alpha = 0.45f)
                )
            ) {
                Icon(if (selected.enabled) Icons.Rounded.PlayArrow else Icons.Rounded.History, contentDescription = null, modifier = Modifier.size(31.dp))
                Spacer(Modifier.width(12.dp))
                Text(selected.actionText, fontSize = 20.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            DurbinHelperStrip(
                icon = Icons.Rounded.Info,
                text = if (selected.enabled) "This opens the existing installer flow. More auto-install logic can be added later." else "DURBIN mod work comes later. The UI placeholder is ready first."
            )
        }
    }
}

@Composable
private fun DurbinInfoCard(icon: ImageVector, title: String, body: String) {
    Surface(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.045f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = DurbinOrange, modifier = Modifier.size(18.dp))
            Column {
                Text(title, color = Color.White.copy(alpha = 0.55f), fontWeight = FontWeight.Black, fontSize = 9.sp)
                Text(body, color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun DurbinVersionDropDown(selected: DurbinVersionOption) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        onClick = { expanded = true },
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(16.dp),
        color = DurbinPanelSoft.copy(alpha = 0.72f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 14.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Selected version", color = Color.White.copy(alpha = 0.54f), fontSize = 10.sp, fontWeight = FontWeight.Black)
                Text(selected.version, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.65f),
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 14.dp).size(16.dp)
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text(selected.version) },
                    onClick = { expanded = false }
                )
            }
        }
    }
}

@Composable
private fun DurbinGlass(
    modifier: Modifier,
    corner: Int,
    borderAlpha: Float,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(corner.dp))
            .border(BorderStroke(1.dp, Color.White.copy(alpha = borderAlpha)), RoundedCornerShape(corner.dp)),
        color = DurbinPanel.copy(alpha = 0.86f),
        shape = RoundedCornerShape(corner.dp),
        tonalElevation = 0.dp
    ) {
        content()
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,orientation=landscape")
@Composable
fun ProfileTypeSelectScreenPreview() {
    PojavTheme(dynamicColor = true) {
        ProfileTypeSelectScreen(
            onBack = {},
            onVanillaClick = {},
            onOptifineClick = {},
            onFabricClick = {},
            onForgeClick = {},
            onQuiltClick = {},
            onNeoForgeClick = {},
            onLegacyFabricClick = {},
            onModpackClick = {},
            onBTAClick = {}
        )
    }
}
