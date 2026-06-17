package net.kdt.pojavlaunch.kotlin.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.ashmeet.hyperlauncher.R
import net.kdt.pojavlaunch.authenticator.accounts.Accounts
import net.kdt.pojavlaunch.authenticator.accounts.MinecraftAccount
import net.kdt.pojavlaunch.extra.ExtraConstants
import net.kdt.pojavlaunch.extra.ExtraCore
import net.kdt.pojavlaunch.extra.ExtraListener
import net.kdt.pojavlaunch.instances.Instance
import net.kdt.pojavlaunch.instances.InstanceIconProvider
import net.kdt.pojavlaunch.instances.Instances
import net.kdt.pojavlaunch.skin.SkinUtils
import net.kdt.pojavlaunch.ui.theme.PojavTheme
import net.kdt.pojavlaunch.durbin.firebase.DurbinFirebaseHubActivity
import net.kdt.pojavlaunch.durbin.DurbinInstaller

@Composable
fun rememberDrawablePainter(drawable: Drawable?): Painter {
    return remember(drawable) {
        if (drawable == null) {
            object : Painter() {
                override val intrinsicSize: Size get() = Size.Unspecified
                override fun DrawScope.onDraw() {}
            }
        } else {
            object : Painter() {
                override val intrinsicSize: Size
                    get() = Size(drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight.toFloat())

                override fun DrawScope.onDraw() {
                    drawIntoCanvas { canvas ->
                        drawable.setBounds(0, 0, size.width.toInt(), size.height.toInt())
                        drawable.draw(canvas.nativeCanvas)
                    }
                }
            }
        }
    }
}

