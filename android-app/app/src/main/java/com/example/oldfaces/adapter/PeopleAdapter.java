package com.example.oldfaces.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.oldfaces.R;
import com.example.oldfaces.points.People;

import java.util.ArrayList;

public class PeopleAdapter extends ArrayAdapter<People> {
    private Context context;
    private ArrayList<People> list;

    public PeopleAdapter(@NonNull Context context, ArrayList<People> list) {
        super(context, android.R.layout.simple_list_item_1, list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.people_row, null, false);
        }
        People p = list.get(position);
        ImageView img = v.findViewById(R.id.peoplePicture);
        TextView name = v.findViewById(R.id.peopleName);
        TextView count = v.findViewById(R.id.countPicture);

        img.setImageResource(p.getSrc());
        name.setText(p.getName());
        count.setText("数量："+p.getCount());

        return v;
    }
}
