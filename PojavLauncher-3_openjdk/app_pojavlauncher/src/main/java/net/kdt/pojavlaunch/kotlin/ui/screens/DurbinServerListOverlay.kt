package net.kdt.pojavlaunch.ui.screens

import androidx.compose.foundation.border
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import java.net.URL
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.ashmeet.hyperlauncher.R
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.durbin.firebase.DurbinFirebaseConfig
import org.json.JSONObject
import java.net.URLEncoder
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream

private data class DurbinServerEntry(
    val id: String,
    val name: String,
    val ip: String,
    val motd: String,
    val iconUrl: String,
    val bannerUrl: String,
    val manualStatus: String,
    val featured: Boolean,
    val enabled: Boolean,
    val order: Int
)

private data class MinecraftServerLiveStatus(
    val loading: Boolean = true,
    val online: Boolean = false,
    val statusText: String = "CHECKING",
    val playersOnline: Int = -1,
    val playersMax: Int = -1
)


@Composable
fun DurbinServerListOverlay(onBack: () -> Unit) {
    BackHandler { onBack() }

    val context = LocalContext.current
    var loading by remember { mutableStateOf(true) }
    var status by remember { mutableStateOf("Loading servers...") }
    var servers by remember { mutableStateOf<List<DurbinServerEntry>>(emptyList()) }

    fun loadServers() {
        loading = true
        status = "Loading servers..."
        runCatching {
            if (!DurbinFirebaseConfig.ensureInitialized(context)) {
                loading = false
                status = "Firebase is not ready."
                return
            }

            FirebaseDatabase.getInstance(context.getString(R.string.durbin_firebase_database_url).trim())
                .getReference("durbin/servers")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list = snapshot.children.mapNotNull { child ->
                            val name = child.child("name").getValue(String::class.java).orEmpty().trim()
                            val ip = child.child("ip").getValue(String::class.java).orEmpty().trim()
                            if (name.isBlank() || ip.isBlank()) return@mapNotNull null

                            val rawStatus = child.child("status").getValue(String::class.java).orEmpty().trim()

                            DurbinServerEntry(
                                id = child.key ?: ip,
                                name = name,
                                ip = ip,
                                motd = child.child("motd").getValue(String::class.java).orEmpty(),
                                iconUrl = child.child("logoUrl").getValue(String::class.java).orEmpty().ifBlank {
                                    child.child("iconUrl").getValue(String::class.java).orEmpty()
                                },
                                bannerUrl = child.child("bannerUrl").getValue(String::class.java).orEmpty(),
                                manualStatus = rawStatus,
                                featured = child.child("featured").getValue(Boolean::class.java) ?: false,
                                enabled = child.child("enabled").getValue(Boolean::class.java) ?: true,
                                order = (child.child("order").value as? Number)?.toInt() ?: 0
                            )
                        }
                            .groupBy { normalizedServerKey(it) }
                            .map { (_, duplicates) ->
                                duplicates.sortedWith(
                                    compareByDescending<DurbinServerEntry> { it.enabled }
                                        .thenByDescending { it.featured }
                                        .thenBy { it.order }
                                        .thenBy { it.name.lowercase() }
                                ).first()
                            }
                            .sortedWith(compareByDescending<DurbinServerEntry> { it.featured }.thenBy { it.order }.thenBy { it.name.lowercase() })

                        servers = list
                        loading = false
                        status = if (list.isEmpty()) "No servers added yet." else "${list.size} servers • live API status"
                    }

                    override fun onCancelled(error: DatabaseError) {
                        loading = false
                        status = error.message
                    }
                })
        }.onFailure {
            loading = false
            status = it.message ?: "Could not load servers."
        }
    }

    LaunchedEffect(Unit) { loadServers() }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Black.copy(alpha = 0.34f),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Servers",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 30.sp,
                        modifier = Modifier.weight(1f)
                    )

                    if (loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 3.dp, color = MaterialTheme.colorScheme.primary)
                    } else {
                        Text(status, color = Color.White.copy(alpha = 0.70f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    ServerActionButton(
                        text = "Refresh",
                        icon = R.drawable.ic_px_refresh,
                        onClick = { loadServers() },
                        compact = true
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = Color.Black.copy(alpha = 0.40f),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
            ) {
                if (servers.isEmpty() && !loading) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(painter = painterResource(R.drawable.ic_px_server), contentDescription = null, tint = Color.White, modifier = Modifier.size(62.dp))
                        Spacer(Modifier.height(14.dp))
                        Text("No servers yet", color = Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(servers, key = { it.id }) { server ->
                            ServerCard(
                                server = server,
                                onSync = {
                                    val enabled = listOf(server).filter { it.enabled }
                                    runCatching {
                                        val result = writeMinecraftServersDat(enabled)
                                        Toast.makeText(context, result, Toast.LENGTH_LONG).show()
                                        status = result
                                    }.onFailure {
                                        Toast.makeText(context, it.message ?: "Sync failed", Toast.LENGTH_LONG).show()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServerCard(server: DurbinServerEntry, onSync: () -> Unit) {
    var liveStatus by remember(server.ip) { mutableStateOf(MinecraftServerLiveStatus()) }

    LaunchedEffect(server.ip, server.manualStatus, server.enabled) {
        liveStatus = MinecraftServerLiveStatus(loading = true, statusText = "CHECKING")
        liveStatus = withContext(Dispatchers.IO) {
            fetchMinecraftServerLiveStatus(server.ip, server.manualStatus, server.enabled)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF101010))
            .border(
                BorderStroke(
                    1.dp,
                    if (server.featured) MaterialTheme.colorScheme.primary.copy(alpha = 0.48f)
                    else Color.White.copy(alpha = 0.14f)
                ),
                RoundedCornerShape(18.dp)
            )
    ) {
        RemoteServerBanner(
            url = server.bannerUrl,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.88f),
                            Color.Black.copy(alpha = 0.50f),
                            Color.Black.copy(alpha = 0.26f)
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ServerLogoBox(
                logoUrl = server.iconUrl,
                serverName = server.name
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = server.name,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    ServerStatusBadge(
                        loading = liveStatus.loading,
                        online = liveStatus.online,
                        statusText = liveStatus.statusText,
                        playersOnline = liveStatus.playersOnline,
                        playersMax = liveStatus.playersMax
                    )
                    if (server.featured) {
                        Text("FEATURED", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black, fontSize = 10.sp)
                    }
                    if (!server.enabled) {
                        Text("OFF", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Black, fontSize = 10.sp)
                    }
                }
                Text(
                    text = server.ip,
                    color = Color.White.copy(alpha = 0.84f),
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (server.motd.isNotBlank()) {
                    Text(
                        text = server.motd,
                        color = Color.White.copy(alpha = 0.68f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            ServerActionButton(
                text = "PLAY",
                icon = R.drawable.ic_px_play,
                onClick = onSync,
                compact = true
            )
        }
    }
}

private fun fetchMinecraftServerLiveStatus(
    serverIp: String,
    manualStatus: String,
    enabled: Boolean
): MinecraftServerLiveStatus {
    if (!enabled) {
        return MinecraftServerLiveStatus(
            loading = false,
            online = false,
            statusText = "OFFLINE"
        )
    }

    if (manualStatus.equals("maintenance", ignoreCase = true)) {
        return MinecraftServerLiveStatus(
            loading = false,
            online = false,
            statusText = "MAINTENANCE"
        )
    }

    return runCatching {
        val encodedIp = URLEncoder.encode(serverIp.trim(), "UTF-8")
        val jsonText = URL("https://api.mcsrvstat.us/3/$encodedIp")
            .openConnection()
            .apply {
                connectTimeout = 5000
                readTimeout = 5000
                setRequestProperty("User-Agent", "DURBIN-Launcher")
            }
            .getInputStream()
            .bufferedReader()
            .use { it.readText() }

        val root = JSONObject(jsonText)
        val online = root.optBoolean("online", false)
        val players = root.optJSONObject("players")
        val playersOnline = players?.optInt("online", -1) ?: -1
        val playersMax = players?.optInt("max", -1) ?: -1

        MinecraftServerLiveStatus(
            loading = false,
            online = online,
            statusText = if (online) "ONLINE" else "OFFLINE",
            playersOnline = playersOnline,
            playersMax = playersMax
        )
    }.getOrElse {
        val cleanManualStatus = manualStatus.trim()
        val fallbackOnline = cleanManualStatus.equals("online", ignoreCase = true)
        val fallbackStatus = cleanManualStatus
            .takeIf { it.isNotBlank() && !it.equals("auto", ignoreCase = true) }
            ?.uppercase()
            ?: "STATUS N/A"

        MinecraftServerLiveStatus(
            loading = false,
            online = fallbackOnline,
            statusText = if (fallbackOnline) "ONLINE" else fallbackStatus
        )
    }
}



@Composable
private fun ServerStatusBadge(
    loading: Boolean,
    online: Boolean,
    statusText: String,
    playersOnline: Int,
    playersMax: Int
) {
    val playerText = if (online && playersOnline >= 0 && playersMax > 0) " ${playersOnline}/${playersMax}" else ""
    val label = when {
        loading -> "CHECKING"
        statusText.isNotBlank() -> statusText.uppercase() + playerText
        online -> "ONLINE$playerText"
        else -> "OFFLINE"
    }

    val color = when {
        loading -> MaterialTheme.colorScheme.primary
        online -> Color(0xFF35E884)
        label.contains("STATUS N/A") || label.contains("UNKNOWN") -> Color(0xFFFFC857)
        label.contains("MAINTENANCE") -> Color(0xFFFFC857)
        else -> Color(0xFFFF5C66)
    }

    Surface(
        color = color.copy(alpha = 0.18f),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.36f))
    ) {
        Text(
            text = label,
            color = color,
            fontWeight = FontWeight.Black,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}



@Composable
private fun ServerLogoBox(logoUrl: String, serverName: String) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.Black.copy(alpha = 0.42f))
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        RemoteServerLogo(
            url = logoUrl,
            fallbackName = serverName,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun RemoteServerLogo(url: String, fallbackName: String, modifier: Modifier = Modifier) {
    var bitmap: Bitmap? by remember(url) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(url) {
        bitmap = null
        if (url.isBlank()) return@LaunchedEffect

        bitmap = withContext(Dispatchers.IO) {
            runCatching {
                URL(url).openStream().use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            }.getOrNull()
        }
    }

    val loaded = bitmap
    if (loaded != null) {
        Image(
            bitmap = loaded.asImageBitmap(),
            contentDescription = fallbackName,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Icon(
            painter = painterResource(R.drawable.ic_px_server),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun RemoteServerBanner(url: String, modifier: Modifier = Modifier) {
    var bitmap: Bitmap? by remember(url) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(url) {
        bitmap = null
        if (url.isBlank()) return@LaunchedEffect

        bitmap = withContext(Dispatchers.IO) {
            runCatching {
                URL(url).openStream().use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            }.getOrNull()
        }
    }

    val loaded = bitmap
    if (loaded != null) {
        Image(
            bitmap = loaded.asImageBitmap(),
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier.background(
                Brush.horizontalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.28f),
                        Color(0xFF151515),
                        Color.Black
                    )
                )
            )
        )
    }
}

@Composable
private fun ServerActionButton(
    text: String,
    icon: Int,
    onClick: () -> Unit,
    compact: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 90),
        label = "serverButtonScale"
    )

    OutlinedButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .scale(scale)
            .height(if (compact) 44.dp else 54.dp)
            .then(if (compact) Modifier.widthIn(min = 108.dp) else Modifier.fillMaxWidth()),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Black.copy(alpha = 0.26f), contentColor = Color.White),
        contentPadding = PaddingValues(horizontal = 14.dp)
    ) {
        Icon(painter = painterResource(icon), contentDescription = null, tint = Color.White, modifier = Modifier.size(if (compact) 18.dp else 20.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, color = Color.White, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

private fun normalizedServerKey(server: DurbinServerEntry): String {
    val ipKey = server.ip
        .trim()
        .lowercase()
        .removePrefix("minecraft://")
        .removePrefix("mc://")
        .removeSuffix("/")

    return ipKey.ifBlank { server.name.trim().lowercase() }
}

private fun writeMinecraftServersDat(servers: List<DurbinServerEntry>): String {
    val clean = servers.filter { it.enabled && it.name.isNotBlank() && it.ip.isNotBlank() }
    if (clean.isEmpty()) return "No enabled servers to sync."

    val gameDir = File(Tools.DIR_GAME_NEW)
    if (!gameDir.exists()) gameDir.mkdirs()

    val file = File(gameDir, "servers.dat")
    if (file.exists()) {
        runCatching { file.copyTo(File(gameDir, "servers.dat.durbin_backup"), overwrite = true) }
    }

    DataOutputStream(BufferedOutputStream(FileOutputStream(file))).use { out ->
        out.writeByte(10) // TAG_Compound
        writeNbtString(out, "") // root name

        out.writeByte(9) // TAG_List
        writeNbtString(out, "servers")
        out.writeByte(10) // list type TAG_Compound
        out.writeInt(clean.size)

        clean.forEach { server ->
            out.writeByte(8) // TAG_String
            writeNbtString(out, "name")
            writeNbtString(out, server.name)

            out.writeByte(8) // TAG_String
            writeNbtString(out, "ip")
            writeNbtString(out, server.ip)

            out.writeByte(1) // TAG_Byte
            writeNbtString(out, "hideAddress")
            out.writeByte(0)

            out.writeByte(0) // TAG_End compound
        }

        out.writeByte(0) // TAG_End root
    }

    return "Synced ${clean.size} servers into Minecraft server list."
}

private fun writeNbtString(out: DataOutputStream, value: String) {
    val bytes = value.toByteArray(Charsets.UTF_8)
    out.writeShort(bytes.size)
    out.write(bytes)
}
