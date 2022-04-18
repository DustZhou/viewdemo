package com.hudunzht.viewdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hudunzht.cropimageview.CropImageActivity;

/**
 * author: ZHT
 * date: 2022/4/13
 * desc: 主页。
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //    public MainActivity() {
//        super(R.layout.activity_main);
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_crop).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        int id = v.getId();
        if (id == R.id.tv_crop) {
            Log.e("11", "11");

            intent.setClass(this, CropImageActivity.class);
            startActivity(intent);
        }
    }
}