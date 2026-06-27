package net.kdt.pojavlaunch.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.rotate
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kdt.mcgui.ProgressLayout
import kotlinx.coroutines.delay
import net.ashmeet.hyperlauncher.R
import net.kdt.pojavlaunch.BaseActivity
import net.kdt.pojavlaunch.authenticator.AuthType
import net.kdt.pojavlaunch.authenticator.accounts.Accounts
import net.kdt.pojavlaunch.authenticator.accounts.MinecraftAccount
import net.kdt.pojavlaunch.authenticator.listener.LoginListener
import net.kdt.pojavlaunch.extra.ExtraConstants
import net.kdt.pojavlaunch.extra.ExtraCore
import net.kdt.pojavlaunch.extra.ExtraListener
import net.kdt.pojavlaunch.fragments.MainMenuFragment
import net.kdt.pojavlaunch.prefs.LauncherPreferences
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper
import net.kdt.pojavlaunch.progresskeeper.ProgressListener
import net.kdt.pojavlaunch.ui.theme.PojavTheme
import net.kdt.pojavlaunch.PojavApplication
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.durbin.DurbinClientInstaller
import net.kdt.pojavlaunch.kotlin.ui.viewmodel.ContentInstallerViewModel
import net.kdt.pojavlaunch.kotlin.ui.viewmodel.DirectoryManagerViewModel
import net.kdt.pojavlaunch.skin.AndroidSkinAnalyzer
import net.kdt.pojavlaunch.skin.LocalUuidUtils
import net.kdt.pojavlaunch.skin.LocalUuidUtils.toFormattedUuid
import net.kdt.pojavlaunch.skin.SkinModelType
import net.kdt.pojavlaunch.skin.SkinUtils
import net.kdt.pojavlaunch.utils.UpdateUtils
import java.io.File

private val m3MotionSpec = spring<Float>(
    dampingRatio = 0.8f,
    stiffness = 380f
)

private val m3SizeSpec = spring<IntSize>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMediumLow
)

@Composable
fun getTransitionSpec(): AnimatedContentTransitionScope<*>.() -> ContentTransform {
    val animPreset = LauncherPreferences.PREF_TRANSITION_ANIMATION_STATE.value
    val animDuration = LauncherPreferences.PREF_TRANSITION_DURATION_STATE.value
    val animIntensity = LauncherPreferences.PREF_TRANSITION_INTENSITY_STATE.value

    return {
        when (animPreset) {
            "fade" -> {
                fadeIn(animationSpec = tween(animDuration)) togetherWith fadeOut(animationSpec = tween(animDuration))
            }
            "bounce" -> {
                (slideInVertically(
                    initialOffsetY = { -(it * animIntensity).toInt() },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
                ) + fadeIn(animationSpec = tween(animDuration))).togetherWith(
                    slideOutVertically(targetOffsetY = { (it * animIntensity).toInt() }) + fadeOut(animationSpec = tween(animDuration))
                )
            }
            else -> {
                (fadeIn(animationSpec = tween(240)) + scaleIn(initialScale = 0.96f, animationSpec = tween(240)))
                    .togetherWith(fadeOut(animationSpec = tween(160)) + scaleOut(targetScale = 0.985f, animationSpec = tween(160)))
            }
        }
    }
}

@Composable
fun LauncherBackground() {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val backgroundPath = LauncherPreferences.PREF_BACKGROUND_PATH_STATE.value
    val backgroundTransparency = LauncherPreferences.PREF_BACKGROUND_TRANSPARENCY_STATE.value
    val backgroundBlurEnabled = LauncherPreferences.PREF_BACKGROUND_BLUR_ENABLED_STATE.value
    val backgroundBlurIntensity = LauncherPreferences.PREF_BACKGROUND_BLUR_STATE.value

    val backgroundImage = remember(backgroundPath) {
        if (backgroundPath != null) {
            try {
                if (backgroundPath.startsWith("content://")) {
                    context.contentResolver.openInputStream(Uri.parse(backgroundPath))?.use {
                        BitmapFactory.decodeStream(it)?.asImageBitmap()
                    }
                } else {
                    BitmapFactory.decodeFile(backgroundPath)?.asImageBitmap()
                }
            } catch (e: Exception) {
                null
            }
        } else null
    }

    val previewBackgroundBitmap = if (isPreview) BaseActivity.getBackgroundBitmap() else null

    Box(modifier = Modifier.fillMaxSize()) {
        if (backgroundImage != null) {
            Image(
                bitmap = backgroundImage,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (backgroundBlurEnabled) Modifier.blur((backgroundBlurIntensity * 40).dp) else Modifier),
                contentScale = ContentScale.Crop
            )
        } else if (isPreview && previewBackgroundBitmap != null) {
            Image(
                bitmap = previewBackgroundBitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
        }

        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = backgroundTransparency)))
    }
}

