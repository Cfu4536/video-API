package com.example.oldfaces.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oldfaces.R;
import com.example.oldfaces.adapter.ImageAdapter;
import com.example.oldfaces.points.Image;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ShowActivity extends AppCompatActivity {
    ListView lv;
    TextView textView;
    ArrayList<Image> list = new ArrayList<>();
    ImageAdapter adapter;
    String name;
    String dir;
    int showFirstId = 0;

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
//        System.exit(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //清除文件缓存
        clearCache();
    }

    //context menu
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        ContextMenu.ContextMenuInfo menuInfo = item.getMenuInfo();
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = adapterContextMenuInfo.position;
        switch (item.getItemId()) {
            case R.id.deleteItem:
//                File("/storage/emulated/0/OldFaces/src/" + dir);
//                "/storage/emulated/0/OldFaces/cache/"
                String itemPath = list.get(position).getSrcPath();
                String itemName = itemPath.split("/")[itemPath.split("/").length - 1];
                itemPath = "/storage/emulated/0/OldFaces/src/" + dir + "/" + itemName;
                deleteFileByPath(itemPath);
                list.remove(position);
                adapter.notifyDataSetChanged();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.video_context, menu);
    }

    private void deleteFileByPath(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);


        //获取数据
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        dir = intent.getStringExtra("dir");
        int count = intent.getIntExtra("count", 0);
        //设置标题
        getSupportActionBar().setTitle(name);

        //listView
        textView = findViewById(R.id.progress_show);
        ViewGroup parentView = (ViewGroup) textView.getParent();
        lv = findViewById(R.id.showListView);
        adapter = new ImageAdapter(this, list);
        lv.setAdapter(adapter);

        //createList
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 执行耗时操作
                long t1 = System.currentTimeMillis();
                createList(dir);
                // 更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //移除textView
                        if (parentView != null) {
                            parentView.removeView(textView);
                        }
                        adapter.notifyDataSetChanged();
                        Toast.makeText(ShowActivity.this, "加载完成-" + name + ":" + count + "-用时:" + (System.currentTimeMillis() - t1) + "ms", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String imgPath = list.get(i).getSrcPath();
                Intent intent = new Intent(ShowActivity.this, ReviewImgActivity.class);
                intent.putExtra("imgPath", imgPath);
                intent.putExtra("type","local");
                startActivity(intent);
            }
        });

        registerForContextMenu(lv);
    }

    private void createList(String dir) {
        File file = new File("/storage/emulated/0/OldFaces/src/" + dir);
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                String imgPathRow = tempList[i].getPath();
                String imgPathNew = "/storage/emulated/0/OldFaces/cache/" + tempList[i].getName();
                System.out.println(imgPathRow);

                //解密代码
                BufferedInputStream bis = null;
                try {
                    bis = new BufferedInputStream(new FileInputStream(imgPathRow));
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imgPathNew));
                    int b;
                    try {
                        while ((b = bis.read()) != -1) {
                            bos.write(b ^ 123);
                        }
                        bis.close();
                        bos.close();
                        list.add(new Image(imgPathNew));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("已加载：" + list.size() + "/" + tempList.length);
                    }
                });
            }
        }
    }

    public class MyTask implements Runnable {
        private String imgPathRow;
        private String imgPathNew;

        public MyTask(String imgPathRow, String imgPathNew) {
            this.imgPathRow = imgPathRow;
            this.imgPathNew = imgPathNew;
        }

        @Override
        public void run() {
            // 在这里执行具体的任务，可以使用传递进来的参数 param
            //解密代码
            BufferedInputStream bis = null;
            try {
                bis = new BufferedInputStream(new FileInputStream(imgPathRow));
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imgPathNew));
                int b;
                try {
                    while ((b = bis.read()) != -1) {
                        bos.write(b ^ 123);
                    }
                    bis.close();
                    bos.close();
                    list.add(new Image(imgPathNew));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("已加载：" + list.size() + "/");
                }
            });
        }
    }

    private void createList_executor(String dir) {
        File file = new File("/storage/emulated/0/OldFaces/src/" + dir);
        File[] tempList = file.listFiles();
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                String imgPathRow = tempList[i].getPath();
                String imgPathNew = "/storage/emulated/0/OldFaces/cache/" + tempList[i].getName();
                Future<?> future = executor.submit(new MyTask(imgPathRow, imgPathNew));
                try {
                    future.get(); // 等待任务完成
                } catch (InterruptedException | ExecutionException e) {
                    // 处理异常
                }
            }
        }
        executor.shutdown(); // 停止接收新任务

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