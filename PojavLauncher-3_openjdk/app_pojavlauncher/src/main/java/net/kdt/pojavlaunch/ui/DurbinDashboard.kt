package net.kdt.pojavlaunch.ui

import android.content.Context
import android.content.Intent
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
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
    NONE, VERSIONS, LAUNCH_MODE, COMING_SOON, THEME
}

private enum class DurbinSound {
    CLICK, LAUNCH, POPUP, LINK
}

private enum class DurbinUiTheme {
    ORANGE, RED_WHITE, BLACK, BLUE, PURPLE, CYAN, MINECRAFT, GOLD
}

private data class DurbinPalette(
    val name: String,
    val accent: Color,
    val launch: Color,
    val launchDark: Color,
    val card: Color,
    val cardActive: Color,
    val border: Color,
    val strongBorder: Color,
    val glow: Color
)

private val OrangeDurbinPalette = DurbinPalette(
    name = "Orange",
    accent = Color(0xFFFF7A00),
    launch = Color(0xFF1BD964),
    launchDark = Color(0xFF138A3F),
    card = Color(0x66101010),
    cardActive = Color(0x66161616),
    border = Color(0x24FFFFFF),
    strongBorder = Color(0x33FFFFFF),
    glow = Color(0x00FF7A00)
)

private val RedWhiteDurbinPalette = DurbinPalette(
    name = "Red / White",
    accent = Color(0xFFFF2E2E),
    launch = Color(0xFF1BD964),
    launchDark = Color(0xFF0D8F3C),
    card = Color(0x55FFFFFF),
    cardActive = Color(0x33FFFFFF),
    border = Color(0x30FFFFFF),
    strongBorder = Color(0x42FFFFFF),
    glow = Color(0x00FF2E2E)
)

private val BlackDurbinPalette = DurbinPalette(
    name = "Black",
    accent = Color(0xFFFFFFFF),
    launch = Color(0xFF1BD964),
    launchDark = Color(0xFF0D8F3C),
    card = Color(0x88111111),
    cardActive = Color(0x99222222),
    border = Color(0x24FFFFFF),
    strongBorder = Color(0x38FFFFFF),
    glow = Color(0x00000000)
)


private val BlueDurbinPalette = DurbinPalette(
    name = "Blue",
    accent = Color(0xFF31A8FF),
    launch = Color(0xFF16D95C),
    launchDark = Color(0xFF07963A),
    card = Color(0x66101824),
    cardActive = Color(0x66203246),
    border = Color(0x2831A8FF),
    strongBorder = Color(0x4431A8FF),
    glow = Color(0x0031A8FF)
)

private val PurpleDurbinPalette = DurbinPalette(
    name = "Purple",
    accent = Color(0xFFB66BFF),
    launch = Color(0xFF16D95C),
    launchDark = Color(0xFF07963A),
    card = Color(0x66150D24),
    cardActive = Color(0x66281642),
    border = Color(0x2DB66BFF),
    strongBorder = Color(0x4DB66BFF),
    glow = Color(0x00B66BFF)
)

private val CyanDurbinPalette = DurbinPalette(
    name = "Cyan",
    accent = Color(0xFF00E5FF),
    launch = Color(0xFF16D95C),
    launchDark = Color(0xFF07963A),
    card = Color(0x66101618),
    cardActive = Color(0x66203438),
    border = Color(0x2600E5FF),
    strongBorder = Color(0x4400E5FF),
    glow = Color(0x0000E5FF)
)

private val MinecraftDurbinPalette = DurbinPalette(
    name = "Minecraft",
    accent = Color(0xFF65D84A),
    launch = Color(0xFF19C95B),
    launchDark = Color(0xFF087E38),
    card = Color(0x66101810),
    cardActive = Color(0x66203620),
    border = Color(0x2E65D84A),
    strongBorder = Color(0x4865D84A),
    glow = Color(0x0065D84A)
)

private val GoldDurbinPalette = DurbinPalette(
    name = "Gold",
    accent = Color(0xFFFFC857),
    launch = Color(0xFF16D95C),
    launchDark = Color(0xFF07963A),
    card = Color(0x661B1608),
    cardActive = Color(0x66342612),
    border = Color(0x2EFFC857),
    strongBorder = Color(0x4AFFC857),
    glow = Color(0x00FFC857)
)

