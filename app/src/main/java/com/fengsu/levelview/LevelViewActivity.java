package com.fengsu.levelview;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fengsu.levelview.LevelView;
import com.fengsu.levelview.R;

/**
 * author: liuyu
 * data: 2022-4-19
 * desc: 启动View
 */
public class LevelViewActivity extends AppCompatActivity {

    private LevelView view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spirit_view);
        initView();
    }

    public void initView() {
        view = (LevelView)findViewById(R.id.lv_content);
        view.init(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.openSensor();
    }

    @Override
    protected void onPause() {
        super.onPause();
        view.closeSensor();
    }
}
