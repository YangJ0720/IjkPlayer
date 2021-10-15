package com.example.ijk.player.ui.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.BatteryManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.ijk.player.R
import com.example.ijk.player.service.FloatingService
import com.example.ijk.player.ui.activity.PlayActivity
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

class IjkVideoControllerTopView : LinearLayout, LifecycleEventObserver {

    private var mIsVerticalScreen = true
    private lateinit var mIvBack: ImageView
    private lateinit var mTvTitle: TextView
    private lateinit var mBatteryView: ExoVideoBatteryView
    private lateinit var mTvTime: TextView
    private lateinit var mIvMore: ImageView

    // 播放组件
    private var mMediaPlayer: IjkMediaPlayerI? = null
    private var mReceiver: MainBroadcastReceiver? = null

    constructor(context: Context) : super(context) {
        initialize(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize(context)
    }

    private fun initialize(context: Context) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.view_ijk_video_controller_top, this)
        val ivBack = view.findViewById<ImageView>(R.id.iv_back)
        ivBack.setOnClickListener {
            val isFloating = this.mMediaPlayer?.isFloating() ?: false
            if (isFloating) {
                val intent = Intent(context, FloatingService::class.java)
                context.stopService(intent)
            } else {
                if (context is AppCompatActivity) {
                    if (this.mIsVerticalScreen) {
                        // 竖屏模式
                        context.finish()
                    } else {
                        // 横屏模式
                        val portrait = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        context.requestedOrientation = portrait
                    }
                }
            }
        }
        this.mIvBack = ivBack
        this.mTvTitle = view.findViewById(R.id.tv_title)
        // 电量
        val batteryView = view.findViewById<ExoVideoBatteryView>(R.id.batteryView)
        val manager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        batteryView.setLevel(manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY))
        this.mBatteryView = batteryView
        // 时间
        val tvTime = view.findViewById<TextView>(R.id.tv_time)
        tvTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        this.mTvTime = tvTime
        val ivMore = view.findViewById<ImageView>(R.id.iv_more)
        ivMore.setOnClickListener {
            this.mMediaPlayer?.let {
                if (it.isFloating()) {
                    val intent = Intent(context, FloatingService::class.java)
                    context.stopService(intent)
                    it.getDataSource()?.let { path ->
                        PlayActivity.launch(context, "Title", path)
                    }
                } else {
                    val viewParent = parent
                    if (viewParent is ViewGroup) {
                        val infoView = IjkVideoControllerInfoView(context)
                        infoView.setPath(it.getDataSource())
                        viewParent.addView(infoView)
                    }
                }
            }
        }
        this.mIvMore = ivMore
        //
        if (context is AppCompatActivity) {
            context.lifecycle.addObserver(this)
        }
    }

    fun setTitle(title: String?) {
        this.mTvTitle.text = title
    }

    fun requestedOrientation(isVerticalScreen: Boolean) {
        val batteryView = this.mBatteryView
        val tvTime = this.mTvTime
        val visibility = if (isVerticalScreen) {
            refreshToBattery(-1)
            refreshToTime()
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
        batteryView.visibility = visibility
        tvTime.visibility = visibility
        this.mIsVerticalScreen = isVerticalScreen
    }

    fun setupMediaPlayer(player: IjkMediaPlayerI) {
        if (player.isFloating()) {
            this.mIvBack.setImageResource(R.drawable.ic_player_exit)
            this.mTvTitle.visibility = View.INVISIBLE
            this.mBatteryView.visibility = View.GONE
            this.mTvTime.visibility = View.GONE
            this.mIvMore.setImageResource(R.drawable.ic_player_full)
        }
        this.mMediaPlayer = player
    }

    private fun refreshToBattery(level: Int) {
        val battery = if (level >= 0) {
            level
        } else {
            val manager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        }
        this.mBatteryView.setLevel(battery)
    }

    private fun refreshToTime() {
        this.mTvTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    private fun registerReceiver() {
        var receiver = this.mReceiver
        if (receiver == null) {
            receiver = MainBroadcastReceiver(this)
            this.mReceiver = receiver
        }
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_TIME_TICK)
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(receiver, filter)
    }

    private fun unregisterReceiver() {
        this.mReceiver?.let {
            context.unregisterReceiver(it)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        val context = context
        if (context is AppCompatActivity) {
            context.lifecycle.removeObserver(this)
        }
    }

    private class MainBroadcastReceiver(view: IjkVideoControllerTopView) : BroadcastReceiver() {
        private val mReference = WeakReference(view)
        override fun onReceive(context: Context, intent: Intent) {
            val view = this.mReference.get() ?: return
            when (intent.action) {
                Intent.ACTION_TIME_TICK -> {
                    view.refreshToTime()
                }
                Intent.ACTION_BATTERY_CHANGED -> {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    view.refreshToBattery(level)
                }
                else -> {

                }
            }
        }

    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (Lifecycle.Event.ON_RESUME == event) {
            registerReceiver()
        } else if (Lifecycle.Event.ON_PAUSE == event) {
            unregisterReceiver()
        }
    }

}