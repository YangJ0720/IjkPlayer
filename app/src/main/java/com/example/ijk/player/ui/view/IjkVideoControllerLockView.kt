package com.example.ijk.player.ui.view

import android.content.Context
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
        val resId = if (lock) {
            R.drawable.ic_player_lock
        } else {
            R.drawable.ic_player_unlock
        }
        setImageResource(resId)
        //
        this.mIsLock = lock
        return lock
    }

}