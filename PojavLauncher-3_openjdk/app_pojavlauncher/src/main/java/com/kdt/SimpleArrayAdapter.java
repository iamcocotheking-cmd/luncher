package com.kdt;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimpleArrayAdapter<T> extends ArrayAdapter<T> {
    private final List<T> objects = new ArrayList<>();

    public SimpleArrayAdapter(Collection<T> initial) {
        super(net.kdt.pojavlaunch.PojavApplication.getContext(), android.R.layout.simple_spinner_dropdown_item);
        setObjects(initial);
    }

    public void setObjects(Collection<T> values) {
        objects.clear();
        clear();
        if (values != null) {
            objects.addAll(values);
            addAll(values);
        }
        notifyDataSetChanged();
    }
}
