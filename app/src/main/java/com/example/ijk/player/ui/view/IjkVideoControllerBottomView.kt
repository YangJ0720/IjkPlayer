package com.example.ijk.player.ui.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.example.ijk.player.R
import java.io.File
import java.lang.ref.WeakReference
import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * @author YangJ 视频播放器底部控制器
 */
class IjkVideoControllerBottomView : LinearLayout {

    // 是否播放
    private var mIsPlayer = false

    private lateinit var mHandler: MainHandler
    private lateinit var mIvPlay: ImageView
    private lateinit var mSeekBar: SeekBar
    private lateinit var mTvDuration: TextView

    // 播放组件
    private var mMediaPlayer: IjkMediaPlayerI? = null

    // 回调函数
    private var mCallback: Callback? = null

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
        this.mHandler = MainHandler(this)
        //
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.view_ijk_video_controller_bottom, this)
        this.mIvPlay = view.findViewById<ImageView>(R.id.iv_play).apply {
            setOnClickListener {
                val isPlayer = this@IjkVideoControllerBottomView.mIsPlayer
                this@IjkVideoControllerBottomView.mCallback?.player(isPlayer)
                this@IjkVideoControllerBottomView.player(isPlayer)
                val resId = if (isPlayer) {
                    R.drawable.ic_player_pause
                } else {
                    R.drawable.ic_player_start
                }
                this.setImageResource(resId)
            }
        }
        this.mSeekBar = view.findViewById<SeekBar>(R.id.seekBar).apply {
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        val bottomView = this@IjkVideoControllerBottomView
                        val value = (progress * 1000).toLong()
                        bottomView.mMediaPlayer?.seekToByGestureDetector(value, false)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    val bottomView = this@IjkVideoControllerBottomView
                    bottomView.mCallback?.onTrackingTouch(true)
                    bottomView.mHandler.removeMessageToRefresh()
                    bottomView.mMediaPlayer?.pause()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    val bottomView = this@IjkVideoControllerBottomView
                    bottomView.mCallback?.onTrackingTouch(false)
                    bottomView.mHandler.sendMessageToRefresh()
                    val progress = (seekBar.progress * 1000).toLong()
                    bottomView.mMediaPlayer?.seekToByGestureDetector(progress, true)
                }

            })
        }
        this.mTvDuration = view.findViewById(R.id.tv_duration)
        val tvSpeed = view.findViewById<TextView>(R.id.tv_speed)
        tvSpeed.text = convert(IjkVideoControllerSpeedView.SPEED_0_1_0_X)
        tvSpeed.setOnClickListener {
            val bottomView = this@IjkVideoControllerBottomView
            bottomView.mCallback?.let {
                val viewParent = parent
                if (viewParent is ViewGroup) {
                    val speedView = IjkVideoControllerSpeedView(context)
                    speedView.setCallback(object :IjkVideoControllerSpeedView.Callback {
                        override fun callback(value: Float) {
                            tvSpeed.text = convert(value)
                        }
                    })
                    viewParent.addView(speedView)
                }
            }
        }
    }

    private fun convert(float: Float): String {
        this.mMediaPlayer?.speed(float)
        val value = if (IjkVideoControllerSpeedView.SPEED_0_7_5_X == float) {
            String.format("%.2f", float)
        } else {
            String.format("%.1f", float)
        }
        return resources.getString(R.string.video_speed_x, value)
    }

    private fun refresh() {
        this.mMediaPlayer?.let { player ->
            val seekBar = this.mSeekBar
            val progress = (player.getCurrentPosition() / 1000).toInt()
            seekBar.progress = progress
            //
            val sb = StringBuilder().append(progress).append(File.separator).append(seekBar.max)
            this.mTvDuration.text = sb
        }
    }

    fun setupMediaPlayer(player: IjkMediaPlayerI) {
        this.mMediaPlayer = player
    }

    fun setMax(max: Int) {
        this.mSeekBar.max = max
    }

    fun seekToByGestureDetector(progress: Int) {
        this.mMediaPlayer?.let { player ->
            val position = player.getCurrentPosition() + progress
            player.seekTo(position)
            this.mSeekBar.progress = (position / 1000).toInt()
        }
    }

    fun player(isPlayer: Boolean) {
        if (isPlayer) {
            this.mIvPlay.setImageResource(R.drawable.ic_player_pause)
            this.mHandler.sendMessageToRefresh()
        } else {
            this.mIvPlay.setImageResource(R.drawable.ic_player_start)
            this.mHandler.removeMessageToRefresh()
        }
        this.mIsPlayer = !isPlayer
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        this.mHandler.removeMessageToRefresh()
    }

    fun setCallback(callback: Callback) {
        this.mCallback = callback
    }

    interface Callback {
        fun player(isPlayer: Boolean)
        fun onTrackingTouch(isTouch: Boolean)
    }

    private class MainHandler(view: IjkVideoControllerBottomView) :
        Handler(Looper.getMainLooper()) {
        companion object {
            private const val HANDLER_WHAT_REFRESH = 1
            private const val HANDLER_DELAY_MILLIS_REFRESH = 100L
        }

        private val mReference = WeakReference(view)

        fun sendMessageToRefresh() {
            sendEmptyMessageDelayed(HANDLER_WHAT_REFRESH, HANDLER_DELAY_MILLIS_REFRESH)
        }

        fun removeMessageToRefresh() {
            removeMessages(HANDLER_WHAT_REFRESH)
        }

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val view = this.mReference.get() ?: return
            if (HANDLER_WHAT_REFRESH == msg.what) {
                view.refresh()
                sendMessageToRefresh()
            }
        }
    }

}