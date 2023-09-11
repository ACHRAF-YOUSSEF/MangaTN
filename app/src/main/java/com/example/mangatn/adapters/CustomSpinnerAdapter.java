package com.example.mangatn.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    private int selectedItemPosition = -1;

    public CustomSpinnerAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        // Highlight the selected item
        if (position == selectedItemPosition) {
            view.setBackgroundColor(Color.parseColor("#808080")); // grey
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    public void setSelectedItemPosition(int position) {
        selectedItemPosition = position;

        notifyDataSetChanged();
    }
}
