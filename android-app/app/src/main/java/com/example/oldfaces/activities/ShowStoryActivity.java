package com.example.oldfaces.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.oldfaces.R;
import com.example.oldfaces.points.Story;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class ShowStoryActivity extends AppCompatActivity {
    ListView lv;
    ArrayAdapter<String> adapter = null;
    String[] titleList = null;
    ArrayList<Story> storyList = new ArrayList<>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.story_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_story:
                refresh();
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "刷新成功", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_story);

        //获取数据
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String dir = intent.getStringExtra("dir");
        int count = intent.getIntExtra("count", 0);
        Toast.makeText(this, name + " " + count, Toast.LENGTH_SHORT).show();
        //设置标题
        getSupportActionBar().setTitle(name);
        //list data
        createDataList(dir);
        lv = findViewById(R.id.story_listView);
        //lv
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, titleList);//适配器
        lv.setAdapter(adapter);
        //listener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Story story = storyList.get(i);
                Intent intent = new Intent(ShowStoryActivity.this, ReviewStoryActivity.class);
                intent.putExtra("title", story.getTitle());
                intent.putExtra("path", story.getPath());
                startActivity(intent);
            }
        });
    }

    private void createDataList(String dir) {
        File file = new File("/storage/emulated/0/OldFaces/src/" + dir);
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                String storyPathRow = tempList[i].getPath();
                String storyName = tempList[i].getName().substring(0, tempList[i].getName().length() - 4);
                storyList.add(new Story(storyPathRow));
            }
        }
        Collections.shuffle(storyList);
        //title list
        titleList = new String[storyList.size()];
        for (int i = 0; i < storyList.size(); i++) {
            titleList[i] = storyList.get(i).getTitle();
        }

    }

    private void refresh() {
        Collections.shuffle(storyList);
        //title list
        for (int i = 0; i < storyList.size(); i++) {
            titleList[i] = storyList.get(i).getTitle();
        }
    }
}