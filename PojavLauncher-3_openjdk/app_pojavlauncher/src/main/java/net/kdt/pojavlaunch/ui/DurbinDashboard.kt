package net.kdt.pojavlaunch.ui

import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.SoundEffectConstants
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.ui.theme.DurbinAccentOrange
import net.kdt.pojavlaunch.ui.theme.DurbinBackground
import net.kdt.pojavlaunch.ui.theme.DurbinBorderColor
import net.kdt.pojavlaunch.ui.theme.DurbinCardActiveBg
import net.kdt.pojavlaunch.ui.theme.DurbinCardBg
import net.kdt.pojavlaunch.ui.theme.DurbinLaunchGreen
import net.kdt.pojavlaunch.ui.theme.DurbinLaunchGreenDark
import net.kdt.pojavlaunch.ui.theme.DurbinMutedText
import net.kdt.pojavlaunch.ui.theme.DurbinOrangeGlow
import net.kdt.pojavlaunch.ui.theme.DurbinPrimaryText
import net.kdt.pojavlaunch.ui.theme.DurbinSecondaryText
import net.kdt.pojavlaunch.ui.theme.DurbinStrongBorderColor
import net.kdt.pojavlaunch.ui.theme.DurbinTheme

private enum class DurbinDialog {
    NONE, VERSIONS, LAUNCH_MODE, COMING_SOON
}

private enum class DurbinSound {
    CLICK, LAUNCH, POPUP
}

private data class LaunchModeOption(
    val title: String,
    val subtitle: String,
    val enabled: Boolean,
    val onClick: () -> Unit
)


/**
 * Java-friendly entry point used by MainMenuFragment.
 * Java cannot safely call a @Composable function directly inside ComposeView.setContent(),
 * so this normal Kotlin function owns setContent and exposes a simple method to Java.
 */
fun setDurbinDashboardContent(composeView: ComposeView, callbacks: DurbinMenuCallbacks) {
    composeView.setContent {
        DurbinDashboardHost(callbacks)
    }
}

@Composable
fun DurbinDashboardHost(callbacks: DurbinMenuCallbacks) {
    DurbinTheme {
        DurbinDashboard(callbacks)
    }
}

@Composable
private fun DurbinDashboard(callbacks: DurbinMenuCallbacks) {
    var activeDialog by remember { mutableStateOf(DurbinDialog.NONE) }
    var contentVisible by remember { mutableStateOf(false) }
    val entryProgress by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 520),
        label = "dashboardEntry"
    )
    val isLandscape = LocalConfiguration.current.screenWidthDp > LocalConfiguration.current.screenHeightDp

    LaunchedEffect(Unit) {
        contentVisible = true
    }

    Box(modifier = Modifier.fillMaxSize().background(DurbinBackground)) {
        DurbinCosmicBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .graphicsLayer {
                    alpha = entryProgress
                    translationY = (1f - entryProgress) * 42f
                }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            DurbinTopBar(
                onSettings = callbacks.onOpenSettings,
                onAccounts = callbacks.onOpenAccounts
            )

            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        DurbinHeroCard(callbacks) { activeDialog = DurbinDialog.LAUNCH_MODE }
                        DurbinLaunchButton(callbacks.onLaunch)
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        DurbinAccountCard(callbacks)
                        DurbinQuickActions(callbacks) { activeDialog = DurbinDialog.LAUNCH_MODE }
                        DurbinFooter()
                    }
                }
            } else {
                DurbinHeroCard(callbacks) { activeDialog = DurbinDialog.LAUNCH_MODE }
                DurbinLaunchButton(callbacks.onLaunch)
                DurbinAccountCard(callbacks)
                DurbinQuickActions(callbacks) { activeDialog = DurbinDialog.LAUNCH_MODE }
                DurbinFooter()
            }
        }

        when (activeDialog) {
            DurbinDialog.VERSIONS -> DurbinBottomSheet(onDismiss = { activeDialog = DurbinDialog.NONE }) {
                DurbinVersionsSheet(
                    callbacks = callbacks,
                    onClose = { activeDialog = DurbinDialog.NONE }
                )
            }
            DurbinDialog.LAUNCH_MODE -> DurbinBottomSheet(onDismiss = { activeDialog = DurbinDialog.NONE }) {
                DurbinLaunchModeSheet(
                    callbacks = callbacks,
                    onComingSoon = { activeDialog = DurbinDialog.COMING_SOON },
                    onClose = { activeDialog = DurbinDialog.NONE }
                )
            }
            DurbinDialog.COMING_SOON -> DurbinBottomSheet(onDismiss = { activeDialog = DurbinDialog.NONE }) {
                DurbinComingSoonSheet(onClose = { activeDialog = DurbinDialog.NONE })
            }
            DurbinDialog.NONE -> Unit
        }
    }
}

