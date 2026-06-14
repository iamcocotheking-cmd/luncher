package net.kdt.pojavlaunch.ui

import com.kdt.mcgui.mcVersionSpinner

/**
 * Callback bridge from Compose dashboard to existing PojavLauncher Java components.
 */
class DurbinMenuCallbacks(
    val onLaunch: () -> Unit,
    val onOpenSettings: () -> Unit,
    val onOpenControls: () -> Unit,
    val onOpenDirectory: () -> Unit,
    val onShareLogs: () -> Unit,
    val onInstallJar: () -> Unit,
    val onOpenModDownloader: () -> Unit,
    val onOpenAccounts: () -> Unit,
    val onOpenVersions: () -> Unit,
    val onEditProfile: () -> Unit,
    val onSelectVanilla: () -> Unit,
    val onSelectFabric: () -> Unit,
    val onSelectForge: () -> Unit,
    val onSelectDurbin: () -> Unit,
    val onDurbinComingSoon: () -> Unit,
    val versionSpinner: mcVersionSpinner,
    val getProfileName: () -> String,
    val getVersionId: () -> String,
    val getLoaderLabel: () -> String,
    val getAccountName: () -> String,
    val isOfflineAccount: () -> Boolean,
    val getRamAllocation: () -> String,
    val getRenderer: () -> String,
    val getRuntime: () -> String,
)
