package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.toArgb
import android.graphics.Matrix
import android.graphics.SweepGradient
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.VideoView
import android.net.Uri
import coil.compose.SubcomposeAsyncImage
import org.json.JSONObject
import org.json.JSONArray
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DurbinTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
                ) {
                    DurbinDashboard()
                }
            }
        }
    }
}

// ==========================================
// CUSTOM PIXEL ART TELESCOPE VECTOR WRITING
// ==========================================
@Composable
fun PixelTelescopeLogo(
    modifier: Modifier = Modifier,
    alpha: Float = 1.0f
) {
    val accent = AccentOrange.copy(alpha = alpha)
    val yellow = Color(0xFFFBBF24).copy(alpha = alpha) // gold/yellow
    val cyan = Color(0xFF22D3EE).copy(alpha = alpha) // cyan lens glass
    val brown = Color(0xFF78350F).copy(alpha = alpha) // dark brown shaft/wood handle
    val trans = Color.Transparent

    val grid = listOf(
        listOf(trans, trans, trans, trans, trans, trans, trans, trans, trans, trans, cyan, cyan),
        listOf(trans, trans, trans, trans, trans, trans, trans, trans, trans, cyan, cyan, cyan),
        listOf(trans, trans, trans, trans, trans, trans, trans, trans, accent, accent, cyan, cyan),
        listOf(trans, trans, trans, trans, trans, trans, trans, accent, yellow, yellow, accent, trans),
        listOf(trans, trans, trans, trans, trans, trans, accent, yellow, yellow, accent, trans, trans),
        listOf(trans, trans, trans, trans, trans, accent, yellow, yellow, accent, trans, trans, trans),
        listOf(trans, trans, trans, trans, accent, yellow, yellow, accent, trans, trans, trans, trans),
        listOf(trans, trans, trans, accent, brown, brown, accent, trans, trans, trans, trans, trans),
        listOf(trans, trans, accent, brown, brown, accent, trans, trans, trans, trans, trans, trans),
        listOf(trans, accent, brown, brown, accent, trans, trans, trans, trans, trans, trans, trans),
        listOf(accent, brown, brown, accent, trans, trans, trans, trans, trans, trans, trans, trans),
        listOf(accent, accent, accent, trans, trans, trans, trans, trans, trans, trans, trans, trans)
    )

    Canvas(modifier = modifier) {
        val pixelWidth = size.width / 12f
        val pixelHeight = size.height / 12f
        for (r in 0 until 12) {
            for (c in 0 until 12) {
                val color = grid[r][c]
                if (color != trans) {
                    drawRect(
                        color = color,
                        topLeft = Offset(c * pixelWidth, r * pixelHeight),
                        size = androidx.compose.ui.geometry.Size(pixelWidth + 0.5f, pixelHeight + 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun SpyglassLogo(
    modifier: Modifier = Modifier,
    alpha: Float = 1.0f
) {
    SubcomposeAsyncImage(
        model = "https://static.wikia.nocookie.net/minecraft_gamepedia/images/6/6f/Spyglass_%28item%29_JE3_BE1.png/revision/latest?cb=20210310162651",
        contentDescription = "Durbin Spyglass Logo",
        modifier = modifier,
        alpha = alpha,
        loading = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF1BD964),
                    strokeWidth = 1.5.dp,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        error = {
            PixelTelescopeLogo(
                modifier = Modifier.fillMaxSize(),
                alpha = alpha
            )
        }
    )
}

// Sparkle data model for dynamic universe backgrounds
data class CosmicBackgroundStar(
    val xRate: Float,
    val yRate: Float,
    val sizeScale: Float,
    val pulseSpeed: Float
)

@Composable
fun DurbinVideoBackground(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    // Generate star coordinates securely once using remember
    val stars = remember {
        List(40) {
            CosmicBackgroundStar(
                xRate = (0.02f + Math.random().toFloat() * 0.96f),
                yRate = (0.02f + Math.random().toFloat() * 0.96f),
                sizeScale = (0.8f + Math.random().toFloat() * 1.5f),
                pulseSpeed = (0.5f + Math.random().toFloat() * 1.5f)
            )
        }
    }

    val cosmicTransition = rememberInfiniteTransition(label = "cosmic_twinkle_system")
    val cosmicTime by cosmicTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f, // Full sine wave cycle
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cosmic_wave_phase"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF020203)) // Deep stellar background shade
    ) {
        // 1. VIDEO BACKDROP PLAYER (Placed first at the bottom so it never blocks the starfield drawn on top)
        AndroidView(
            factory = { ctx ->
                VideoView(ctx).apply {
                    try {
                        setVideoURI(Uri.parse(videoUrl))
                        setOnPreparedListener { mp ->
                            try {
                                mp.isLooping = true
                                mp.setVolume(0f, 0f) // Mute audio
                                start()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        setOnErrorListener { _, _, _ ->
                            true // Suppress default system dialog and elegantly display the majestic Canvas fallback starfield
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            },
            update = { /* Prevent infinite layout cycles */ },
            modifier = Modifier.fillMaxSize()
        )

        // 2. GPU-Rendered animated cosmic space nebula (100% offline, highly responsive)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height

            // A. Radiant nebula glows matching the DURBIN creative theme (vibrant green, orange, and purple)
            val glowAnimFactor = 0.82f + 0.18f * kotlin.math.sin(cosmicTime.toDouble()).toFloat()
            val rotationShiftX = 50f * kotlin.math.sin(cosmicTime.toDouble()).toFloat()
            val rotationShiftY = 50f * kotlin.math.cos(cosmicTime.toDouble()).toFloat()

            // 1. Cosmic purple dust glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1B072C).copy(alpha = 0.55f * glowAnimFactor), 
                        Color(0xFF040109).copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = Offset(canvasW * 0.75f + rotationShiftX, canvasH * 0.3f + rotationShiftY),
                    radius = canvasW * 0.9f
                )
            )

            // 2. High-fidelity bright green auroral bloom (themed around Durbin green)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF052B11).copy(alpha = 0.45f * glowAnimFactor), 
                        Color(0xFF010A04).copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(canvasW * 0.25f - rotationShiftX, canvasH * 0.75f - rotationShiftY),
                    radius = canvasW * 0.8f
                )
            )

            // 3. Warm copper solar wind glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF2E1502).copy(alpha = 0.35f * (1.1f - glowAnimFactor / 2f)),
                        Color.Transparent
                    ),
                    center = Offset(canvasW * 0.5f + rotationShiftY, canvasH * 0.5f - rotationShiftX),
                    radius = canvasW * 0.65f
                )
            )

            // B. Draw glittering, warm pixel stars conforming to the design grid
            stars.forEach { star ->
                val px = star.xRate * canvasW
                // Gentle infinite coordinate drift
                val py = ((star.yRate + (cosmicTime / 180f)) % 1.0f) * canvasH

                // Calculate organic twinkling rates
                val starTwinkle = 0.35f + 0.65f * kotlin.math.sin((cosmicTime * star.pulseSpeed * 2.5).toDouble()).toFloat()
                val finalAlpha = (starTwinkle).coerceIn(0.15f, 1.0f)

                // Render dynamic star core
                drawCircle(
                    color = Color.White.copy(alpha = finalAlpha),
                    center = Offset(px, py),
                    radius = 1.3f * star.sizeScale
                )

                // Give larger stars an ambient custom green or golden flare
                if (star.sizeScale > 1.6f) {
                    val flareColor = if (star.pulseSpeed > 1.0f) Color(0xFF1BD964) else Color(0xFFFFA726)
                    drawCircle(
                        color = flareColor.copy(alpha = finalAlpha * 0.28f),
                        center = Offset(px, py),
                        radius = 4.8f * star.sizeScale
                    )
                }
            }
        }

        // 3. SECURE HUD READABILITY PANEL OVERLAY
        // Slightly reduced opacity from 0.65f to 0.48f for rich star visibility without text pollution
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.48f))
        )
    }
}

