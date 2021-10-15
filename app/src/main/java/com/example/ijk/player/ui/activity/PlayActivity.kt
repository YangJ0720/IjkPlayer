package com.example.ijk.player.ui.activity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.*
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.ijk.player.ui.view.IjkVideoView
import com.example.ijk.player.R
import com.example.ijk.player.service.FloatingService
import java.io.File

/**
 * @author YangJ 视频播放界面
 */
class PlayActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_TITLE = "extra_title"
        private const val EXTRA_URL = "extra_url"

        fun launch(context: Context, title: String, url: String) {
            val intent = Intent(context, PlayActivity::class.java)
            intent.putExtra(EXTRA_TITLE, title)
            intent.putExtra(EXTRA_URL, url)
            context.startActivity(intent)
        }
    }

    private lateinit var mVideoView: IjkVideoView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 竖屏 -> 设置状态栏透明
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.statusBarColor = Color.TRANSPARENT
        // 横屏
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        //
        setContentView(R.layout.activity_play)
        initView()
    }

    override fun onResume() {
        super.onResume()
        this.mVideoView.onResume()
    }

    override fun onPause() {
        super.onPause()
        this.mVideoView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.mVideoView.release()
    }

    private fun initView() {
        val videoView = findViewById<IjkVideoView>(R.id.videoView)
        val title = intent.getStringExtra(EXTRA_TITLE)
        val url = intent.getStringExtra(EXTRA_URL)
        videoView.setDataSource(title, url)
        this.mVideoView = videoView
    }

    fun showFloatWindow() {
        val intent = Intent(this, FloatingService::class.java)
        startService(intent)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val orientation = newConfig.orientation
        if (Configuration.ORIENTATION_PORTRAIT == orientation) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        } else if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
        this.mVideoView.requestedOrientation(orientation)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            val videoView = this.mVideoView
            if (videoView.unLock()) {
                // 解锁控制
                return true
            }
            val orientation = videoView.getRequestedOrientation()
            if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
                val portrait = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                requestedOrientation = portrait
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}