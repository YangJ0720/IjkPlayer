package com.example.ijk.player.ui.view

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.example.ijk.player.R

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
        setImageResource(R.drawable.ic_player_unlock)
    }

    fun isLock(): Boolean {
        return this.mIsLock
    }

    fun toggle(): Boolean {
        val lock = !this.mIsLock
        val orientation: Int
        val resId = if (lock) {
            orientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
            R.drawable.ic_player_lock
        } else {
            orientation = ActivityInfo.SCREEN_ORIENTATION_USER
            R.drawable.ic_player_unlock
        }
        setImageResource(resId)
        // 设置Activity方向锁定 or 解锁
        if (context is Activity) {
            (context as Activity).requestedOrientation = orientation
        }
        //
        this.mIsLock = lock
        return lock
    }

}