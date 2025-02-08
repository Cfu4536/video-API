package com.example.oldfaces.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.example.oldfaces.R;

public class GateActivity extends AppCompatActivity {
    ImageButton localButton, internetButton, crawlerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate);

        localButton = findViewById(R.id.localButton);
        internetButton = findViewById(R.id.internetButton);
        crawlerButton = findViewById(R.id.crawlerButton);

        //init background color
        localButton.getBackground().setAlpha(180);
        internetButton.getBackground().setAlpha(180);
        crawlerButton.getBackground().setAlpha(180);
        //点击事件
        localButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GateActivity.this, ClassActivity.class);
                startActivity(intent);
            }
        });

        internetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GateActivity.this, ShowUrlActivity.class);
                startActivity(intent);
            }
        });

        crawlerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(GateActivity.this, "正在开发中", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GateActivity.this, ShowOnlineActivity.class);
                startActivity(intent);
            }
        });

        //touch events
        localButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //更改为按下时的背景图片
                    localButton.getBackground().setAlpha(50);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //改为抬起时的图片
                    localButton.getBackground().setAlpha(180);
                }
                return false;
            }
        });
        internetButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //更改为按下时的背景图片
                    internetButton.getBackground().setAlpha(50);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //改为抬起时的图片
                    internetButton.getBackground().setAlpha(180);
                }
                return false;
            }
        });
        crawlerButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //更改为按下时的背景图片
                    crawlerButton.getBackground().setAlpha(50);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //改为抬起时的图片
                    crawlerButton.getBackground().setAlpha(180);
                }
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gate_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gateSettings:
                Intent intent = new Intent(GateActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}