@Composable
private fun DurbinCosmicBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        DurbinVideoBackground()
        DurbinAnimatedBackgroundOverlay()
    }
}

@Composable
private fun DurbinVideoBackground() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val videoUri = remember(context) {
        Uri.parse("android.resource://${context.packageName}/${R.raw.durbin_background}")
    }
    var videoView by remember { mutableStateOf<VideoView?>(null) }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            VideoView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setVideoURI(videoUri)
                setOnPreparedListener { player ->
                    player.isLooping = true
                    player.setVolume(0f, 0f)
                    try {
                        player.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                    } catch (_: Throwable) {
                        // Some devices ignore this; the dark overlay still keeps the UI clean.
                    }
                    start()
                }
                setOnErrorListener { _, _, _ -> true }
                videoView = this
            }
        },
        update = { view ->
            if (!view.isPlaying) {
                try {
                    view.start()
                } catch (_: Throwable) {
                    // Keep launcher usable even if the video decoder fails.
                }
            }
        }
    )

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    try {
                        if (videoView?.isPlaying != true) videoView?.start()
                    } catch (_: Throwable) {
                        // Keep launcher usable if video playback is unavailable.
                    }
                }
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                    try {
                        if (videoView?.isPlaying == true) videoView?.pause()
                    } catch (_: Throwable) {
                        // No-op.
                    }
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            try {
                videoView?.stopPlayback()
            } catch (_: Throwable) {
                // No-op.
            }
        }
    }
}

@Composable
private fun DurbinAnimatedBackgroundOverlay() {
    val transition = rememberInfiniteTransition(label = "cosmic")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(Color(0xB8050505))
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x661B072C),
                    Color.Transparent
                ),
                center = Offset(size.width * (0.3f + phase * 0.4f), size.height * 0.2f),
                radius = size.maxDimension * 0.9f
            )
        )
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x552E1502),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.85f, size.height * 0.75f),
                radius = size.maxDimension * 0.55f
            )
        )
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0x88000000),
                    Color.Transparent,
                    Color(0xCC000000)
                )
            )
        )
    }
}

@Composable
private fun DurbinTopBar(onSettings: () -> Unit, onAccounts: () -> Unit) {
    val view = LocalView.current
    val transition = rememberInfiniteTransition(label = "logoPulse")
    val logoPulse by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoPulseScale"
    )

    GlassCard(modifier = Modifier.fillMaxWidth(), glowColor = DurbinOrangeGlow) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = null,
                tint = DurbinMutedText,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.durbin_logo),
                    contentDescription = "DURBIN",
                    modifier = Modifier
                        .size(36.dp)
                        .graphicsLayer {
                            scaleX = logoPulse
                            scaleY = logoPulse
                        }
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "DURBIN",
                    color = DurbinPrimaryText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    letterSpacing = 2.4.sp
                )
            }
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = {
                    playDurbinSound(view, DurbinSound.CLICK)
                    onSettings()
                },
                modifier = Modifier.size(38.dp)
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = DurbinSecondaryText)
            }
            IconButton(
                onClick = {
                    playDurbinSound(view, DurbinSound.CLICK)
                    onAccounts()
                },
                modifier = Modifier.size(38.dp)
            ) {
                Icon(Icons.Default.AccountCircle, contentDescription = "Accounts", tint = DurbinAccentOrange)
            }
        }
    }
}

