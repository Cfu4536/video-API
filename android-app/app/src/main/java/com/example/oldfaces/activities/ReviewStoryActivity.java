package com.example.oldfaces.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.oldfaces.R;

import java.io.File;

public class ReviewStoryActivity extends AppCompatActivity {
    WebView webview;
    int fontSize = 120;
    String path;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.story_review_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_zoom:
                fontSize+=10;
                resettings();
                loadData(path);
                break;
            case R.id.action_zoom_out:
                fontSize-=10;
                resettings();
                loadData(path);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_story);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        path = intent.getStringExtra("path");
        getSupportActionBar().setTitle(title);

        webview = findViewById(R.id.story_webView);
        //settings
        resettings();

        //load
        loadData(path);

    }

    private void loadData(String path) {
        File file = new File(path);
        Uri uri = Uri.parse(file.getAbsolutePath());
        webview.loadUrl("file://" + uri.getPath()); // 注意这里采用file协议加载
    }

    private void resettings() {
        WebSettings settings = webview.getSettings();
        settings.setAllowFileAccess(true); // 允许加载文件资源
        settings.setTextZoom(fontSize); // 通过百分比来设置文字的大小，默认值是100。

    }

}