@Composable
fun MainMenuRevamp(
    onEditProfileClick: () -> Unit,
    onCustomControlsClick: () -> Unit,
    onInstallJarClick: () -> Unit,
    onShareLogsClick: () -> Unit,
    onOpenFilesClick: () -> Unit,
    onYoutubeClick: () -> Unit,
    onSocialMediaClick: () -> Unit,
    onPlayClick: () -> Unit,
    onTerminateClick: () -> Unit,
    onInstanceSelect: () -> Unit
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    var selectedInstance by remember {
        mutableStateOf<Instance?>(if (isPreview) null else try { Instances.loadSelectedInstance() } catch (_: Exception) { null })
    }

    SideEffect {
        if (!isPreview) {
            val instance = try { Instances.loadSelectedInstance() } catch (_: Exception) { null }
            if (selectedInstance != instance) selectedInstance = instance
        }
    }

    var currentAccount by remember {
        mutableStateOf<MinecraftAccount?>(if (isPreview) null else Accounts.getCurrent())
    }

    DisposableEffect(Unit) {
        if (isPreview) return@DisposableEffect onDispose {}
        val listener = ExtraListener<Boolean> { _, _ ->
            currentAccount = Accounts.getCurrent()
            false
        }
        ExtraCore.addExtraListener(ExtraConstants.REFRESH_ACCOUNT_SPINNER, listener)
        onDispose { ExtraCore.removeExtraListenerFromValue(ExtraConstants.REFRESH_ACCOUNT_SPINNER, listener) }
    }

    val skinHead by produceState<Bitmap?>(initialValue = null, currentAccount) {
        value = SkinUtils.renderHead(context, currentAccount)
    }

    val instanceIcon = remember(selectedInstance) {
        if (!isPreview && selectedInstance != null) InstanceIconProvider.fetchIcon(context.resources, selectedInstance!!)
        else null
    }

    val headInteractionSource = remember { MutableInteractionSource() }
    val isHeadPressed by headInteractionSource.collectIsPressedAsState()
    val headScale by animateFloatAsState(
        targetValue = if (isHeadPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "headScale"
    )

    val glowPulse = 0.12f

    var terminateRotationAngle by remember { mutableFloatStateOf(0f) }
    val animatedTerminateRotation by animateFloatAsState(
        targetValue = terminateRotationAngle,
        animationSpec = tween(durationMillis = 600),
        label = "terminateRotation"
    )

    var showTerminateConfirm by remember { mutableStateOf(false) }
    if (showTerminateConfirm) {
        AlertDialog(
            onDismissRequest = { showTerminateConfirm = false },
            title = { Text("Terminate Game", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(id = R.string.mcn_exit_confirm), style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                Button(
                    onClick = {
                        showTerminateConfirm = false
                        onTerminateClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) { Text("Terminate", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showTerminateConfirm = false }, shape = RoundedCornerShape(12.dp)) {
                    Text(stringResource(id = android.R.string.cancel))
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        )
    }

    val terminateClick = {
        terminateRotationAngle += 360f
        showTerminateConfirm = true
    }

    DurbinBackground(glowPulse = glowPulse) {
        // V5: no vertical/portrait layout. Always use the clean horizontal dashboard.
        DurbinLandscapeHome(
            selectedInstance = selectedInstance,
            instanceIcon = instanceIcon,
            currentAccount = currentAccount,
            skinHead = skinHead,
            headScale = headScale,
            headInteractionSource = headInteractionSource,
            animatedTerminateRotation = animatedTerminateRotation,
            glowPulse = glowPulse,
            onEditProfileClick = onEditProfileClick,
            onCustomControlsClick = onCustomControlsClick,
            onInstallJarClick = onInstallJarClick,
            onShareLogsClick = onShareLogsClick,
            onOpenFilesClick = onOpenFilesClick,
            onYoutubeClick = onYoutubeClick,
            onSocialMediaClick = onSocialMediaClick,
            onPlayClick = onPlayClick,
            onTerminateClick = terminateClick,
            onInstanceSelect = onInstanceSelect
        )
    }
}



@Composable
private fun DurbinBackground(glowPulse: Float, content: @Composable BoxScope.() -> Unit) {
    // V5: cleaner and faster background. No heavy animation, no giant orange blur.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF080808),
                        Color(0xFF030303),
                        Color.Black
                    )
                )
            ),
        content = content
    )
}


@Composable
private fun DurbinLandscapeHome(
    selectedInstance: Instance?,
    instanceIcon: Drawable?,
    currentAccount: MinecraftAccount?,
    skinHead: Bitmap?,
    headScale: Float,
    headInteractionSource: MutableInteractionSource,
    animatedTerminateRotation: Float,
    glowPulse: Float,
    onEditProfileClick: () -> Unit,
    onCustomControlsClick: () -> Unit,
    onInstallJarClick: () -> Unit,
    onShareLogsClick: () -> Unit,
    onOpenFilesClick: () -> Unit,
    onYoutubeClick: () -> Unit,
    onSocialMediaClick: () -> Unit,
    onPlayClick: () -> Unit,
    onTerminateClick: () -> Unit,
    onInstanceSelect: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(rememberScrollState())
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        DurbinTopBar(
            onYoutubeClick = onYoutubeClick,
            onSocialMediaClick = onSocialMediaClick,
            compact = true,
            glowPulse = glowPulse
        )

        DurbinVersionBanner(selectedInstance = selectedInstance)

        Row(
            modifier = Modifier
                .widthIn(min = 980.dp)
                .fillMaxWidth()
                .heightIn(min = 420.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DurbinActionPanel(
                modifier = Modifier
                    .weight(1.22f)
                    .heightIn(min = 420.dp),
                selectedInstance = selectedInstance,
                onCustomControlsClick = onCustomControlsClick,
                onInstallJarClick = onInstallJarClick,
                onShareLogsClick = onShareLogsClick,
                onOpenFilesClick = onOpenFilesClick,
                onYoutubeClick = onYoutubeClick,
                onSocialMediaClick = onSocialMediaClick,
                singleColumn = false
            )

            DurbinProfilePanel(
                modifier = Modifier
                    .weight(0.78f)
                    .heightIn(min = 420.dp),
                selectedInstance = selectedInstance,
                instanceIcon = instanceIcon,
                currentAccount = currentAccount,
                skinHead = skinHead,
                headScale = headScale,
                headInteractionSource = headInteractionSource,
                animatedTerminateRotation = animatedTerminateRotation,
                glowPulse = glowPulse,
                onEditProfileClick = onEditProfileClick,
                onPlayClick = onPlayClick,
                onTerminateClick = onTerminateClick,
                onInstanceSelect = onInstanceSelect
            )
        }
    }
}

@Composable
private fun DurbinPortraitHome(
    selectedInstance: Instance?,
    instanceIcon: Drawable?,
    currentAccount: MinecraftAccount?,
    skinHead: Bitmap?,
    headScale: Float,
    headInteractionSource: MutableInteractionSource,
    animatedTerminateRotation: Float,
    glowPulse: Float,
    onEditProfileClick: () -> Unit,
    onCustomControlsClick: () -> Unit,
    onInstallJarClick: () -> Unit,
    onShareLogsClick: () -> Unit,
    onOpenFilesClick: () -> Unit,
    onYoutubeClick: () -> Unit,
    onSocialMediaClick: () -> Unit,
    onPlayClick: () -> Unit,
    onTerminateClick: () -> Unit,
    onInstanceSelect: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        DurbinTopBar(onYoutubeClick = onYoutubeClick, onSocialMediaClick = onSocialMediaClick, compact = true, glowPulse = glowPulse)

        DurbinActionPanel(
            modifier = Modifier.fillMaxWidth(),
            onCustomControlsClick = onCustomControlsClick,
            onInstallJarClick = onInstallJarClick,
            onShareLogsClick = onShareLogsClick,
            onOpenFilesClick = onOpenFilesClick,
            onYoutubeClick = onYoutubeClick,
            onSocialMediaClick = onSocialMediaClick,
            singleColumn = true
        )

        DurbinProfilePanel(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 320.dp),
            selectedInstance = selectedInstance,
            instanceIcon = instanceIcon,
            currentAccount = currentAccount,
            skinHead = skinHead,
            headScale = headScale,
            headInteractionSource = headInteractionSource,
            animatedTerminateRotation = animatedTerminateRotation,
            glowPulse = glowPulse,
            onEditProfileClick = onEditProfileClick,
            onPlayClick = onPlayClick,
            onTerminateClick = onTerminateClick,
            onInstanceSelect = onInstanceSelect
        )
    }
}