@Composable
private fun DurbinHeroCard(callbacks: DurbinMenuCallbacks, onOpenLaunchMode: () -> Unit) {
    GlassCard(
        modifier = Modifier.fillMaxWidth().durbinClickable(DurbinSound.POPUP) { onOpenLaunchMode() },
        glowColor = DurbinOrangeGlow,
        borderColor = DurbinStrongBorderColor
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = DurbinAccentOrange.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "SELECTED",
                        color = DurbinAccentOrange,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = callbacks.getLoaderLabel(),
                    color = DurbinMutedText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text("CLIENT VERSION", color = DurbinMutedText, fontSize = 11.sp, letterSpacing = 1.sp)
            Text(
                text = callbacks.getVersionId(),
                color = DurbinAccentOrange,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = callbacks.getProfileName(),
                color = DurbinSecondaryText,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DurbinStatTile("RUNTIME", callbacks.getRuntime(), Modifier.weight(1f))
                DurbinStatTile("RAM", callbacks.getRamAllocation(), Modifier.weight(1f))
                DurbinStatTile("RENDERER", callbacks.getRenderer(), Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth().durbinClickable(DurbinSound.POPUP) { callbacks.onOpenVersions() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Menu, contentDescription = null, tint = DurbinAccentOrange, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Open saved profiles", color = DurbinSecondaryText, fontSize = 12.sp)
                Spacer(Modifier.weight(1f))
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = DurbinAccentOrange
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().durbinClickable(DurbinSound.POPUP) { onOpenLaunchMode() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Extension, contentDescription = null, tint = DurbinAccentOrange, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Create / change launch mode", color = DurbinSecondaryText, fontSize = 12.sp)
                Spacer(Modifier.weight(1f))
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = DurbinAccentOrange
                )
            }
        }
    }
}

@Composable
private fun DurbinStatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = DurbinCardActiveBg,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DurbinBorderColor)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(label, color = DurbinMutedText, fontSize = 9.sp, letterSpacing = 0.8.sp)
            Text(
                value,
                color = DurbinPrimaryText,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DurbinLaunchButton(onLaunch: () -> Unit) {
    val transition = rememberInfiniteTransition(label = "launchPulse")
    val iconPulse by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "launchIconPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(listOf(DurbinLaunchGreenDark, DurbinLaunchGreen, DurbinLaunchGreenDark))
            )
            .glowOverlay(DurbinLaunchGreen.copy(alpha = 0.25f))
            .durbinClickable(DurbinSound.LAUNCH, onClick = onLaunch),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.10f),
                radius = size.minDimension * iconPulse,
                center = Offset(size.width * 0.5f, size.height * 0.5f)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(28.dp).graphicsLayer {
                    scaleX = iconPulse
                    scaleY = iconPulse
                }
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "LAUNCH MINECRAFT",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun DurbinAccountCard(callbacks: DurbinMenuCallbacks) {
    GlassCard(modifier = Modifier.fillMaxWidth().durbinClickable { callbacks.onOpenAccounts() }) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DurbinCardActiveBg)
                    .border(1.dp, DurbinAccentOrange.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = DurbinAccentOrange, modifier = Modifier.size(30.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("ACCOUNT", color = DurbinMutedText, fontSize = 10.sp)
                Text(
                    callbacks.getAccountName(),
                    color = DurbinPrimaryText,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Surface(
                color = if (callbacks.isOfflineAccount()) DurbinAccentOrange.copy(alpha = 0.15f) else Color(0xFF1BD964).copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (callbacks.isOfflineAccount()) "OFFLINE" else "ONLINE",
                    color = if (callbacks.isOfflineAccount()) DurbinAccentOrange else DurbinLaunchGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun DurbinQuickActions(callbacks: DurbinMenuCallbacks, onVersions: () -> Unit) {
    val actions = listOf(
        Triple("Saved Profiles", Icons.Default.Menu, callbacks.onOpenVersions),
        Triple("New Profile", Icons.Outlined.Extension, onVersions),
        Triple("Controls", Icons.Default.Gamepad, callbacks.onOpenControls),
        Triple("Directory", Icons.Default.Folder, callbacks.onOpenDirectory),
        Triple("Logs", Icons.Default.Share, callbacks.onShareLogs),
        Triple("Install JAR", Icons.Default.Storage, callbacks.onInstallJar),
        Triple("Edit Profile", Icons.AutoMirrored.Filled.KeyboardArrowRight, callbacks.onEditProfile)
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("QUICK ACTIONS", color = DurbinMutedText, fontSize = 11.sp, letterSpacing = 1.sp)
        actions.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { (label, icon, action) ->
                    QuickActionCard(label, icon, action, Modifier.weight(1f))
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun QuickActionCard(label: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier.durbinClickable(onClick = onClick)) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = DurbinAccentOrange, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(label, color = DurbinPrimaryText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun DurbinFooter() {
    Text(
        text = "DURBIN LAUNCHER • MOBILE",
        color = DurbinMutedText,
        fontSize = 10.sp,
        letterSpacing = 1.5.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    )
}

@Composable
private fun DurbinVersionsSheet(callbacks: DurbinMenuCallbacks, onClose: () -> Unit) {
    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Profiles & Versions", color = DurbinPrimaryText, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Open your saved profiles, edit the current one, or create a new profile.", color = DurbinSecondaryText, fontSize = 13.sp)

        GlassCard(modifier = Modifier.fillMaxWidth().durbinClickable(DurbinSound.POPUP) {
            callbacks.onOpenVersions()
            onClose()
        }) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Menu, contentDescription = null, tint = DurbinAccentOrange, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Open saved profiles", color = DurbinPrimaryText, fontWeight = FontWeight.SemiBold)
                    Text("Switch to any profile you already saved", color = DurbinMutedText, fontSize = 12.sp)
                }
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = DurbinAccentOrange)
            }
        }

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Current profile", color = DurbinMutedText, fontSize = 11.sp)
                    Text(callbacks.getProfileName(), color = DurbinPrimaryText, fontWeight = FontWeight.SemiBold)
                    Text(callbacks.getVersionId(), color = DurbinAccentOrange, fontSize = 13.sp)
                }
            }
        }

        GlassCard(modifier = Modifier.fillMaxWidth().durbinClickable {
            callbacks.onEditProfile()
            onClose()
        }) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = DurbinAccentOrange, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Edit current profile", color = DurbinPrimaryText, fontWeight = FontWeight.SemiBold)
                    Text("Change profile settings", color = DurbinMutedText, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun DurbinLaunchModeSheet(
    callbacks: DurbinMenuCallbacks,
    onComingSoon: () -> Unit,
    onClose: () -> Unit
) {
    val modes = listOf(
        LaunchModeOption("Vanilla", "Standard Minecraft Java Edition", true) {
            callbacks.onSelectVanilla()
            onClose()
        },
        LaunchModeOption("Fabric", "Lightweight mod loader", true) {
            callbacks.onSelectFabric()
            onClose()
        },
        LaunchModeOption("Forge", "Classic mod loader", true) {
            callbacks.onSelectForge()
            onClose()
        },
        LaunchModeOption("DURBIN Client", "Coming Soon", false) {
            onComingSoon()
        }
    )

    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Launch Mode", color = DurbinPrimaryText, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Choose how you want to play Minecraft.", color = DurbinSecondaryText, fontSize = 13.sp)
        modes.forEach { mode ->
            LaunchModeRow(mode)
        }
    }
}