// Concentric glow effect without depending on modern RenderEffects (compatible with all API levels)
fun Modifier.glowOverlay(
    alpha: Float = 0.05f,
    cornerRadius: Dp = 16.dp,
    glowColor: Color = AccentOrange
): Modifier = this.drawBehind {
    val pxCorner = cornerRadius.toPx()
    val shadowColor = glowColor.copy(alpha = alpha)
    for (i in 1..4) {
        val strokeMultiplier = i * 4.5f
        drawRoundRect(
            color = shadowColor,
            topLeft = Offset(-strokeMultiplier / 2f, -strokeMultiplier / 2f),
            size = this.size.copy(
                width = this.size.width + strokeMultiplier,
                height = this.size.height + strokeMultiplier
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(pxCorner + strokeMultiplier / 2f),
            style = Stroke(width = strokeMultiplier)
        )
    }
}

// ==========================================
// DYNAMIC MOCK DATA HOLDER & CONTROLS
// ==========================================
@Composable
fun DurbinDashboard() {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Launcher Configuration Sates (Controlled by dialogs!)
    var currentVersion by rememberSaveable { mutableStateOf("Minecraft 1.20.4") }
    var currentProfileName by rememberSaveable { mutableStateOf("Vanilla profile • optimized for mobile") }
    var currentLoader by rememberSaveable { mutableStateOf("Vanilla") }
    var currentRuntime by rememberSaveable { mutableStateOf("Java 21") }
    var currentRamAlloc by rememberSaveable { mutableStateOf("700 MB") }
    var currentRenderer by rememberSaveable { mutableStateOf("MobileGlues") }

    var userAccountName by rememberSaveable { mutableStateOf("Player") }
    var userAccountTypeOffline by rememberSaveable { mutableStateOf(true) }

    // Logs buffer simulator
    val simulatedLogsList = remember {
        mutableStateListOf(
            "[17:15:10] [launcher/INFO]: DURBIN Launcher starting (version v0.4.1)...",
            "[17:15:10] [launcher/INFO]: Initializing Java runtime environment: Java 21 LTS...",
            "[17:15:11] [launcher/INFO]: MobileGlues GLES3 context successfully compiled.",
            "[17:15:11] [launcher/INFO]: Profile linked: Minecraft 1.20.4 (Vanilla)",
            "[17:15:12] [launcher/INFO]: Safe virtual allocator bounds validated: RAM=700MB",
            "[17:15:13] [launcher/INFO]: Controller overlay loaded: default-preset-v2.json",
            "[17:15:13] [launcher/INFO]: Standing by for client boot sequence"
        )
    }

    // Navigation and Drawer Dialog Overlays
    var activeDialog by remember { mutableStateOf("") } // "", "versions", "accounts", "controls", "mods", "directory", "storage", "settings"
    var selectedPresetConfigName by rememberSaveable { mutableStateOf("Touch • default") }
    
    // Mods list state
    var modsList by remember {
        mutableStateOf(
            listOf(
                "Sodium Mobile Shader Optimization" to true,
                "Lithium Engine Allocator Patch" to true,
                "FastChest Mobile Renderer" to false,
                "BetterGrass Touch Controls Controller" to false,
                "AutoReconnect Client Ping Tool" to false
            )
        )
    }
    val enabledModsCount = modsList.count { it.second }

    // ===============================================
    // MODRINTH EXPLORER ACTIVE STATE REGISTRY
    // ===============================================
    var modrinthQuery by rememberSaveable { mutableStateOf("") }
    var modrinthVersion by rememberSaveable { mutableStateOf("1.20.4") }
    var modrinthLoader by rememberSaveable { mutableStateOf("Fabric") }
    val modrinthSelectedCategories = remember { mutableStateListOf<String>() }
    var modrinthApiKey by rememberSaveable { mutableStateOf("") }
    var modrinthErrorMsg by remember { mutableStateOf<String?>(null) }
    var isModrinthSearching by remember { mutableStateOf(false) }

    // High fidelity default items matching Modrinth's native catalog style and visual data counts
    val modrinthPreloadedResults = remember {
        listOf(
            ModrinthHit(
                id = "AANobbH4",
                title = "Fabric API",
                author = "modmuss50",
                description = "Lightweight and modular API providing common hooks and intercompatibility measures utilized by mods using the Fabric toolchain.",
                iconUrl = "https://minecraft.wiki/images/Spyglass_%28item%29_JE3_BE1.png?39671", // fallback image model support
                downloads = 181590000L,
                follows = 31400L,
                latestVersion = "0.100.0",
                categories = listOf("client", "library", "fabric"),
                loaders = listOf("Fabric"),
                dateModified = "Yesterday"
            ),
            ModrinthHit(
                id = "hXf9Xv4Z",
                title = "Sodium",
                author = "CaffeineMC",
                description = "A high-performance rendering engine replacement for Minecraft, which greatly improves frame rates and reduces micro-stutter.",
                iconUrl = null,
                downloads = 164550000L,
                follows = 37100L,
                latestVersion = "0.5.8",
                categories = listOf("client", "optimization", "fabric", "neoforge"),
                loaders = listOf("Fabric", "NeoForge"),
                dateModified = "2 weeks ago"
            ),
            ModrinthHit(
                id = "F0wJ9tC5",
                title = "Iris Shaders",
                author = "coderbot",
                description = "A modern shader pack loader for Minecraft intended to be compatible with existing OptiFine shader packs and performance mods.",
                iconUrl = null,
                downloads = 128300000L,
                follows = 26700L,
                latestVersion = "1.7.0",
                categories = listOf("client", "decoration", "optimization", "fabric"),
                loaders = listOf("Fabric"),
                dateModified = "2 months ago"
            ),
            ModrinthHit(
                id = "mXp8N7vH",
                title = "Cloth Config API",
                author = "shedaniel",
                description = "Configuration Library for Minecraft Mods. Required by major performant client modules.",
                iconUrl = null,
                downloads = 126860000L,
                follows = 15300L,
                latestVersion = "14.0.0",
                categories = listOf("client", "library", "fabric", "forge"),
                loaders = listOf("Fabric", "Forge"),
                dateModified = "2 months ago"
            ),
            ModrinthHit(
                id = "TrH8WfXa",
                title = "Entity Culling",
                author = "tr7zw",
                description = "Using async path-tracing to hide Block-Entities and Entities that are not visible to improve frames.",
                iconUrl = null,
                downloads = 118930000L,
                follows = 15700L,
                latestVersion = "1.6.5",
                categories = listOf("client", "optimization", "fabric", "forge"),
                loaders = listOf("Fabric", "Forge"),
                dateModified = "Last month"
            )
        )
    }

    val modrinthSearchResults = remember { mutableStateListOf<ModrinthHit>().apply { addAll(modrinthPreloadedResults) } }

    fun triggerModrinthLiveSearch() {
        isModrinthSearching = true
        modrinthErrorMsg = null
        val query = modrinthQuery.lowercase()
        val loader = modrinthLoader.lowercase()

        coroutineScope.launch {
            delay(150) // simulate swift local filtering response
            isModrinthSearching = false
            modrinthSearchResults.clear()
            
            val filtered = modrinthPreloadedResults.filter { hit ->
                val titleMatch = hit.title.lowercase().contains(query)
                val descMatch = hit.description.lowercase().contains(query)
                val queryMatch = query.isEmpty() || titleMatch || descMatch
                val loaderMatch = loader.isEmpty() || loader == "any" || hit.loaders.any { it.equals(loader, ignoreCase = true) }
                queryMatch && loaderMatch
            }
            modrinthSearchResults.addAll(filtered)
        }
    }

    // Auto-fetch from Modrinth API when dialogue is activated or search query evolves
    LaunchedEffect(activeDialog) {
        if (activeDialog == "mods") {
            triggerModrinthLiveSearch()
        }
    }

    LaunchedEffect(modrinthQuery) {
        if (activeDialog == "mods") {
            // Type-debounce to avoid hitting API limitations during fast input entries 
            delay(500)
            triggerModrinthLiveSearch()
        }
    }


    // Storage info states
    var mockStorageCacheSizeMb by rememberSaveable { mutableStateOf(215) }
    var mockStorageDirUsedGb by rememberSaveable { mutableStateOf(1.2) }

    // Launch Simulator States
    var isLaunching by remember { mutableStateOf(false) }
    var launchProgress by remember { mutableStateOf(0.0f) }
    var launchStatusText by remember { mutableStateOf("Ready to launch") }

    // Simulated Boot Trigger Function
    fun triggerSimulatedLaunch() {
        if (isLaunching) return
        isLaunching = true
        launchProgress = 0.0f
        coroutineScope.launch {
            simulatedLogsList.add("[Launch] Initializing game runtime bootstrap...")
            launchStatusText = "Booting Java Virtual Machine..."
            delay(1000)
            
            simulatedLogsList.add("[Launch] Creating EGL viewport context...")
            launchProgress = 0.35f
            launchStatusText = "Compiling MobileGlues Shaders..."
            delay(1000)
            
            simulatedLogsList.add("[Launch] Loading base assets archive...")
            launchProgress = 0.70f
            launchStatusText = "Syncing local assets index..."
            delay(1000)
            
            simulatedLogsList.add("[Launch] Initializing Minecraft main thread...")
            launchProgress = 1.0f
            launchStatusText = "Game execution success!"
            delay(800)
            
            isLaunching = false
            launchStatusText = "Ready to launch"
            Toast.makeText(context, "DURBIN Sandbox client successfully triggered!", Toast.LENGTH_LONG).show()
        }
    }

    // Rotating infinite transition for Gemini colored border (applied to launcher buttons)
    val infiniteTransition = rememberInfiniteTransition(label = "gemini_border_anim")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gemini_border_rotation"
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { padding ->
        val configuration = androidx.compose.ui.platform.LocalConfiguration.current
        val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

        // COMPOSABLE COMPONENT BUCKETS FOR PORTRAIT & LANDSCAPE LAYOUTS
        val topBarSection = @Composable {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface)
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            Toast.makeText(context, "DURBIN v0.4.1 Menu options triggered.", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu Sidebar",
                            tint = PrimaryText
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        SpyglassLogo(
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DURBIN",
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                letterSpacing = 4.sp,
                                color = PrimaryText
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { activeDialog = "settings" }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings Icon",
                                tint = SecondaryText,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(onClick = { activeDialog = "accounts" }) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(CardActiveBg)
                                    .border(1.dp, StrongBorderColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile Account Indicator",
                                    tint = PrimaryText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        val heroLaunchCardSection = @Composable {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("durbin_launch_card")
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                CardBg,
                                Surface
                            )
                        )
                    )
                    .border(1.dp, StrongBorderColor, RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                SpyglassLogo(
                    modifier = Modifier
                        .testTag("durbin_launch_logo_watermark")
                        .align(Alignment.BottomEnd)
                        .offset(x = (16).dp, y = (16).dp)
                        .size(120.dp),
                    alpha = 0.10f
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(AccentOrange)
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "SELECTED",
                                style = TextStyle(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            )
                        }

                        Text(
                            text = "CLIENT VERSION",
                            style = TextStyle(
                                color = SecondaryText,
                                fontSize = 10.sp,
                                letterSpacing = 1.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "Minecraft ",
                            style = TextStyle(
                                color = PrimaryText,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = currentVersion.substringAfter("Minecraft "),
                            style = TextStyle(
                                color = AccentOrange,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = currentProfileName,
                        style = TextStyle(
                            color = MutedText,
                            fontSize = 12.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("durbin_card_version")
                            ) {
                                Text(
                                    text = "LOADER",
                                    style = TextStyle(color = MutedText, fontSize = 9.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currentLoader,
                                    style = TextStyle(color = PrimaryText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("durbin_card_runtime")
                            ) {
                                Text(
                                    text = "RUNTIME",
                                    style = TextStyle(color = MutedText, fontSize = 9.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currentRuntime,
                                    style = TextStyle(color = PrimaryText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "RAM",
                                    style = TextStyle(color = MutedText, fontSize = 9.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currentRamAlloc,
                                    style = TextStyle(color = PrimaryText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "RENDERER",
                                    style = TextStyle(color = MutedText, fontSize = 9.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currentRenderer,
                                    style = TextStyle(color = PrimaryText, fontSize = 12.sp, fontWeight = FontWeight.Medium),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F0F0F))
                            .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(if (isLaunching) Color(0xFFFFB000) else AccentOrange)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Status",
                                    style = TextStyle(color = SecondaryText, fontSize = 12.sp)
                                )
                            }

                            Text(
                                text = if (isLaunching) launchStatusText else "Ready to launch",
                                style = TextStyle(
                                    color = PrimaryText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    AnimatedVisibility(visible = isLaunching) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { launchProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .clip(CircleShape),
                                color = AccentOrange,
                                trackColor = StrongBorderColor,
                            )
                        }
                    }
                }
            }
        }

        val playButtonSection = @Composable {
            val launchInteractionSource = remember { MutableInteractionSource() }
            val launchIsPressed by launchInteractionSource.collectIsPressedAsState()
            
            val launchPressScale by animateFloatAsState(
                targetValue = if (launchIsPressed) 0.93f else 1.00f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "launch_press_spring"
            )

            val launchBtnTransition = rememberInfiniteTransition(label = "launch_btn_anim_system")
            
            val breatheScale by launchBtnTransition.animateFloat(
                initialValue = 0.99f,
                targetValue = if (isLaunching) 1.04f else 1.01f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = if (isLaunching) 450 else 1800,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "breathe_scale_pulsing"
            )

            val laserShimmerProgress by launchBtnTransition.animateFloat(
                initialValue = -0.4f,
                targetValue = 1.4f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = if (isLaunching) 700 else 1300,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "laser_sweep_shimmer"
            )

            val greenPulseAlpha by launchBtnTransition.animateFloat(
                initialValue = 0.08f,
                targetValue = if (isLaunching) 0.38f else 0.16f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = if (isLaunching) 350 else 1150,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "green_glow_pulse"
            )

            val rapidBorderSpin by launchBtnTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = if (isLaunching) 1000 else 3800,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rapid_border_spin"
            )

            val launchParticlePhase by launchBtnTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "launch_matrix_particle_phase"
            )

            val compositeLaunchScale = breatheScale * launchPressScale

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .graphicsLayer {
                        scaleX = compositeLaunchScale
                        scaleY = compositeLaunchScale
                    }
                    .testTag("durbin_btn_play")
                    .glowOverlay(
                        alpha = greenPulseAlpha,
                        cornerRadius = 16.dp,
                        glowColor = Color(0xFF1BD964)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(
                        indication = LocalIndication.current,
                        interactionSource = launchInteractionSource
                    ) {
                        triggerSimulatedLaunch()
                    }
                    .background(
                        Brush.horizontalGradient(
                            colors = if (isLaunching) {
                                listOf(Color(0xFF2EFE74), Color(0xFF11622D), Color(0xFF2EFE74))
                            } else {
                                listOf(Color(0xFF1BD964), Color(0xFF138A3F))
                            }
                        )
                    )
                    .drawWithContent {
                        drawContent()

                        val widthPx = size.width
                        val heightPx = size.height
                        val shimmerCenter = laserShimmerProgress * widthPx

                        val shimmerBrush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x1BFFFFFF),
                                Color(0xAAFFFFFF),
                                Color.White,
                                Color(0xDDFFFFFF),
                                Color(0x2BFFFFFF),
                                Color.Transparent
                            ),
                            start = Offset(shimmerCenter - 32.dp.toPx(), 0f),
                            end = Offset(shimmerCenter + 32.dp.toPx(), heightPx)
                        )
                        drawRect(brush = shimmerBrush)

                        if (isLaunching) {
                            val dotPaintColor = Color.White.copy(alpha = 0.35f)
                            for (col in 0..5) {
                                val baseX = (widthPx / 6f) * col + (widthPx / 12f)
                                val speedMod = 1f + (col % 3) * 0.2f
                                val dynamicY = ((1.0f - ((launchParticlePhase * speedMod) % 1.0f)) * heightPx)
                                
                                drawCircle(
                                    color = dotPaintColor,
                                    center = Offset(baseX, dynamicY),
                                    radius = 2.dp.toPx()
                                )
                            }
                        }
                    }
                    .geminiSweepBorder(
                        rotation = rapidBorderSpin,
                        cornerRadius = 16.dp,
                        borderWidth = 2.2.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isLaunching) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier
                                .size(22.dp)
                                .padding(2.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play icon trigger",
                            tint = Color.Black,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isLaunching) "LAUNCHING CLIENT..." else "LAUNCH CLIENT",
                        style = TextStyle(
                            color = if (isLaunching) Color.White else Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    )
                }
            }
        }

        val accountCardSection = @Composable {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("durbin_card_account")
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBg)
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .clickable { activeDialog = "accounts" }
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(46.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(CardActiveBg)
                                    .border(1.dp, StrongBorderColor, CircleShape),
                            contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "User avatar",
                                    tint = SecondaryText,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(AccentOrange)
                                    .border(2.dp, Background, CircleShape)
                                    .align(Alignment.BottomEnd)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = userAccountName,
                                    style = TextStyle(
                                        color = PrimaryText,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(StrongBorderColor)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (userAccountTypeOffline) "OFFLINE" else "PREMIUM",
                                        style = TextStyle(
                                            color = if (userAccountTypeOffline) SecondaryText else AccentOrange,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (userAccountTypeOffline) "Offline Account • Ready" else "Microsoft Live Sync • Active",
                                style = TextStyle(color = SecondaryText, fontSize = 12.sp)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Edit profiles",
                        tint = MutedText,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        val quickActionsSection = @Composable {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "QUICK ACTIONS",
                        style = TextStyle(
                            color = MutedText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    )
                    Text(
                        text = "MANAGE",
                        modifier = Modifier.clickable {
                            Toast.makeText(context, "Durbin client customization panel opened.", Toast.LENGTH_SHORT).show()
                        },
                        style = TextStyle(
                            color = AccentOrange,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionCard(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("durbin_btn_versions"),
                            title = "Versions",
                            subtitle = "${currentVersion.substringAfter("Minecraft ")} • ${if (currentLoader != "Vanilla") "Custom" else "6 installed"}",
                            icon = Icons.Default.Layers,
                            onClick = { activeDialog = "versions" }
                        )

                        QuickActionCard(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("durbin_btn_accounts"),
                            title = "Accounts",
                            subtitle = if (userAccountTypeOffline) "1 profile" else "Premium sync",
                            icon = Icons.Default.People,
                            onClick = { activeDialog = "accounts" }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionCard(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("durbin_btn_controls"),
                            title = "Controls",
                            subtitle = selectedPresetConfigName,
                            icon = Icons.Default.Gamepad,
                            onClick = { activeDialog = "controls" }
                        )

                        QuickActionCard(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("durbin_btn_mods"),
                            title = "Mods",
                            subtitle = "$enabledModsCount enabled",
                            icon = Icons.Default.Extension,
                            onClick = { activeDialog = "mods" }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionCard(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("durbin_btn_directory"),
                            title = "Directory",
                            subtitle = "Game files",
                            icon = Icons.Default.Folder,
                            onClick = { activeDialog = "directory" }
                        )

                        QuickActionCard(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("durbin_btn_open_game_directory"),
                            title = "Storage",
                            subtitle = "${mockStorageDirUsedGb} GB used",
                            icon = Icons.Default.Storage,
                            onClick = { activeDialog = "storage" }
                        )
                    }
                }
            }
        }

        val aboutSection = @Composable {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("durbin_section_about")
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About section logo",
                            tint = Color(0xFF1BD964),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ABOUT DURBIN",
                            style = TextStyle(
                                color = MutedText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        )
                    }
                    Text(
                        text = "INFO & HELP",
                        style = TextStyle(
                            color = Color(0xFF1BD964),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardBg)
                        .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                        .clickable {
                            Toast.makeText(context, "Durbin Launcher v0.4.1 details: stable binary payload.", Toast.LENGTH_SHORT).show()
                        }
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "DURBIN Launcher is an ultra-fast, high-performance Minecraft Java Edition game client loader optimized for mobile platforms. Features native high-fidelity touch simulation controllers, a granular JVM sandbox state, and an offline assets directory browser with direct API support.",
                            style = TextStyle(
                                color = PrimaryText,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        )

                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderColor))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        try {
                                            val intent = android.content.Intent(
                                                android.content.Intent.ACTION_VIEW,
                                                Uri.parse("https://www.youtube.com/@Cosa_5023_YT")
                                            )
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Opening YouTube: @Cosa_5023_YT", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Creator", color = SecondaryText, fontSize = 11.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "COSA_5024",
                                        color = Color(0xFF1BD964),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        style = TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.OpenInNew,
                                        contentDescription = "YouTube channel link",
                                        tint = Color(0xFF1BD964),
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Build Release", color = SecondaryText, fontSize = 11.sp)
                                Text(text = "v0.4.1 Beta", color = PrimaryText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Architecture", color = SecondaryText, fontSize = 11.sp)
                                Text(text = "ARM64-v8a", color = PrimaryText, fontSize = 11.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Active Engine", color = SecondaryText, fontSize = 11.sp)
                                Text(text = "MobileGlues GLES3 Renderer", color = PrimaryText, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        val footerSection = @Composable {
            Text(
                text = "DURBIN  •  V0.4.1  •  MOBILE",
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    color = MutedText,
                    fontSize = 10.sp,
                    letterSpacing = 2.5.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Live loop video background containing premium cosmic visual accents and automatic mute setup
            DurbinVideoBackground(
                videoUrl = "https://cdn.discordapp.com/attachments/1474466632666583284/1509713934360248380/05291.mp4?ex=6a28aeab&is=6a275d2b&hm=57ab6230b5d0eba5933638618a64dce47d1ff0220f03dc84ea0b18f69e069bfe&"
            )

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
                    .then(if (isLandscape) Modifier else Modifier.verticalScroll(rememberScrollState())),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                topBarSection()

                Spacer(modifier = Modifier.height(12.dp))

                if (isLandscape) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            heroLaunchCardSection()
                            playButtonSection()
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Column(
                            modifier = Modifier
                                .weight(1.1f)
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            accountCardSection()
                            quickActionsSection()
                            aboutSection()
                            footerSection()
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        heroLaunchCardSection()
                        playButtonSection()
                        accountCardSection()
                        quickActionsSection()
                        aboutSection()
                        footerSection()
                    }
                }
            }

            // ==========================================================
            // FULLY INTERACTIVE GLASS DIALOGS OVERLAY (STABLE SIMULATOR)
            // ==========================================================
            if (activeDialog.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.75f))
                        .clickable { activeDialog = "" }, // clicking background closes dialog
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (isLandscape) 0.65f else 1.0f)
                            .widthIn(max = 600.dp)
                            .heightIn(max = if (activeDialog == "mods") 640.dp else 450.dp)
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            .background(Surface)
                            .border(1.dp, StrongBorderColor, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            .clickable(enabled = true, onClick = {}) // disable click-through
                            .navigationBarsPadding()
                            .padding(20.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Heading Indicator Line & Title
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = activeDialog.uppercase(),
                                    style = TextStyle(
                                        color = AccentOrange,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                )
                                IconButton(onClick = { activeDialog = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close overlay context",
                                        tint = SecondaryText,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            when (activeDialog) {
                                "versions" -> {
                                    DurbinVersionsDialogContent(
                                        currentVersion = currentVersion,
                                        onVersionSelected = { version, profile, loader ->
                                            currentVersion = version
                                            currentProfileName = profile
                                            currentLoader = loader
                                            simulatedLogsList.add("[Config] Updated client profile to: $version ($loader)")
                                        },
                                        onClose = { activeDialog = "" }
                                    )
                                }

                                "accounts" -> {
                                    Text(
                                        text = "Player Profile Manager",
                                        style = TextStyle(color = PrimaryText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    
                                    // Enter Name TextField
                                    Text(text = "PROFILE USERNAME", color = MutedText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = userAccountName,
                                        onValueChange = { userAccountName = it },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = PrimaryText,
                                            unfocusedTextColor = PrimaryText,
                                            focusedBorderColor = AccentOrange,
                                            unfocusedBorderColor = BorderColor,
                                            focusedContainerColor = CardActiveBg,
                                            unfocusedContainerColor = CardBg
                                        ),
                                        modifier = Modifier.fillMaxWidth(),
                                        textStyle = TextStyle(fontSize = 14.sp)
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Toggle Offline Profile Row
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(CardBg)
                                            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                                            .clickable { userAccountTypeOffline = !userAccountTypeOffline }
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = "Offline Mode Support", color = PrimaryText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text(text = "Bypass MS authenticator checks for local environments", color = SecondaryText, fontSize = 11.sp)
                                        }
                                        Switch(
                                            checked = userAccountTypeOffline,
                                            onCheckedChange = { userAccountTypeOffline = it },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = PrimaryText,
                                                checkedTrackColor = AccentOrange,
                                                uncheckedThumbColor = MutedText,
                                                uncheckedTrackColor = CardActiveBg
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(AccentOrange)
                                            .clickable {
                                                simulatedLogsList.add("[Accounts] Synced credentials user details updated.")
                                                activeDialog = ""
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "SAVE & SYNC ACCOUNT", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                    }
                                }

                                "controls" -> {
                                    Text(
                                        text = "Touch Layout Presets",
                                        style = TextStyle(color = PrimaryText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    val presets = listOf("Touch • default", "Touch • custom size", "On-Screen Console", "Hardware Keyboard Emulation")
                                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(presets) { presetName ->
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(if (selectedPresetConfigName == presetName) CardActiveBg else CardBg)
                                                    .border(1.dp, if (selectedPresetConfigName == presetName) AccentOrange else BorderColor, RoundedCornerShape(10.dp))
                                                    .clickable {
                                                        selectedPresetConfigName = presetName
                                                        simulatedLogsList.add("[Controls] Presets config mapped: $presetName")
                                                        activeDialog = ""
                                                    }
                                                    .padding(12.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(text = presetName, color = PrimaryText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                    if (selectedPresetConfigName == presetName) {
                                                        Icon(imageVector = Icons.Default.Check, contentDescription = "Checked", tint = AccentOrange, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                "mods" -> {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        // Brand Header Block
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(Color(0xFF031C0C))
                                                            .border(1.dp, Color(0xFF1BD964).copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = "MODRINTH PLATFORM",
                                                            style = TextStyle(
                                                                color = Color(0xFF1BD964),
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                letterSpacing = 1.sp
                                                            )
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "✦ EXPLORER",
                                                        style = TextStyle(color = PrimaryText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "Browse, optimize, and install Minecraft modifications.",
                                                    style = TextStyle(color = SecondaryText, fontSize = 10.sp)
                                                )
                                            }

                                            // Confirmed installed counter
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(CardActiveBg)
                                                    .border(1.dp, BorderColor, RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = "$enabledModsCount Active",
                                                    style = TextStyle(color = AccentOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // ==========================================
                                        // API KEY / AUTHOR_TOKEN (User request)
                                        // ==========================================
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFF0C0D0E))
                                                .border(1.dp, Color(0xFF1D1E22), RoundedCornerShape(8.dp))
                                                .padding(8.dp)
                                        ) {
                                            Column {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Lock,
                                                        contentDescription = "Token Lock",
                                                        tint = Color(0xFF1BD964),
                                                        modifier = Modifier.size(10.dp)
                                                    )
                                                    Text(
                                                        text = "MODRINTH AUTHORIZATION TOKEN / API KEY (OPTIONAL)",
                                                        style = TextStyle(
                                                            color = SecondaryText,
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            letterSpacing = 0.5.sp
                                                        )
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    // Custom basic password-masked textfield for Modrinth API Key
                                                    BasicTextField(
                                                        value = modrinthApiKey,
                                                        onValueChange = { modrinthApiKey = it },
                                                        singleLine = true,
                                                        textStyle = TextStyle(color = Color(0xFF1BD964), fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                                                        cursorBrush = SolidColor(Color(0xFF1BD964)),
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .height(24.dp)
                                                            .background(Color.Black, RoundedCornerShape(4.dp))
                                                            .border(1.dp, Color(0xFF26282E), RoundedCornerShape(4.dp))
                                                            .padding(horizontal = 6.dp, vertical = 4.dp),
                                                        decorationBox = { innerTextField ->
                                                            if (modrinthApiKey.isEmpty()) {
                                                                Text("Enter token for high limit querying...", color = MutedText, fontSize = 10.sp)
                                                            }
                                                            innerTextField()
                                                        }
                                                    )

                                                    if (modrinthApiKey.isNotEmpty()) {
                                                        Text(
                                                            text = "CLEAR",
                                                            modifier = Modifier.clickable { modrinthApiKey = "" },
                                                            style = TextStyle(color = AccentOrange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // ==========================================
                                        // CONTROLS DRAWER: SEARCH BAR + FILTERS
                                        // ==========================================
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Search Textfield
                                            BasicTextField(
                                                value = modrinthQuery,
                                                onValueChange = {
                                                    modrinthQuery = it
                                                },
                                                singleLine = true,
                                                textStyle = TextStyle(color = PrimaryText, fontSize = 12.sp),
                                                cursorBrush = SolidColor(Color(0xFF1BD964)),
                                                modifier = Modifier
                                                    .weight(1.5f)
                                                    .height(34.dp)
                                                    .background(CardBg, RoundedCornerShape(8.dp))
                                                    .border(1.dp, Color(0xFF222428), RoundedCornerShape(8.dp))
                                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                                decorationBox = { innerTextField ->
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Box(modifier = Modifier.weight(1f)) {
                                                            if (modrinthQuery.isEmpty()) {
                                                                Text("Search mods (e.g. Sodium)...", color = MutedText, fontSize = 11.sp)
                                                            }
                                                            innerTextField()
                                                        }
                                                        Icon(
                                                            imageVector = Icons.Default.Search,
                                                            contentDescription = "SearchIcon",
                                                            tint = SecondaryText,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }
                                            )

                                            // QUERY TRIGGER BUTTON
                                            Box(
                                                modifier = Modifier
                                                    .height(34.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isModrinthSearching) CardActiveBg else Color(0xFF163C1E))
                                                    .border(1.dp, Color(0xFF1BD964).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                                    .clickable(enabled = !isModrinthSearching) {
                                                        triggerModrinthLiveSearch()
                                                    }
                                                    .padding(horizontal = 12.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isModrinthSearching) {
                                                    CircularProgressIndicator(
                                                        color = Color(0xFF1BD964),
                                                        strokeWidth = 1.5.dp,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                } else {
                                                    Text(
                                                        text = "SEARCH API",
                                                        color = Color(0xFF1BD964),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        letterSpacing = 0.5.sp
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Scrollable Version / Loader Filters Row
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "VERSION:", color = MutedText, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            listOf("1.21.1", "1.20.4", "1.19.2", "1.16.5").forEach { ver ->
                                                val isSel = modrinthVersion == ver
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(if (isSel) Color(0xFF031C0C) else CardBg)
                                                        .border(1.dp, if (isSel) Color(0xFF1BD964) else BorderColor, RoundedCornerShape(6.dp))
                                                        .clickable {
                                                            modrinthVersion = ver
                                                            triggerModrinthLiveSearch()
                                                        }
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Text(text = ver, color = if (isSel) Color(0xFF1BD964) else SecondaryText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = "LOADER:", color = MutedText, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            listOf("Fabric", "Forge", "NeoForge", "Any").forEach { ldr ->
                                                val isSel = modrinthLoader == ldr
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(if (isSel) Color(0xFF031C0C) else CardBg)
                                                        .border(1.dp, if (isSel) Color(0xFF1BD964) else BorderColor, RoundedCornerShape(6.dp))
                                                        .clickable {
                                                            modrinthLoader = ldr
                                                            triggerModrinthLiveSearch()
                                                        }
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Text(text = ldr, color = if (isSel) Color(0xFF1BD964) else SecondaryText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Web Category Scrolling Buttons
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "TAGS:", color = MutedText, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            listOf("Optimization", "Performance", "Library", "Decoration", "Utility").forEach { cat ->
                                                val isSelected = modrinthSelectedCategories.contains(cat)
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(if (isSelected) Color(0xFF052D12) else CardBg)
                                                        .border(1.dp, if (isSelected) Color(0xFF1BD964).copy(alpha = 0.8f) else BorderColor, RoundedCornerShape(12.dp))
                                                        .clickable {
                                                            if (isSelected) {
                                                                modrinthSelectedCategories.remove(cat)
                                                            } else {
                                                                modrinthSelectedCategories.add(cat)
                                                            }
                                                            triggerModrinthLiveSearch()
                                                        }
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        if (isSelected) {
                                                            Icon(
                                                                imageVector = Icons.Default.Check,
                                                                contentDescription = "Sel",
                                                                tint = Color(0xFF1BD964),
                                                                modifier = Modifier.size(8.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(3.dp))
                                                        }
                                                        Text(text = cat, color = if (isSelected) Color(0xFF1BD964) else SecondaryText, fontSize = 8.sp)
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // ERROR BOX IF APPLICABLE
                                        if (modrinthErrorMsg != null) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFF2B1212))
                                                    .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                                    .padding(10.dp)
                                            ) {
                                                Text(
                                                    text = "Error code: ${modrinthErrorMsg}. Defaulting Explorer to cached presets.",
                                                    color = Color(0xFFFCA5A5),
                                                    fontSize = 10.sp
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(10.dp))
                                        }

                                        // ==========================================
                                        // MOD LIST VIEWPORT (LAZYCOLUMN)
                                        // ==========================================
                                        LazyColumn(
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(260.dp)
                                        ) {
                                            items(modrinthSearchResults) { hit ->
                                                // Check if actually installed in local laundry list
                                                val isInstalled = modsList.any { localMod ->
                                                    localMod.first.equals(hit.title, ignoreCase = true) || localMod.first.startsWith(hit.title)
                                                }

                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(Color(0xFF101113))
                                                        .border(1.dp, Color(0xFF1E1F22), RoundedCornerShape(10.dp))
                                                        .padding(10.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        // Left Mod logo + Info Details
                                                        Row(
                                                            modifier = Modifier.weight(1f),
                                                            verticalAlignment = Alignment.Top,
                                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                        ) {
                                                            // Custom Rounded Loader/Coil Image
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(46.dp)
                                                                    .clip(RoundedCornerShape(8.dp))
                                                                    .background(Color(0xFF26282E)),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                if (hit.iconUrl != null) {
                                                                    SubcomposeAsyncImage(
                                                                        model = hit.iconUrl,
                                                                        contentDescription = hit.title,
                                                                        modifier = Modifier.fillMaxSize(),
                                                                        loading = {
                                                                            Box(
                                                                                modifier = Modifier.fillMaxSize().background(Color(0xFF0C2415)),
                                                                                contentAlignment = Alignment.Center
                                                                            ) {
                                                                                Text(text = hit.title.take(1), color = Color(0xFF1BD964), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                                                            }
                                                                        },
                                                                        error = {
                                                                            Box(
                                                                                modifier = Modifier.fillMaxSize().background(Color(0xFF0C2415)),
                                                                                contentAlignment = Alignment.Center
                                                                            ) {
                                                                                Text(text = hit.title.take(1), color = Color(0xFF1BD964), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                                                            }
                                                                        }
                                                                    )
                                                                } else {
                                                                    // Fallback Letter Stamp
                                                                    Box(
                                                                        modifier = Modifier.fillMaxSize().background(Color(0xFF031C0C)),
                                                                        contentAlignment = Alignment.Center
                                                                    ) {
                                                                        Text(text = hit.title.take(1), color = Color(0xFF1BD964), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                                                    }
                                                                }
                                                            }

                                                            // Mod Core Info
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                    text = hit.title,
                                                                    color = PrimaryText,
                                                                    fontSize = 13.sp,
                                                                    fontWeight = FontWeight.Bold
                                                                )
                                                                Text(
                                                                    text = "by ${hit.author}",
                                                                    color = SecondaryText,
                                                                    fontSize = 9.sp,
                                                                    fontWeight = FontWeight.Medium
                                                                )
                                                                Spacer(modifier = Modifier.height(3.dp))
                                                                Text(
                                                                    text = hit.description,
                                                                    color = SecondaryText,
                                                                    fontSize = 10.sp,
                                                                    maxLines = 2,
                                                                    overflow = TextOverflow.Ellipsis
                                                                )

                                                                Spacer(modifier = Modifier.height(4.dp))

                                                                // Tiny Tags Row representation
                                                                Row(
                                                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                                    modifier = Modifier.horizontalScroll(rememberScrollState())
                                                                ) {
                                                                    hit.loaders.forEach { ldr ->
                                                                        Box(
                                                                            modifier = Modifier
                                                                                .clip(RoundedCornerShape(4.dp))
                                                                                .background(Color(0xFF093016))
                                                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                                                        ) {
                                                                            Text(text = ldr.uppercase(), color = Color(0xFF1BD964), fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                                                        }
                                                                    }
                                                                    hit.categories.take(2).forEach { tag ->
                                                                        Box(
                                                                            modifier = Modifier
                                                                                .clip(RoundedCornerShape(4.dp))
                                                                                .background(Color(0xFF1E2024))
                                                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                                                        ) {
                                                                            Text(text = tag, color = SecondaryText, fontSize = 7.sp)
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        Spacer(modifier = Modifier.width(10.dp))

                                                        // Right panel: Downloads counter + ACTIVE TOGGLE TRIGGER
                                                        Column(
                                                            horizontalAlignment = Alignment.End,
                                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                                        ) {
                                                            // Stats
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.ArrowDownward,
                                                                    contentDescription = "Downloads",
                                                                    tint = SecondaryText,
                                                                    modifier = Modifier.size(10.dp)
                                                                )
                                                                val formattedDls = if (hit.downloads >= 1000000) {
                                                                    "${String.format("%.2f", hit.downloads / 1000000f)}M"
                                                                } else {
                                                                    "${hit.downloads / 1000}K"
                                                                }
                                                                Text(text = formattedDls, color = SecondaryText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                            }

                                                            // Live Install or active confirmation trigger
                                                            Box(
                                                                modifier = Modifier
                                                                    .clip(RoundedCornerShape(8.dp))
                                                                    .background(if (isInstalled) CardActiveBg else Color(0xFF1BD964))
                                                                    .border(1.dp, if (isInstalled) BorderColor else Color(0xFF00E676), RoundedCornerShape(8.dp))
                                                                    .clickable {
                                                                        // Perform the live injection into launcher mods checklist!
                                                                        val existingIdx = modsList.indexOfFirst {
                                                                            it.first.equals(hit.title, ignoreCase = true) || it.first.startsWith(hit.title)
                                                                        }
                                                                        if (existingIdx >= 0) {
                                                                            // Toggle existing mod
                                                                            val toggledVal = !modsList[existingIdx].second
                                                                            val newList = modsList.toMutableList()
                                                                            newList[existingIdx] = modsList[existingIdx].first to toggledVal
                                                                            modsList = newList
                                                                            simulatedLogsList.add("[Mods] Toggle Modrinth package: ${hit.title} = $toggledVal")
                                                                            Toast.makeText(context, "${hit.title} toggled state: $toggledVal", Toast.LENGTH_SHORT).show()
                                                                        } else {
                                                                            // Install new mod in local checklist
                                                                            val newList = modsList.toMutableList()
                                                                            newList.add(hit.title to true)
                                                                            modsList = newList
                                                                            simulatedLogsList.add("[Mods] Installed & loaded Modrinth mod: ${hit.title} (v${hit.latestVersion ?: "latest"})")
                                                                            Toast.makeText(context, "${hit.title} synchronized with client!", Toast.LENGTH_SHORT).show()
                                                                        }
                                                                    }
                                                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Text(
                                                                    text = if (isInstalled) "INSTALLED" else "INSTALL",
                                                                    color = if (isInstalled) SecondaryText else Color.Black,
                                                                    fontSize = 9.sp,
                                                                    fontWeight = FontWeight.Black
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Confirm Confirm Actions footer row
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(38.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFF161619))
                                                    .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                                                    .clickable { activeDialog = "" },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = "CLOSE EXPLORER", color = SecondaryText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                "directory" -> {
                                    DurbinDirectoryDialogContent(
                                        context = context,
                                        onClose = { activeDialog = "" }
                                    )
                                }

                                "storage" -> {
                                    DurbinStorageDialogContent(
                                        context = context,
                                        cacheSizeMb = mockStorageCacheSizeMb,
                                        dirUsedGb = mockStorageDirUsedGb,
                                        onCleanCache = {
                                            if (mockStorageCacheSizeMb > 0) {
                                                mockStorageCacheSizeMb = 0
                                                mockStorageDirUsedGb = 1.0
                                                simulatedLogsList.add("[Storage] Cleaned client cache files indexes.")
                                                Toast.makeText(context, "Temporary game caches successfully cleaned!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Cache already fully sanitized.", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        onClose = { activeDialog = "" }
                                    )
                                }

                                "settings" -> {
                                    DurbinSettingsDialogContent(
                                        currentRamAlloc = currentRamAlloc,
                                        onRamResize = {
                                            currentRamAlloc = if (currentRamAlloc == "700 MB") "1200 MB" else "700 MB"
                                        },
                                        currentRenderer = currentRenderer,
                                        onRendererChange = {
                                            currentRenderer = if (currentRenderer == "MobileGlues") "VulkanMobile" else "MobileGlues"
                                        },
                                        onClose = { activeDialog = "" }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// CUSTOM COMPRESSED VIEWS FOR NESTED SPECS
// ==========================================
@Composable
fun QuickActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column {
            // Icon layout in concentric transparent box matching screenshot 2 shape
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardActiveBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "$title trigger icon indicator",
                    tint = AccentOrange,
                    modifier = Modifier.size(15.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = TextStyle(
                    color = PrimaryText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(1.dp))

            Text(
                text = subtitle,
                style = TextStyle(
                    color = MutedText,
                    fontSize = 11.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun NewsCard(
    badgeText: String,
    timeText: String,
    titleText: String,
    subtitleText: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // News star icon on left (Sleek bento style)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(CardActiveBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome, // star sparkling shape
                        contentDescription = "Highlight News Icon Sparkle",
                        tint = AccentOrange,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // All-caps News Type Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF1E1400))
                                .border(1.dp, AccentOrange.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 1f.dp)
                        ) {
                            Text(
                                text = badgeText,
                                style = TextStyle(
                                    color = AccentOrange,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Text(
                            text = timeText,
                            style = TextStyle(
                                color = MutedText,
                                fontSize = 9.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = titleText,
                        style = TextStyle(
                            color = PrimaryText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(1.dp))

                    Text(
                        text = subtitleText,
                        style = TextStyle(
                            color = SecondaryText,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Read article",
                tint = MutedText,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun SystemStatusCard(
    modifier: Modifier = Modifier,
    headerText: String,
    boldValue: String,
    icon: ImageVector
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .padding(11.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Little orange status dot at top right
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(AccentOrange)
                    .align(Alignment.TopEnd)
            )

            Column {
                Icon(
                    imageVector = icon,
                    contentDescription = "$headerText status icon indicator",
                    tint = SecondaryText,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = headerText,
                    style = TextStyle(
                        color = MutedText,
                        fontSize = 8.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = boldValue,
                    style = TextStyle(
                        color = PrimaryText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

// Custom sweep gradient rotating border modifier representing the beautiful AI Studio Build Chat glow
fun Modifier.geminiSweepBorder(
    rotation: Float,
    cornerRadius: Dp = 16.dp,
    borderWidth: Dp = 1.2.dp
): Modifier = this.drawWithContent {
    drawContent()

    val pxCorner = cornerRadius.toPx()
    val pxWidth = borderWidth.toPx()
    val centerPoint = Offset(size.width / 2f, size.height / 2f)

    // Vivid cosmic greens representing the beautiful high-contrast launcher styling
    val colors = listOf(
        Color(0xFF1BD964), // Neon Green
        Color(0xFF00E676), // Bright Green
        Color(0xFF163C1E), // Dark Moss Green
        Color(0xFF0D5325), // Forest Shaded Green
        Color(0xFF1BD964)  // Neon Green Loop
    )

    val nativeColors = colors.map { it.toArgb() }.toIntArray()
    val nativePositions = floatArrayOf(0.0f, 0.25f, 0.5f, 0.75f, 1.0f)
    val sweepShader = SweepGradient(
        centerPoint.x, centerPoint.y,
        nativeColors,
        nativePositions
    )

    val matrix = Matrix()
    matrix.postRotate(rotation, centerPoint.x, centerPoint.y)
    sweepShader.setLocalMatrix(matrix)

    val rotatedBrush = ShaderBrush(sweepShader)

    // Draw multiple smooth concentric glow shadow strokes
    for (i in 1..3) {
        val glowOuterWidth = pxWidth + (i * 2.5f)
        val alphaStrength = 0.12f / i
        drawRoundRect(
            brush = rotatedBrush,
            alpha = alphaStrength,
            topLeft = Offset(-glowOuterWidth / 2f, -glowOuterWidth / 2f),
            size = size.copy(
                width = size.width + glowOuterWidth,
                height = size.height + glowOuterWidth
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(pxCorner + glowOuterWidth / 2f),
            style = Stroke(width = glowOuterWidth)
        )
    }

    // Draw the sharp primary border stroke
    drawRoundRect(
        brush = rotatedBrush,
        topLeft = Offset(pxWidth / 2f, pxWidth / 2f),
        size = size.copy(
            width = size.width - pxWidth,
            height = size.height - pxWidth
        ),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(pxCorner - pxWidth / 2f),
        style = Stroke(width = pxWidth)
    )
}

// ==========================================
// MODRINTH API CONNECTOR MODEL & CLIENT
// ==========================================
data class ModrinthHit(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val iconUrl: String?,
    val downloads: Long,
    val follows: Long,
    val latestVersion: String?,
    val categories: List<String>,
    val loaders: List<String>,
    val dateModified: String
)

fun performModrinthSearch(
    query: String,
    version: String,
    loader: String,
    categories: List<String>,
    apiKey: String,
    onResult: (List<ModrinthHit>?, String?) -> Unit
) {
    val client = OkHttpClient()

    val selectedLoader = loader.lowercase()
    val facetsList = mutableListOf<String>()
    facetsList.add("\"project_type:mod\"")

    if (version.isNotEmpty()) {
        facetsList.add("\"versions:$version\"")
    }
    if (loader.isNotEmpty() && !loader.equals("any", ignoreCase = true)) {
        facetsList.add("\"loaders:$selectedLoader\"")
    }

    // Convert categories to facets
    for (cat in categories) {
        facetsList.add("\"categories:${cat.lowercase().replace(" ", "-")}\"")
    }

    val facetsJson = "[" + facetsList.joinToString(",") { "[$it]" } + "]"

    val encodedQuery = Uri.encode(query)
    val encodedFacets = Uri.encode(facetsJson)

    val url = "https://api.modrinth.com/v2/search?query=$encodedQuery&facets=$encodedFacets&limit=20"

    val requestBuilder = Request.Builder()
        .url(url)
        .addHeader("User-Agent", "DurbinLauncher/1.0 (iamcocotheking@gmail.com; custom)")

    if (apiKey.isNotEmpty()) {
        val authVal = if (apiKey.startsWith("Bearer ", ignoreCase = true) || apiKey.startsWith("Token ", ignoreCase = true)) {
            apiKey
        } else {
            "Token $apiKey"
        }
        requestBuilder.addHeader("Authorization", authVal)
    }

    val request = requestBuilder.build()

    client.newCall(request).enqueue(object : okhttp3.Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {
            onResult(null, e.localizedMessage ?: "Network connection failed")
        }

        override fun onResponse(call: okhttp3.Call, response: Response) {
            response.use { resp ->
                if (!resp.isSuccessful) {
                    onResult(null, "API returned code ${resp.code}: ${resp.message}")
                    return
                }

                try {
                    val bodyString = resp.body?.string() ?: ""
                    if (bodyString.isEmpty()) {
                        onResult(emptyList(), null)
                        return
                    }

                    val root = JSONObject(bodyString)
                    val hitsArr = root.getJSONArray("hits")
                    val results = mutableListOf<ModrinthHit>()

                    val possibleLoaders = listOf("fabric", "forge", "neoforge", "quilt", "liteloader", "canvas")

                    for (i in 0 until hitsArr.length()) {
                        val hit = hitsArr.getJSONObject(i)

                        // Parse categories list
                        val cats = mutableListOf<String>()
                        val catsArr = hit.optJSONArray("categories")
                        if (catsArr != null) {
                            for (j in 0 until catsArr.length()) {
                                cats.add(catsArr.getString(j))
                            }
                        }

                        // Parse loaders
                        val lds = mutableListOf<String>()
                        for (c in cats) {
                            if (possibleLoaders.contains(c.lowercase())) {
                                lds.add(c)
                            }
                        }

                        if (lds.isEmpty()) {
                            // fallback search
                            val displayCats = hit.optJSONArray("display_categories")
                            if (displayCats != null) {
                                for (k in 0 until displayCats.length()) {
                                    val catStr = displayCats.getString(k).lowercase()
                                    if (possibleLoaders.contains(catStr)) {
                                        lds.add(catStr)
                                    }
                                }
                            }
                        }

                        results.add(
                            ModrinthHit(
                                id = hit.optString("project_id"),
                                title = hit.optString("title"),
                                author = hit.optString("author"),
                                description = hit.optString("description"),
                                iconUrl = hit.optString("icon_url", null),
                                downloads = hit.optLong("downloads", 0L),
                                follows = hit.optLong("follows", 0L),
                                latestVersion = hit.optString("latest_version", null),
                                categories = cats,
                                loaders = lds,
                                dateModified = hit.optString("date_modified")
                            )
                        )
                    }
                    onResult(results, null)
                } catch (e: Exception) {
                    onResult(null, "Parsing error: ${e.localizedMessage}")
                }
            }
        }
    })
}

// ==========================================
// EXTRACED COMPOSE DIALOG COMPONENT METHODS
// (To prevent Kotlin compiler bytecode MethodTooLargeException)
// ==========================================
@Composable
fun DurbinDirectoryDialogContent(
    context: android.content.Context,
    onClose: () -> Unit
) {
    var currentPath by remember { mutableStateOf("/games/durbin") }

    // Mock sandbox file-tree hierarchy
    val filesList = when (currentPath) {
        "/games/durbin" -> listOf(
            Pair("config", "directory"),
            Pair("mods", "directory"),
            Pair("resourcepacks", "directory"),
            Pair("saves", "directory"),
            Pair("options.txt", "file (4.2 KB)"),
            Pair("launcher_profiles.json", "file (2.1 KB)")
        )
        "/games/durbin/config" -> listOf(
            Pair("modmenu.json", "file (1.2 KB)"),
            Pair("sodium-options.json", "file (0.8 KB)")
        )
        "/games/durbin/mods" -> listOf(
            Pair("Sodium-1.20.4.jar", "file (1.4 MB)"),
            Pair("Lithium-1.20.4.jar", "file (0.5 MB)"),
            Pair("FabricAPI.jar", "file (3.2 MB)")
        )
        "/games/durbin/resourcepacks" -> listOf(
            Pair("Faithful-128x.zip", "file (18.2 MB)"),
            Pair("BareBones.zip", "file (4.1 MB)")
        )
        "/games/durbin/saves" -> listOf(
            Pair("Epic Survival World", "directory"),
            Pair("Redstone Testing", "directory")
        )
        "/games/durbin/saves/Epic Survival World" -> listOf(
            Pair("level.dat", "file (16 KB)"),
            Pair("regions", "directory")
        )
        "/games/durbin/saves/Redstone Testing" -> listOf(
            Pair("level.dat", "file (14 KB)"),
            Pair("regions", "directory")
        )
        else -> emptyList()
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Sandbox Directory Explorer",
                style = TextStyle(color = PrimaryText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            )
            Text(
                text = currentPath,
                style = TextStyle(color = SecondaryText, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            )
        }
        if (currentPath != "/games/durbin") {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardBg)
                    .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                    .clickable {
                        // Safe parent folder back pointer
                        currentPath = if (currentPath.endsWith("/Epic Survival World") || currentPath.endsWith("/Redstone Testing")) {
                            "/games/durbin/saves"
                        } else {
                            "/games/durbin"
                        }
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(text = "BACK", color = Color(0xFF1BD964), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF070707))
            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
            .padding(6.dp)
    ) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(filesList) { item ->
                val name = item.first
                val type = item.second
                val isDir = type == "directory"

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isDir) Color(0xFF112215) else Color.Transparent)
                        .clickable {
                            if (isDir) {
                                currentPath = "$currentPath/$name"
                            } else {
                                Toast.makeText(context, "$name ($type) metadata selected.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .padding(vertical = 8.dp, horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isDir) Icons.Default.Folder else Icons.Default.Description,
                        contentDescription = if (isDir) "Subfolder" else "Data file",
                        tint = if (isDir) Color(0xFF1BD964) else SecondaryText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = name + (if (isDir) "/" else ""),
                            color = if (isDir) Color(0xFF1BD964) else PrimaryText,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isDir) FontWeight.Bold else FontWeight.Normal
                        )
                        if (!isDir) {
                            Text(
                                text = type,
                                color = MutedText,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CardBg)
                .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                .clickable {
                    Toast.makeText(context, "Scanning folder updates... complete.", Toast.LENGTH_SHORT).show()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "RE-INDEX", color = SecondaryText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF12341D))
                .border(1.dp, Color(0xFF1BD964), RoundedCornerShape(8.dp))
                .clickable {
                    onClose()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "CLOSE OVERLAY", color = Color(0xFF1BD964), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DurbinStorageDialogContent(
    context: android.content.Context,
    cacheSizeMb: Int,
    dirUsedGb: Double,
    onCleanCache: () -> Unit,
    onClose: () -> Unit
) {
    Text(
        text = "Client Storage Allocator",
        style = TextStyle(color = PrimaryText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(10.dp))
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CardBg)
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Client directory base:", color = SecondaryText, fontSize = 12.sp)
            Text(text = "/games/durbin/assets", color = PrimaryText, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CardBg)
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Java temporary cache size:", color = SecondaryText, fontSize = 12.sp)
            Text(text = "$cacheSizeMb MB", color = AccentOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CardBg)
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Total index content used:", color = SecondaryText, fontSize = 12.sp)
            Text(text = "$dirUsedGb GB", color = PrimaryText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }

    Spacer(modifier = Modifier.height(14.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AccentOrange)
            .clickable {
                onCleanCache()
                onClose()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = if (cacheSizeMb > 0) "CLEAN TEMPORARY CACHE" else "ALREADY SANITIZED", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
    }
}

@Composable
fun DurbinSettingsDialogContent(
    currentRamAlloc: String,
    onRamResize: () -> Unit,
    currentRenderer: String,
    onRendererChange: () -> Unit,
    onClose: () -> Unit
) {
    Text(
        text = "Client Settings presets",
        style = TextStyle(color = PrimaryText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(10.dp))
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CardBg)
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "RAM Allocator limit", color = PrimaryText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = "Current slider boundary", color = SecondaryText, fontSize = 11.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = currentRamAlloc,
                    color = AccentOrange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = { onRamResize() },
                    colors = ButtonDefaults.buttonColors(containerColor = StrongBorderColor),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(text = "Resize", color = PrimaryText, fontSize = 10.sp)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CardBg)
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Renderer Driver", color = PrimaryText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = "Graphics driver backend pipeline", color = SecondaryText, fontSize = 11.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(StrongBorderColor)
                    .clickable { onRendererChange() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(text = currentRenderer, color = PrimaryText, fontSize = 11.sp)
            }
        }
    }
    Spacer(modifier = Modifier.height(14.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(StrongBorderColor)
            .clickable { onClose() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = "CLOSE SETTINGS", color = PrimaryText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DurbinVersionsDialogContent(
    currentVersion: String,
    onVersionSelected: (String, String, String) -> Unit,
    onClose: () -> Unit
) {
    Text(
        text = "Select Client Version",
        style = TextStyle(color = PrimaryText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(12.dp))
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val versionsCatalog = listOf(
            Triple("Minecraft 1.20.4", "Vanilla profile • optimized for mobile", "Vanilla"),
            Triple("Minecraft 1.21-Pre1", "Fabric support • unstable test profile", "Fabric"),
            Triple("Minecraft 1.19.2", "Forge compatibility • legacy mods", "Forge"),
            Triple("Minecraft 1.16.5", "Optifine renderer • high frames preset", "OptiFine")
        )
        items(versionsCatalog) { item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (currentVersion == item.first) CardActiveBg else CardBg)
                    .border(
                        1.dp,
                        if (currentVersion == item.first) AccentOrange else BorderColor,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        onVersionSelected(item.first, item.second, item.third)
                        onClose()
                    }
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = item.first, color = PrimaryText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = item.second, color = SecondaryText, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}