@Composable
private fun DurbinTopBar(
    onYoutubeClick: () -> Unit,
    onSocialMediaClick: () -> Unit,
    compact: Boolean = false,
    glowPulse: Float
) {
    DurbinGlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 58.dp),
        corner = 20,
        borderAlpha = 0.18f
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "DURBIN Launcher",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "COSA Edition • News • PvP Tier • Minecraft Java",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            SmallIconButton(icon = Icons.Rounded.PlayArrow, label = "YouTube", onClick = onYoutubeClick)
            SmallIconButton(icon = Icons.Rounded.Share, label = "Discord", onClick = onSocialMediaClick)
        }
    }
}

@Composable
private fun DurbinHeroCard(modifier: Modifier, compact: Boolean) {
    DurbinGlassSurface(modifier = modifier, corner = 30, borderAlpha = 0.34f) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(if (compact) 62.dp else 92.dp)
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
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Premium Android Java Launcher",
                    fontWeight = FontWeight.Black,
                    fontSize = if (compact) 19.sp else 24.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Fast profile access, custom controls, mod installs, and DURBIN themed dashboard.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    maxLines = if (compact) 3 else 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (!compact) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DurbinStatusChip("DURBIN UI")
                        DurbinStatusChip("default2 controls")
                        DurbinStatusChip("COSA")
                    }
                }
            }
        }
    }
}

@Composable
private fun SmallIconButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = CircleShape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.48f)),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 9.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(6.dp))
        Text(label, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}




