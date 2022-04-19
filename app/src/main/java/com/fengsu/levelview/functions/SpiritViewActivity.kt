package com.fengsu.levelview.functions

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.fengsu.levelview.LevelView
import com.fengsu.levelview.R

/**
 * author: liuyu
 * data: 2022-4-14
 * desc: 添加水平仪
 */
class SpiritViewActivity : Activity() {
    private lateinit var myview: LevelView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spirit_view)
        initView(myview)
    }

    private fun initView(view: View) {
        myview = view.findViewById(R.id.lv_content)
    }
}