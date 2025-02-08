package com.example.oldfaces.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.oldfaces.R;
import com.example.oldfaces.adapter.URLAdapter;
import com.example.oldfaces.dialogs.UrlChangeDialog;
import com.example.oldfaces.points.URL;
import com.example.oldfaces.sqlite.UrlsDataBase;

import java.util.ArrayList;

public class ShowUrlActivity extends AppCompatActivity {
    URLAdapter urlAdapter;
    ArrayList<URL> list = new ArrayList<>();
    ListView lv;
    UrlsDataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url);

        setTitle("全网资源");
        db = new UrlsDataBase(this);
        db.open();

//        //插入
//        String n = "koxueyuan";
//        String u = "https://sky-appears-blue.koxueyuan1qqq111.xyz/ko";
//        db.insertData(n, u);

        urlAdapter = new URLAdapter(this, list);
        lv = findViewById(R.id.urlList);
        lv.setAdapter(urlAdapter);
        registerForContextMenu(lv);
        //listener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ShowUrlActivity.this, BrowserActivity.class);
                intent.putExtra("url", list.get(i).getUrl());
                intent.putExtra("name", list.get(i).getName());
                startActivity(intent);

            }
        });
        //刷新
        upgradeListView();
    }

    @SuppressLint("Range")
    public void upgradeListView() {
        list.clear();
        //查询
        Cursor c = db.queryAll();
        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndex("name"));
            String url = c.getString(c.getColumnIndex("url"));
            System.out.println(name + " " + url);
            list.add(new URL(url, name));
        }
        urlAdapter.notifyDataSetChanged();

    }

    //context menu
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        ContextMenu.ContextMenuInfo menuInfo = item.getMenuInfo();
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = adapterContextMenuInfo.position;
        switch (item.getItemId()) {
            case R.id.copyURL:
                ClipboardManager mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", list.get(position).getUrl());
                mClipboardManager.setPrimaryClip(mClipData);
                Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.changeURL:
                new UrlChangeDialog(this, "修改").showDialog(list.get(position), new UrlChangeDialog.OnSubmitListener() {
                    @Override
                    public void onSubmit(URL uptatedata) {
                        String name = uptatedata.getName();
                        String url = uptatedata.getUrl();
                        db.updateData(list.get(position).getName(), list.get(position).getUrl(), name, url);
                        upgradeListView();
                    }
                });

                break;
            case R.id.deleteURL:
                db.deleteData(list.get(position).getName(), list.get(position).getUrl());
                upgradeListView();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.url_context, menu);
    }

    //optiom menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.url_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addUrl:
                new UrlChangeDialog(this, "添加").showDialog(null, new UrlChangeDialog.OnSubmitListener() {
                    @Override
                    public void onSubmit(URL uptatedata) {
                        db.insertData(uptatedata.getName(), uptatedata.getUrl());
                        upgradeListView();
                    }
                });
                Toast.makeText(this, "正在添加", Toast.LENGTH_SHORT).show();
                break;
            case R.id.refrashUrl:
                upgradeListView();
                Toast.makeText(this, "刷新成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.urlsSettings:
                Intent intent = new Intent(ShowUrlActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}