private val LocalDurbinPalette = staticCompositionLocalOf { OrangeDurbinPalette }

private fun paletteFor(theme: DurbinUiTheme): DurbinPalette = when (theme) {
    DurbinUiTheme.ORANGE -> OrangeDurbinPalette
    DurbinUiTheme.RED_WHITE -> RedWhiteDurbinPalette
    DurbinUiTheme.BLACK -> BlackDurbinPalette
    DurbinUiTheme.BLUE -> BlueDurbinPalette
    DurbinUiTheme.PURPLE -> PurpleDurbinPalette
    DurbinUiTheme.CYAN -> CyanDurbinPalette
    DurbinUiTheme.MINECRAFT -> MinecraftDurbinPalette
    DurbinUiTheme.GOLD -> GoldDurbinPalette
}

private fun loadDurbinUiTheme(context: Context): DurbinUiTheme {
    val stored = context.getSharedPreferences("durbin_ui", Context.MODE_PRIVATE)
        .getString("theme", DurbinUiTheme.ORANGE.name)
    return try {
        DurbinUiTheme.valueOf(stored ?: DurbinUiTheme.ORANGE.name)
    } catch (_: Throwable) {
        DurbinUiTheme.ORANGE
    }
}

private fun saveDurbinUiTheme(context: Context, theme: DurbinUiTheme) {
    context.getSharedPreferences("durbin_ui", Context.MODE_PRIVATE)
        .edit()
        .putString("theme", theme.name)
        .apply()
}


private data class LaunchModeOption(
    val title: String,
    val subtitle: String,
    val enabled: Boolean,
    val onClick: () -> Unit
)

private data class DurbinQuickAction(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val sound: DurbinSound = DurbinSound.CLICK,
    val visible: Boolean = true
)

private const val DurbinYoutubeUrl = "https://www.youtube.com/@Cosa_5023_YT"
private const val DurbinDiscordUrl = "https://discord.gg/PqnbXNrtHR"


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
    val context = LocalContext.current
    var selectedTheme by remember { mutableStateOf(loadDurbinUiTheme(context)) }

    DurbinTheme {
        CompositionLocalProvider(LocalDurbinPalette provides paletteFor(selectedTheme)) {
            DurbinDashboard(
                callbacks = callbacks,
                selectedTheme = selectedTheme,
                onThemeSelected = { theme ->
                    selectedTheme = theme
                    saveDurbinUiTheme(context, theme)
                }
            )
        }
    }
}

@Composable
private fun DurbinDashboard(
    callbacks: DurbinMenuCallbacks,
    selectedTheme: DurbinUiTheme,
    onThemeSelected: (DurbinUiTheme) -> Unit
) {
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
                        DurbinQuickActions(
                            callbacks = callbacks,
                            onVersions = { activeDialog = DurbinDialog.LAUNCH_MODE },
                            onThemePicker = { activeDialog = DurbinDialog.THEME }
                        )
                        DurbinCommunityCard()
                        DurbinFooter()
                    }
                }
            } else {
                DurbinHeroCard(callbacks) { activeDialog = DurbinDialog.LAUNCH_MODE }
                DurbinLaunchButton(callbacks.onLaunch)
                DurbinAccountCard(callbacks)
                DurbinQuickActions(
                            callbacks = callbacks,
                            onVersions = { activeDialog = DurbinDialog.LAUNCH_MODE },
                            onThemePicker = { activeDialog = DurbinDialog.THEME }
                        )
                DurbinCommunityCard()
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
            DurbinDialog.THEME -> DurbinBottomSheet(onDismiss = { activeDialog = DurbinDialog.NONE }) {
                DurbinThemePickerSheet(
                    selectedTheme = selectedTheme,
                    onThemeSelected = { theme ->
                        onThemeSelected(theme)
                        activeDialog = DurbinDialog.NONE
                    }
                )
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
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = 1.7f
                scaleY = 1.7f
            },
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
    val palette = LocalDurbinPalette.current

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
                    Color(0x8E000000),
                    Color(0x33000000),
                    Color(0xD8000000)
                )
            )
        )

        // Lightweight animated particles. This is drawn in Compose, so no image assets
        // or heavy animation engine is needed.
        val particleCount = 22
        for (index in 0 until particleCount) {
            val base = index / particleCount.toFloat()
            val x = ((base * 1.73f + phase) % 1f) * size.width
            val y = ((base * 0.91f + phase * 0.28f) % 1f) * size.height
            val alpha = 0.10f + (index % 4) * 0.025f
            drawCircle(
                color = if (index % 3 == 0) palette.accent.copy(alpha = alpha) else Color.White.copy(alpha = alpha),
                radius = 1.2f + (index % 3),
                center = Offset(x, y)
            )
        }

        // Very soft top shine so the transparent cards feel more premium.
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(Color.White.copy(alpha = 0.055f), Color.Transparent),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height * 0.55f)
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

    GlassCard(modifier = Modifier.fillMaxWidth(), glowColor = LocalDurbinPalette.current.glow) {
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
                Icon(Icons.Default.AccountCircle, contentDescription = "Accounts", tint = LocalDurbinPalette.current.accent)
            }
        }
    }
}

