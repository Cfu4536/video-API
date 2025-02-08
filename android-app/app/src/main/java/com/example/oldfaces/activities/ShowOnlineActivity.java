package com.example.oldfaces.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.oldfaces.R;


public class ShowOnlineActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String ip;
    int port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crawler);

        setTitle("在线资源");

        Button randomPicBtn = findViewById(R.id.randomPicBtn);
        Button onlineVideoBtn = findViewById(R.id.onlineVideoBtn);
        Button onlineImgBtn = findViewById(R.id.onlineVideoWebBtn);

        sharedPreferences = getSharedPreferences("MyConfig", MODE_PRIVATE);
        ip = sharedPreferences.getString("ip", "");
        port = sharedPreferences.getInt("port", 12345);

        randomPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ShowOnlineActivity.this, ReviewCrawlerImgActivity.class);
                startActivity(intent);

            }
        });

        onlineVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowOnlineActivity.this, ShowVideoActivity.class);
                intent.putExtra("dir", "<online_video>");
                intent.putExtra("subPath", "");
                intent.putExtra("name", "在线视频(接口版)");
                startActivity(intent);
            }
        });

        onlineImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://" + ip + ":" + port;
                Intent intent = new Intent(ShowOnlineActivity.this, BrowserActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("name", "在线视频(网页版)");
                startActivity(intent);
            }
        });
    }
}