@Composable
private fun DurbinVersionBanner(selectedInstance: Instance?) {
    val versionText = selectedInstance?.versionId ?: "No version selected"
    val nameText = selectedInstance?.name ?: ""
    val isDurbin = remember(versionText, nameText) {
        versionText.contains("durbin", ignoreCase = true) || nameText.contains("durbin", ignoreCase = true)
    }
    val bannerRes = if (isDurbin) R.drawable.durbin_banner else R.drawable.minecraft_banner
    val title = if (isDurbin) "DURBIN Client Mode" else "Minecraft Java Mode"
    val subtitle = if (isDurbin) {
        "Fabric + Sodium + Iris + Lithium ready"
    } else {
        "Vanilla, Fabric, Forge and normal Minecraft profiles"
    }

    Box(
        modifier = Modifier
            .widthIn(min = 980.dp)
            .fillMaxWidth()
            .height(118.dp)
            .clip(RoundedCornerShape(22.dp))
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)),
                RoundedCornerShape(22.dp)
            )
    ) {
        Image(
            painter = painterResource(id = bannerRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.72f),
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.12f)
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = versionText,
                color = Color(0xFFE0E0E0),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DurbinActionPanel(
    modifier: Modifier,
    selectedInstance: Instance?,
    onCustomControlsClick: () -> Unit,
    onInstallJarClick: () -> Unit,
    onShareLogsClick: () -> Unit,
    onOpenFilesClick: () -> Unit,
    onYoutubeClick: () -> Unit,
    onSocialMediaClick: () -> Unit,
    singleColumn: Boolean = false
) {
    val context = LocalContext.current

    DurbinGlassSurface(modifier = modifier, corner = 24, borderAlpha = 0.16f) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Dashboard, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                }
                Column {
                    Text(
                        text = "Launcher Hub",
                        fontWeight = FontWeight.Black,
                        fontSize = 21.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Fast tools, cleaner layout, no vertical dashboard",
                        color = Color(0xFFCFCFCF),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                DurbinActionCard(Modifier.weight(1f).height(70.dp), "News", Icons.Rounded.Article, "Latest launcher updates", {
                    context.startActivity(DurbinFirebaseHubActivity.newsIntent(context))
                }, accent = true)
                DurbinActionCard(Modifier.weight(1f).height(70.dp), "PvP Tier List", Icons.Rounded.EmojiEvents, "Tank • NethPot • Sword • Crystal", {
                    context.startActivity(DurbinFirebaseHubActivity.tierListIntent(context))
                }, accent = true)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                DurbinActionCard(Modifier.weight(1f).height(62.dp), "Wiki", Icons.Rounded.Book, "Open help page", {
                    context.startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://pojavlauncherteam.github.io/")))
                })
                DurbinActionCard(Modifier.weight(1f).height(62.dp), "Discord", Icons.Rounded.Share, "Join community", onSocialMediaClick)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                DurbinActionCard(Modifier.weight(1f).height(62.dp), stringResource(id = R.string.mcl_option_customcontrol), Icons.Rounded.SportsEsports, "Edit mobile buttons", onCustomControlsClick)
                DurbinActionCard(Modifier.weight(1f).height(62.dp), stringResource(id = R.string.main_install_jar_file), Icons.Rounded.Terminal, "Run or install .jar", onInstallJarClick)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                DurbinActionCard(Modifier.weight(1f).height(62.dp), stringResource(id = R.string.main_share_logs), Icons.AutoMirrored.Rounded.Send, "Send crash logs", onShareLogsClick)
                DurbinActionCard(Modifier.weight(1f).height(62.dp), stringResource(id = R.string.mcl_button_open_directory), Icons.Rounded.Folder, "Open game folder", onOpenFilesClick)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                DurbinActionCard(Modifier.weight(1f).height(58.dp), "YouTube", Icons.Rounded.PlayArrow, "@Cosa_5023_YT", onYoutubeClick)
                DurbinActionCard(Modifier.weight(1f).height(58.dp), "Install DURBIN", Icons.Rounded.AutoAwesome, "Fabric + Sodium + Iris + Lithium", {
                    Toast.makeText(context, "Installing DURBIN Fabric + mods...", Toast.LENGTH_LONG).show()
                    DurbinInstaller.installDurbin(context, selectedInstance?.versionId)
                }, accent = true)
            }
        }
    }
}