@Composable
private fun DurbinHeroCard(callbacks: DurbinMenuCallbacks, onOpenLaunchMode: () -> Unit) {
    GlassCard(
        modifier = Modifier.fillMaxWidth().durbinClickable(DurbinSound.POPUP) { onOpenLaunchMode() },
        glowColor = LocalDurbinPalette.current.glow,
        borderColor = LocalDurbinPalette.current.strongBorder
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = LocalDurbinPalette.current.accent.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "SELECTED",
                        color = LocalDurbinPalette.current.accent,
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
                color = LocalDurbinPalette.current.accent,
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
                Icon(Icons.Default.Menu, contentDescription = null, tint = LocalDurbinPalette.current.accent, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Open saved profiles", color = DurbinSecondaryText, fontSize = 12.sp)
                Spacer(Modifier.weight(1f))
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = LocalDurbinPalette.current.accent
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().durbinClickable(DurbinSound.POPUP) { onOpenLaunchMode() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Extension, contentDescription = null, tint = LocalDurbinPalette.current.accent, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Create / change launch mode", color = DurbinSecondaryText, fontSize = 12.sp)
                Spacer(Modifier.weight(1f))
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = LocalDurbinPalette.current.accent
                )
            }
        }
    }
}

@Composable
private fun DurbinStatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = LocalDurbinPalette.current.cardActive.copy(alpha = 0.42f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.10f))
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
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressProgress by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = tween(durationMillis = 380),
        label = "launchBlobProgress"
    )
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.988f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "launchBlobScale"
    )
    val rainbowMotion by rememberInfiniteTransition(label = "launchRainbow").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "launchRainbowShift"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .clickable(
                enabled = true,
                interactionSource = interactionSource,
                indication = null
            ) {
                try {
                    view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                } catch (_: Throwable) { }
                playDurbinSound(view, DurbinSound.LAUNCH)
                onLaunch()
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.height / 2f
            val borderWidth = 3.dp.toPx()
            val rainbowColors = listOf(
                Color(0xFFFF4D4D),
                Color(0xFFFF9F1C),
                Color(0xFFFFE45E),
                Color(0xFF56F000),
                Color(0xFF00E5FF),
                Color(0xFF4D7CFF),
                Color(0xFFB26BFF),
                Color(0xFFFF4D4D)
            )
            val shift = size.width * 2f * rainbowMotion
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = rainbowColors,
                    start = Offset(-size.width + shift, 0f),
                    end = Offset(shift, size.height)
                ),
                cornerRadius = CornerRadius(radius, radius)
            )
            drawRoundRect(
                color = Color(0xFF0D9A42),
                topLeft = Offset(borderWidth, borderWidth),
                size = androidx.compose.ui.geometry.Size(size.width - borderWidth * 2f, size.height - borderWidth * 2f),
                cornerRadius = CornerRadius(radius, radius)
            )

            val blobColors = listOf(
                Color(0xFF39FF88),
                Color(0xFF17D860),
                Color(0xFF83FFB2),
                Color(0xFF10B84C)
            )
            for (i in 0 until 4) {
                val section = size.width / 4f
                val cx = section * (i + 0.5f)
                val startY = size.height * 1.48f
                val targetY = size.height * 0.54f
                val cy = startY + (targetY - startY) * pressProgress
                drawCircle(
                    color = blobColors[i].copy(alpha = 0.42f + pressProgress * 0.18f),
                    radius = size.height * (0.74f + i * 0.03f),
                    center = Offset(cx, cy)
                )
            }

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.18f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.14f)
                    )
                ),
                cornerRadius = CornerRadius(radius, radius)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "LAUNCH MINECRAFT",
                color = Color.White,
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
                    .background(LocalDurbinPalette.current.cardActive)
                    .border(1.dp, LocalDurbinPalette.current.accent.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = LocalDurbinPalette.current.accent, modifier = Modifier.size(30.dp))
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
                color = if (callbacks.isOfflineAccount()) LocalDurbinPalette.current.accent.copy(alpha = 0.15f) else Color(0xFF1BD964).copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (callbacks.isOfflineAccount()) "OFFLINE" else "ONLINE",
                    color = if (callbacks.isOfflineAccount()) LocalDurbinPalette.current.accent else LocalDurbinPalette.current.launch,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun DurbinQuickActions(
    callbacks: DurbinMenuCallbacks,
    onVersions: () -> Unit,
    onThemePicker: () -> Unit
) {
    val context = LocalContext.current
    val loaderLabel = callbacks.getLoaderLabel()
    val showModrinth = loaderLabel.equals("Fabric", ignoreCase = true) ||
            loaderLabel.equals("Forge", ignoreCase = true) ||
            loaderLabel.equals("Durbin", ignoreCase = true) ||
            loaderLabel.contains("Fabric", ignoreCase = true) ||
            loaderLabel.contains("Forge", ignoreCase = true) ||
            loaderLabel.contains("Durbin", ignoreCase = true)

    val actions = listOf(
        DurbinQuickAction("Saved Profiles", Icons.Default.Menu, callbacks.onOpenVersions),
        DurbinQuickAction("New Profile", Icons.Outlined.Extension, onVersions),
        DurbinQuickAction("Modrinth Mods", Icons.Outlined.Extension, callbacks.onOpenModDownloader, visible = showModrinth),
        DurbinQuickAction("Controls", Icons.Default.Gamepad, callbacks.onOpenControls),
        DurbinQuickAction("Directory", Icons.Default.Folder, callbacks.onOpenDirectory),
        DurbinQuickAction("Logs", Icons.Default.Share, callbacks.onShareLogs),
        DurbinQuickAction("Install JAR", Icons.Default.Storage, callbacks.onInstallJar),
        DurbinQuickAction("Theme Picker", Icons.Default.Settings, onThemePicker),
        DurbinQuickAction("Join Discord", Icons.Default.Share, { openDurbinUrl(context, DurbinDiscordUrl) }, DurbinSound.LINK),
        DurbinQuickAction("Edit Profile", Icons.AutoMirrored.Filled.KeyboardArrowRight, callbacks.onEditProfile)
    ).filter { it.visible }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("QUICK ACTIONS", color = DurbinMutedText, fontSize = 11.sp, letterSpacing = 1.sp)
        actions.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { action ->
                    QuickActionCard(
                        label = action.label,
                        icon = action.icon,
                        onClick = action.onClick,
                        sound = action.sound,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    sound: DurbinSound = DurbinSound.CLICK,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.height(56.dp).durbinClickable(sound = sound, onClick = onClick)) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = LocalDurbinPalette.current.accent, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                label,
                color = DurbinPrimaryText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DurbinCommunityCard() {
    val context = LocalContext.current
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.07f))
                        .border(1.dp, Color.White.copy(alpha = 0.14f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(R.drawable.cosa_owner_logo),
                        contentDescription = "COSA",
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("AUTHOR / OWNER", color = DurbinMutedText, fontSize = 10.sp, letterSpacing = 1.sp)
                    Text("COSA", color = DurbinPrimaryText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CommunityButton(
                    label = "YouTube",
                    icon = Icons.Default.PlayArrow,
                    modifier = Modifier.weight(1f)
                ) { openDurbinUrl(context, DurbinYoutubeUrl) }
                CommunityButton(
                    label = "Join Discord",
                    icon = Icons.Default.Share,
                    modifier = Modifier.weight(1f)
                ) { openDurbinUrl(context, DurbinDiscordUrl) }
            }
        }
    }
}

