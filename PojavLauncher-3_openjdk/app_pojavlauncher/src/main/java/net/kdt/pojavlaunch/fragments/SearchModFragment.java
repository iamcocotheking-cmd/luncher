package net.kdt.pojavlaunch.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.math.MathUtils;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.modloaders.modpacks.ModItemAdapter;
import net.kdt.pojavlaunch.modloaders.modpacks.api.CommonApi;
import net.kdt.pojavlaunch.modloaders.modpacks.api.ModpackApi;
import net.kdt.pojavlaunch.modloaders.modpacks.api.ModrinthApi;
import net.kdt.pojavlaunch.modloaders.modpacks.models.SearchFilters;
import net.kdt.pojavlaunch.profiles.VersionSelectorDialog;
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchModFragment extends Fragment implements ModItemAdapter.SearchResultCallback {

    public static final String TAG = "SearchModFragment";
    public static final String ARG_IS_MODPACK = "is_modpack";
    public static final String ARG_MODRINTH_ONLY = "modrinth_only";
    private View mOverlay;
    private VideoView mBackgroundVideo;
    private float mOverlayTopCache; // Padding cache reduce resource lookup

    private final RecyclerView.OnScrollListener mOverlayPositionListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            mOverlay.setY(MathUtils.clamp(mOverlay.getY() - dy, -mOverlay.getHeight(), mOverlayTopCache));
        }
    };

    private EditText mSearchEditText;
    private ImageButton mFilterButton;
    private RecyclerView mRecyclerview;
    private ModItemAdapter mModItemAdapter;
    private ProgressBar mSearchProgressBar;
    private TextView mStatusTextView;
    private ColorStateList mDefaultTextColor;

    private ModpackApi modpackApi;

    private final SearchFilters mSearchFilters;

    public SearchModFragment(){
        super(R.layout.fragment_mod_search);
        mSearchFilters = new SearchFilters();
        mSearchFilters.isModpack = true;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Bundle args = getArguments();
        if (args != null) {
            mSearchFilters.isModpack = args.getBoolean(ARG_IS_MODPACK, true);
        }
        if (!mSearchFilters.isModpack) {
            mSearchFilters.mcVersion = getCurrentMinecraftVersion();
            mSearchFilters.loader = getCurrentModLoader();
        }
        boolean modrinthOnly = args != null && args.getBoolean(ARG_MODRINTH_ONLY, false);
        modpackApi = modrinthOnly ? new ModrinthApi() : new CommonApi(context.getString(R.string.curseforge_api_key));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // You can only access resources after attaching to current context
        mModItemAdapter = new ModItemAdapter(getResources(), modpackApi, this);
        ProgressKeeper.addTaskCountListener(mModItemAdapter);
        mOverlayTopCache = getResources().getDimension(R.dimen.fragment_padding_medium);

        mOverlay = view.findViewById(R.id.search_mod_overlay);
        mSearchEditText = view.findViewById(R.id.search_mod_edittext);
        mSearchProgressBar = view.findViewById(R.id.search_mod_progressbar);
        mRecyclerview = view.findViewById(R.id.search_mod_list);
        mStatusTextView = view.findViewById(R.id.search_mod_status_text);
        mFilterButton = view.findViewById(R.id.search_mod_filter);
        mBackgroundVideo = view.findViewById(R.id.search_mod_background_video);
        setupBackgroundVideo();
        animateModrinthEntrance();

        mDefaultTextColor = mStatusTextView.getTextColors();

        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerview.setAdapter(mModItemAdapter);
        mRecyclerview.setHasFixedSize(false);
        try {
            mRecyclerview.setItemAnimator(new androidx.recyclerview.widget.DefaultItemAnimator());
        } catch (Throwable ignored) { }

        mRecyclerview.addOnScrollListener(mOverlayPositionListener);

        mSearchEditText.setOnEditorActionListener((v, actionId, event) -> {
            searchMods(mSearchEditText.getText().toString());
            mSearchEditText.clearFocus();
            return false;
        });

        mOverlay.post(()->{
           int overlayHeight = mOverlay.getHeight();
           mRecyclerview.setPadding(mRecyclerview.getPaddingLeft(),
                   mRecyclerview.getPaddingTop() + overlayHeight,
                   mRecyclerview.getPaddingRight(),
                   mRecyclerview.getPaddingBottom());
        });
        mFilterButton.setOnClickListener(v -> displayFilterDialog());

        if (!mSearchFilters.isModpack) {
            String loader = mSearchFilters.loader == null ? "modded" : mSearchFilters.loader.toUpperCase(Locale.ROOT);
            String version = mSearchFilters.mcVersion == null ? "current version" : mSearchFilters.mcVersion;
            mSearchEditText.setHint(loader + " mods for " + version);
        }

        searchMods(null);
    }


    private void setupBackgroundVideo() {
        if (mBackgroundVideo == null) return;
        try {
            Uri videoUri = Uri.parse("android.resource://" + requireContext().getPackageName() + "/" + R.raw.durbin_background);
            mBackgroundVideo.setVideoURI(videoUri);
            mBackgroundVideo.setOnPreparedListener(mp -> {
                mp.setLooping(true);
                mp.setVolume(0f, 0f);
                try {
                    mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                } catch (Throwable ignored) { }
                mBackgroundVideo.start();
            });
            mBackgroundVideo.setOnErrorListener((mp, what, extra) -> true);
        } catch (Throwable ignored) { }
    }

    private void animateModrinthEntrance() {
        if (mOverlay != null) {
            mOverlay.setAlpha(0f);
            mOverlay.setTranslationY(-36f);
            mOverlay.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(360)
                    .setStartDelay(80)
                    .start();
        }
        if (mRecyclerview != null) {
            mRecyclerview.setAlpha(0f);
            mRecyclerview.setTranslationY(40f);
            mRecyclerview.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(420)
                    .setStartDelay(160)
                    .start();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if (mBackgroundVideo != null) mBackgroundVideo.stopPlayback();
        } catch (Throwable ignored) { }
        ProgressKeeper.removeTaskCountListener(mModItemAdapter);
        mRecyclerview.removeOnScrollListener(mOverlayPositionListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (mBackgroundVideo != null && mBackgroundVideo.isPlaying()) mBackgroundVideo.pause();
        } catch (Throwable ignored) { }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (mBackgroundVideo != null && !mBackgroundVideo.isPlaying()) mBackgroundVideo.start();
        } catch (Throwable ignored) { }
    }

    @Override
    public void onSearchFinished() {
        mSearchProgressBar.setVisibility(View.GONE);
        mStatusTextView.setVisibility(View.GONE);
    }

    @Override
    public void onSearchError(int error) {
        mSearchProgressBar.setVisibility(View.GONE);
        mStatusTextView.setVisibility(View.VISIBLE);
        switch (error) {
            case ERROR_INTERNAL:
                mStatusTextView.setTextColor(Color.RED);
                mStatusTextView.setText(mSearchFilters.isModpack
                        ? getString(R.string.search_modpack_error)
                        : "Could not connect to Modrinth. Check internet or clear filters.");
                break;
            case ERROR_NO_RESULTS:
                mStatusTextView.setTextColor(mDefaultTextColor);
                mStatusTextView.setText(mSearchFilters.isModpack
                        ? getString(R.string.search_modpack_no_result)
                        : "No compatible mods found. Try a mod name like Sodium, Iris, or Fabric API.");
                break;
        }
    }

    private void searchMods(String name) {
        mSearchProgressBar.setVisibility(View.VISIBLE);
        mSearchFilters.name = name == null ? "" : name;
        mModItemAdapter.performSearchQuery(mSearchFilters);
    }


    private String getCurrentMinecraftVersion() {
        try {
            LauncherProfiles.load();
            String currentProfile = LauncherPreferences.DEFAULT_PREF.getString(LauncherPreferences.PREF_KEY_CURRENT_PROFILE, null);
            if (!Tools.isValidString(currentProfile) || LauncherProfiles.mainProfileJson == null) return null;
            MinecraftProfile profile = LauncherProfiles.mainProfileJson.profiles.get(currentProfile);
            if (profile == null || !Tools.isValidString(profile.lastVersionId)) return null;
            Matcher matcher = Pattern.compile("(?<!\\d)1\\.\\d+(?:\\.\\d+)?(?!\\d)").matcher(profile.lastVersionId);
            if (matcher.find()) return matcher.group();
        } catch (Throwable ignored) { }
        return null;
    }

    private String getCurrentModLoader() {
        try {
            LauncherProfiles.load();
            String currentProfile = LauncherPreferences.DEFAULT_PREF.getString(LauncherPreferences.PREF_KEY_CURRENT_PROFILE, null);
            if (!Tools.isValidString(currentProfile) || LauncherProfiles.mainProfileJson == null) return null;
            MinecraftProfile profile = LauncherProfiles.mainProfileJson.profiles.get(currentProfile);
            if (profile == null || profile.lastVersionId == null) return null;
            String id = profile.lastVersionId.toLowerCase(Locale.ROOT);
            if (id.contains("fabric") || id.contains("durbin")) return "fabric";
            if (id.contains("forge")) return "forge";
            if (id.contains("quilt")) return "quilt";
        } catch (Throwable ignored) { }
        return null;
    }

    private void displayFilterDialog() {
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(R.layout.dialog_mod_filters)
                .create();

        // setup the view behavior
        dialog.setOnShowListener(dialogInterface -> {
            TextView mSelectedVersion = dialog.findViewById(R.id.search_mod_selected_mc_version_textview);
            Button mSelectVersionButton = dialog.findViewById(R.id.search_mod_mc_version_button);
            Button mApplyButton = dialog.findViewById(R.id.search_mod_apply_filters);

            assert mSelectVersionButton != null;
            assert mSelectedVersion != null;
            assert mApplyButton != null;

            // Setup the expendable list behavior
            mSelectVersionButton.setOnClickListener(v -> VersionSelectorDialog.open(v.getContext(), true, (id, snapshot)-> mSelectedVersion.setText(id)));

            // Apply visually all the current settings
            mSelectedVersion.setText(mSearchFilters.mcVersion);

            // Apply the new settings
            mApplyButton.setOnClickListener(v -> {
                mSearchFilters.mcVersion = mSelectedVersion.getText().toString();
                searchMods(mSearchEditText.getText().toString());
                dialogInterface.dismiss();
            });
        });


        dialog.show();
    }
}
