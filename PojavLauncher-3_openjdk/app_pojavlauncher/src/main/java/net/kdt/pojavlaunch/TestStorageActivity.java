package net.kdt.pojavlaunch;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import android.widget.VideoView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.tasks.AsyncAssetManager;

public class TestStorageActivity extends Activity {
    private final int REQUEST_STORAGE_REQUEST_CODE = 1;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private VideoView mLoadingVideo;
    private volatile boolean mPrepDone = false;
    private volatile boolean mMinDelayDone = false;
    private volatile boolean mLaunched = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT < 29 && !isStorageAllowed(this)) {
            requestStoragePermission();
        } else {
            startLoadingFlow();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLoadingFlow();
            } else {
                Toast.makeText(this, R.string.toast_permission_denied, Toast.LENGTH_LONG).show();
                requestStoragePermission();
            }
        }
    }

    public static boolean isStorageAllowed(Context context) {
        int result1 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_REQUEST_CODE);
    }

    private void startLoadingFlow() {
        setContentView(R.layout.activity_loading_animation);
        startLoadingScreenAnimations();
        mLoadingVideo = findViewById(R.id.loading_video);
        try {
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.durbin_loading);
            mLoadingVideo.setVideoURI(videoUri);
            mLoadingVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                    mp.setVolume(0f, 0f);
                    mLoadingVideo.start();
                }
            });
        } catch (Throwable ignored) {}

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMinDelayDone = true;
                maybeLaunch();
            }
        }, 2400);

        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareLauncher();
            }
        }, "durbin-loading-prep").start();
    }

    private void startLoadingScreenAnimations() {
        View content = findViewById(R.id.loading_content);
        View subtitle = findViewById(R.id.loading_subtitle);
        if (content != null) {
            AnimationSet intro = new AnimationSet(true);
            intro.setInterpolator(new DecelerateInterpolator());
            AlphaAnimation fade = new AlphaAnimation(0f, 1f);
            fade.setDuration(520);
            ScaleAnimation scale = new ScaleAnimation(
                    0.92f, 1f, 0.92f, 1f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            scale.setDuration(520);
            intro.addAnimation(fade);
            intro.addAnimation(scale);
            content.startAnimation(intro);

            ScaleAnimation pulse = new ScaleAnimation(
                    1f, 1.035f, 1f, 1.035f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            pulse.setDuration(1150);
            pulse.setRepeatMode(Animation.REVERSE);
            pulse.setRepeatCount(Animation.INFINITE);
            pulse.setInterpolator(new LinearInterpolator());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!mLaunched && !isFinishing()) content.startAnimation(pulse);
                }
            }, 620);
        }
        if (subtitle != null) {
            AlphaAnimation blink = new AlphaAnimation(0.45f, 1f);
            blink.setDuration(900);
            blink.setRepeatMode(Animation.REVERSE);
            blink.setRepeatCount(Animation.INFINITE);
            subtitle.startAnimation(blink);
        }
    }

    private void prepareLauncher() {
        if(!Tools.checkStorageRoot(this)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) startActivity(new Intent(TestStorageActivity.this, MissingStorageActivity.class));
                    finish();
                }
            });
            return;
        }
        LauncherPreferences.loadPreferences(this);
        AsyncAssetManager.unpackComponents(this);
        AsyncAssetManager.unpackSingleFiles(this);
        mPrepDone = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                maybeLaunch();
            }
        });
    }

    private void maybeLaunch() {
        if (mLaunched || !mPrepDone || !mMinDelayDone || isFinishing()) return;
        mLaunched = true;
        Intent intent =  new Intent(this, LauncherActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLoadingVideo != null && mLoadingVideo.isPlaying()) mLoadingVideo.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLoadingVideo != null && !mLaunched) {
            try { mLoadingVideo.start(); } catch (Throwable ignored) {}
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        if (mLoadingVideo != null) {
            try { mLoadingVideo.stopPlayback(); } catch (Throwable ignored) {}
        }
        super.onDestroy();
    }
}
