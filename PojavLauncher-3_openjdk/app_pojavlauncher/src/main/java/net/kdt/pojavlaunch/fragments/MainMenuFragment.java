package net.kdt.pojavlaunch.fragments;

import static net.kdt.pojavlaunch.Tools.openPath;
import static net.kdt.pojavlaunch.Tools.shareLog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.compose.ui.platform.ComposeView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewTreeLifecycleOwner;

import com.kdt.mcgui.mcAccountSpinner;
import com.kdt.mcgui.mcVersionSpinner;

import net.kdt.pojavlaunch.CustomControlsActivity;
import net.kdt.pojavlaunch.LauncherActivity;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceFragment;
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper;
import net.kdt.pojavlaunch.ui.DurbinDashboardKt;
import net.kdt.pojavlaunch.ui.DurbinMenuCallbacks;
import net.kdt.pojavlaunch.value.MinecraftAccount;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;

import java.io.File;

public class MainMenuFragment extends Fragment {
    public static final String TAG = "MainMenuFragment";

    private mcVersionSpinner mVersionSpinner;

    public MainMenuFragment() {
        super(R.layout.fragment_durbin_launcher);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // PojavLauncher uses some legacy view containers that do not always install
        // a ViewTreeLifecycleOwner automatically. ComposeView needs this owner to
        // create its lifecycle-aware recomposer, otherwise the home screen can crash
        // with: "ViewTreeLifecycleOwner not found".
        ViewTreeLifecycleOwner.set(view, getViewLifecycleOwner());

        mVersionSpinner = view.findViewById(R.id.mc_version_spinner);
        ComposeView composeView = view.findViewById(R.id.durbin_compose_view);
        ViewTreeLifecycleOwner.set(composeView, getViewLifecycleOwner());
        DurbinDashboardKt.setDurbinDashboardContent(composeView, buildCallbacks());
    }

    private DurbinMenuCallbacks buildCallbacks() {
        return new DurbinMenuCallbacks(
                () -> {
                    ExtraCore.setValue(ExtraConstants.LAUNCH_GAME, true);
                    return kotlin.Unit.INSTANCE;
                },
                () -> {
                    Tools.swapFragment(requireActivity(), LauncherPreferenceFragment.class,
                            LauncherActivity.SETTING_FRAGMENT_TAG, null);
                    return kotlin.Unit.INSTANCE;
                },
                () -> {
                    startActivity(new Intent(requireContext(), CustomControlsActivity.class));
                    return kotlin.Unit.INSTANCE;
                },
                () -> {
                    openGameDirectory();
                    return kotlin.Unit.INSTANCE;
                },
                () -> {
                    shareLog(requireContext());
                    return kotlin.Unit.INSTANCE;
                },
                () -> {
                    runInstallerWithConfirmation(false);
                    return kotlin.Unit.INSTANCE;
                },
                () -> {
                    ExtraCore.setValue(ExtraConstants.SELECT_AUTH_METHOD, true);
                    return kotlin.Unit.INSTANCE;
                },
                () -> {
                    mVersionSpinner.performClick();
                    return kotlin.Unit.INSTANCE;
                },
                () -> {
                    mVersionSpinner.openProfileEditor(requireActivity());
                    return kotlin.Unit.INSTANCE;
                },
                () -> {
                    Tools.swapFragment(requireActivity(), ProfileEditorFragment.class,
                            ProfileEditorFragment.TAG, new Bundle(1));
                    return kotlin.Unit.INSTANCE;
                },
                () -> {
                    Tools.swapFragment(requireActivity(), FabricInstallFragment.class,
                            FabricInstallFragment.TAG, null);
                    return kotlin.Unit.INSTANCE;
                },
                () -> {
                    Tools.swapFragment(requireActivity(), ForgeInstallFragment.class,
                            ForgeInstallFragment.TAG, null);
                    return kotlin.Unit.INSTANCE;
                },
                () -> kotlin.Unit.INSTANCE,
                mVersionSpinner,
                this::getCurrentProfileName,
                this::getCurrentVersionId,
                this::getCurrentLoaderLabel,
                this::getAccountName,
                this::isOfflineAccount,
                this::getRamAllocation,
                () -> LauncherPreferences.PREF_RENDERER,
                this::getRuntimeLabel
        );
    }

