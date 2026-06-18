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
import net.kdt.pojavlaunch.durbin.firebase.DurbinFirebaseConfig
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
    // V9: fully transparent main background.
    Box(
        modifier = Modifier.fillMaxSize(),
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
    val mainScroll = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DurbinSideRail(
            modifier = Modifier
                .width(62.dp)
                .fillMaxHeight(),
            currentAccount = currentAccount,
            skinHead = skinHead,
            onYoutubeClick = onYoutubeClick,
            onSocialMediaClick = onSocialMediaClick,
            onOpenFilesClick = onOpenFilesClick,
            onCustomControlsClick = onCustomControlsClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(mainScroll),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            DurbinTopBar(
                onYoutubeClick = onYoutubeClick,
                onSocialMediaClick = onSocialMediaClick,
                compact = true,
                glowPulse = glowPulse
            )

            DurbinHeroLaunchBanner(
                selectedInstance = selectedInstance,
                onPlayClick = onPlayClick,
                onInstanceSelect = onInstanceSelect
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1.45f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DurbinLatestNewsGrid()
                    DurbinQuickToolsPanel(
                        selectedInstance = selectedInstance,
                        onCustomControlsClick = onCustomControlsClick,
                        onInstallJarClick = onInstallJarClick,
                        onShareLogsClick = onShareLogsClick,
                        onOpenFilesClick = onOpenFilesClick,
                        onSocialMediaClick = onSocialMediaClick
                    )
                }

                DurbinFriendsPanel(
                    modifier = Modifier.weight(0.62f),
                    currentAccount = currentAccount,
                    skinHead = skinHead,
                    selectedInstance = selectedInstance,
                    onEditProfileClick = onEditProfileClick,
                    onTerminateClick = onTerminateClick,
                    animatedTerminateRotation = animatedTerminateRotation
                )
            }

            Text("v13 Final Polish • DURBIN Launcher by COSA", color = Color.White.copy(alpha = 0.45f), fontWeight = FontWeight.Bold, fontSize = 10.sp)
            Spacer(Modifier.height(18.dp))
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
        onTerminateClick = onTerminateClick,
        onInstanceSelect = onInstanceSelect
    )
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
private fun DurbinSideRail(
    modifier: Modifier,
    currentAccount: MinecraftAccount?,
    skinHead: Bitmap?,
    onYoutubeClick: () -> Unit,
    onSocialMediaClick: () -> Unit,
    onOpenFilesClick: () -> Unit,
    onCustomControlsClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            if (skinHead != null) {
                Image(bitmap = skinHead.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
            } else {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
        }

        DurbinRailButton(Icons.Rounded.Dashboard, "Home") {}
        DurbinRailButton(Icons.Rounded.SportsEsports, "Controls", onCustomControlsClick)
        DurbinRailButton(Icons.Rounded.Article, "News", onYoutubeClick)
        DurbinRailButton(Icons.Rounded.EmojiEvents, "PvP", onSocialMediaClick)
        DurbinRailButton(Icons.Rounded.Folder, "Files", onOpenFilesClick)

        Spacer(Modifier.weight(1f))

        DurbinRailButton(Icons.Rounded.Settings, "Settings", onCustomControlsClick)
    }
}