@Composable
private fun DurbinActionCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    subtitle: String,
    onClick: () -> Unit,
    accent: Boolean = false
) {
    val borderColor = if (accent) MaterialTheme.colorScheme.primary.copy(alpha = 0.38f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
    val iconBoxColor = if (accent) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.06f)

    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (accent) MaterialTheme.colorScheme.primary.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.035f),
            contentColor = Color.White
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
                    .size(38.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(iconBoxColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp), tint = if (accent) MaterialTheme.colorScheme.primary else Color.White)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(title, color = Color.White, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, color = Color(0xFFBDBDBD), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (accent) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.55f))
        }
    }
}


@Composable
private fun DurbinProfilePanel(
    modifier: Modifier,
    selectedInstance: Instance?,
    instanceIcon: Drawable?,
    currentAccount: MinecraftAccount?,
    skinHead: Bitmap?,
    headScale: Float,
    headInteractionSource: MutableInteractionSource,
    animatedTerminateRotation: Float,
    glowPulse: Float,
    onEditProfileClick: () -> Unit,
    onPlayClick: () -> Unit,
    onTerminateClick: () -> Unit,
    onInstanceSelect: () -> Unit
) {
    DurbinGlassSurface(modifier = modifier, corner = 24, borderAlpha = 0.18f) {
        Column(
            modifier = Modifier.padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Launch Center", color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp)
            Text("Owner: COSA", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)

            Box(
                modifier = Modifier
                    .size(70.dp)
                    .scale(headScale)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f))
                    .clickable(interactionSource = headInteractionSource, indication = null, onClick = {}),
                contentAlignment = Alignment.Center
            ) {
                if (skinHead != null) {
                    Image(bitmap = skinHead.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                } else {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(44.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }

            Text(currentAccount?.username ?: "Steve", color = Color.White, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)

            OutlinedButton(
                onClick = onEditProfileClick,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White.copy(alpha = 0.04f), contentColor = Color.White)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Edit Profile", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onInstanceSelect,
                modifier = Modifier.fillMaxWidth().height(70.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.30f)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.09f), contentColor = Color.White)
            ) {
                if (instanceIcon != null) {
                    Image(painter = rememberDrawablePainter(instanceIcon), contentDescription = null, modifier = Modifier.size(36.dp), contentScale = ContentScale.Fit)
                } else {
                    Icon(painter = painterResource(id = R.drawable.ic_px_home), contentDescription = null, modifier = Modifier.size(30.dp), tint = Color.Unspecified)
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    val name = selectedInstance?.name
                    Text(
                        text = when {
                            selectedInstance == null -> stringResource(id = R.string.no_instance)
                            name.isNullOrBlank() -> "UNNAMED"
                            else -> name
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = selectedInstance?.versionId ?: stringResource(id = R.string.version_select_hint),
                        color = Color(0xFFCFCFCF),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(17.dp), tint = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.weight(1f, fill = false))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onPlayClick,
                    modifier = Modifier.weight(1f).height(58.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(id = R.string.main_play).uppercase(), fontWeight = FontWeight.Black)
                }
                Button(
                    onClick = onTerminateClick,
                    modifier = Modifier.size(58.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = MaterialTheme.colorScheme.onError),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Rounded.Clear, contentDescription = "Terminate", modifier = Modifier.size(25.dp).rotate(animatedTerminateRotation))
                }
            }
        }
    }
}

@Composable
private fun DurbinStatusChip(text: String) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.28f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}



@Composable
private fun DurbinGlassSurface(
    modifier: Modifier = Modifier,
    corner: Int,
    borderAlpha: Float,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(corner.dp),
        color = Color(0xFF070707).copy(alpha = 0.94f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = borderAlpha.coerceIn(0f, 0.35f))),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        content = content
    )
}

@Composable
fun MainMenuRevampPreview() {
    PojavTheme(dynamicColor = true) {
        MainMenuRevamp(
            onEditProfileClick = {},
            onCustomControlsClick = {},
            onInstallJarClick = {},
            onShareLogsClick = {},
            onOpenFilesClick = {},
            onYoutubeClick = {},
            onSocialMediaClick = {},
            onPlayClick = {},
            onTerminateClick = {},
            onInstanceSelect = {}
        )
    }
}
