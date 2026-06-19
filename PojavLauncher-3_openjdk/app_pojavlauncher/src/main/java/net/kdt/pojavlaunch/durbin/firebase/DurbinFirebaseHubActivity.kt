package net.kdt.pojavlaunch.durbin.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Article
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import net.ashmeet.hyperlauncher.R
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.ui.theme.PojavTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DurbinFirebaseHubActivity : ComponentActivity() {
    private var firebaseReady by mutableStateOf(false)
    private var loading by mutableStateOf(true)
    private var errorMessage by mutableStateOf<String?>(null)
    private var currentUser by mutableStateOf<FirebaseUser?>(null)
    private var newsItems by mutableStateOf<List<DurbinNewsItem>>(emptyList())
    private var tierCategories by mutableStateOf<List<DurbinTierCategory>>(emptyList())
    private var myRanks by mutableStateOf<List<DurbinMyRank>>(emptyList())
    private var selectedTab by mutableIntStateOf(0)

    private val auth: FirebaseAuth? get() = runCatching { FirebaseAuth.getInstance() }.getOrNull()
    private val database: FirebaseDatabase? get() = runCatching { FirebaseDatabase.getInstance() }.getOrNull()

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken.isNullOrBlank()) {
                showToast("Google login failed: empty ID token")
                return@registerForActivityResult
            }
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth?.signInWithCredential(credential)?.addOnCompleteListener { signInTask ->
                if (signInTask.isSuccessful) {
                    currentUser = auth?.currentUser
                    saveUserProfile()
                    loadMyRanks()
                    showToast("Signed in as ${currentUser?.displayName ?: currentUser?.email ?: "Google account"}")
                } else {
                    showToast(signInTask.exception?.message ?: "Firebase login failed")
                }
            }
        } catch (e: Exception) {
            showToast(e.message ?: "Google login cancelled")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedTab = if (intent.getStringExtra(EXTRA_START_TAB) == TAB_TIER_LIST) 1 else 0
        firebaseReady = DurbinFirebaseConfig.ensureInitialized(this)
        currentUser = auth?.currentUser

        setContent {
            PojavTheme {
                DurbinFirebaseHubScreen(
                    firebaseReady = firebaseReady,
                    loading = loading,
                    errorMessage = errorMessage,
                    currentUser = currentUser,
                    selectedTab = selectedTab,
                    onTabChange = { selectedTab = it },
                    newsItems = newsItems,
                    tierCategories = tierCategories,
                    myRanks = myRanks,
                    onBack = { finish() },
                    onRefresh = { loadRemoteData() },
                    onSignIn = { startGoogleSignIn() },
                    onSignOut = { signOut() },
                    onOpenLink = { url -> Tools.openURL(this, url) }
                )
            }
        }

        if (firebaseReady) loadRemoteData() else {
            loading = false
            errorMessage = "Firebase is not configured yet. Add your Firebase values in res/values/durbin_firebase_config.xml."
        }
    }

    private fun loadRemoteData() {
        loading = true
        errorMessage = null
        loadNews()
        loadTierLists()
        loadMyRanks()
    }

    private fun loadNews() {
        val db = database ?: return
        db.getReference("durbin/news").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.map { child ->
                    DurbinNewsItem(
                        id = child.key ?: "",
                        title = child.child("title").getValue(String::class.java) ?: "Untitled",
                        body = child.child("body").getValue(String::class.java) ?: "",
                        imageUrl = child.child("imageUrl").getValue(String::class.java) ?: "",
                        linkUrl = child.child("linkUrl").getValue(String::class.java) ?: "",
                        tag = child.child("tag").getValue(String::class.java) ?: "News",
                        timestamp = child.child("timestamp").getValue(Long::class.java) ?: 0L,
                        pinned = child.child("pinned").getValue(Boolean::class.java) ?: false
                    )
                }.sortedWith(compareByDescending<DurbinNewsItem> { it.pinned }.thenByDescending { it.timestamp })
                newsItems = list
                loading = false
            }

            override fun onCancelled(error: DatabaseError) {
                loading = false
                errorMessage = error.message
            }
        })
    }

    private fun loadTierLists() {
        val db = database ?: return
        db.getReference("durbin/pvpTierLists").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = snapshot.children.map { category ->
                    val entries = category.child("entries").children.map { entry ->
                        DurbinTierEntry(
                            uid = entry.child("uid").getValue(String::class.java) ?: entry.key.orEmpty(),
                            ign = entry.child("ign").getValue(String::class.java) ?: "Unknown",
                            tier = entry.child("tier").getValue(String::class.java) ?: "Unranked",
                            score = entry.child("score").getValue(Int::class.java) ?: 0,
                            region = entry.child("region").getValue(String::class.java) ?: "",
                            country = entry.child("country").getValue(String::class.java) ?: "",
                            notes = entry.child("notes").getValue(String::class.java) ?: "",
                            verified = entry.child("verified").getValue(Boolean::class.java) ?: false,
                            updatedAt = entry.child("updatedAt").getValue(Long::class.java) ?: 0L
                        )
                    }.sortedWith(compareBy<DurbinTierEntry> { tierWeight(it.tier) }.thenByDescending { it.score })
                    DurbinTierCategory(
                        id = category.key ?: "",
                        name = category.child("name").getValue(String::class.java) ?: category.key.orEmpty(),
                        description = category.child("description").getValue(String::class.java) ?: "",
                        entries = entries
                    )
                }.sortedBy { it.name }
                tierCategories = categories
                loading = false
            }

            override fun onCancelled(error: DatabaseError) {
                loading = false
                errorMessage = error.message
            }
        })
    }

    private fun loadMyRanks() {
        val user = currentUser ?: run {
            myRanks = emptyList()
            return
        }
        val db = database ?: return
        db.getReference("durbin/userRanks/${user.uid}/ranks").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoriesById = tierCategories.associateBy { it.id }
                myRanks = snapshot.children.map { rank ->
                    val categoryId = rank.key ?: ""
                    DurbinMyRank(
                        categoryId = categoryId,
                        categoryName = rank.child("categoryName").getValue(String::class.java)
                            ?: categoriesById[categoryId]?.name
                            ?: categoryId,
                        tier = rank.child("tier").getValue(String::class.java) ?: "Unranked",
                        score = rank.child("score").getValue(Int::class.java) ?: 0,
                        ign = rank.child("ign").getValue(String::class.java) ?: "",
                        region = rank.child("region").getValue(String::class.java) ?: "",
                        updatedAt = rank.child("updatedAt").getValue(Long::class.java) ?: 0L
                    )
                }.sortedBy { tierWeight(it.tier) }
            }

            override fun onCancelled(error: DatabaseError) {
                errorMessage = error.message
            }
        })
    }

    private fun saveUserProfile() {
        val user = currentUser ?: return
        val db = database ?: return
        val data = mapOf(
            "displayName" to (user.displayName ?: ""),
            "email" to (user.email ?: ""),
            "photoUrl" to (user.photoUrl?.toString() ?: ""),
            "lastLogin" to System.currentTimeMillis()
        )
        db.getReference("durbin/users/${user.uid}").updateChildren(data)
    }

    private fun startGoogleSignIn() {
        if (!firebaseReady) {
            showToast("Firebase is not configured yet")
            return
        }
        if (!DurbinFirebaseConfig.isGoogleLoginConfigured(this)) {
            showToast("Paste your Firebase Web Client ID first")
            return
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.durbin_firebase_web_client_id))
            .requestEmail()
            .build()
        signInLauncher.launch(GoogleSignIn.getClient(this, gso).signInIntent)
    }

    private fun signOut() {
        auth?.signOut()
        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
        currentUser = null
        myRanks = emptyList()
        showToast("Signed out")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun tierWeight(tier: String): Int = when (tier.uppercase(Locale.US).replace(" ", "")) {
        "HT1" -> 1
        "LT1" -> 2
        "HT2" -> 3
        "LT2" -> 4
        "HT3" -> 5
        "LT3" -> 6
        "HT4" -> 7
        "LT4" -> 8
        "HT5" -> 9
        "LT5" -> 10
        else -> 99
    }

    companion object {
        const val EXTRA_START_TAB = "durbin_start_tab"
        const val TAB_NEWS = "news"
        const val TAB_TIER_LIST = "tier_list"

        fun newsIntent(context: android.content.Context): Intent =
            Intent(context, DurbinFirebaseHubActivity::class.java).putExtra(EXTRA_START_TAB, TAB_NEWS)

        fun tierListIntent(context: android.content.Context): Intent =
            Intent(context, DurbinFirebaseHubActivity::class.java).putExtra(EXTRA_START_TAB, TAB_TIER_LIST)
    }
}

