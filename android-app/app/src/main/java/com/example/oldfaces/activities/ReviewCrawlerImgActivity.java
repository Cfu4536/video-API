package com.example.oldfaces.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.oldfaces.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class ReviewCrawlerImgActivity extends AppCompatActivity {
    ArrayList<String> urlList = new ArrayList<String>();
    Stack<Integer> urlStack = new Stack<Integer>();
    PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_crawler);

        Button nextButton = (Button) findViewById(R.id.nextPicture);
        Button lastButton = (Button) findViewById(R.id.prevPicture);
        photoView = (PhotoView) findViewById(R.id.crawler_photo_view);
        createUrlList();
        loadPic(1);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPic(1);
            }
        });

        lastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPic(-1);
            }
        });

    }

    private void loadPic(int no) {
        //随机显示一张图片
        if (no == 1) {
            no = (int) (Math.random() * urlList.size());
            urlStack.push(no);
        } else {
            if (!urlStack.empty()){
                urlStack.pop();
            }
            if (urlStack.empty()) {
                Toast.makeText(this, "已经是第一张了", Toast.LENGTH_SHORT).show();
                return;
            } else {
                no = urlStack.peek();
            }
        }
        Glide.with(this)
                .load(urlList.get(no))
                .into(photoView);
    }

    private void createUrlList() {
        //按行读取文件内容
        String filePath = getString(R.string.data_path) + "/urls";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                urlList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}