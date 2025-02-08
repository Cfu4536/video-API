package com.example.oldfaces.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.oldfaces.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.nio.file.Paths;

public class ReviewImgActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String ip;
    int port;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Intent intent = getIntent();
        String imgPath = intent.getStringExtra("imgPath");
        String type = intent.getStringExtra("type");

        PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);
        photoView.setMaximumScale(10.0f); // 最大缩放比例
        photoView.setMinimumScale(1.0f); // 最小缩放比例

        if (type.equals("local")) {
            File file = new File(imgPath);
            Glide.with(this)
                    .load(file)
                    .into(photoView);
        }else if(type.equals("url")){
            Glide.with(this)
                    .load(imgPath)
                    .apply(new RequestOptions().placeholder(R.drawable.ic_video)) // 设置占位图
                    .into(photoView);
        }else if (type.equals("online")) {
            //获取配置
            sharedPreferences = getSharedPreferences("MyConfig", MODE_PRIVATE);
            ip = sharedPreferences.getString("ip", "");
            port = sharedPreferences.getInt("port", 12345);
            // 设置
            String url = "http://" + ip + ":" + port + "/get_image?img_path=" + imgPath;
            Glide.with(this)
                    .load(url)
                    .apply(new RequestOptions().placeholder(R.drawable.ic_video)) // 设置占位图
                    .into(photoView);
        }


    }
}