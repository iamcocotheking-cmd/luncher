package com.kdt.pickafile;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

public class FileListView extends LinearLayout {
    public File fullPath;
    private boolean showFiles = true;
    private boolean showFolders = true;
    private DialogTitleListener titleListener;
    private FileSelectedListener selectedListener;

    public interface DialogTitleListener {
        void onTitleChanged(String title);
    }

    public FileListView(Context context) {
        this(context, null, null);
    }

    public FileListView(Context context, AttributeSet attrs) {
        this(context, attrs, null);
    }

    public FileListView(Context context, AttributeSet attrs, String[] ignored) {
        super(context, attrs);
        setOrientation(VERTICAL);
        fullPath = context.getFilesDir();
        TextView label = new TextView(context);
        label.setText("File selector");
        addView(label);
    }

    public void setShowFiles(boolean value) {
        showFiles = value;
    }

    public void setShowFolders(boolean value) {
        showFolders = value;
    }

    public void lockPathAt(File path) {
        if (path != null) {
            fullPath = path;
            if (titleListener != null) titleListener.onTitleChanged(path.getAbsolutePath());
        }
    }

    public void setDialogTitleListener(DialogTitleListener listener) {
        titleListener = listener;
        if (listener != null && fullPath != null) listener.onTitleChanged(fullPath.getAbsolutePath());
    }

    public void setFileSelectedListener(FileSelectedListener listener) {
        selectedListener = listener;
    }

    public void listFileAt(File file) {
        if (file != null) {
            fullPath = file;
            if (titleListener != null) titleListener.onTitleChanged(file.getAbsolutePath());
            if (selectedListener != null && file.isFile()) selectedListener.onFileSelected(file, file.getAbsolutePath());
        }
    }

    public void refreshPath() {
        if (titleListener != null && fullPath != null) titleListener.onTitleChanged(fullPath.getAbsolutePath());
    }
}