@Composable
private fun CommunityButton(label: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .height(46.dp)
            .durbinClickable(DurbinSound.LINK, onClick = onClick),
        color = Color.White.copy(alpha = 0.06f),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (label.contains("Discord")) {
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.ic_discord),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(icon, contentDescription = null, tint = LocalDurbinPalette.current.accent, modifier = Modifier.size(19.dp))
            }
            Spacer(Modifier.width(8.dp))
            Text(label, color = DurbinPrimaryText, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun DurbinFooter() {
    Text(
        text = "DURBIN LAUNCHER • OWNER COSA",
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
                Icon(Icons.Default.Menu, contentDescription = null, tint = LocalDurbinPalette.current.accent, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Open saved profiles", color = DurbinPrimaryText, fontWeight = FontWeight.SemiBold)
                    Text("Switch to any profile you already saved", color = DurbinMutedText, fontSize = 12.sp)
                }
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = LocalDurbinPalette.current.accent)
            }
        }

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Current profile", color = DurbinMutedText, fontSize = 11.sp)
                    Text(callbacks.getProfileName(), color = DurbinPrimaryText, fontWeight = FontWeight.SemiBold)
                    Text(callbacks.getVersionId(), color = LocalDurbinPalette.current.accent, fontSize = 13.sp)
                }
            }
        }

        GlassCard(modifier = Modifier.fillMaxWidth().durbinClickable {
            callbacks.onEditProfile()
            onClose()
        }) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = LocalDurbinPalette.current.accent, modifier = Modifier.size(22.dp))
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
        LaunchModeOption("DURBIN Client", "Fabric base for DURBIN Client mod", true) {
            callbacks.onSelectDurbin()
            onClose()
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
    val bg = if (mode.enabled) LocalDurbinPalette.current.card else LocalDurbinPalette.current.card.copy(alpha = 0.5f)
    val border = if (mode.enabled) LocalDurbinPalette.current.border else LocalDurbinPalette.current.border.copy(alpha = 0.5f)
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
                Surface(color = LocalDurbinPalette.current.accent.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
                    Text(
                        "LOCKED",
                        color = LocalDurbinPalette.current.accent,
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
private fun DurbinThemePickerSheet(
    selectedTheme: DurbinUiTheme,
    onThemeSelected: (DurbinUiTheme) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Theme Picker", color = DurbinPrimaryText, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Choose your DURBIN Launcher color style.", color = DurbinSecondaryText, fontSize = 13.sp)

        DurbinThemeOptionRow(
            title = "Orange",
            subtitle = "Classic DURBIN orange glow",
            theme = DurbinUiTheme.ORANGE,
            selectedTheme = selectedTheme,
            accent = OrangeDurbinPalette.accent,
            onThemeSelected = onThemeSelected
        )
        DurbinThemeOptionRow(
            title = "Red / White",
            subtitle = "Clean red and white premium style",
            theme = DurbinUiTheme.RED_WHITE,
            selectedTheme = selectedTheme,
            accent = RedWhiteDurbinPalette.accent,
            onThemeSelected = onThemeSelected
        )
        DurbinThemeOptionRow(
            title = "Black",
            subtitle = "Dark minimal stealth style",
            theme = DurbinUiTheme.BLACK,
            selectedTheme = selectedTheme,
            accent = BlackDurbinPalette.accent,
            onThemeSelected = onThemeSelected
        )
        DurbinThemeOptionRow(
            title = "Blue",
            subtitle = "Cool blue client style",
            theme = DurbinUiTheme.BLUE,
            selectedTheme = selectedTheme,
            accent = BlueDurbinPalette.accent,
            onThemeSelected = onThemeSelected
        )
        DurbinThemeOptionRow(
            title = "Purple",
            subtitle = "Neon purple premium style",
            theme = DurbinUiTheme.PURPLE,
            selectedTheme = selectedTheme,
            accent = PurpleDurbinPalette.accent,
            onThemeSelected = onThemeSelected
        )
        DurbinThemeOptionRow(
            title = "Cyan",
            subtitle = "Clean cyan glass style",
            theme = DurbinUiTheme.CYAN,
            selectedTheme = selectedTheme,
            accent = CyanDurbinPalette.accent,
            onThemeSelected = onThemeSelected
        )
        DurbinThemeOptionRow(
            title = "Minecraft",
            subtitle = "Green Minecraft inspired style",
            theme = DurbinUiTheme.MINECRAFT,
            selectedTheme = selectedTheme,
            accent = MinecraftDurbinPalette.accent,
            onThemeSelected = onThemeSelected
        )
        DurbinThemeOptionRow(
            title = "Gold",
            subtitle = "Warm gold premium style",
            theme = DurbinUiTheme.GOLD,
            selectedTheme = selectedTheme,
            accent = GoldDurbinPalette.accent,
            onThemeSelected = onThemeSelected
        )
    }
}

@Composable
private fun DurbinThemeOptionRow(
    title: String,
    subtitle: String,
    theme: DurbinUiTheme,
    selectedTheme: DurbinUiTheme,
    accent: Color,
    onThemeSelected: (DurbinUiTheme) -> Unit
) {
    val selected = selectedTheme == theme
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .durbinClickable(DurbinSound.POPUP) { onThemeSelected(theme) },
        color = if (selected) accent.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.055f),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) accent.copy(alpha = 0.42f) else Color.White.copy(alpha = 0.10f))
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(accent)
                    .border(1.dp, Color.White.copy(alpha = 0.30f), CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = DurbinPrimaryText, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(subtitle, color = DurbinMutedText, fontSize = 12.sp)
            }
            if (selected) {
                Text("SELECTED", color = accent, fontWeight = FontWeight.Bold, fontSize = 10.sp)
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
        Text("DURBIN Client", color = LocalDurbinPalette.current.accent, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text(
            "The DURBIN Client mod system is not ready yet.\nVanilla, Fabric, and Forge are available now.",
            color = DurbinSecondaryText,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            color = LocalDurbinPalette.current.accent,
            trackColor = LocalDurbinPalette.current.border
        )
        Text("Coming Soon", color = DurbinMutedText, fontSize = 12.sp, letterSpacing = 2.sp)
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(LocalDurbinPalette.current.accent)
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
            color = LocalDurbinPalette.current.card,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, LocalDurbinPalette.current.strongBorder)
        ) {
            content()
        }
    }
}

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    glowColor: Color = Color.Transparent,
    borderColor: Color = Color.White.copy(alpha = 0.075f),
    content: @Composable () -> Unit
) {
    val palette = LocalDurbinPalette.current

    Surface(
        modifier = modifier,
        color = palette.card.copy(alpha = 0.36f),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Box {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.055f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.12f)
                        )
                    )
                )
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            palette.accent.copy(alpha = 0.045f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.08f, size.height * 0.05f),
                        radius = size.maxDimension * 0.75f
                    )
                )
            }
            content()
        }
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
            try {
                view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            } catch (_: Throwable) {
                // Haptics can be disabled by device settings.
            }
            playDurbinSound(view, sound)
            onClick()
        }
}

private fun openDurbinUrl(context: android.content.Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Throwable) {
        // Keep launcher stable even if there is no browser installed.
    }
}

private fun playDurbinSound(view: View, sound: DurbinSound) {
    val rawSound = when (sound) {
        DurbinSound.LINK -> R.raw.durbin_teleport
        else -> R.raw.durbin_orb
    }

    try {
        val player = MediaPlayer.create(view.context.applicationContext, rawSound)
        if (player != null) {
            val volume = if (sound == DurbinSound.LINK) 0.72f else 0.55f
            player.setVolume(volume, volume)
            player.setOnCompletionListener {
                try {
                    it.release()
                } catch (_: Throwable) {
                    // No-op.
                }
            }
            player.start()
            return
        }
    } catch (_: Throwable) {
        // Fall back to Android click sound below.
    }

    try {
        view.playSoundEffect(SoundEffectConstants.CLICK)
    } catch (_: Throwable) {
        // Sound effects can be disabled by the system.
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
