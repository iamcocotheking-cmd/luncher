package net.kdt.pojavlaunch.kotlin.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
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

    // V28: static glow is much lighter on low-end phones than infinite background animation.
    val glowPulse = 0.20f

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
        // DURBIN V48: force the home screen to use the horizontal layout only.
        // The app manifest also locks launcher activities to sensorLandscape.
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
    val primary = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .background(
                Brush.radialGradient(
                    colors = listOf(primary.copy(alpha = 0.13f), Color.Transparent),
                    center = Offset(260f, 140f),
                    radius = 720f
                )
            )
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.04f), Color.Black.copy(alpha = 0.70f))
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
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        DurbinTopBar(onYoutubeClick = onYoutubeClick, onSocialMediaClick = onSocialMediaClick, glowPulse = glowPulse)
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.58f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                DurbinHeroCard(modifier = Modifier.fillMaxWidth().height(170.dp), compact = false, selectedInstance = selectedInstance)
                DurbinActionPanel(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    onCustomControlsClick = onCustomControlsClick,
                    onInstallJarClick = onInstallJarClick,
                    onShareLogsClick = onShareLogsClick,
                    onOpenFilesClick = onOpenFilesClick,
                    onYoutubeClick = onYoutubeClick,
                    onSocialMediaClick = onSocialMediaClick
                )
            }
            DurbinProfilePanel(
                modifier = Modifier
                    .weight(0.42f)
                    .fillMaxHeight(),
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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        DurbinTopBar(onYoutubeClick = onYoutubeClick, onSocialMediaClick = onSocialMediaClick, compact = true, glowPulse = glowPulse)
        DurbinHeroCard(modifier = Modifier.fillMaxWidth().height(190.dp), compact = true, selectedInstance = selectedInstance)
        DurbinProfilePanel(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 330.dp),
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
        modifier = Modifier.fillMaxWidth(),
        corner = 28,
        borderAlpha = 0.28f
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = null,
                modifier = Modifier
                    .size(if (compact) 44.dp else 54.dp)
                    .clip(RoundedCornerShape(15.dp)),
                contentScale = ContentScale.Fit
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "DURBIN Launcher",
                    style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (!compact) {
                SmallIconButton(icon = Icons.Rounded.PlayArrow, label = "YouTube", onClick = onYoutubeClick)
                SmallIconButton(icon = Icons.Rounded.Share, label = "Discord", onClick = onSocialMediaClick)
            }
        }
    }
}

