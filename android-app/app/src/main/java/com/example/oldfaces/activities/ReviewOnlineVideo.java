package com.example.oldfaces.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.oldfaces.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.concurrent.atomic.AtomicBoolean;

public class ReviewOnlineVideo extends AppCompatActivity {
    private PlayerView playerView;
    private ExoPlayer player;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_online_video);

        //接收数据
        Intent intent = getIntent();
        String videoPath = intent.getStringExtra("path");

        // 初始化 PlayerView
        playerView = findViewById(R.id.player_view);

        // 创建 LoadControl，并设置缓冲参数
        DefaultLoadControl loadControl = new DefaultLoadControl.Builder()
                .setBufferDurationsMs(5000, 50000,
                        5000, 5000)
                .build();

        // 初始化 ExoPlayer
        player = new ExoPlayer.Builder(this).setLoadControl(loadControl).build();
        playerView.setPlayer(player);

        // 添加长按倍速
        playerView.setOnTouchListener(new View.OnTouchListener() {
            final AtomicBoolean isAdjustPlaySpeed = new AtomicBoolean(false);

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 500毫秒
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isAdjustPlaySpeed.set(true);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 在此处执行希望延迟的代码
                                // 例如：执行某个操作，或者恢复UI状态
                                if (isAdjustPlaySpeed.get()) {
                                    adjustPlaybackSpeed(event);
                                }
                            }
                        }, 200); // 延迟2000毫秒（2秒）
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // 释放时重置速度
                        isAdjustPlaySpeed.set(false);
                        resetPlaybackSpeed();
                        v.performClick(); // 调用performClick以允许PlayerView继续处理
                        return true;
                }
                return false;
            }
        });

        // 获取 SharedPreferences 实例
        SharedPreferences sharedPreferences = getSharedPreferences("MyConfig", MODE_PRIVATE);
        String ip = sharedPreferences.getString("ip", "");
        int port = sharedPreferences.getInt("port", 12345);

        // 设置视频源
        String videoUrl;
        assert videoPath != null;
        if (videoPath.contains("http://" + ip + ":" + port + "/get_video?file_path=")) {
            videoUrl = videoPath;
        } else {
            videoUrl = "http://" + ip + ":" + port + "/get_video?file_path=" + videoPath; // 替换为你的视频URL
        }
        MediaItem mediaItem = MediaItem.fromUri(videoUrl);

        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    private void adjustPlaybackSpeed(MotionEvent event) {
        player.setPlaybackSpeed(3.0f);
    }

    private void resetPlaybackSpeed() {
        player.setPlaybackSpeed(1.0f); // 重置为正常速度
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.release(); // 释放资源
    }
}