@Composable
private fun LaunchModeRow(mode: LaunchModeOption) {
    val bg = if (mode.enabled) DurbinCardBg else DurbinCardBg.copy(alpha = 0.5f)
    val border = if (mode.enabled) DurbinBorderColor else DurbinBorderColor.copy(alpha = 0.5f)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (mode.enabled) Modifier.durbinClickable { mode.onClick() }
                else Modifier.durbinClickable(DurbinSound.POPUP) { mode.onClick() }
            ),
        color = bg,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, border)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = if (mode.enabled) mode.title else "${mode.title} — Coming Soon",
                    color = if (mode.enabled) DurbinPrimaryText else DurbinSecondaryText,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Text(mode.subtitle, color = DurbinMutedText, fontSize = 12.sp)
            }
            if (!mode.enabled) {
                Surface(color = DurbinAccentOrange.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
                    Text(
                        "LOCKED",
                        color = DurbinAccentOrange,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DurbinComingSoonSheet(onClose: () -> Unit) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("DURBIN Client", color = DurbinAccentOrange, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text(
            "The DURBIN Client mod system is not ready yet.\nVanilla, Fabric, and Forge are available now.",
            color = DurbinSecondaryText,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            color = DurbinAccentOrange,
            trackColor = DurbinBorderColor
        )
        Text("Coming Soon", color = DurbinMutedText, fontSize = 12.sp, letterSpacing = 2.sp)
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DurbinAccentOrange)
                .durbinClickable(onClick = onClose)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Got it", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DurbinBottomSheet(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    val sheetProgress by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 260),
        label = "durbinSheetProgress"
    )

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = sheetProgress }
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = sheetProgress
                    translationY = (1f - sheetProgress) * 180f
                    scaleX = 0.98f + (sheetProgress * 0.02f)
                    scaleY = 0.98f + (sheetProgress * 0.02f)
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                ),
            color = DurbinCardBg,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DurbinStrongBorderColor)
        ) {
            content()
        }
    }
}

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    glowColor: Color = DurbinOrangeGlow,
    borderColor: Color = DurbinBorderColor,
    content: @Composable () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "cardGlow")
    val glowPulse by transition.animateFloat(
        initialValue = 0.72f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cardGlowPulse"
    )

    Surface(
        modifier = modifier.glowOverlay(glowColor.copy(alpha = glowColor.alpha * glowPulse)),
        color = DurbinCardBg.copy(alpha = 0.86f),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        content()
    }
}