@Composable
private fun DurbinHeroCard(modifier: Modifier, compact: Boolean, selectedInstance: Instance?) {
    val versionText = selectedInstance?.versionId.orEmpty()
    val nameText = selectedInstance?.name.orEmpty()
    val key = (nameText + " " + versionText).lowercase()

    val bannerRes = when {
        key.contains("durbin") -> R.drawable.durbin_forge_banner
        key.contains("forge") -> R.drawable.durbin_forge_banner
        key.contains("optifine") || key.contains("of") -> R.drawable.durbin_optifine_banner
        key.contains("fabric") -> R.drawable.durbin_minecraft_banner
        key.contains("vanilla") || key.contains("release") -> R.drawable.durbin_minecraft_banner
        else -> R.drawable.durbin_minecraft_banner
    }

    val modeLabel = when {
        key.contains("forge") -> "FORGE MODE"
        key.contains("optifine") || key.contains("of") -> "OPTIFINE MODE"
        key.contains("fabric") -> "DURBIN MODE"
        key.contains("durbin") -> "DURBIN MODE"
        else -> "DURBIN MODE"
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.34f)), RoundedCornerShape(30.dp))
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
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.12f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.58f)
                        )
                    )
                )
        )

        Text(
            text = modeLabel,
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = if (compact) 14.sp else 17.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(14.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.Black.copy(alpha = 0.38f))
                .padding(horizontal = 12.dp, vertical = 7.dp)
        )

        Text(
            text = (selectedInstance?.name ?: "Select a version"),
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = if (compact) 16.sp else 20.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
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
private fun DurbinActionPanel(
    modifier: Modifier,
    onCustomControlsClick: () -> Unit,
    onInstallJarClick: () -> Unit,
    onShareLogsClick: () -> Unit,
    onOpenFilesClick: () -> Unit,
    onYoutubeClick: () -> Unit,
    onSocialMediaClick: () -> Unit,
    singleColumn: Boolean = false
) {
    DurbinGlassSurface(modifier = modifier, corner = 30, borderAlpha = 0.26f) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Quick Actions",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                DurbinStatusChip("no wiki")
            }
            Text(
                text = "DURBIN links only. Old tools are hidden from the home screen.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )

            val context = LocalContext.current
            if (singleColumn) {
                DurbinActionCard(Modifier.fillMaxWidth().height(58.dp), "News", Icons.Rounded.Article, "Firebase launcher news", {
                    context.startActivity(DurbinFirebaseHubActivity.newsIntent(context))
                })
                DurbinActionCard(Modifier.fillMaxWidth().height(58.dp), "PvP Tier List", Icons.Rounded.EmojiEvents, "Tank, NethPot, Sword, Crystal", {
                    context.startActivity(DurbinFirebaseHubActivity.tierListIntent(context))
                })
                DurbinActionCard(Modifier.fillMaxWidth().height(58.dp), "Discord", Icons.Rounded.Share, "Join the DURBIN community", onSocialMediaClick)
                DurbinActionCard(Modifier.fillMaxWidth().height(58.dp), "YouTube", Icons.Rounded.PlayArrow, "Open @Cosa_5023_YT", onYoutubeClick)
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DurbinActionCard(Modifier.weight(1f).height(62.dp), "News", Icons.Rounded.Article, "Firebase updates", {
                        context.startActivity(DurbinFirebaseHubActivity.newsIntent(context))
                    })
                    DurbinActionCard(Modifier.weight(1f).height(62.dp), "PvP Tier List", Icons.Rounded.EmojiEvents, "HT1/LT1 ranks", {
                        context.startActivity(DurbinFirebaseHubActivity.tierListIntent(context))
                    })
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DurbinActionCard(Modifier.weight(1f).height(62.dp), "Discord", Icons.Rounded.Share, "Community", onSocialMediaClick)
                    DurbinActionCard(Modifier.weight(1f).height(62.dp), "YouTube", Icons.Rounded.PlayArrow, "COSA channel", onYoutubeClick)
                }
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
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(19.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.30f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.30f),
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
                    .size(38.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(title, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
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
    DurbinGlassSurface(modifier = modifier, corner = 32, borderAlpha = 0.36f + glowPulse * 0.20f) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Text("Launch Center", fontWeight = FontWeight.Black, fontSize = 25.sp)
            Text("Owner: COSA", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .scale(headScale)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
                    .clickable(interactionSource = headInteractionSource, indication = null, onClick = {}),
                contentAlignment = Alignment.Center
            ) {
                if (skinHead != null) {
                    Image(bitmap = skinHead.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                } else {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(54.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }
            Text(currentAccount?.username ?: "Steve", fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                DurbinStatusChip("Minecraft Java")
                DurbinStatusChip("Android")
            }

            OutlinedButton(
                onClick = onInstanceSelect,
                modifier = Modifier.fillMaxWidth().height(68.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
            ) {
                if (instanceIcon != null) {
                    Image(painter = rememberDrawablePainter(instanceIcon), contentDescription = null, modifier = Modifier.size(36.dp), contentScale = ContentScale.Fit)
                } else {
                    Icon(painter = painterResource(id = R.drawable.ic_px_home), contentDescription = null, modifier = Modifier.size(28.dp), tint = Color.Unspecified)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    val name = selectedInstance?.name
                    Text(
                        text = when {
                            selectedInstance == null -> stringResource(id = R.string.no_instance)
                            name.isNullOrBlank() -> "UNNAMED"
                            else -> name
                        },
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = selectedInstance?.versionId ?: stringResource(id = R.string.version_select_hint),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.weight(1f, fill = false))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onPlayClick,
                    modifier = Modifier.weight(1f).height(58.dp),
                    shape = RoundedCornerShape(21.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(25.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(stringResource(id = R.string.main_play).uppercase(), fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                }
                Button(
                    onClick = onTerminateClick,
                    modifier = Modifier.size(58.dp),
                    shape = RoundedCornerShape(21.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = MaterialTheme.colorScheme.onError),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Rounded.Clear, contentDescription = "Terminate", modifier = Modifier.size(27.dp).rotate(animatedTerminateRotation))
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
            fontSize = 10.sp,
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
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = borderAlpha.coerceIn(0f, 0.75f))),
        tonalElevation = 6.dp,
        shadowElevation = 3.dp,
        content = content
    )
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,orientation=landscape")
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
