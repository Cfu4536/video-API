package com.example.oldfaces.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.oldfaces.R;
import com.example.oldfaces.adapter.PeopleAdapter;
import com.example.oldfaces.points.People;
import com.example.oldfaces.sqlite.FavotiteVideoDataBase;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClassActivity extends AppCompatActivity {
    ListView lv;
    ArrayList<People> peopleArrayList;
    PeopleAdapter adapter;
    FavotiteVideoDataBase db;

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
//        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);
        setTitle("本地资源");

        db = new FavotiteVideoDataBase(this);
        lv = findViewById(R.id.peopleListView);
        peopleArrayList = new ArrayList<>();
        createPeopleList();
        adapter = new PeopleAdapter(this, peopleArrayList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent;
                if (peopleArrayList.get(i).getDir().equals("video")) {
                    intent = new Intent(ClassActivity.this, ShowVideoActivity.class);
                    intent.putExtra("name", peopleArrayList.get(i).getName());
                    intent.putExtra("count", peopleArrayList.get(i).getCount());
                    intent.putExtra("dir", getString(R.string.src_path) + peopleArrayList.get(i).getDir());
                } else if (peopleArrayList.get(i).getDir().equals("my_video")) {
                    intent = new Intent(ClassActivity.this, ShowVideoActivity.class);
                    intent.putExtra("name", peopleArrayList.get(i).getName());
                    intent.putExtra("dir", getString(R.string.src_path) + peopleArrayList.get(i).getDir());
                } else if (peopleArrayList.get(i).getDir().equals("my_story")) {
                    intent = new Intent(ClassActivity.this, ShowStoryActivity.class);
                    intent.putExtra("name", peopleArrayList.get(i).getName());
                    intent.putExtra("count", peopleArrayList.get(i).getCount());
                    intent.putExtra("dir", peopleArrayList.get(i).getDir());
                } else if (peopleArrayList.get(i).getDir().equals("<my_favorites>")) {
                    intent = new Intent(ClassActivity.this, ShowVideoActivity.class);
                    intent.putExtra("name", peopleArrayList.get(i).getName());
                    intent.putExtra("dir", peopleArrayList.get(i).getDir());

                } else {//pic
                    intent = new Intent(ClassActivity.this, ShowActivity.class);
                    intent.putExtra("name", peopleArrayList.get(i).getName());
                    intent.putExtra("count", peopleArrayList.get(i).getCount());
                    intent.putExtra("dir", peopleArrayList.get(i).getDir());
                }
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.class_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.classRefresh:
                createPeopleList();
                adapter.notifyDataSetChanged();
                Toast.makeText(ClassActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.classClearCache:
                new AlertDialog.Builder(this)
                        .setTitle("清理缓存")
                        .setMessage("将清除：\n-解密图片cache文件\n-视频封面缩略图")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // 用户点击了“是”
                                clearCache();
                                //清除glide缓存
                                GlideCacheUtil.getInstance().clearImageDiskCache(ClassActivity.this);
                                Toast.makeText(ClassActivity.this, "清除缓存成功", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // 用户点击了“否”
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
            case R.id.classSettings:
                Intent intent = new Intent(ClassActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createPeopleList() {
        peopleArrayList.clear();
        //名字映射
        Map<String, People> hashmap = new HashMap<>();
        hashmap.put("aaa", new People("本地视频", "video", R.drawable.icon, 0));
        hashmap.put("aac", new People("我的视频", "my_video", R.drawable.ic_video, 0));
        hashmap.put("aab", new People("我的图片", "row", R.drawable.ic_picture, 0));
        hashmap.put("aad", new People("我的小说", "my_story", R.drawable.ic_book, 0));
        hashmap.put("aae", new People("我的收藏", "<my_favorites>", R.drawable.ic_favorites, 0));


        String[] keys = hashmap.keySet().toArray(new String[hashmap.keySet().size()]);
        Arrays.sort(keys);

        for (String key : keys) {
            if (key == "aae") {
                db.open();
                hashmap.get(key).setCount(db.countAll());
                db.close();
                peopleArrayList.add(hashmap.get(key));
                continue;
            }
            File file = new File(getString(R.string.src_path) + hashmap.get(key).getDir());
            File[] tempList = file.listFiles();
            People people = hashmap.get(key);
            if (people != null) {
                people.setCount(tempList.length);
                peopleArrayList.add(people);
            }
        }

    }

    private void clearCache() {
        String cache = getString(R.string.cache_path);
        File folder = new File(cache);
        File[] files = folder.listFiles();
        for (File file : files) {
            file.delete();
        }
    }
}