@Composable
private fun DurbinFirebaseHubScreen(
    firebaseReady: Boolean,
    loading: Boolean,
    errorMessage: String?,
    currentUser: FirebaseUser?,
    selectedTab: Int,
    onTabChange: (Int) -> Unit,
    newsItems: List<DurbinNewsItem>,
    tierCategories: List<DurbinTierCategory>,
    myRanks: List<DurbinMyRank>,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    onOpenLink: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.screenHeightDp > configuration.screenWidthDp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .background(
                Brush.radialGradient(
                    colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.38f), Color.Transparent)
                )
            )
    ) {
        if (isPortrait) {
            Column(modifier = Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FirebaseHeader(currentUser, onBack, onRefresh, onSignIn, onSignOut)
                FirebaseTabs(selectedTab, onTabChange)
                FirebaseContent(firebaseReady, loading, errorMessage, selectedTab, newsItems, tierCategories, myRanks, currentUser, onOpenLink, Modifier.weight(1f))
            }
        } else {
            Row(modifier = Modifier.fillMaxSize().padding(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.width(330.dp).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    FirebaseHeader(currentUser, onBack, onRefresh, onSignIn, onSignOut, compact = true)
                    FirebaseTabs(selectedTab, onTabChange, vertical = true)
                    SetupHint(firebaseReady)
                    MyRanksPanel(currentUser, myRanks, Modifier.weight(1f))
                }
                FirebaseContent(firebaseReady, loading, errorMessage, selectedTab, newsItems, tierCategories, myRanks, currentUser, onOpenLink, Modifier.weight(1f).fillMaxHeight())
            }
        }
    }
}

