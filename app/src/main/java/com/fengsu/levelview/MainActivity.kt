package com.fengsu.levelview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var spiritView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        onClick()
    }

    private fun onClick() {
        spiritView = findViewById(R.id.spirit_view_functions)
        spiritView.setOnClickListener {
            startActivity(Intent(this@MainActivity, LevelViewActivity::class.java))
        }
    }
}