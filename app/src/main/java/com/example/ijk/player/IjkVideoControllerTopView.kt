package com.example.ijk.player

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.BatteryManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class IjkVideoControllerTopView : LinearLayout {

    private var mIsVerticalScreen = true
    private lateinit var mTvTitle: TextView
    private lateinit var mTvBattery: TextView
    private lateinit var mTvTime: TextView

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
        view.findViewById<View>(R.id.iv_back).setOnClickListener {
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
        this.mTvTitle = view.findViewById(R.id.tv_title)
        this.mTvBattery = view.findViewById(R.id.tv_battery)
        this.mTvTime = view.findViewById(R.id.tv_time)
        view.findViewById<View>(R.id.iv_more).setOnClickListener {

        }
    }

    fun setTitle(title: String?) {
        this.mTvTitle.text = title
    }

    fun requestedOrientation(isVerticalScreen: Boolean) {
        val tvBattery = this.mTvBattery
        val tvTime = this.mTvTime
        val visibility = if (isVerticalScreen) {
            val manager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            val battery = StringBuilder()
            battery.append(manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY))
            battery.append("%")
            tvBattery.text = battery
            tvTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
        tvBattery.visibility = visibility
        tvTime.visibility = visibility
        this.mIsVerticalScreen = isVerticalScreen
    }

}