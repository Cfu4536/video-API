package com.example.oldfaces.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.oldfaces.R;
import com.example.oldfaces.points.Video;

import java.io.File;
import java.util.ArrayList;

public class VideoAdapter extends ArrayAdapter<Video> {
    private Context context;
    private ArrayList<Video> list;// 获取 SharedPreferences 实例

    public VideoAdapter(@NonNull Context context, ArrayList<Video> list) {
        super(context, android.R.layout.simple_list_item_1, list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.video_row, null, false);
        }
        ImageView imageView = v.findViewById(R.id.VideoImg);
        TextView name = v.findViewById(R.id.videoName);
        TextView size = v.findViewById(R.id.videoSize);
        TextView isFavorite = v.findViewById(R.id.tvIsWatch);

        Video video = list.get(position);
//        imageView.setImageResource(video.getSrc());

        name.setText(video.getName());
        size.setText(video.getSize() + "MB");
        if (video.getSize() >= 1024) {
            size.setText(String.format("%.2f GB", video.getSize() / 1024.0));
        } else {
            size.setText(video.getSize() + "MB");
        }
        if (video.getFavorite()) isFavorite.setText("√");
        else isFavorite.setText("");
        if (video.getType().equals("dir")) {//文件夹
            imageView.setImageResource(R.drawable.ic_dir);
        } else if (video.getType().equals("on-video")) {
            Glide.with(context)
                    .load(video.getSrcPic())
                    .apply(new RequestOptions().placeholder(R.drawable.ic_video)) // 设置占位图
                    .into(imageView);
        } else if (video.getType().equals("on-picture")) {
            Glide.with(context)
                    .load(video.getSrcPic())
                    .apply(new RequestOptions().placeholder(R.drawable.ic_video)) // 设置占位图
                    .into(imageView);
        } else {//视频
            Glide.with(context)
                    .load(Uri.fromFile(new File(video.getPath())))
                    .into(imageView);
        }
        return v;
    }
}
