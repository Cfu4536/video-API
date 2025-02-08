package com.example.oldfaces.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.oldfaces.R;
import com.example.oldfaces.points.URL;

import java.util.ArrayList;

public class URLAdapter extends ArrayAdapter<URL> {
    Context context;
    ArrayList<URL> list;

    public URLAdapter(@NonNull Context context, ArrayList<URL> list) {
        super(context, android.R.layout.simple_list_item_1, list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.url_row, null, false);
        }
        TextView urlName = v.findViewById(R.id.urlName);
        urlName.setText(list.get(position).getName());
        TextView urlLink = v.findViewById(R.id.urlLink);
        urlLink.setText(list.get(position).getUrl());
        return v;
    }
}
