package com.example.ijk.player.ui.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.ijk.player.R
import java.lang.ref.WeakReference
import kotlin.math.abs

/**
 * @author YangJ 视频控制器
 */
class IjkVideoControllerView : FrameLayout {

    companion object {
        private const val HANDLER_WHAT_VISIBILITY = 1
        private const val HANDLER_WHAT_HIDE_POSITION_TIPS = 2
        private const val HANDLER_DELAY_MILLIS_VISIBILITY = 5000L
        private const val HANDLER_DELAY_MILLIS_HIDE_POSITION_TIPS = 3000L

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
    private lateinit var mTipsView: TextView
    private var mPositionView: View? = null
    private lateinit var mCenterView: IjkVideoControllerCenterView
    private lateinit var mBottomView: IjkVideoControllerBottomView
    private lateinit var mLockView: IjkVideoControllerLockView

    // Listener
    private var mListener: OnTouchListener? = null

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
        this.mTopView = view.findViewById(R.id.videoControllerTopView)
        val tipsView = view.findViewById<TextView>(R.id.videoControllerTipsView)
        tipsView.background = GradientDrawable().apply {
            setColor(ContextCompat.getColor(context, R.color.color_player_controller_bg_center))
            val value = 15.0f
            val radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, metrics)
            cornerRadius = radius
        }
        val left = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0f, metrics).toInt()
        val top = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f, metrics).toInt()
        tipsView.setPadding(left, top, left, top)
        this.mTipsView = tipsView
        this.mCenterView = view.findViewById(R.id.videoControllerCenterView)
        this.mBottomView = view.findViewById<IjkVideoControllerBottomView>(R.id.bottomView).apply {
            setCallback(object : IjkVideoControllerBottomView.Callback {
                override fun player(isPlayer: Boolean) {
                    val controllerView = this@IjkVideoControllerView
                    controllerView.setVisibility()
                    if (isPlayer) {
                        controllerView.mMediaPlayer?.start()
                    } else {
                        controllerView.mMediaPlayer?.pause()
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
            override fun onDown(e: MotionEvent): Boolean {
                this@IjkVideoControllerView.setVisibility()
                return true
            }

            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                if (isFloating()) {
                    this@IjkVideoControllerView.mListener?.onTouch(e1, e2)
                    return true
                }
                // 如果是锁定状态，不需要响应手势操作
                if (isLock()) return true
                val controllerView = this@IjkVideoControllerView
                // 如果是全屏状态，需要判断手势是否从屏幕边缘开始滑动
                if (e1.x + DURATION_FULLSCREEN > controllerView.mScreenWidth || e1.y < DURATION_FULLSCREEN) return true
                val x = abs(distanceX)
                val y = abs(distanceY)
                if (x > y) {
                    // 左右滑动
                    val direction = controllerView.mDirection
                    if (DIRECTION_UNKNOWN == direction) {
                        scrollHorizontal(distanceX, false)
                        controllerView.mDirection = DIRECTION_HORIZONTAL
                    } else if (DIRECTION_HORIZONTAL == direction) {
                        scrollHorizontal(distanceX, false)
                    }
                } else if (x < y) {
                    // 上下滑动
                    val direction = controllerView.mDirection
                    if (DIRECTION_UNKNOWN == direction) {
                        scrollVertical(e1, distanceY)
                        controllerView.mDirection = DIRECTION_VERTICAL
                    } else if (DIRECTION_VERTICAL == direction) {
                        scrollVertical(e1, distanceY)
                    }

                }
                return false
            }

            override fun onLongPress(e: MotionEvent?) {
                if (isFloating()) return
                val controllerView = this@IjkVideoControllerView
                // 显示倍速播放tips
                controllerView.mTipsView.text = resources.getString(R.string.video_speed_3x_tips)
                controllerView.mTipsView.visibility = View.VISIBLE
                // 设置视频播放倍速
                val value = IjkVideoControllerSpeedView.SPEED_0_3_0_X
                controllerView.mMediaPlayer?.speed(value)
                controllerView.mIsLongPress = true
            }

        }
        val gestureDetector = GestureDetector(context, listener)
        setOnTouchListener { v, event ->
            if (!gestureDetector.onTouchEvent(event) && MotionEvent.ACTION_UP == event?.action) {
                val direction = this.mDirection
                if (DIRECTION_HORIZONTAL == direction) {
                    this.mTipsView.visibility = View.GONE
                    scrollHorizontal(0.0f, true)
                }
                this.mDirection = DIRECTION_UNKNOWN
                if (this.mIsLongPress) {
                    this.mTipsView.visibility = View.GONE
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

    fun showPositionTipsView() {
        val viewStub = rootView.findViewById<ViewStub>(R.id.videoControllerPositionView)
        val view = viewStub.inflate()
        val metrics = resources.displayMetrics
        view.background = GradientDrawable().apply {
            setColor(ContextCompat.getColor(context, R.color.color_player_controller_bg_center))
            val value = 15.0f
            val radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, metrics)
            cornerRadius = radius
        }
        val left = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0f, metrics).toInt()
        val top = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f, metrics).toInt()
        view.setPadding(left, top, left, top)
        this.mPositionView = view
        this.mHandler.sendEmptyMessageDelayed(HANDLER_WHAT_HIDE_POSITION_TIPS, HANDLER_DELAY_MILLIS_HIDE_POSITION_TIPS)
    }

    private fun hidePositionTipsView() {
        this.mPositionView?.visibility = View.GONE
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
        val buffer = this.mScrollBuffer
        if (buffer in 0.0f..1.0f) {
            this.mScrollBuffer += 0.1f
            return
        } else {
            this.mScrollBuffer = 0.5f
        }
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
        // 显示播放进度tips
        this.mMediaPlayer?.let { player ->
            val tipsView = this.mTipsView
            val currentPosition = player.getCurrentPosition()
            tipsView.text = if (currentPosition <= 0 || currentPosition >= 24 * 3600 * 1000) {
                "00:00"
            } else {
                val totalSeconds = currentPosition / 1000
                val seconds = totalSeconds % 60
                val minutes = (totalSeconds / 60) % 60
                val hours = totalSeconds / 3600
                if (hours > 0) {
                    String.format("%02d:%02d:%02d", hours, minutes, seconds)
                } else {
                    String.format("%02d:%02d", minutes, seconds)
                }
            }
            tipsView.visibility = View.VISIBLE
        }
        // 刷新播放进度seekBar
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

    private fun isFloating(): Boolean {
        return this.mMediaPlayer?.isFloating() ?: false
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
        this.mHandler.removeAllMessage()
    }

    fun setOnTouchListener(listener: OnTouchListener) {
        this.mListener = listener
    }

    interface OnTouchListener {
        fun onTouch(e1: MotionEvent, e2: MotionEvent)
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

        fun removeAllMessage() {
            removeMessages(HANDLER_WHAT_VISIBILITY)
            removeMessages(HANDLER_WHAT_HIDE_POSITION_TIPS)
        }

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            this.mReference.get()?.let {
                when (msg.what) {
                    HANDLER_WHAT_VISIBILITY -> {
                        it.setVisibilityToGone()
                    }
                    HANDLER_WHAT_HIDE_POSITION_TIPS -> {
                        it.hidePositionTipsView()
                    }
                }
            }
        }
    }

}