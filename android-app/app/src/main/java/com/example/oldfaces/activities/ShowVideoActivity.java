package com.example.oldfaces.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oldfaces.R;
import com.example.oldfaces.adapter.VideoAdapter;
import com.example.oldfaces.points.Video;
import com.example.oldfaces.sqlite.FavotiteVideoDataBase;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShowVideoActivity extends AppCompatActivity {
    ArrayList<String> paths = new ArrayList<>();
    ArrayList<Video> list = new ArrayList<>();
    VideoAdapter adapter;
    ListView lv;
    TextView tvSize;
    int sumSize = 0;
    String dir;
    String name;
    String subPath;
    FavotiteVideoDataBase db;
    SharedPreferences sharedPreferences;
    String ip;
    int port;

    //option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vedio_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refreshOnlineVideoList:
                if (dir.equals("<online_video>")) {
                    createListByRequest(subPath);
                    break;
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.videoListSettings:
                Intent intent = new Intent(ShowVideoActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.videoClearCache:
                new AlertDialog.Builder(this)
                        .setTitle("清理缓存")
                        .setMessage("将清除：\n-解密图片cache文件\n-视频封面缩略图")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // 用户点击了“是”
                                clearCache();
                                //清除glide缓存
                                GlideCacheUtil.getInstance().clearImageDiskCache(ShowVideoActivity.this);
                                Toast.makeText(ShowVideoActivity.this, "清除缓存成功", Toast.LENGTH_SHORT).show();
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
            case R.id.fileName:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    list.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.fileNameReverse:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    list.sort((o2, o1) -> o1.getName().compareTo(o2.getName()));
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.fileSize:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    list.sort((o1, o2) -> Long.compare(o1.getSize(), o2.getSize()));
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.fileSizeReverse:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    list.sort((o2, o1) -> Long.compare(o1.getSize(), o2.getSize()));
                }
                adapter.notifyDataSetChanged();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    //context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.video_context, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        ContextMenu.ContextMenuInfo menuInfo = item.getMenuInfo();
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = adapterContextMenuInfo.position;
        String itemPath = list.get(position).getPath();
        switch (item.getItemId()) {

            case R.id.deleteItem:
                //确认删除弹窗
                new AlertDialog.Builder(this)
                        .setTitle("删除")
                        .setMessage("确定要删除该视频文件吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            public void onClick(DialogInterface dialog, int which) {
                                // 用户点击了“是”
                                if (dir.equals("<online_video>")) {//在线视频
                                    Video v = list.get(position);
                                    String itemPath = null;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        itemPath = Paths.get(v.getPath(),v.getName()).toString();
                                    }
                                    String url = "http://" + ip + ":" + port + "/delete_video?file_path=" + itemPath;
                                    new Thread(() -> {
                                        HttpURLConnection connection = null;
                                        try {
                                            // 创建 URL 对象
                                            URL deleteUrl = new URL(url);
                                            // 开启连接
                                            connection = (HttpURLConnection) deleteUrl.openConnection();
                                            // 设置请求方式
                                            connection.setRequestMethod("DELETE");
                                            // 获取响应码
                                            System.out.println(connection.getResponseCode());
                                            connection.disconnect(); // 关闭连接
                                        } catch (Exception e) {
                                            e.printStackTrace(); // 打印异常信息
                                        }
                                    }).start();
                                    createListByRequest(subPath);
                                    adapter.notifyDataSetChanged();
                                }else {//本地视频
                                    if (deleteFileByPath(list.get(position).getPath())) {
                                        list.remove(position);
                                        adapter.notifyDataSetChanged();
                                    }
                                }

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
            case R.id.favoriteItem:
                if (dir.equals("<online_video>")) {
                    Toast.makeText(this, "在线视频：无操作权限", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (list.get(position).getType().equals("dir")) {
                    Toast.makeText(this, "请选择视频文件", Toast.LENGTH_SHORT).show();
                    break;
                }
                itemPath = list.get(position).getPath();
                list.get(position).setFavorite(true);
                if (db.insertData(itemPath, FavotiteVideoDataBase.TYPE_VIDEO)) {
                    Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "收藏失败", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.unfavoriteItem:
                if (dir.equals("<online_video>")) {
                    Toast.makeText(this, "在线视频：无操作权限", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (list.get(position).getType().equals("dir")) {
                    Toast.makeText(this, "请选择视频文件", Toast.LENGTH_SHORT).show();
                    break;
                }
                itemPath = list.get(position).getPath();
                list.get(position).setFavorite(false);
                if (db.deleteData(itemPath)) {
                    Toast.makeText(this, "取消收藏成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "取消收藏失败", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
                break;
        }
        return super.onContextItemSelected(item);
    }


    private boolean deleteFileByPath(String filePath) {
        File file = new File(filePath);
        if (file.isFile()) {
            file.delete();
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
            return true;
        }
        Toast.makeText(this, "无法删除文件夹", Toast.LENGTH_SHORT).show();
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);

        //数据库
        db = new FavotiteVideoDataBase(this);
        db.open();

        //获取配置
        sharedPreferences = getSharedPreferences("MyConfig", MODE_PRIVATE);
        ip = sharedPreferences.getString("ip", "");
        port = sharedPreferences.getInt("port", 12345);

        //获取数据
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        dir = intent.getStringExtra("dir");

        //设置标题
        getSupportActionBar().setTitle(name);

        //显示总大小
        tvSize = findViewById(R.id.allFileSize);

        //构建list
        if (dir.equals("<my_favorites>")) {
            createListByDB();
            tvSize.setText("总共：" + sumSize + "个最爱");
        } else if (dir.equals("<online_video>")) {
            subPath = intent.getStringExtra("subPath");
            createListByRequest(subPath);
        } else {
            createListByPath(dir);
            if (sumSize >= 1024) {
                tvSize.setText(String.format("总大小：%.2f GB", sumSize / 1024.0));
            } else {
                tvSize.setText("总大小：" + sumSize + "MB");
            }
        }


        //listview
        lv = findViewById(R.id.videoListView);
        adapter = new VideoAdapter(this, list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Video videoOrDir = list.get(i);
                if (dir.equals("<online_video>")) {
                    String itemPath = "";
                    try {
                        itemPath = Paths.get(videoOrDir.getPath(), URLEncoder.encode(videoOrDir.getName(), "UTF-8")).toString();
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    if (videoOrDir.getType().equals("dir")) {
                        //文件夹
                        Intent intent = new Intent(ShowVideoActivity.this, ShowVideoActivity.class);
                        intent.putExtra("dir", "<online_video>");
                        intent.putExtra("subPath", itemPath);
                        intent.putExtra("name", videoOrDir.getName());
                        startActivity(intent);
                    } else if (videoOrDir.getType().equals("on-picture")) {
                        Intent intent = new Intent(ShowVideoActivity.this, ReviewImgActivity.class);
                        intent.putExtra("imgPath", itemPath);
                        intent.putExtra("type", "online");
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(ShowVideoActivity.this, ReviewOnlineVideo.class);
                        intent.putExtra("path", itemPath);
//                        createPathList(dir);//创建当前文件夹下的视频路径列表
//                        intent.putExtra("paths", (Serializable) paths);
//                        intent.putExtra("index", i);
                        startActivity(intent);
                    }
                } else {
                    if (videoOrDir.getType().equals("dir")) {
                        //文件夹
                        Intent intent = new Intent(ShowVideoActivity.this, ShowVideoActivity.class);
                        intent.putExtra("name", videoOrDir.getName());
                        intent.putExtra("dir", videoOrDir.getPath());
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(ShowVideoActivity.this, ReviewVideoActivity.class);
                        createPathList(dir);//创建当前文件夹下的视频路径列表
                        intent.putExtra("paths", (Serializable) paths);
                        intent.putExtra("index", i);
                        startActivity(intent);
                    }
                }
            }
        });
        registerForContextMenu(lv);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createListByRequest(String subPath) {
        list.clear();
        sumSize = 0;
        // 初始化ExecutorService
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        String urlString = "http://" + ip + ":" + port + "/get_video_list";
        if (!subPath.isEmpty()) {
            urlString += "?folder_path=" + subPath;
        }

        // 在子线程中执行网络请求
        String finalUrlString = urlString;
        executorService.submit(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(finalUrlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // 获取响应
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    // 在主线程更新UI
                    runOnUiThread(() -> {
                        Toast.makeText(this, "请求成功", Toast.LENGTH_LONG).show();
                        String resp = response.toString();
                        try {
                            JSONObject jsonObject = new JSONObject(resp);
                            // 提取 children
                            JSONArray children = jsonObject.getJSONArray("children");
                            JSONArray type = jsonObject.getJSONArray("type");
                            JSONArray size = jsonObject.getJSONArray("size");
                            for (int i = 0; i < children.length(); i++) {
                                String fileName = children.getString(i);
                                String fileType = type.getString(i);
                                long fileSize = size.getInt(i);
                                Video v = new Video(fileName, subPath, (int) fileSize);
                                v.setType(fileType);
                                v.setFavorite(false);
                                try {
                                    fileName = URLEncoder.encode(fileName, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    throw new RuntimeException(e);
                                }
                                String filePath = Paths.get(subPath, fileName).toString();
                                if (fileType.equals("on-video")) {
                                    v.setSrcPic("http://" + ip + ":" + port + "/get_video_img?video_path=" + filePath);
                                } else if (fileType.equals("on-picture")) {
                                    v.setSrcPic("http://" + ip + ":" + port + "/get_image?img_path=" + filePath);
                                }
                                System.out.println(v.getSrcPic());
                                list.add(v);
                                sumSize += (int) fileSize;
                            }
                            adapter.notifyDataSetChanged();
                            if (sumSize >= 1024) {
                                tvSize.setText(String.format("总大小：%.2f GB", sumSize / 1024.0));
                            } else {
                                tvSize.setText("总大小：" + sumSize + "MB");
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    // 处理错误情况
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Error: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                // 处理网络错误
                runOnUiThread(() -> {
                    Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show();
                });
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    @SuppressLint("Range")
    private void createListByDB() {
        list.clear();
        sumSize = 0;
        Cursor cursor = db.queryAll();
        System.out.println("createListByDB");
        while (cursor.moveToNext()) {
            String type = cursor.getString(cursor.getColumnIndex("type"));
            String path = cursor.getString(cursor.getColumnIndex("path"));
            if (type.equals("video")) {
                File file = new File(path);
                int size = (int) (file.length() / (1024 * 1024));
                Video v = new Video(file.getName(), path, size);
                v.setFavorite(true);
                list.add(v);
                sumSize++;
            }
        }
    }

    private void createListByPath(String dir) {
        list.clear();
        File file = new File(dir);
        File[] tempList = file.listFiles();
        Arrays.sort(tempList);
        Video v = null;
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {//是文件
                String videoPathRow = tempList[i].getPath();
                String videoName = tempList[i].getName().substring(0, tempList[i].getName().length() - 4);
                if (tempList[i].getName().endsWith(".cfu")) {//有效文件
                    int size = (int) (tempList[i].length() / (1024 * 1024));
                    sumSize += size;
                    v = new Video(videoName, videoPathRow, size);
                    if (db.isExist(videoPathRow)) {
                        v.setFavorite(true);
                    }
                }
            } else {//是文件夹
                String videoPathRow = tempList[i].getPath();
                String videoName = tempList[i].getName();
                int size = (int) calSize(tempList[i]) / (1024 * 1024);
                sumSize += size;
                v = new Video(videoName, videoPathRow, size, "dir");
                if (db.isExist(videoPathRow)) {
                    v.setFavorite(true);
                }
            }
            list.add(v);
        }
    }

    private long calSize(File folder) {
        //递归计算文件夹大小
        if (folder.isFile()) return folder.length();
        long size = 0;
        for (File file : folder.listFiles()) {
            size += calSize(file);
        }
        return size;
    }

    private void createPathList(String dir) {
        //购进播放文件列表
        paths.clear();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getType() == "video") {//不是文件夹
                paths.add(list.get(i).getPath());
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