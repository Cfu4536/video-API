package com.example.oldfaces.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.oldfaces.R;

import java.nio.file.Paths;
import java.util.Stack;

public class BrowserActivity extends AppCompatActivity {
    WebView webView;
    String UrlName;
    String Url;
    private Stack<String> historyStack = new Stack<>();

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        webView = findViewById(R.id.Browser);
        Intent intent = getIntent();
        UrlName = intent.getStringExtra("name");
        Url = intent.getStringExtra("url");
        //设置标题
        setTitle(UrlName);

        webView.getSettings().setJavaScriptEnabled(true);//支持javascript
        webView.getSettings().setBuiltInZoomControls(true);//设置出现缩放工具
        webView.getSettings().setDisplayZoomControls(false);//设置是否显示缩放工具
        webView.getSettings().setSupportZoom(true);// 设置可以支持缩放
        webView.getSettings().setUseWideViewPort(true);//扩大比例的缩放
        //自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        webView.getSettings().setDomStorageEnabled(true);//DOM Storage
//        webView.getSettings().setUserAgentString("User-Agent:Android");//设置用户代理，一般不用

        //如果不设置WebViewClient，请求会跳转系统浏览器
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.contains("file_path")){
                    Intent intent = new Intent(BrowserActivity.this, ReviewOnlineVideo.class);
                    intent.putExtra("path", url);
                    startActivity(intent);
                }else if (url.contains("get_image")){
                    Intent intent = new Intent(BrowserActivity.this, ReviewImgActivity.class);
                    intent.putExtra("imgPath", url);
                    intent.putExtra("type","url");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(BrowserActivity.this, BrowserActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("name", UrlName);
                    startActivity(intent);
                }
                return true;
            }
        });

//        webView.setOnLongClickListener(new View.OnLongClickListener() {
//            // 重写长按事件
//            @Override
//            public boolean onLongClick(View v) {
//                return true;  // 返回 true 表示长按事件被消费掉，不会触发选择
//            }
//        });

        //加载网页
        webView.loadUrl(Url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.browser_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.coptLink:
                ClipboardManager mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", webView.getUrl());
                mClipboardManager.setPrimaryClip(mClipData);
                Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.openBrowser:
                openBrowser(webView.getUrl());
                break;
            case R.id.zoomTool:
                if (webView.getSettings().getBuiltInZoomControls()) {
                    webView.getSettings().setBuiltInZoomControls(false);// 设置可以支持缩放
                } else {
                    webView.getSettings().setBuiltInZoomControls(true);// 设置出现缩放工具
                }
            case R.id.refreshWeb:
                webView.reload();
        }
        return super.onOptionsItemSelected(item);
    }

    public void openBrowser(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}