package com.example.oldfaces.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.oldfaces.R;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    Button loginBtn;
    EditText userNameEdit;
    EditText passWordEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("登录");
        init();

        loginBtn = findViewById(R.id.login);
        userNameEdit = findViewById(R.id.userName);
        passWordEdit = findViewById(R.id.passWord);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usrName = userNameEdit.getText().toString();
                String passWord = passWordEdit.getText().toString();
                SharedPreferences sharedPreferences = getSharedPreferences("MyConfig", MODE_PRIVATE);
                String myPwd = sharedPreferences.getString("password", "541818");
                if (usrName.equals("Cfu") && passWord.equals(myPwd)) {
                    Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, GateActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    passWordEdit.setText("");
                    Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                }
                //创文件夹
                createFolder();
                //清缓存
                clearCache();
            }
        });
    }

    private void init() {
        //权限申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {// android 11  且 不是已经被拒绝
            // 先判断有没有权限
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1024);
            } else {
                Toast.makeText(MainActivity.this, "权限申请成功", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void createFolder() {
        String base = getString(R.string.base_path);
        String rootdir = getString(R.string.src_path);
        String cache = getString(R.string.cache_path);
        String data = getString(R.string.data_path);


        String[] dirPaths = new String[]{base, data, cache, rootdir + "row", rootdir + "video", rootdir + "my_video",
                rootdir + "my_story"};
        for (int i = 0; i < dirPaths.length; i++) {
            String dirPath = dirPaths[i];
            File dir = new File(dirPath);
            if (!dir.exists()) {
                //创建目录
                dir.mkdirs();
            }
        }
    }

    private void clearCache() {
        String cache = "/storage/emulated/0/OldFaces/cache/";
        File folder = new File(cache);
        File[] files = folder.listFiles();
        for (File file : files) {
            file.delete();
        }
    }
}