@Composable
private fun Modifier.durbinClickable(
    sound: DurbinSound = DurbinSound.CLICK,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.965f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "durbinPress"
    )

    return this
        .graphicsLayer {
            scaleX = pressScale
            scaleY = pressScale
        }
        .clickable(
            enabled = enabled,
            interactionSource = interactionSource,
            indication = null
        ) {
            playDurbinSound(view, sound)
            onClick()
        }
}

private fun playDurbinSound(view: View, sound: DurbinSound) {
    try {
        view.playSoundEffect(SoundEffectConstants.CLICK)
    } catch (_: Throwable) {
        // Sound effects can be disabled by the system.
    }

    if (sound == DurbinSound.LAUNCH || sound == DurbinSound.POPUP) {
        val tone = ToneGenerator(AudioManager.STREAM_MUSIC, if (sound == DurbinSound.LAUNCH) 36 else 22)
        val toneType = if (sound == DurbinSound.LAUNCH) ToneGenerator.TONE_PROP_BEEP else ToneGenerator.TONE_PROP_ACK
        val duration = if (sound == DurbinSound.LAUNCH) 130 else 70
        try {
            tone.startTone(toneType, duration)
        } catch (_: Throwable) {
            tone.release()
            return
        }
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                tone.release()
            } catch (_: Throwable) {
                // No-op.
            }
        }, (duration + 60).toLong())
    }
}

private fun Modifier.glowOverlay(glowColor: Color): Modifier = drawBehind {
    val stroke = 2.dp.toPx()
    for (i in 1..3) {
        drawRoundRect(
            color = glowColor.copy(alpha = glowColor.alpha / i),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx()),
            style = Stroke(width = stroke * i)
        )
    }
}
