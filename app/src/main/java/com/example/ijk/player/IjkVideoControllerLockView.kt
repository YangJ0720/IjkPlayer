package com.example.ijk.player

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * @author YangJ 锁定控制
 */
class IjkVideoControllerLockView : AppCompatImageView {

    private var mIsLock = false

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize()
    }

    private fun initialize() {
        setBackground(Color.GRAY)
    }

    fun isLock(): Boolean {
        return this.mIsLock
    }

    fun toggle(): Boolean {
        val lock = !this.mIsLock
        val orientation: Int
        val color = if (lock) {
            orientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
            Color.RED
        } else {
            orientation = ActivityInfo.SCREEN_ORIENTATION_USER
            Color.GRAY
        }
        setBackground(color)
        // 设置Activity方向锁定 or 解锁
        if (context is Activity) {
            (context as Activity).requestedOrientation = orientation
        }
        //
        this.mIsLock = lock
        return lock
    }

    private fun setBackground(color: Int) {
        val drawable = GradientDrawable()
        drawable.setColor(color)
        drawable.cornerRadius = 10.0f
        background = drawable
    }
}