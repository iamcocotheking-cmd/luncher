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

private val DurbinOrange = Color(0xFFFF7A00)
private val DurbinGreen = Color(0xFF28D56A)
private val DurbinPanel = Color(0xFF111316)
private val DurbinPanelSoft = Color(0xFF171A1F)

private enum class VersionCategory(val label: String) {
    ALL("All"),
    VANILLA("Vanilla"),
    MODDED("Modded"),
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
                id = "vanilla",
                title = "Vanilla",
                version = "1.21",
                category = VersionCategory.VANILLA,
                subtitle = "Official Minecraft",
                description = "Pure Minecraft. No mods. Simple and stable for normal gameplay.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Home,
                status = "Ready",
                installed = false,
                addons = listOf("No mods", "Official", "Stable"),
                actionText = "CREATE VANILLA",
                action = onVanillaClick
            ),
            DurbinVersionOption(
                id = "fabric",
                title = "Fabric",
                version = "1.21.1",
                category = VersionCategory.MODDED,
                subtitle = "Lightweight mod loader",
                description = "Best choice for modern performance mods and small client mods.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Build,
                status = "Ready",
                installed = false,
                addons = listOf("Fabric API", "Sodium", "Iris"),
                actionText = "INSTALL FABRIC",
                action = onFabricClick
            ),
            DurbinVersionOption(
                id = "forge",
                title = "Forge",
                version = "1.20.1",
                category = VersionCategory.MODDED,
                subtitle = "Classic mod loader",
                description = "Great for bigger modpacks and older popular Minecraft mods.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Settings,
                status = "Ready",
                installed = false,
                addons = listOf("Forge", "Mods", "Modpacks"),
                actionText = "INSTALL FORGE",
                action = onForgeClick
            ),
            DurbinVersionOption(
                id = "quilt",
                title = "Quilt",
                version = "1.20.6",
                category = VersionCategory.MODDED,
                subtitle = "Community mod loader",
                description = "Clean modded profile option with Quilt loader support.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Extension,
                status = "Ready",
                installed = false,
                addons = listOf("Quilt", "Mods", "Lightweight"),
                actionText = "INSTALL QUILT",
                action = onQuiltClick
            ),
            DurbinVersionOption(
                id = "optifine",
                title = "OptiFine",
                version = "1.20.1",
                category = VersionCategory.MODDED,
                subtitle = "Performance and shaders",
                description = "Classic FPS and shader setup for older Minecraft versions.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Star,
                status = "Ready",
                installed = false,
                addons = listOf("FPS", "Shaders", "Classic"),
                actionText = "INSTALL OPTIFINE",
                action = onOptifineClick
            ),
            DurbinVersionOption(
                id = "modpacks",
                title = "Modpacks",
                version = "Browse",
                category = VersionCategory.MODDED,
                subtitle = "Search and install packs",
                description = "Open the modpack browser and install a ready-made pack.",
                imageRes = R.drawable.minecraft_banner,
                icon = Icons.Rounded.Download,
                status = "Browse",
                installed = false,
                addons = listOf("CurseForge", "Packs", "Fast install"),
                actionText = "BROWSE MODPACKS",
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
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        DurbinSideBar(
            onBack = onBack,
            modifier = Modifier.width(190.dp).fillMaxHeight()
        )

        DurbinVersionListPanel(
            options = options,
            selected = selected,
            selectedCategory = selectedCategory,
            onCategoryChange = onCategoryChange,
            onSelect = onSelect,
            modifier = Modifier.weight(1f).fillMaxHeight()
        )

        DurbinSelectedVersionPanel(
            selected = selected,
            modifier = Modifier.weight(1.08f).fillMaxHeight()
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
        corner = 28,
        borderAlpha = 0.18f
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Versions", color = Color.White, fontWeight = FontWeight.Black, fontSize = 23.sp)
                    Text("Pick a profile type. Install logic stays simple.", color = Color.White.copy(alpha = 0.62f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = DurbinOrange.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, DurbinOrange.copy(alpha = 0.35f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Tune, contentDescription = null, tint = DurbinOrange)
                    }
                }
            }

            DurbinCategoryTabs(
                selectedCategory = selectedCategory,
                onCategoryChange = onCategoryChange
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(options, key = { it.id }) { option ->
                    DurbinVersionCard(
                        option = option,
                        selected = selected.id == option.id,
                        onClick = { onSelect(option) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DurbinCategoryTabs(
    selectedCategory: VersionCategory,
    onCategoryChange: (VersionCategory) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        VersionCategory.values().forEach { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategoryChange(category) },
                label = {
                    Text(category.label, fontWeight = FontWeight.Black, maxLines = 1)
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = DurbinPanelSoft.copy(alpha = 0.74f),
                    selectedContainerColor = DurbinOrange.copy(alpha = 0.20f),
                    labelColor = Color.White.copy(alpha = 0.78f),
                    selectedLabelColor = DurbinOrange
                ),
                border = FilterChipDefaults.filterChipBorder(
                    selected = selectedCategory == category,
                    enabled = true,
                    borderColor = Color.White.copy(alpha = 0.12f),
                    selectedBorderColor = DurbinOrange.copy(alpha = 0.45f)
                )
            )
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
        modifier = Modifier.fillMaxWidth().height(104.dp),
        shape = RoundedCornerShape(22.dp),
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
                        colors = listOf(Color.Black.copy(alpha = 0.72f), Color.Black.copy(alpha = 0.36f), Color.Black.copy(alpha = 0.70f))
                    )
                )
            )

            Row(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(17.dp))
                        .background(if (option.id == "durbin") DurbinOrange.copy(alpha = 0.20f) else Color.Black.copy(alpha = 0.42f))
                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)), RoundedCornerShape(17.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (option.id == "durbin") {
                        Image(
                            painter = painterResource(id = R.drawable.icon),
                            contentDescription = null,
                            modifier = Modifier.size(42.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Icon(option.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(29.dp))
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${option.title} ${option.version}",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = option.subtitle,
                        color = Color.White.copy(alpha = 0.70f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                DurbinStatusBadge(option.status, option.installed)
            }
        }
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
        corner = 28,
        borderAlpha = 0.18f
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 7.3f)
                    .clip(RoundedCornerShape(24.dp))
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)), RoundedCornerShape(24.dp))
            ) {
                Image(
                    painter = painterResource(id = selected.imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.58f)))))
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.Black.copy(alpha = 0.46f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f))
                ) {
                    Text(
                        selected.version,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DurbinOrange.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (selected.id == "durbin") {
                        Image(
                            painter = painterResource(id = R.drawable.icon),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Icon(selected.icon, contentDescription = null, tint = DurbinOrange, modifier = Modifier.size(28.dp))
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${selected.title} ${selected.version}",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = selected.subtitle,
                        color = Color.White.copy(alpha = 0.70f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                DurbinStatusBadge(selected.status, selected.installed)
            }

            Text(
                text = selected.description,
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            DurbinVersionDropDown(selected)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Addons", color = Color.White.copy(alpha = 0.62f), fontWeight = FontWeight.Black, fontSize = 11.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    selected.addons.take(4).forEach { addon ->
                        AssistChip(
                            onClick = {},
                            label = { Text(addon, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = DurbinPanelSoft.copy(alpha = 0.78f),
                                labelColor = Color.White.copy(alpha = 0.82f)
                            ),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            ElevatedButton(
                onClick = { selected.action() },
                enabled = selected.enabled,
                modifier = Modifier.fillMaxWidth().height(68.dp),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = if (selected.enabled) DurbinOrange else Color.White.copy(alpha = 0.10f),
                    contentColor = if (selected.enabled) Color.White else Color.White.copy(alpha = 0.45f),
                    disabledContainerColor = Color.White.copy(alpha = 0.10f),
                    disabledContentColor = Color.White.copy(alpha = 0.45f)
                )
            ) {
                Icon(if (selected.enabled) Icons.Rounded.PlayArrow else Icons.Rounded.History, contentDescription = null, modifier = Modifier.size(34.dp))
                Spacer(Modifier.width(12.dp))
                Text(selected.actionText, fontSize = 23.sp, fontWeight = FontWeight.Black)
            }

            Text(
                text = if (selected.enabled) "One tap, then continue setup." else "DURBIN mod work comes later. UI is ready first.",
                color = Color.White.copy(alpha = 0.56f),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
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
                Text("Version", color = Color.White.copy(alpha = 0.54f), fontSize = 10.sp, fontWeight = FontWeight.Black)
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