@Composable
private fun DurbinRailButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.Transparent)
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)), RoundedCornerShape(14.dp))
    ) {
        Icon(icon, contentDescription = label, tint = Color.White.copy(alpha = 0.92f), modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun DurbinHeroLaunchBanner(
    selectedInstance: Instance?,
    onPlayClick: () -> Unit,
    onInstanceSelect: () -> Unit
) {
    val versionText = selectedInstance?.versionId ?: "Select Version"
    val nameText = selectedInstance?.name ?: ""
    val isDurbin = remember(versionText, nameText) {
        versionText.contains("durbin", ignoreCase = true) || nameText.contains("durbin", ignoreCase = true)
    }
    val bannerRes = if (isDurbin) R.drawable.durbin_banner else R.drawable.minecraft_banner
    val launchText = if (versionText.isBlank() || versionText == "latest_release") "LAUNCH MINECRAFT" else "LAUNCH $versionText"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(22.dp))
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)), RoundedCornerShape(22.dp))
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
                    Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.58f),
                            Color.Black.copy(alpha = 0.82f)
                        ),
                        center = Offset(850f, 170f),
                        radius = 900f
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .widthIn(min = 310.dp, max = 420.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onPlayClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.94f),
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = launchText.uppercase(),
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            OutlinedButton(
                onClick = onInstanceSelect,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Black.copy(alpha = 0.35f), contentColor = Color.White)
            ) {
                Text(
                    text = (selectedInstance?.name ?: "Choose instance") + " • Ready to launch",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.width(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(15.dp))
            }
        }

        Text(
            text = if (isDurbin) "DURBIN MODE" else "MINECRAFT MODE",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 15.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.Black.copy(alpha = 0.42f))
                .padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}

@Composable
private fun DurbinLatestNewsGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("LATEST NEWS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 21.sp)
            Spacer(Modifier.weight(1f))
            Text("Inline Firebase news", color = Color.White.copy(alpha = 0.65f), fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            DurbinNewsTile(
                modifier = Modifier.weight(1f),
                imageRes = R.drawable.durbin_banner,
                title = "DURBIN Updates",
                subtitle = "Launcher news and client updates"
            )
            DurbinNewsTile(
                modifier = Modifier.weight(1f),
                imageRes = R.drawable.minecraft_banner,
                title = "Minecraft Mode",
                subtitle = "Vanilla, Fabric and Forge"
            )
        }

        DurbinInlineNewsPanel()
    }
}

@Composable
private fun DurbinNewsTile(
    modifier: Modifier,
    imageRes: Int,
    title: String,
    subtitle: String
) {
    Box(
        modifier = modifier
            .height(146.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)), RoundedCornerShape(18.dp))
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.82f))
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(13.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 17.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(subtitle, color = Color.White.copy(alpha = 0.76f), fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun DurbinQuickToolsPanel(
    selectedInstance: Instance?,
    onCustomControlsClick: () -> Unit,
    onInstallJarClick: () -> Unit,
    onShareLogsClick: () -> Unit,
    onOpenFilesClick: () -> Unit,
    onSocialMediaClick: () -> Unit
) {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Text("QUICK TOOLS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            DurbinActionCard(Modifier.weight(1f).height(64.dp), "PvP Tier List", Icons.Rounded.EmojiEvents, "Tank • NethPot • Sword", {
                context.startActivity(DurbinFirebaseHubActivity.tierListIntent(context))
            }, accent = true)
            DurbinActionCard(Modifier.weight(1f).height(64.dp), "Install DURBIN", Icons.Rounded.AutoAwesome, "Fabric + Sodium + Iris", {
                Toast.makeText(context, "Installing DURBIN Fabric + mods...", Toast.LENGTH_LONG).show()
                DurbinInstaller.installDurbin(context, selectedInstance?.versionId)
            }, accent = true)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            DurbinActionCard(Modifier.weight(1f).height(58.dp), "Controls", Icons.Rounded.SportsEsports, "Edit mobile layout", onCustomControlsClick)
            DurbinActionCard(Modifier.weight(1f).height(58.dp), "Install Jar", Icons.Rounded.Terminal, "Run installer", onInstallJarClick)
            DurbinActionCard(Modifier.weight(1f).height(58.dp), "Files", Icons.Rounded.Folder, "Open folder", onOpenFilesClick)
        }

        DurbinActionCard(Modifier.fillMaxWidth().height(56.dp), "Discord", Icons.Rounded.Share, "Join the DURBIN community", onSocialMediaClick)
    }
}

