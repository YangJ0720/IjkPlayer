package com.example.ijk.player.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.example.ijk.player.R
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_READ = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        findViewById<View>(R.id.button).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, REQUEST_CODE_READ)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (REQUEST_CODE_READ == requestCode) {
            val size = permissions.size
            for (i in 0 until size) {
                if (Manifest.permission.READ_EXTERNAL_STORAGE == permissions[i] && PackageManager.PERMISSION_GRANTED == grantResults[i]) {
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
    }
}