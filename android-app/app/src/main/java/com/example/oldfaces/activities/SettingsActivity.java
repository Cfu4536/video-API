package com.example.oldfaces.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.oldfaces.R;

public class SettingsActivity extends AppCompatActivity {
    Button clearFavoriteBtn;
    Button removeFavoriteBtn;
    Button saveIPAndPortBtn;
    Button savePasswordBtn;
    EditText ipEt;
    EditText portEt;
    EditText passwordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        setTitle("设置");

        clearFavoriteBtn = findViewById(R.id.settingClearFavorites);
        removeFavoriteBtn = findViewById(R.id.settingRemoveFavorites);
        saveIPAndPortBtn = findViewById(R.id.settingSaveIPAndPort);
        savePasswordBtn = findViewById(R.id.settingSavePassword);
        ipEt = findViewById(R.id.settingIP);
        portEt = findViewById(R.id.settingPort);
        passwordEt = findViewById(R.id.settingPassward);

        // 获取 SharedPreferences 实例+配置
        SharedPreferences sharedPreferences = getSharedPreferences("MyConfig", MODE_PRIVATE);
        String ip = sharedPreferences.getString("ip", "");
        int port = sharedPreferences.getInt("port", 12345);
        String myPwd = sharedPreferences.getString("password", "541818");

        //设置 editview 的值
        ipEt.setText(ip);
        portEt.setText(String.valueOf(port));
        passwordEt.setText(myPwd);

        saveIPAndPortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = ipEt.getText().toString();
                int port = Integer.parseInt(portEt.getText().toString());
                // 获取 Editor 对象
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // 写入数据
                editor.putString("ip", ip); // 存储字符串
                editor.putInt("port", port); // 存储整型
                // 提交更改
                editor.apply(); // 异步提交

                Toast.makeText(SettingsActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
            }
        });
        savePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myPwd = passwordEt.getText().toString();
                // 获取 Editor 对象
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // 写入数据
                editor.putString("password", myPwd); // 存储字符串
                // 提交更改
                editor.apply(); // 异步提交
                Toast.makeText(SettingsActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

}