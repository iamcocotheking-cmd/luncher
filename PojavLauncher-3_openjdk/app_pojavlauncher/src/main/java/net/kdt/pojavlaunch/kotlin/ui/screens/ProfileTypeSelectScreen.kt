package net.kdt.pojavlaunch.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.ashmeet.hyperlauncher.R
import net.kdt.pojavlaunch.BaseActivity
import net.kdt.pojavlaunch.ui.theme.PojavTheme

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
        try { BaseActivity.getBackgroundBitmap() } catch (e: Exception) { null }
    } else null
    val hasBackground = backgroundBitmap != null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .background(
                Brush.radialGradient(
                    colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), Color.Transparent),
                    radius = 900f
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
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = if (hasBackground) 0.55f else 0f)))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (isPortrait) 14.dp else 22.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            DurbinProfileTopBar(onBack = onBack)

            LazyVerticalGrid(
                columns = if (isPortrait) GridCells.Fixed(1) else GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    DurbinCreateHero(onFabricClick = onFabricClick)
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    CategoryHeader(title = stringResource(id = R.string.create_profile_vanilla_like_versions))
                }

                item {
                    ProfileTypeButton(
                        text = stringResource(id = R.string.create_instance_vanilla),
                        subtitle = "Clean Minecraft profile with no extra mods",
                        icon = Icons.Rounded.Home,
                        onClick = onVanillaClick
                    )
                }
                item {
                    ProfileTypeButton(
                        text = stringResource(id = R.string.mod_dl_install_optifine),
                        subtitle = "Classic performance and shader setup",
                        icon = Icons.Rounded.Star,
                        onClick = onOptifineClick
                    )
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    CategoryHeader(title = stringResource(id = R.string.create_profile_modded_versions))
                }

                item {
                    ProfileTypeButton(
                        text = "Create DURBIN Client",
                        subtitle = "Recommended: Fabric profile for DURBIN mod mode",
                        icon = Icons.Rounded.PlayArrow,
                        featured = true,
                        onClick = onFabricClick
                    )
                }
                item {
                    ProfileTypeButton(
                        text = stringResource(id = R.string.modloader_dl_install_fabric_instance),
                        subtitle = "Fabric loader profile",
                        icon = Icons.Rounded.Build,
                        onClick = onFabricClick
                    )
                }
                item {
                    ProfileTypeButton(
                        text = stringResource(id = R.string.modloader_dl_install_quilt_instance),
                        subtitle = "Quilt loader profile",
                        icon = Icons.Rounded.Extension,
                        onClick = onQuiltClick
                    )
                }
                item {
                    ProfileTypeButton(
                        text = stringResource(id = R.string.modloader_dl_install_legacy_fabric_instance),
                        subtitle = "Legacy Fabric loader profile",
                        icon = Icons.Rounded.History,
                        onClick = onLegacyFabricClick
                    )
                }
                item {
                    ProfileTypeButton(
                        text = stringResource(id = R.string.modloader_dl_install_forge_instance),
                        subtitle = "Forge loader profile",
                        icon = Icons.Rounded.Settings,
                        onClick = onForgeClick
                    )
                }
                item {
                    ProfileTypeButton(
                        text = stringResource(id = R.string.modloader_dl_install_neoforge_instance),
                        subtitle = "NeoForge loader profile",
                        icon = Icons.Rounded.SettingsSuggest,
                        onClick = onNeoForgeClick
                    )
                }
                item {
                    ProfileTypeButton(
                        text = stringResource(id = R.string.modpack_install_instance_button),
                        subtitle = "Browse/install modpacks",
                        icon = Icons.Rounded.Download,
                        onClick = onModpackClick
                    )
                }
                item {
                    ProfileTypeButton(
                        text = stringResource(id = R.string.create_bta_instance),
                        subtitle = "Better Than Adventure profile",
                        icon = Icons.Rounded.Games,
                        onClick = onBTAClick
                    )
                }
            }
        }
    }
}

@Composable
private fun DurbinProfileTopBar(onBack: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Image(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = null,
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Fit
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Create New Profile",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "DURBIN profile setup • Made by COSA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DurbinCreateHero(onFabricClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.76f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("DURBIN Client Mode", fontWeight = FontWeight.Black, fontSize = 22.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    "Create a Fabric-based profile for FPS HUD, keystrokes, crosshair, and future DURBIN modules.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Button(
                onClick = onFabricClick,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
            ) {
                Text("Start", fontWeight = FontWeight.Black)
                Spacer(Modifier.width(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun CategoryHeader(title: String) {
    Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f))
    }
}

@Composable
fun ProfileTypeButton(
    text: String,
    subtitle: String,
    icon: ImageVector,
    featured: Boolean = false,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (featured) MaterialTheme.colorScheme.primary.copy(alpha = 0.65f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.24f)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (featured) MaterialTheme.colorScheme.primary.copy(alpha = 0.20f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.74f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = PaddingValues(horizontal = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = if (featured) 0.26f else 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(23.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
        }
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
