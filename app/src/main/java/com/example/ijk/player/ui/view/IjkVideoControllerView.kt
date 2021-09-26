package com.example.ijk.player.ui.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import com.example.ijk.player.R
import java.lang.ref.WeakReference
import kotlin.math.abs

/**
 * @author YangJ 视频控制器
 */
class IjkVideoControllerView : FrameLayout {

    companion object {
        private const val HANDLER_WHAT_VISIBILITY = 1
        private const val HANDLER_DELAY_MILLIS_VISIBILITY = 5000L

        //
        private const val DIRECTION_UNKNOWN = 0

        // 纵向滑动
        private const val DIRECTION_VERTICAL = 1

        // 横向滑动
        private const val DIRECTION_HORIZONTAL = 2

        // 全屏模式 -> 屏幕边缘滑动判定
        private const val DURATION_FULLSCREEN = 100

    }

    // 滑动缓冲阀值
    private var mScrollBuffer = 0.5f

    // 是否长按3倍速播放
    private var mIsLongPress = false

    // 滑动方向
    private var mDirection = DIRECTION_UNKNOWN

    private var mScreenWidth: Int = 0
    private var mScreenHeight: Int = 0

    private lateinit var mHandler: MainHandler
    private var mMediaPlayer: IjkMediaPlayerI? = null
    private lateinit var mTopView: IjkVideoControllerTopView
    private lateinit var mCenterView: IjkVideoControllerCenterView
    private lateinit var mBottomView: IjkVideoControllerBottomView
    private lateinit var mLockView: IjkVideoControllerLockView

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
        val handler = MainHandler(this)
        handler.sendMessage()
        this.mHandler = handler
        //
        val metrics = context.resources.displayMetrics
        this.mScreenWidth = metrics.widthPixels
        this.mScreenHeight = metrics.heightPixels
        //
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.view_ijk_video_controller, this)
        val topView = view.findViewById<IjkVideoControllerTopView>(R.id.videoControllerTopView)
        this.mTopView = topView
        this.mCenterView = view.findViewById(R.id.videoControllerCenterView)
        this.mBottomView = view.findViewById<IjkVideoControllerBottomView>(R.id.bottomView).apply {
            setCallback(object : IjkVideoControllerBottomView.Callback {
                override fun player(isPlayer: Boolean) {
                    this@IjkVideoControllerView.setVisibility()
                    if (isPlayer) {
                        this@IjkVideoControllerView.mMediaPlayer?.start()
                    } else {
                        this@IjkVideoControllerView.mMediaPlayer?.pause()
                    }
                }

                override fun onTrackingTouch(isTouch: Boolean) {
                    if (isTouch) {
                        this@IjkVideoControllerView.mHandler.removeMessage()
                    } else {
                        this@IjkVideoControllerView.mHandler.sendMessage()
                    }
                }

            })
        }
        // 锁定
        val lock = view.findViewById<IjkVideoControllerLockView>(R.id.videoControllerLockView)
        lock.setOnClickListener {
            if (lock.toggle()) {
                // 横屏状态
                setVisibilityToGone()
            } else {
                // 竖屏状态
                setVisibility()
            }
        }
        this.mLockView = lock
        // 手势监听器
        val listener = object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent?): Boolean {
                this@IjkVideoControllerView.setVisibility()
                return true
            }

            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                // 如果是锁定状态，不需要响应手势操作
                if (isLock()) return true
                // 如果是全屏状态，需要判断手势是否从屏幕边缘开始滑动
                if (e1.x + DURATION_FULLSCREEN > this@IjkVideoControllerView.mScreenWidth || e1.y < DURATION_FULLSCREEN) return true
                val x = abs(distanceX)
                val y = abs(distanceY)
                if (x > y) {
                    // 左右滑动
                    val direction = this@IjkVideoControllerView.mDirection
                    if (DIRECTION_UNKNOWN == direction) {
                        scrollHorizontal(distanceX, false)
                        this@IjkVideoControllerView.mDirection = DIRECTION_HORIZONTAL
                    } else if (DIRECTION_HORIZONTAL == direction) {
                        scrollHorizontal(distanceX, false)
                    }
                } else if (x < y) {
                    // 上下滑动
                    val direction = this@IjkVideoControllerView.mDirection
                    if (DIRECTION_UNKNOWN == direction) {
                        scrollVertical(e1, distanceY)
                        this@IjkVideoControllerView.mDirection = DIRECTION_VERTICAL
                    } else if (DIRECTION_VERTICAL == direction) {
                        scrollVertical(e1, distanceY)
                    }

                }
                return false
            }

            override fun onLongPress(e: MotionEvent?) {
                val value = IjkVideoControllerSpeedView.SPEED_0_3_0_X
                this@IjkVideoControllerView.mMediaPlayer?.speed(value)
                this@IjkVideoControllerView.mIsLongPress = true
            }

        }
        val gestureDetector = GestureDetector(context, listener)
        setOnTouchListener { v, event ->
            if (!gestureDetector.onTouchEvent(event) && MotionEvent.ACTION_UP == event?.action) {
                val direction = this.mDirection
                if (DIRECTION_HORIZONTAL == direction) {
                    scrollHorizontal(0.0f, true)
                }
                this.mDirection = DIRECTION_UNKNOWN
                if (this.mIsLongPress) {
                    val speed = this.mBottomView.getSpeed()
                    val value = speed ?: IjkVideoControllerSpeedView.SPEED_0_1_0_X
                    this.mMediaPlayer?.speed(value)
                    this.mIsLongPress = false
                }
                v.performClick()
            }
            true
        }
    }

    fun setupMediaPlayer(player: IjkMediaPlayerI) {
        this.mTopView.setupMediaPlayer(player)
        this.mBottomView.setupMediaPlayer(player)
        this.mMediaPlayer = player
    }

    fun setTitle(title: String?) {
        this.mTopView.setTitle(title)
    }

    fun player(isPlayer: Boolean) {
        this.mBottomView.player(isPlayer)
    }

    fun setMax(max: Int) {
        this.mBottomView.setMax(max)
    }

    private fun seekByLight(progress: Int) {
        this.mCenterView.seekByLight(progress)
    }

    private fun seekByVoice(progress: Int) {
        this.mCenterView.seekByVoice(progress)
    }

    fun requestedOrientation(isVerticalScreen: Boolean) {
        this.mLockView.visibility = if (isVerticalScreen) {
            View.GONE
        } else {
            View.VISIBLE
        }
        this.mTopView.requestedOrientation(isVerticalScreen)
    }

    private fun scrollVertical(e1: MotionEvent, distanceY: Float) {
        if (e1.x >= this.mScreenWidth / 2) {
            seekByVoice(distanceY.toInt())
        } else {
            seekByLight(distanceY.toInt())
        }
    }

    private fun scrollHorizontal(distanceX: Float, isPlayer: Boolean) {
        val buffer = this.mScrollBuffer
        if (buffer in 0.0f..1.0f) {
            this.mScrollBuffer += 0.1f
            return
        } else {
            this.mScrollBuffer = 0.5f
        }
        val progress = when {
            distanceX > 0 -> {
                -1000
            }
            distanceX < 0 -> {
                1000
            }
            else -> {
                0
            }
        }
        this.mBottomView.seekToByGestureDetector(progress, false)
    }

    private fun setVisibility() {
        if (isLock()) return
        this.mTopView.visibility = View.VISIBLE
        this.mBottomView.visibility = View.VISIBLE
        this.mHandler.sendMessage()
    }

    private fun isLock(): Boolean {
        return this.mLockView.isLock()
    }

    fun unlock(): Boolean {
        val lockView = this.mLockView
        if (lockView.isLock()) {
            lockView.toggle()
            return true
        }
        return false
    }

    private fun setVisibilityToGone() {
        this.mTopView.visibility = View.GONE
        this.mBottomView.visibility = View.GONE
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        this.mHandler.removeMessage()
    }

    class MainHandler(view: IjkVideoControllerView) : Handler(Looper.getMainLooper()) {
        private val mReference = WeakReference(view)

        fun sendMessage() {
            removeMessage()
            sendEmptyMessageDelayed(HANDLER_WHAT_VISIBILITY, HANDLER_DELAY_MILLIS_VISIBILITY)
        }

        fun removeMessage() {
            removeMessages(HANDLER_WHAT_VISIBILITY)
        }

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            this.mReference.get()?.setVisibilityToGone()
        }
    }

}