package com.kdt.pickafile;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class FileListView extends LinearLayout {
    public File fullPath;
    private File lockRoot;
    private boolean showFiles = true;
    private boolean showFolders = true;
    private FileSelectedListener fileSelectedListener;
    private DialogTitleListener dialogTitleListener;
    private final String[] extensions;

    public interface DialogTitleListener {
        void onTitleChanged(String title);
    }

    public FileListView(Dialog dialog, String extension) {
        this(dialog.getContext(), null, extension == null ? new String[0] : new String[]{extension});
    }

    public FileListView(Context context, AttributeSet attrs, String[] extensions) {
        super(context, attrs);
        this.extensions = extensions == null ? new String[0] : extensions;
        setOrientation(VERTICAL);
        fullPath = new File("/");
    }

    public void setShowFiles(boolean showFiles) {
        this.showFiles = showFiles;
        refreshPath();
    }

    public void setShowFolders(boolean showFolders) {
        this.showFolders = showFolders;
        refreshPath();
    }

    public void setDialogTitleListener(DialogTitleListener listener) {
        this.dialogTitleListener = listener;
    }

    public void setFileSelectedListener(FileSelectedListener listener) {
        this.fileSelectedListener = listener;
    }

    public void lockPathAt(File root) {
        lockRoot = root;
        listFileAt(root);
    }

    public void listFileAt(File file) {
        if (file == null) return;
        if (!file.exists()) file.mkdirs();
        if (!file.isDirectory()) file = file.getParentFile();
        if (file == null) return;
        if (lockRoot != null) {
            try {
                String rootPath = lockRoot.getCanonicalPath();
                String targetPath = file.getCanonicalPath();
                if (!targetPath.startsWith(rootPath)) file = lockRoot;
            } catch (Exception ignored) {
                file = lockRoot;
            }
        }
        fullPath = file;
        render();
    }

    public void refreshPath() {
        if (fullPath != null) render();
    }

    private boolean acceptFile(File file) {
        if (file.isDirectory()) return showFolders;
        if (!showFiles) return false;
        if (extensions.length == 0) return true;
        String name = file.getName().toLowerCase();
        for (String ext : extensions) {
            if (ext == null || ext.length() == 0) return true;
            String e = ext.toLowerCase();
            if (!e.startsWith(".")) e = "." + e;
            if (name.endsWith(e)) return true;
        }
        return false;
    }

    private TextView row(String text) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setTextSize(16f);
        tv.setPadding(18, 16, 18, 16);
        return tv;
    }

    private void render() {
        removeAllViews();
        if (dialogTitleListener != null && fullPath != null) dialogTitleListener.onTitleChanged(fullPath.getAbsolutePath());
        ScrollView scrollView = new ScrollView(getContext());
        LinearLayout list = new LinearLayout(getContext());
        list.setOrientation(VERTICAL);
        scrollView.addView(list, new ScrollView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        if (fullPath != null && lockRoot != null && !sameFile(fullPath, lockRoot)) {
            TextView up = row("../");
            up.setOnClickListener(v -> listFileAt(fullPath.getParentFile()));
            list.addView(up);
        }

        File[] files = fullPath == null ? null : fullPath.listFiles();
        if (files != null) {
            Arrays.sort(files, Comparator.comparing((File f) -> !f.isDirectory()).thenComparing(File::getName, String.CASE_INSENSITIVE_ORDER));
            for (File f : files) {
                if (!acceptFile(f)) continue;
                TextView item = row(f.getName() + (f.isDirectory() ? "/" : ""));
                item.setOnClickListener(v -> {
                    if (f.isDirectory()) listFileAt(f);
                    else if (fileSelectedListener != null) fileSelectedListener.onFileSelected(f, f.getAbsolutePath());
                });
                list.addView(item);
            }
        }
        addView(scrollView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    private boolean sameFile(File a, File b) {
        try {
            return a.getCanonicalFile().equals(b.getCanonicalFile());
        } catch (Exception e) {
            return a.equals(b);
        }
    }
}
