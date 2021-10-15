package com.example.ijk.player.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import com.example.ijk.player.ui.activity.PlayActivity
import com.example.ijk.player.ui.view.IjkVideoView
import java.io.File

class FloatingService : Service() {

    private lateinit var mView: IjkVideoView

    override fun onBind(intent: Intent): IBinder {
        return MyBinder()
    }

    override fun onCreate() {
        super.onCreate()
        println("onCreate")
        val child = "Download/mm.mp4"
        val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(Environment.getStorageDirectory(), child)
        } else {
            File(Environment.getExternalStorageDirectory(), child)
        }
        showFloatWindow(file.path)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        println("onStartCommand")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        println("onDestroy")
        hideFloatWindow()
    }

    private fun showFloatWindow(path: String) {
        val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val view = IjkVideoView(this)
        view.setDataSource(path)
        manager.addView(view, view.getWindowLayoutParams())
        this.mView = view
    }

    private fun hideFloatWindow() {
        val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val view = this.mView
        view.onPause()
        manager.removeView(view)
    }

    private class MyBinder: Binder()
}