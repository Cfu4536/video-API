package com.example.oldfaces.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.oldfaces.R;

import java.util.ArrayList;

public class ReviewVideoActivity extends AppCompatActivity {
    int videoIndex = 0;
    ArrayList<String> videoPaths;
    String videoPath;
    VideoView videoView;


    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        Toast.makeText(this, "您已经离开此页面", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(getApplicationContext(), "横屏", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "竖屏", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_video);

        videoView = (VideoView) findViewById(R.id.videoView);
        //接收数据
        Intent intent = getIntent();
        videoPaths = (ArrayList<String>) intent.getSerializableExtra("paths");
        videoIndex = intent.getIntExtra("index", 0);
        videoPath = videoPaths.get(videoIndex);

        //加载指定的视频文件
        videoView.setVideoPath(videoPath);

        //创建MediaController对象
        myMediaController mediaController = new myMediaController(this);

        //VideoView与MediaController建立关联
        videoView.setMediaController(mediaController);

        //自动播放视频
        if (!videoView.isPlaying()) {
            videoView.start();
        }

        //循环播放
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                nextVideo();
                videoView.start();
                Toast.makeText(ReviewVideoActivity.this, "重新播放", Toast.LENGTH_SHORT).show();
            }
        });

//        //点击暂停
//        videoView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (videoView.isPlaying()) {
//                    videoView.pause();
//                } else {
//                    videoView.start();
//                }
//                return false;
//            }
//        });


        //设置下一个视频监听事件
        mediaController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextVideo();
                Toast.makeText(ReviewVideoActivity.this, "下一首", Toast.LENGTH_SHORT).show();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevVideo();
                Toast.makeText(ReviewVideoActivity.this, "上一首", Toast.LENGTH_SHORT).show();
            }
        });

        //让VideoView获取焦点
        videoView.requestFocus();
    }

    private void nextVideo() {
        if (videoIndex < videoPaths.size() - 1) {
            videoIndex++;
        } else {
            videoIndex = 0;
        }
        updatePath();
    }

    private void prevVideo() {
        if (videoIndex > 0) {
            videoIndex--;
        } else {
            videoIndex = videoPaths.size() - 1;
        }
        updatePath();
    }

    private void updatePath() {
        videoPath = videoPaths.get(videoIndex);
        videoView.setVideoPath(videoPath);
    }


    class myMediaController extends MediaController {
        Context context;

        @Override
        public void setAnchorView(View view) {
            super.setAnchorView(view);

            Button searchButton = new Button(context);
            searchButton.setText("");
            searchButton.setBackgroundColor(0x00000000);
            searchButton.setTextColor(0xFFFFFFFF);
            searchButton.setBackgroundResource(android.R.drawable.ic_menu_zoom);
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        // 当前为竖屏
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        // 当前为横屏
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                }
            });
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.RIGHT;
            params.width = 100;
            addView(searchButton, params);
        }

        public myMediaController(Context context) {
            super(context);
            this.context = context;
        }

        @Override
        public void hide() {
            super.hide();
        }

        @Override
        public void show() {
            super.show(1000000000);
        }

    }
}