@Composable
private fun FirebaseHeader(
    user: FirebaseUser?,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    compact: Boolean = false
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) }
            Column(modifier = Modifier.weight(1f)) {
                Text("DURBIN Firebase Hub", fontWeight = FontWeight.Black, fontSize = if (compact) 18.sp else 22.sp)
                Text("News + PvP Tier List", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            IconButton(onClick = onRefresh) { Icon(Icons.Rounded.Refresh, contentDescription = null) }
            if (user == null) {
                Button(onClick = onSignIn, shape = RoundedCornerShape(18.dp), contentPadding = PaddingValues(horizontal = 12.dp)) {
                    Icon(Icons.Rounded.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Google", fontWeight = FontWeight.Bold)
                }
            } else {
                OutlinedButton(onClick = onSignOut, shape = RoundedCornerShape(18.dp), contentPadding = PaddingValues(horizontal = 12.dp)) {
                    Icon(Icons.Rounded.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Sign out", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FirebaseTabs(selectedTab: Int, onTabChange: (Int) -> Unit, vertical: Boolean = false) {
    if (vertical) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TabButton("News", Icons.Rounded.Article, selectedTab == 0) { onTabChange(0) }
            TabButton("PvP Tier List", Icons.Rounded.EmojiEvents, selectedTab == 1) { onTabChange(1) }
            TabButton("My Rank", Icons.Rounded.AccountCircle, selectedTab == 2) { onTabChange(2) }
        }
    } else {
        TabRow(selectedTabIndex = selectedTab, containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.primary) {
            Tab(selected = selectedTab == 0, onClick = { onTabChange(0) }, text = { Text("News") }, icon = { Icon(Icons.Rounded.Article, null) })
            Tab(selected = selectedTab == 1, onClick = { onTabChange(1) }, text = { Text("Tier List") }, icon = { Icon(Icons.Rounded.EmojiEvents, null) })
            Tab(selected = selectedTab == 2, onClick = { onTabChange(2) }, text = { Text("My Rank") }, icon = { Icon(Icons.Rounded.AccountCircle, null) })
        }
    }
}

@Composable
private fun TabButton(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = if (selected) 0.9f else 0.24f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f)
        )
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun FirebaseContent(
    firebaseReady: Boolean,
    loading: Boolean,
    errorMessage: String?,
    selectedTab: Int,
    newsItems: List<DurbinNewsItem>,
    tierCategories: List<DurbinTierCategory>,
    myRanks: List<DurbinMyRank>,
    currentUser: FirebaseUser?,
    onOpenLink: (String) -> Unit,
    modifier: Modifier
) {
    GlassCard(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize().padding(14.dp)) {
            when {
                loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                !firebaseReady -> SetupMessage()
                errorMessage != null -> Text("Firebase error: $errorMessage", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                else -> AnimatedContent(targetState = selectedTab, label = "firebaseTab") { tab ->
                    when (tab) {
                        0 -> NewsList(newsItems, onOpenLink)
                        1 -> TierList(tierCategories)
                        else -> MyRankScreen(currentUser, myRanks)
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsList(items: List<DurbinNewsItem>, onOpenLink: (String) -> Unit) {
    if (items.isEmpty()) {
        EmptyState("No news yet", "Add news items in Firebase Realtime Database: durbin/news")
        return
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(items, key = { it.id }) { item ->
            GlassCard(borderAlpha = if (item.pinned) 0.65f else 0.22f) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Chip(item.tag)
                        if (item.pinned) Chip("PINNED")
                        Spacer(Modifier.weight(1f))
                        Text(formatDate(item.timestamp), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(item.title, fontSize = 19.sp, fontWeight = FontWeight.Black)
                    Text(item.body, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 6, overflow = TextOverflow.Ellipsis)
                    if (item.linkUrl.isNotBlank()) {
                        ElevatedButton(onClick = { onOpenLink(item.linkUrl) }, shape = RoundedCornerShape(16.dp)) {
                            Text("Open link", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TierList(categories: List<DurbinTierCategory>) {
    if (categories.isEmpty()) {
        EmptyState("No tier list yet", "Add categories like tank, nethpot, sword, crystal in Firebase.")
        return
    }
    var selectedCategory by androidx.compose.runtime.remember(categories) { mutableStateOf(categories.firstOrNull()?.id.orEmpty()) }
    val current = categories.firstOrNull { it.id == selectedCategory } ?: categories.first()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.forEach { category ->
                OutlinedButton(
                    onClick = { selectedCategory = category.id },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = if (category.id == selectedCategory) 0.8f else 0.2f)),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = if (category.id == selectedCategory) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f) else Color.Transparent)
                ) { Text(category.name, fontWeight = FontWeight.Bold) }
            }
        }
        Text(current.description.ifBlank { "${current.name} leaderboard" }, color = MaterialTheme.colorScheme.onSurfaceVariant)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(current.entries, key = { it.uid + it.ign }) { entry ->
                TierEntryRow(entry)
            }
        }
    }
}

@Composable
private fun TierEntryRow(entry: DurbinTierEntry) {
    GlassCard(borderAlpha = 0.18f) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TierBadge(entry.tier)
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(entry.ign, fontWeight = FontWeight.Black, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (entry.verified) Icon(Icons.Rounded.Verified, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                }
                Text(listOf(entry.region, entry.country).filter { it.isNotBlank() }.joinToString(" • ").ifBlank { "No region" }, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("${entry.score}", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun MyRankScreen(user: FirebaseUser?, ranks: List<DurbinMyRank>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (user == null) {
            EmptyState("Login required", "Use Google login to see your personal HT/LT ranks.")
            return@Column
        }
        Text("Signed in: ${user.displayName ?: user.email ?: "Google account"}", fontWeight = FontWeight.Bold)
        if (ranks.isEmpty()) {
            EmptyState("You are not ranked yet", "Ask a DURBIN admin to add your UID rank in Firebase.")
            return@Column
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(ranks, key = { it.categoryId }) { rank ->
                GlassCard {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TierBadge(rank.tier)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(rank.categoryName, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("IGN: ${rank.ign.ifBlank { "not set" }} • ${rank.region.ifBlank { "no region" }}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("${rank.score}", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun MyRanksPanel(user: FirebaseUser?, ranks: List<DurbinMyRank>, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("My Rank", fontWeight = FontWeight.Black, fontSize = 18.sp)
            if (user == null) {
                Text("Login with Google to show your HT/LT ranks.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            } else if (ranks.isEmpty()) {
                Text("No rank saved yet.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            } else {
                ranks.take(5).forEach { rank ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TierBadge(rank.tier, small = true)
                        Text(rank.categoryName, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@Composable
private fun SetupHint(firebaseReady: Boolean) {
    if (firebaseReady) return
    GlassCard(modifier = Modifier.fillMaxWidth(), borderAlpha = 0.32f) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Rounded.Security, null, tint = MaterialTheme.colorScheme.primary)
            Text("Firebase setup needed", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SetupMessage() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(Icons.Rounded.Security, contentDescription = null, modifier = Modifier.size(54.dp), tint = MaterialTheme.colorScheme.primary)
        Text("Firebase setup needed", fontWeight = FontWeight.Black, fontSize = 22.sp)
        Text("Open DURBIN_FIREBASE_SETUP.md and paste your Firebase config values.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EmptyState(title: String, body: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, fontWeight = FontWeight.Black, fontSize = 20.sp)
        Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun GlassCard(modifier: Modifier = Modifier, borderAlpha: Float = 0.25f, content: @Composable () -> Unit) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = borderAlpha)), RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.72f))
            .animateContentSize(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.70f),
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 6.dp
    ) { content() }
}

@Composable
private fun Chip(text: String) {
    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f), border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.30f))) {
        Text(text, modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp), fontWeight = FontWeight.Black, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun TierBadge(tier: String, small: Boolean = false) {
    val normalized = tier.uppercase(Locale.US).replace(" ", "")
    val color = when {
        normalized.contains("HT1") -> Color(0xFFFFD166)
        normalized.contains("LT1") -> Color(0xFFFF8FAB)
        normalized.contains("HT2") -> Color(0xFF80ED99)
        normalized.contains("LT2") -> Color(0xFF72DDF7)
        normalized.contains("HT3") -> Color(0xFFC77DFF)
        else -> MaterialTheme.colorScheme.primary
    }
    Surface(shape = RoundedCornerShape(if (small) 10.dp else 14.dp), color = color.copy(alpha = 0.18f), border = BorderStroke(1.dp, color.copy(alpha = 0.55f))) {
        Text(
            normalized.ifBlank { "--" },
            modifier = Modifier.padding(horizontal = if (small) 8.dp else 12.dp, vertical = if (small) 4.dp else 7.dp),
            fontWeight = FontWeight.Black,
            fontSize = if (small) 11.sp else 14.sp,
            color = color
        )
    }
}

private fun formatDate(timestamp: Long): String {
    if (timestamp <= 0L) return ""
    return try {
        SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date(timestamp))
    } catch (_: Exception) {
        ""
    }
}