@Composable
private fun DurbinFriendsPanel(
    modifier: Modifier,
    currentAccount: MinecraftAccount?,
    skinHead: Bitmap?,
    selectedInstance: Instance?,
    onEditProfileClick: () -> Unit,
    onTerminateClick: () -> Unit,
    animatedTerminateRotation: Float
) {
    DurbinGlassSurface(modifier = modifier, corner = 22, borderAlpha = 0.18f) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("FRIENDS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 19.sp)
            Text("DURBIN Profile", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 11.sp)

            Box(
                modifier = Modifier
                    .size(74.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                if (skinHead != null) {
                    Image(bitmap = skinHead.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                } else {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(42.dp), tint = Color.White)
                }
            }

            Text(currentAccount?.username ?: "Steve", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(selectedInstance?.versionId ?: "No version selected", color = Color.White.copy(alpha = 0.62f), fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)

            OutlinedButton(
                onClick = onEditProfileClick,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent, contentColor = Color.White)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Edit Profile", fontWeight = FontWeight.Black)
            }

            Button(
                onClick = onTerminateClick,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.88f), contentColor = MaterialTheme.colorScheme.onError)
            ) {
                Icon(Icons.Rounded.Clear, contentDescription = null, modifier = Modifier.size(20.dp).rotate(animatedTerminateRotation))
                Spacer(Modifier.width(8.dp))
                Text("Terminate", fontWeight = FontWeight.Black)
            }

            Divider(color = Color.White.copy(alpha = 0.10f))

            listOf(
                "Delta" to "Playing Minecraft",
                "Krag" to "Private Server",
                "Shaaf" to "Online in Launcher"
            ).forEach { friend ->
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    Box(modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(friend.first, color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp, maxLines = 1)
                        Text(friend.second, color = Color.White.copy(alpha = 0.55f), fontWeight = FontWeight.Bold, fontSize = 9.sp, maxLines = 1)
                    }
                }
            }
        }
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
    val title = if (isDurbin) "DURBIN Mode" else "Minecraft Mode"
    val subtitle = if (isDurbin) "Fabric + Sodium + Iris + Lithium" else "Vanilla • Fabric • Forge"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(108.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)),
                RoundedCornerShape(20.dp)
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
                            Color.Black.copy(alpha = 0.76f),
                            Color.Black.copy(alpha = 0.34f),
                            Color.Transparent
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = versionText,
                color = Color(0xFFE0E0E0),
                fontSize = 9.sp,
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                        text = "News, tools, PvP tier and DURBIN setup",
                        color = Color(0xFFCFCFCF),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            DurbinInlineNewsPanel()

            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                DurbinActionCard(Modifier.weight(1f).height(58.dp), "PvP Tier List", Icons.Rounded.EmojiEvents, "Tank • NethPot • Sword • Crystal", {
                    context.startActivity(DurbinFirebaseHubActivity.tierListIntent(context))
                }, accent = true)
                DurbinActionCard(Modifier.weight(1f).height(58.dp), "Install DURBIN", Icons.Rounded.AutoAwesome, "Fabric + Sodium + Iris + Lithium", {
                    Toast.makeText(context, "Installing DURBIN Fabric + mods...", Toast.LENGTH_LONG).show()
                    DurbinInstaller.installDurbin(context, selectedInstance?.versionId)
                }, accent = true)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                DurbinActionCard(Modifier.weight(1f).height(56.dp), "Wiki", Icons.Rounded.Book, "Open help page", {
                    context.startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://pojavlauncherteam.github.io/")))
                })
                DurbinActionCard(Modifier.weight(1f).height(56.dp), "Discord", Icons.Rounded.Share, "Join community", onSocialMediaClick)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                DurbinActionCard(Modifier.weight(1f).height(56.dp), stringResource(id = R.string.mcl_option_customcontrol), Icons.Rounded.SportsEsports, "Edit mobile buttons", onCustomControlsClick)
                DurbinActionCard(Modifier.weight(1f).height(56.dp), stringResource(id = R.string.main_install_jar_file), Icons.Rounded.Terminal, "Run or install .jar", onInstallJarClick)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                DurbinActionCard(Modifier.weight(1f).height(56.dp), stringResource(id = R.string.main_share_logs), Icons.AutoMirrored.Rounded.Send, "Send crash logs", onShareLogsClick)
                DurbinActionCard(Modifier.weight(1f).height(56.dp), stringResource(id = R.string.mcl_button_open_directory), Icons.Rounded.Folder, "Open game folder", onOpenFilesClick)
            }

            DurbinActionCard(Modifier.fillMaxWidth().height(52.dp), "YouTube", Icons.Rounded.PlayArrow, "@Cosa_5023_YT", onYoutubeClick)
        }
    }
}



