package net.kdt.pojavlaunch.ui.screens

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
    val featured: Boolean,
    val enabled: Boolean,
    val order: Int
)

@Composable
fun DurbinServerListOverlay(onBack: () -> Unit) {
    BackHandler { onBack() }

    val context = LocalContext.current
    var loading by remember { mutableStateOf(true) }
    var status by remember { mutableStateOf("Loading server list...") }
    var servers by remember { mutableStateOf<List<DurbinServerEntry>>(emptyList()) }

    fun loadServers() {
        loading = true
        status = "Loading server list..."
        runCatching {
            if (!DurbinFirebaseConfig.ensureInitialized(context)) {
                loading = false
                status = "Firebase config is not ready."
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

                            DurbinServerEntry(
                                id = child.key ?: ip,
                                name = name,
                                ip = ip,
                                motd = child.child("motd").getValue(String::class.java).orEmpty(),
                                iconUrl = child.child("iconUrl").getValue(String::class.java).orEmpty(),
                                featured = child.child("featured").getValue(Boolean::class.java) ?: false,
                                enabled = child.child("enabled").getValue(Boolean::class.java) ?: true,
                                order = (child.child("order").value as? Number)?.toInt() ?: 0
                            )
                        }.sortedWith(compareByDescending<DurbinServerEntry> { it.featured }.thenBy { it.order }.thenBy { it.name.lowercase() })

                        servers = list
                        loading = false
                        status = if (list.isEmpty()) "No servers added from dashboard yet." else "Loaded ${list.size} servers from dashboard."
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

    LaunchedEffect(servers) {
        val enabled = servers.filter { it.enabled }
        if (enabled.isNotEmpty()) {
            val result = withContext(Dispatchers.IO) { writeMinecraftServersDat(enabled) }
            status = result
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(0.82f)
                    .fillMaxHeight(),
                color = Color.Black.copy(alpha = 0.34f),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Server List", color = Color.White, fontWeight = FontWeight.Black, fontSize = 30.sp)
                    Text("Add servers from the dashboard. This page syncs them into Minecraft automatically.", color = Color.White.copy(alpha = 0.72f), fontWeight = FontWeight.Bold)
                    ServerActionButton(
                        text = "Refresh + Auto Sync",
                        icon = R.drawable.ic_px_refresh,
                        onClick = { loadServers() }
                    )
                    ServerActionButton(
                        text = "Back Home",
                        icon = R.drawable.ic_px_home,
                        onClick = onBack
                    )
                    AnimatedVisibility(visible = loading) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 3.dp)
                            Text("Loading...", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(status, color = Color.White.copy(alpha = 0.76f), fontWeight = FontWeight.Bold)
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1.55f)
                    .fillMaxHeight(),
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
                        Text("Open the backend dashboard and add your first server.", color = Color.White.copy(alpha = 0.70f), fontWeight = FontWeight.Bold)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(servers, key = { it.id }) { server ->
                            ServerCard(
                                server = server,
                                onSync = {
                                    val enabled = servers.filter { it.enabled }
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
    Surface(
        color = Color(0xFF0D0D0D).copy(alpha = 0.74f),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, if (server.featured) MaterialTheme.colorScheme.primary.copy(alpha = 0.45f) else Color.White.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(painter = painterResource(R.drawable.ic_px_server), contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(server.name, color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (server.featured) Text("FEATURED", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black, fontSize = 10.sp)
                    if (!server.enabled) Text("OFF", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Black, fontSize = 10.sp)
                }
                Text(server.ip, color = Color.White.copy(alpha = 0.78f), fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (server.motd.isNotBlank()) {
                    Text(server.motd, color = Color.White.copy(alpha = 0.60f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            ServerActionButton(
                text = "Sync",
                icon = R.drawable.ic_px_download,
                onClick = onSync,
                compact = true
            )
        }
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
