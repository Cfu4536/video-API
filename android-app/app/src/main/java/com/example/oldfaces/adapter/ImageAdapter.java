package com.example.oldfaces.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.oldfaces.R;
import com.example.oldfaces.points.Image;

import java.io.File;
import java.util.ArrayList;

public class ImageAdapter extends ArrayAdapter<Image> {
    private Context context;
    private ArrayList<Image> list;


    public ImageAdapter(@NonNull Context context, ArrayList<Image> list) {
        super(context, android.R.layout.simple_list_item_1, list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.show_row, null, false);
        }
        Image image = list.get(position);
        ImageView img = v.findViewById(R.id.showImg);
        //加载图片
        File file=new File(image.getSrcPath());
//        img.setImageURI(Uri.fromFile(file));
        Glide.with(context)
                .load(Uri.fromFile(file))
                .centerInside()//宽度充满屏幕，高度自适应
                .into(img);
        return v;

    }


}