@Composable
private fun DurbinInlineNewsPanel() {
    val context = LocalContext.current
    var title by remember { mutableStateOf("DURBIN News") }
    var body by remember { mutableStateOf("Loading latest launcher news...") }
    var tag by remember { mutableStateOf("News") }

    LaunchedEffect(Unit) {
        try {
            val ready = DurbinFirebaseConfig.ensureInitialized(context)
            if (!ready) {
                title = "DURBIN News"
                body = "Firebase is not ready yet. Rebuild with the included google-services.json."
                tag = "Offline"
            } else {
                FirebaseDatabase.getInstance(context.getString(R.string.durbin_firebase_database_url).trim())
                    .getReference("durbin/news")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val latest = snapshot.children.maxByOrNull {
                                it.child("timestamp").getValue(Long::class.java) ?: 0L
                            }
                            if (latest == null) {
                                title = "DURBIN News"
                                body = "No news added yet. Add your first news in Firebase."
                                tag = "Empty"
                            } else {
                                title = latest.child("title").getValue(String::class.java) ?: "DURBIN News"
                                body = latest.child("body").getValue(String::class.java) ?: "No description"
                                tag = latest.child("tag").getValue(String::class.java) ?: "News"
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            title = "DURBIN News"
                            body = error.message
                            tag = "Error"
                        }
                    })
            }
        } catch (t: Throwable) {
            title = "DURBIN News"
            body = t.message ?: "Could not load news"
            tag = "Offline"
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp),
        shape = RoundedCornerShape(18.dp),
        color = Color.Black.copy(alpha = 0.55f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Article, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(25.dp))
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(
                    text = tag.uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1
                )
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = body.ifBlank { "No description" },
                    color = Color.White.copy(alpha = 0.76f),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
            containerColor = Color.Transparent,
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
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Launch Center", color = Color.White, fontWeight = FontWeight.Black, fontSize = 21.sp)
            Text("Owner: COSA", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 11.sp)

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .scale(headScale)
                    .clip(RoundedCornerShape(17.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f))
                    .clickable(interactionSource = headInteractionSource, indication = null, onClick = {}),
                contentAlignment = Alignment.Center
            ) {
                if (skinHead != null) {
                    Image(bitmap = skinHead.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                } else {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }

            Text(currentAccount?.username ?: "Steve", color = Color.White, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)

            OutlinedButton(
                onClick = onEditProfileClick,
                modifier = Modifier.fillMaxWidth().height(40.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent, contentColor = Color.White)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(8.dp))
                Text("Edit Profile", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onInstanceSelect,
                modifier = Modifier.fillMaxWidth().height(62.dp),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.30f)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent, contentColor = Color.White)
            ) {
                if (instanceIcon != null) {
                    Image(painter = rememberDrawablePainter(instanceIcon), contentDescription = null, modifier = Modifier.size(32.dp), contentScale = ContentScale.Fit)
                } else {
                    Icon(painter = painterResource(id = R.drawable.ic_px_home), contentDescription = null, modifier = Modifier.size(28.dp), tint = Color.Unspecified)
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
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(17.dp), tint = MaterialTheme.colorScheme.primary)
            }

            // Banner belongs here: above the Play button, rounded and cropped to the 16:9 banner.
            DurbinVersionBanner(selectedInstance = selectedInstance)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onPlayClick,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(19.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(23.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(id = R.string.main_play).uppercase(), fontWeight = FontWeight.Black)
                }
                Button(
                    onClick = onTerminateClick,
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(19.dp),
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
        color = Color.Black.copy(alpha = 0.52f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = borderAlpha.coerceIn(0f, 0.36f))),
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