    private void openGameDirectory() {
        Tools.switchDemo(Tools.isDemoProfile(requireContext()));
        if (Tools.isDemoProfile(requireContext())) {
            Toast.makeText(requireContext(), R.string.toast_not_available_demo, Toast.LENGTH_LONG).show();
            return;
        }
        openPath(requireContext(), getCurrentProfileDirectory(), false);
    }

    private File getCurrentProfileDirectory() {
        String currentProfile = LauncherPreferences.DEFAULT_PREF.getString(LauncherPreferences.PREF_KEY_CURRENT_PROFILE, null);
        if (!Tools.isValidString(currentProfile)) return new File(Tools.DIR_GAME_NEW);
        LauncherProfiles.load();
        MinecraftProfile profileObject = LauncherProfiles.mainProfileJson.profiles.get(currentProfile);
        if (profileObject == null) return new File(Tools.DIR_GAME_NEW);
        return Tools.getGameDirPath(profileObject);
    }

    private String getCurrentProfileName() {
        String currentProfile = LauncherPreferences.DEFAULT_PREF.getString(LauncherPreferences.PREF_KEY_CURRENT_PROFILE, "");
        if (!Tools.isValidString(currentProfile)) return "No profile selected";
        return currentProfile;
    }

    private String getCurrentVersionId() {
        LauncherProfiles.load();
        String currentProfile = LauncherPreferences.DEFAULT_PREF.getString(LauncherPreferences.PREF_KEY_CURRENT_PROFILE, "");
        if (!Tools.isValidString(currentProfile) || LauncherProfiles.mainProfileJson == null) return "Select a version";
        MinecraftProfile profile = LauncherProfiles.mainProfileJson.profiles.get(currentProfile);
        if (profile == null || profile.lastVersionId == null) return "Select a version";
        return profile.lastVersionId;
    }

    private String getCurrentLoaderLabel() {
        String versionId = getCurrentVersionId().toLowerCase();
        if (versionId.contains("fabric")) return "Fabric";
        if (versionId.contains("forge")) return "Forge";
        if (versionId.contains("quilt")) return "Quilt";
        if (versionId.contains("optifine")) return "OptiFine";
        return "Vanilla";
    }

    private String getAccountName() {
        if (!(requireActivity() instanceof LauncherActivity)) return "No account";
        mcAccountSpinner spinner = ((LauncherActivity) requireActivity()).getAccountSpinner();
        if (spinner == null) return "No account";
        MinecraftAccount account = spinner.getSelectedAccount();
        if (account == null) return "Add account";
        return account.username;
    }

    private boolean isOfflineAccount() {
        if (!(requireActivity() instanceof LauncherActivity)) return true;
        mcAccountSpinner spinner = ((LauncherActivity) requireActivity()).getAccountSpinner();
        if (spinner == null) return true;
        MinecraftAccount account = spinner.getSelectedAccount();
        return account == null || account.accessToken.equals("0");
    }

    private String getRamAllocation() {
        return LauncherPreferences.PREF_RAM_ALLOCATION + " MB";
    }

    private String getRuntimeLabel() {
        String runtime = LauncherPreferences.PREF_DEFAULT_RUNTIME;
        if (!Tools.isValidString(runtime)) return "Auto";
        return runtime;
    }

    @Override
    public void onResume() {
        super.onResume();
        mVersionSpinner.reloadProfiles();
    }

    private void runInstallerWithConfirmation(boolean isCustomArgs) {
        if (Tools.isLocalProfile(requireContext()) || Tools.isDemoProfile(requireContext())) {
            Toast.makeText(requireContext(), R.string.toast_not_available_demo, Toast.LENGTH_LONG).show();
            return;
        }

        if (ProgressKeeper.getTaskCount() == 0)
            Tools.installMod(requireActivity(), isCustomArgs);
        else
            Toast.makeText(requireContext(), R.string.tasks_ongoing, Toast.LENGTH_LONG).show();
    }
}