class TaskProgress(
    val key: String,
    initialProgress: Int = 0,
    initialText: String = ""
) {
    var progress by mutableIntStateOf(initialProgress)
    var text by mutableStateOf(initialText)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskProgressItem(task: TaskProgress) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        @Suppress("DEPRECATION")
        @SuppressLint("LocalContextGetResourceValueCall")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task.text,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (task.progress >= 0) {
                Text(
                    text = "${task.progress}%",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        if (task.progress >= 0) {
            LinearProgressIndicator(
                progress = { task.progress.toFloat() / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        } else {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}

@Composable
fun ProgressCard(
    modifier: Modifier = Modifier
) {
    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current

    val activeTasks = remember { mutableStateMapOf<String, TaskProgress>() }
    var isExpanded by remember { mutableStateOf(true) }

    if (!isPreview) {
        DisposableEffect(Unit) {
            val keys = arrayOf(
                ProgressLayout.UNPACK_RUNTIME, ProgressLayout.DOWNLOAD_MINECRAFT,
                ProgressLayout.DOWNLOAD_VERSION_LIST, ProgressLayout.AUTHENTICATE,
                ProgressLayout.INSTALL_MODPACK, ProgressLayout.EXTRACT_COMPONENTS,
                ProgressLayout.EXTRACT_SINGLE_FILES, ProgressLayout.INSTANCE_INSTALL,
                ProgressLayout.CONTENT_INSTALL
            )

            val listeners = keys.map { key ->
                val listener = object : ProgressListener {
                    override fun onProgressStarted() {
                        (context as? FragmentActivity)?.runOnUiThread {
                            activeTasks[key] = TaskProgress(key)
                        }
                    }

                    @SuppressLint("LocalContextGetResourceValueCall")
                    override fun onProgressUpdated(progress: Int, resid: Int, vararg va: Any?) {
                        (context as? FragmentActivity)?.runOnUiThread {
                            val task = activeTasks.getOrPut(key) { TaskProgress(key) }
                            task.progress = progress
                            task.text = if (resid > 0) context.getString(resid, *va)
                                       else if (va.isNotEmpty() && va[0] != null) va[0].toString()
                                       else ""
                        }
                    }

                    override fun onProgressEnded() {
                        (context as? FragmentActivity)?.runOnUiThread {
                            activeTasks.remove(key)
                        }
                    }
                }
                ProgressKeeper.addListener(key, listener)
                key to listener
            }

            onDispose {
                listeners.forEach { (key, listener) ->
                    ProgressKeeper.removeListener(key, listener)
                }
            }
        }
    } else {
        LaunchedEffect(Unit) {
            activeTasks["dl"] = TaskProgress("dl", 45, "Downloading Assets...")
            activeTasks["auth"] = TaskProgress("auth", 90, "Authenticating...")
        }
    }

    if (activeTasks.isEmpty() && !isPreview) return

    ElevatedCard(
        modifier = modifier
            .width(dimensionResource(id = R.dimen._280sdp))
            .animateContentSize(animationSpec = m3SizeSpec),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_px_progress),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                @Suppress("DEPRECATION")
                Text(
                    text = stringResource(id = R.string.progresslayout_tasks_in_progress, activeTasks.size),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Icon(
                    painter = painterResource(id = R.drawable.spinner_arrow),
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .alpha(0.6f),
                    tint = Color.White
                )
            }

            @Suppress("DEPRECATION")
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(spring(stiffness = Spring.StiffnessLow)) + expandVertically(spring(stiffness = Spring.StiffnessLow)),
                exit = fadeOut(spring(stiffness = Spring.StiffnessLow)) + shrinkVertically(spring(stiffness = Spring.StiffnessLow))
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                        .heightIn(max = 300.dp),
                    contentPadding = PaddingValues(top = 4.dp)
                ) {
                    items(activeTasks.values.toList(), key = { it.key }) { task ->
                        TaskProgressItem(task)
                    }
                }
            }
        }
    }
}

@Composable
fun AccountSelector(
    accounts: List<MinecraftAccount>,
    currentAccount: MinecraftAccount?,
    onAccountSelect: (MinecraftAccount) -> Unit,
    onAccountDelete: (MinecraftAccount) -> Unit,
    topBarHeight: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var accountToDelete by remember { mutableStateOf<MinecraftAccount?>(null) }

    if (accountToDelete != null) {
        AlertDialog(
            onDismissRequest = { accountToDelete = null },
            confirmButton = {
                Button(
                    onClick = {
                        accountToDelete?.let { onAccountDelete(it) }
                        accountToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    @Suppress("DEPRECATION")
                    Text(stringResource(id = R.string.global_delete))
                }
            },
            dismissButton = {
                @Suppress("DEPRECATION")
                TextButton(onClick = { accountToDelete = null }) {
                    Text(stringResource(id = android.R.string.cancel))
                }
            },
            title = { Text(stringResource(id = R.string.global_error)) },
            text = { Text(stringResource(id = R.string.warning_remove_account)) }
        )
    }

    Box(modifier = modifier) {
        val currentHead by SkinUtils.rememberSkinHead(currentAccount)

        FilledTonalButton(
            onClick = { expanded = true },
            modifier = Modifier
                .height(42.dp)
                .wrapContentWidth()
                .padding(horizontal = 3.dp),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 13.dp)
        ) {
            if (currentHead != null) {
                Image(
                    bitmap = currentHead!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_px_home),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(10.dp))

            @Suppress("DEPRECATION")
            Text(
                text = currentAccount?.username ?: "Steve",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.width(6.dp))

            Icon(
                painter = painterResource(id = R.drawable.ic_px_alt_sliders),
                contentDescription = null,
                modifier = Modifier.size(16.dp).alpha(0.5f)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(280.dp)
                .background(Color(0xFF101010))
        ) {
            accounts.forEach { account ->
                DropdownMenuItem(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    text = {
                        @Suppress("DEPRECATION")
                        Text(
                            account.username,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    onClick = {
                        onAccountSelect(account)
                        expanded = false
                    },
                    leadingIcon = {
                        val head by SkinUtils.rememberSkinHead(account)
                        if (head != null) {
                            Image(
                                bitmap = head!!.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        } else {
                            Icon(painterResource(id = R.drawable.ic_px_home), null, Modifier.size(28.dp), tint = Color.White)
                        }
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val authIcon = account.authType.iconResource
                            if (authIcon != 0) {
                                Icon(painterResource(id = authIcon), null, Modifier.size(20.dp), tint = Color.Unspecified)
                            }
                            Spacer(Modifier.width(12.dp))
                            IconButton(
                                onClick = {
                                    accountToDelete = account
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_px_trash),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            DropdownMenuItem(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                text = { Text("Add Account", color = Color.White, fontSize = 16.sp) },
                onClick = {
                    expanded = false
                    ExtraCore.setValue(ExtraConstants.SELECT_AUTH_METHOD, true)
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                }
            )
        }
    }
}

@Composable
fun TopBarButton(
    onClick: () -> Unit,
    icon: Int,
    label: String,
    topBarHeight: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isSpecialActive: Boolean = false,
    badgeCount: Int = 0
) {
    val defaultContainerColor = Color.Transparent
    val activeColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)

    val finalContainerColor = if (isSelected || isSpecialActive) activeColor else defaultContainerColor

    Box(modifier = modifier) {
        FilledTonalButton(
            onClick = onClick,
            modifier = Modifier
                .height(40.dp)
                .padding(horizontal = 3.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = finalContainerColor,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 13.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                modifier = Modifier.size(18.dp)
            )

            @Suppress("DEPRECATION")
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn(m3MotionSpec) + expandHorizontally(expandFrom = Alignment.Start, clip = false, animationSpec = m3SizeSpec),
                exit = fadeOut(m3MotionSpec) + shrinkHorizontally(shrinkTowards = Alignment.Start, clip = false, animationSpec = m3SizeSpec)
            ) {
                Row {
                    Spacer(Modifier.width(8.dp))
                    @Suppress("DEPRECATION")
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Clip
                    )
                }
            }
        }

        if (badgeCount > 0) {
            Badge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                @Suppress("DEPRECATION")
                Text(badgeCount.toString(), fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun TopBar(
    topBarHeight: androidx.compose.ui.unit.Dp,
    ignoreNotch: Boolean,
    hasBackground: Boolean,
    isAnyScreenOpen: Boolean,
    isProgressVisible: Boolean,
    taskCount: Int,
    selectedCategory: Int,
    accounts: List<MinecraftAccount>,
    currentAccount: MinecraftAccount?,
    onAccountSelect: (MinecraftAccount) -> Unit,
    onAccountDelete: (MinecraftAccount) -> Unit,
    onHomeClick: () -> Unit,
    onProgressClick: () -> Unit,
    onCategoryClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(topBarHeight)
            .background(Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .run {
                    if (ignoreNotch) this
                    else windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                },
            verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.padding(start = 8.dp)) {
                AnimatedContent(
                    targetState = isAnyScreenOpen,
                    transitionSpec = {
                        (fadeIn(animationSpec = m3MotionSpec))
                        .togetherWith(fadeOut(animationSpec = m3MotionSpec))
                    },
                    label = "homeSwitch"
                ) { targetAnyOpen ->
                    if (targetAnyOpen) {
                        TopBarButton(
                            onClick = { onHomeClick() },
                            icon = R.drawable.ic_px_home,
                            label = "Home",
                            topBarHeight = topBarHeight,
                            isSelected = true
                        )
                    } else {
                        AccountSelector(
                            accounts = accounts,
                            currentAccount = currentAccount,
                            onAccountSelect = onAccountSelect,
                            onAccountDelete = onAccountDelete,
                            topBarHeight = topBarHeight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .animateContentSize(animationSpec = m3SizeSpec),
                verticalAlignment = Alignment.CenterVertically) {
                TopBarButton(
                    onClick = onProgressClick,
                    isSelected = isProgressVisible,
                    isSpecialActive = taskCount > 0 && !isProgressVisible,
                    badgeCount = taskCount,
                    icon = R.drawable.ic_px_progress,
                    label = "Tasks",
                    topBarHeight = topBarHeight
                )

                TopBarButton(
                    onClick = { onCategoryClick(4) },
                    isSelected = selectedCategory == 4,
                    icon = R.drawable.ic_px_server,
                    label = "Servers",
                    topBarHeight = topBarHeight
                )

                TopBarButton(
                    onClick = { onCategoryClick(1) },
                    isSelected = selectedCategory == 1,
                    icon = R.drawable.ic_px_folder,
                    label = "Files",
                    topBarHeight = topBarHeight
                )

                TopBarButton(
                    onClick = { onCategoryClick(2) },
                    isSelected = selectedCategory == 2,
                    icon = R.drawable.ic_px_download,
                    label = "Addons",
                    topBarHeight = topBarHeight
                )

                TopBarButton(
                    onClick = { onCategoryClick(5) },
                    isSelected = selectedCategory == 5,
                    icon = R.drawable.ic_px_download,
                    label = "DURBIN",
                    topBarHeight = topBarHeight
                )

                TopBarButton(
                    onClick = { onCategoryClick(3) },
                    isSelected = selectedCategory == 3,
                    icon = R.drawable.ic_px_alt_sliders,
                    label = "Settings",
                    topBarHeight = topBarHeight
                )
            }
        }
    }
}

@Composable
fun LauncherScreen(
    onHomeRequest: () -> Unit,
    onProgressClick: () -> Unit,
    isProgressVisible: Boolean,
    taskCount: Int,
    isFragmentOpen: Boolean = false
) {
    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current
    val topBarHeight = 46.dp
    val ignoreNotch = remember { if (isPreview) true else LauncherPreferences.PREF_IGNORE_NOTCH }

    val backgroundPath = LauncherPreferences.PREF_BACKGROUND_PATH_STATE.value
    val hasBackground = backgroundPath != null || isPreview

    var selectedCategory by rememberSaveable { mutableIntStateOf(-1) }

    val isAnyScreenOpen by remember(selectedCategory, isFragmentOpen) {
        derivedStateOf { selectedCategory != -1 || isFragmentOpen }
    }

    var accounts by remember { mutableStateOf<List<MinecraftAccount>>(emptyList()) }
    var currentAccount by remember { mutableStateOf(if (isPreview) null else Accounts.getCurrent()) }

    var updateInfo by remember { mutableStateOf<UpdateUtils.UpdateInfo?>(null) }
    var showUpdateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!isPreview && !LauncherPreferences.PREF_SKIP_UPDATE_CHECK) {
            val info = UpdateUtils.checkForUpdates()
            if (info != null && info.hasUpdate) {
                updateInfo = info
                showUpdateDialog = true
            }
        }
    }

    if (showUpdateDialog && updateInfo != null) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            ElevatedCard(
                modifier = Modifier.width(320.dp).animateContentSize(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Update Available", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("A new version (${updateInfo!!.latestVersion}) is available!", style = MaterialTheme.typography.bodyLarge)

                    Spacer(Modifier.height(12.dp))
                    Text("Changelog:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                    Box(modifier = Modifier
                        .heightIn(max = 120.dp)
                        .verticalScroll(rememberScrollState())
                    ) {
                        Text(updateInfo!!.changelog, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = {
                            LauncherPreferences.PREF_SKIP_UPDATE_CHECK = true
                            LauncherPreferences.DEFAULT_PREF?.edit()?.putBoolean(LauncherPreferences.PREF_KEY_SKIP_UPDATE_CHECK, true)?.apply()
                            showUpdateDialog = false
                        }) {
                            Text("NEVER")
                        }
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick = { showUpdateDialog = false }) {
                            Text("LATER")
                        }
                        Button(onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateInfo!!.updateUrl))
                            context.startActivity(intent)
                            showUpdateDialog = false
                        }) {
                            Text("UPDATE")
                        }
                    }
                }
            }
        }
    }

    val refreshAccountsList = {
        try {
            val loadedAccounts = Accounts.load().accounts.filterNotNull()
            accounts = loadedAccounts
            currentAccount = Accounts.getCurrent()

            loadedAccounts.forEach { account ->
                if (account.getSkinFace() == null) {
                    PojavApplication.sExecutorService.execute {
                        account.updateSkinFace(context.assets)
                        (context as? FragmentActivity)?.runOnUiThread {

                             accounts = Accounts.load().accounts.filterNotNull()
                             currentAccount = Accounts.getCurrent()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            accounts = emptyList()
        }
    }

    val loginListener = remember {
        object : LoginListener {
            override fun onLoginDone(account: MinecraftAccount) {
                Accounts.setCurrent(account)
                refreshAccountsList()
                ExtraCore.setValue(ExtraConstants.REFRESH_ACCOUNT_SPINNER, true)
            }

            override fun onLoginError(errorMessage: Throwable) {
                Toast.makeText(context, errorMessage.message ?: "Login failed", Toast.LENGTH_LONG).show()
            }

            override fun onLoginProgress(step: Int) {}
            override fun setMaxLoginProgress(max: Int) {}
        }
    }

    val mojangListener = remember {
        ExtraListener<Array<String?>> { _, value ->
            try {
                val username = value.getOrNull(0) ?: "Steve"
                val skinPath = value.getOrNull(2)
                val capePath = value.getOrNull(3)

                val skinModel = if (skinPath != null) {
                    AndroidSkinAnalyzer.detectModel(File(skinPath).readBytes())
                } else SkinModelType.STEVE

                val acc = Accounts.create {
                    it.username = username
                    it.authType = AuthType.LOCAL
                    it.skinPath = skinPath
                    it.capePath = capePath
                    it.skinModel = skinModel
                    it.profileId = LocalUuidUtils.generateProfileId(username, skinModel).toFormattedUuid()
                }

                acc.updateSkinFace(context.assets)
                loginListener.onLoginDone(acc)
            } catch(e: Exception) {
                loginListener.onLoginError(e)
            }
            false
        }
    }

    val microsoftListener = remember {
        ExtraListener<String> { _, value ->
            AuthType.MICROSOFT.createAuth()?.createAccount(loginListener, value)
            false
        }
    }

    val elyByListener = remember {
        ExtraListener<String> { _, value ->
            AuthType.ELY_BY.createAuth()?.createAccount(loginListener, value)
            false
        }
    }

    val refreshListener = remember {
        ExtraListener<Boolean> { _, _ ->
            refreshAccountsList()
            false
        }
    }

    DisposableEffect(Unit) {
        if (!isPreview) {
            refreshAccountsList()
            ExtraCore.addExtraListener(ExtraConstants.REFRESH_ACCOUNT_SPINNER, refreshListener)
            ExtraCore.addExtraListener(ExtraConstants.MOJANG_LOGIN_TODO, mojangListener)
            ExtraCore.addExtraListener(ExtraConstants.MICROSOFT_LOGIN_TODO, microsoftListener)
            ExtraCore.addExtraListener(ExtraConstants.ELYBY_LOGIN_TODO, elyByListener)
        }

        onDispose {
            if (!isPreview) {
                ExtraCore.removeExtraListenerFromValue(ExtraConstants.REFRESH_ACCOUNT_SPINNER, refreshListener)
                ExtraCore.removeExtraListenerFromValue(ExtraConstants.MOJANG_LOGIN_TODO, mojangListener)
                ExtraCore.removeExtraListenerFromValue(ExtraConstants.MICROSOFT_LOGIN_TODO, microsoftListener)
                ExtraCore.removeExtraListenerFromValue(ExtraConstants.ELYBY_LOGIN_TODO, elyByListener)
            }
        }
    }

    val transitionSpec = getTransitionSpec()

    Box(modifier = Modifier.fillMaxSize()) {
        LauncherBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                topBarHeight = topBarHeight,
                ignoreNotch = ignoreNotch,
                hasBackground = hasBackground,
                isAnyScreenOpen = isAnyScreenOpen,
                isProgressVisible = isProgressVisible,
                taskCount = taskCount,
                selectedCategory = selectedCategory,
                accounts = accounts,
                currentAccount = currentAccount,
                onAccountSelect = { account ->
                    Accounts.setCurrent(account)
                    currentAccount = account
                    ExtraCore.setValue(ExtraConstants.REFRESH_ACCOUNT_SPINNER, true)
                    if (account.authType.requiresLogin() && System.currentTimeMillis() > account.expiresAt) {
                        account.authType.createAuth()?.refreshAccount(loginListener, account)
                    }
                },
                onAccountDelete = { account ->
                    Accounts.delete(account)
                    refreshAccountsList()
                },
                onHomeClick = {

                    selectedCategory = -1
                    if (isProgressVisible) onProgressClick()
                    onHomeRequest()
                },
                onProgressClick = onProgressClick,
                onCategoryClick = { category ->
                    if (isProgressVisible) onProgressClick()
                    selectedCategory = if (selectedCategory == category) -1 else category
                }
            )

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {

                if (!isPreview) {
                    AndroidView(
                        factory = { ctx ->
                            FragmentContainerView(ctx).apply {
                                id = R.id.container_fragment
                            }
                        },
                        update = { view ->
                            val activity = view.context as? FragmentActivity
                            val manager = activity?.supportFragmentManager ?: return@AndroidView

                            if (manager.findFragmentByTag(MainMenuFragment.TAG) == null) {
                                view.post {
                                    if (manager.findFragmentByTag(MainMenuFragment.TAG) == null && !manager.isStateSaved) {
                                        manager.beginTransaction()
                                            .replace(R.id.container_fragment, MainMenuFragment(), MainMenuFragment.TAG)
                                            .commitAllowingStateLoss()
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                            .alpha(if (selectedCategory == -1) 1f else 0f)
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Gray.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                        Text("Fragment Container Placeholder")
                    }
                }

                AnimatedContent(
                    targetState = selectedCategory,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.97f, animationSpec = tween(220)))
                            .togetherWith(fadeOut(animationSpec = tween(150)) + scaleOut(targetScale = 0.99f, animationSpec = tween(150)))
                    },
                    label = "overlayTransition"
                ) { category ->
                    when (category) {
                        1 -> DirectoryManagerOverlay(onBack = { selectedCategory = -1 })
                        4 -> DurbinServerListOverlay(onBack = { selectedCategory = -1 })
                        2 -> ContentInstallerOverlay(onBack = { selectedCategory = -1 })
                        5 -> DurbinClientDownloadsOverlay(onBack = { selectedCategory = -1 })
                        3 -> SettingsOverlay(onBack = { selectedCategory = -1 })
                    }
                }
            }
        }

        @Suppress("DEPRECATION")
        AnimatedVisibility(
            visible = isProgressVisible,
            enter = fadeIn(spring(stiffness = Spring.StiffnessLow)) + expandIn(expandFrom = Alignment.TopEnd, animationSpec = spring(stiffness = Spring.StiffnessLow)),
            exit = fadeOut(spring(stiffness = Spring.StiffnessLow)) + shrinkOut(shrinkTowards = Alignment.TopEnd, animationSpec = spring(stiffness = Spring.StiffnessLow)),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = topBarHeight + 12.dp, end = 12.dp)
        ) {
            ProgressCard(
                modifier = Modifier.run {
                    if (ignoreNotch) this
                    else windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                }
            )
        }

        AnimatedVisibility(
            visible = LauncherPreferences.PREF_SHOW_CRYNOIX_LOADING.value,
            enter = fadeIn(),
            exit = fadeOut(animationSpec = tween(1000))
        ) {
            CrynoixLoadingOverlay()
        }
    }
}

@Composable
fun CrynoixLoadingOverlay() {
    LaunchedEffect(Unit) {
        delay(4000)
        LauncherPreferences.PREF_SHOW_CRYNOIX_LOADING.value = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF202628))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CssArcLoader()

            Spacer(modifier = Modifier.height(22.dp))
            Text(
                "Loading DURBIN Client...",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun CssArcLoader() {
    val infiniteTransition = rememberInfiniteTransition(label = "cssLoaderLoop")

    val rotation1 by infiniteTransition.animateFloat(
        initialValue = 360f * (0.8f / 1.15f),
        targetValue = 360f + (360f * (0.8f / 1.15f)),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1150, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cssLoaderRotate1"
    )

    val rotation2 by infiniteTransition.animateFloat(
        initialValue = 360f * (0.4f / 1.15f),
        targetValue = 360f + (360f * (0.4f / 1.15f)),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1150, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cssLoaderRotate2"
    )

    val rotation3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1150, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cssLoaderRotate3"
    )

    Box(
        modifier = Modifier.size(96.dp),
        contentAlignment = Alignment.Center
    ) {
        CssArc(
            rotation = rotation1,
            rotationX = 35f,
            rotationY = -45f
        )
        CssArc(
            rotation = rotation2,
            rotationX = 50f,
            rotationY = 10f
        )
        CssArc(
            rotation = rotation3,
            rotationX = 35f,
            rotationY = 55f
        )
    }
}

@Composable
private fun CssArc(
    rotation: Float,
    rotationX: Float,
    rotationY: Float
) {
    Canvas(
        modifier = Modifier
            .size(70.dp)
            .graphicsLayer {
                this.rotationX = rotationX
                this.rotationY = rotationY
                this.rotationZ = rotation
                cameraDistance = 14f * density
            }
    ) {
        val strokeWidth = 5.dp.toPx()
        drawArc(
            color = Color(0xFFFF0000),
            startAngle = 40f,
            sweepAngle = 100f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun DirectoryManagerOverlay(onBack: () -> Unit) {
    val viewModel: DirectoryManagerViewModel = viewModel()

    LaunchedEffect(Unit) {
        viewModel.init(null, null)
    }

    val uploadLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { _ -> }

    var showNewFolderDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var inputName by remember { mutableStateOf("") }

    if (showNewFolderDialog) {
        AlertDialog(
            onDismissRequest = { showNewFolderDialog = false },
            title = { Text("New Folder") },
            text = {
                OutlinedTextField(
                    value = inputName,
                    onValueChange = { inputName = it },
                    label = { Text("Folder Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.createFolder(inputName)
                    showNewFolderDialog = false
                    inputName = ""
                }) { Text("Create") }
            },
            dismissButton = {
                @Suppress("DEPRECATION")
                TextButton(onClick = { showNewFolderDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename") },
            text = {
                OutlinedTextField(
                    value = inputName,
                    onValueChange = { inputName = it },
                    label = { Text("New Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.renameSelected(inputName)
                    showRenameDialog = false
                    inputName = ""
                }) { Text("Rename") }
            },
            dismissButton = {
                @Suppress("DEPRECATION")
                TextButton(onClick = { showRenameDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete '${viewModel.selectedFile?.name}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSelected()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) { Text("Delete") }
            },
            dismissButton = {
                @Suppress("DEPRECATION")
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }

    BackHandler { onBack() }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
        DirectoryManagerScreen(
            onBack = onBack,
            title = viewModel.title,
            breadcrumbs = viewModel.getBreadcrumbs(),
            entries = viewModel.entries,
            selectedFile = viewModel.selectedFile,
            statusText = viewModel.statusText,
            onEntryClick = { file ->
                if (file.isDirectory) viewModel.openDir(file)
                else viewModel.selectedFile = file
            },
            onEntryLongClick = { file -> viewModel.selectedFile = file },
            onCrumbClick = { file -> viewModel.openDir(file) },
            onUpClick = { viewModel.goUp() },
            onUploadClick = { uploadLauncher.launch("*/*") },
            onNewFolderClick = {
                inputName = ""
                showNewFolderDialog = true
            },
            onRenameClick = {
                inputName = viewModel.selectedFile?.name ?: ""
                showRenameDialog = true
            },
            onDeleteClick = { showDeleteConfirm = true }
        )
    }
}

private data class DurbinClientBuild(
    val version: String,
    val minecraftVersion: String,
    val url: String,
    val note: String
)

private val durbinClientBuilds = listOf(
    DurbinClientBuild(
        version = "DURBIN Client 1.21.11",
        minecraftVersion = "1.21.11",
        url = "https://github.com/iamcocotheking-cmd/luncher/releases/download/cosa/1.21.11.zip",
        note = "Official DURBIN Client build for Minecraft 1.21.11"
    )
)

@Composable
private fun DurbinClientDownloadsOverlay(onBack: () -> Unit) {
    val context = LocalContext.current
    BackHandler { onBack() }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Black.copy(alpha = 0.34f),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("DURBIN Client", color = Color.White, fontWeight = FontWeight.Black, fontSize = 30.sp)
                        Text("Fast install • Fabric • LTW renderer", color = Color.White.copy(alpha = 0.66f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    DurbinClientActionButton(
                        text = "Back",
                        icon = R.drawable.ic_px_home,
                        onClick = onBack,
                        compact = true
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(durbinClientBuilds, key = { it.minecraftVersion }) { build ->
                    DurbinClientBuildCard(
                        build = build,
                        onInstallAndPlay = {
                            DurbinClientInstaller.installAndLaunch(
                                context = context,
                                minecraftVersion = build.minecraftVersion,
                                zipUrl = build.url,
                                onStatus = { /* progress appears in Tasks */ }
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DurbinClientBuildCard(
    build: DurbinClientBuild,
    onInstallAndPlay: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val cardScale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = tween(durationMillis = 110),
        label = "durbinClientBuildCardScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 250.dp)
            .scale(cardScale)
            .clip(RoundedCornerShape(28.dp))
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.34f)), RoundedCornerShape(28.dp))
            .clickable(interactionSource = interactionSource, indication = null) { }
    ) {
        Image(
            painter = painterResource(id = R.drawable.durbin_mod_banner),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.12f),
                            Color.Black.copy(alpha = 0.50f),
                            Color.Black.copy(alpha = 0.86f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(62.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.Black.copy(alpha = 0.48f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.icon),
                            contentDescription = null,
                            modifier = Modifier.size(46.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = build.version,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Minecraft ${build.minecraftVersion} • LTW default",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Installs the official DURBIN Client ZIP, creates/selects the DURBIN Client profile, then starts Minecraft.",
                        color = Color.White.copy(alpha = 0.78f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "Download progress will show in Tasks.",
                        color = Color.White.copy(alpha = 0.56f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                DurbinClientActionButton(
                    text = "Install + Play",
                    icon = R.drawable.ic_px_play,
                    onClick = onInstallAndPlay,
                    compact = true
                )
            }
        }
    }
}

@Composable
private fun DurbinClientActionButton(
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
        label = "durbinClientButtonScale"
    )

    OutlinedButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .scale(scale)
            .height(if (compact) 46.dp else 54.dp)
            .then(if (compact) Modifier.widthIn(min = 154.dp) else Modifier.fillMaxWidth()),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Black.copy(alpha = 0.26f),
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 14.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(if (compact) 18.dp else 20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(text, color = Color.White, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun ContentInstallerOverlay(onBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: ContentInstallerViewModel = viewModel()

    LaunchedEffect(Unit) {
        viewModel.init(context)
    }

    BackHandler {
        if (viewModel.viewingProject != null) {
            viewModel.viewingProject = null
        } else {
            onBack()
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
        ContentInstallerScreen(
            onBack = onBack,
            onOpenDownloads = { /* ... */ },
            onInstallLocal = { /* ... */ },
            onSearch = { q, type, v, l ->
                viewModel.versionFilter = v
                viewModel.loaderFilter = l
                viewModel.triggerSearch(q, type)
            },
            onProjectClick = { viewModel.loadVersions(it) },
            onVersionClick = { viewModel.downloadVersion(context, it, viewModel.selectedType) },
            projects = viewModel.projects,
            isLoading = viewModel.isLoading,
            statusText = viewModel.statusText,
            selectedVersion = viewModel.versionFilter ?: "",
            selectedLoader = viewModel.loaderFilter ?: "",
            onVersionFilterChange = { viewModel.versionFilter = it },
            onLoaderFilterChange = { viewModel.loaderFilter = it },
            instanceVersion = viewModel.instanceVersion,
            instanceLoader = viewModel.instanceLoader,
            viewingProject = viewModel.viewingProject,
            projectVersions = viewModel.projectVersions,
            availableProjectMCVersions = viewModel.availableProjectMCVersions,
            selectedProjectMCVersion = viewModel.selectedProjectMCVersion,
            onProjectMCVersionClick = { viewModel.selectedProjectMCVersion = it.ifEmpty { null } },
            onBackToProjects = { viewModel.viewingProject = null }
        )
    }
}

@Composable
private fun SettingsOverlay(onBack: () -> Unit) {
    BackHandler { onBack() }
    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
        SettingsScreen(
            onBack = onBack
        )
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,orientation=landscape")
@Composable
fun LauncherScreenPreview() {
    PojavTheme(dynamicColor = true) {
        LauncherScreen(
            onHomeRequest = {},
            onProgressClick = {},
            isProgressVisible = true,
            taskCount = 2,
            isFragmentOpen = true
        )
    }
}
