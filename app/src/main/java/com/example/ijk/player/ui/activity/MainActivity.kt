package com.example.ijk.player.ui.activity

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.example.ijk.player.R
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        findViewById<View>(R.id.button).setOnClickListener {
            val child = "Download/mm.mp4"
            val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                File(Environment.getStorageDirectory(), child)
            } else {
                File(Environment.getExternalStorageDirectory(), child)
            }
            PlayActivity.launch(this, "表哥我出来了哦", file.path)
        }
    }

}