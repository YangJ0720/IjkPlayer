package com.example.ijk.player.ui.view

import android.content.Context
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.FrameLayout
import com.example.ijk.player.R
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ui.PlayerView
import java.io.File

/**
 * @author YangJ 视频播放器
 */
class IjkVideoView : FrameLayout, Player.Listener {

    private var mTitle: String? = null
    private var mPath: String? = null
    private var mOrientation = Configuration.ORIENTATION_PORTRAIT
    private var mScreenWidth: Int = 0
    private var mScreenHeight: Int = 0
    private lateinit var mPlayerView: PlayerView
    private lateinit var mControllerView: IjkVideoControllerView

    private val mManager: WindowManager by lazy {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
    private val mParams: WindowManager.LayoutParams by lazy {
        val params = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        params.width = 800
        params.height = 600
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        params.format = PixelFormat.RGBA_8888
        params.gravity = Gravity.CENTER
        params
    }

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
        val metrics = context.resources.displayMetrics
        this.mScreenWidth = metrics.widthPixels
        this.mScreenHeight = metrics.heightPixels
        //
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.view_ijk_video, this)
        // 视频画面
        val playerView = view.findViewById<PlayerView>(R.id.playerView)
        playerView.controllerHideOnTouch = true
        this.mPlayerView = playerView
        // 视频控制器
        val controllerView = view.findViewById<IjkVideoControllerView>(R.id.controllerView)
        controllerView.setOnTouchListener(object : IjkVideoControllerView.OnTouchListener {
            override fun onTouch(e1: MotionEvent, e2: MotionEvent) {
                val params = mParams
                val widthPixels = metrics.widthPixels / 2
                val heightPixels = metrics.heightPixels / 2
                val measuredWidth = measuredWidth / 2
                val measuredHeight = measuredHeight / 2
                var x = params.x + ((e2.x - e1.x)).toInt() / 3
                var y = params.y + ((e2.y - e1.y)).toInt() / 3
                if (x < measuredWidth - widthPixels ) {
                    x = measuredWidth - widthPixels
                } else if (x > widthPixels - measuredWidth) {
                    x = widthPixels - measuredWidth
                }
                if (y < measuredHeight - heightPixels) {
                    y = measuredHeight - heightPixels
                } else if (y > heightPixels - measuredHeight) {
                    y = heightPixels - measuredHeight
                }
                params.x = x
                params.y = y
                mManager.updateViewLayout(this@IjkVideoView, params)
            }
        })
        this.mControllerView = controllerView
    }

    private fun initializePlayer(isFloating: Boolean) {
        val player = SimpleExoPlayer.Builder(context).build()
        player.addListener(this)
        player.setAudioAttributes(AudioAttributes.DEFAULT, true)
        this.mPlayerView.player = player
        this.mControllerView.setupMediaPlayer(IjkMediaPlayerProxy(player, isFloating))
    }

    private fun savePlayerPosition(player: Player) {
        this.mPath?.let {
            // 记录播放位置
            val currentPosition = player.currentPosition
            val position = if (currentPosition >= player.duration - 5000) {
                0
            } else {
                currentPosition
            }
            IjkMediaPlayerAssist.setLastPosition(context, path = it, position = position)
        }
    }

    /**
     * 这个方法用于显示悬浮窗
     */
    fun setDataSource(path: String) {
        initializePlayer(true)
        this.mPath = path
        this.mPlayerView.player?.let { player ->
            val uri = Uri.fromFile(File(path))
            val mediaItem = MediaItem.Builder().setUri(uri).build()
            player.setMediaItem(mediaItem)
            // 判断是否记录上次播放位置
            val position = IjkMediaPlayerAssist.getLastPosition(context, path)
            if (position > 0) {
                player.seekTo(position)
            }
            player.playWhenReady = true
            player.prepare()
        }
    }

    fun setDataSource(title: String?, path: String?) {
        path?.let {
            this.mTitle = title
            this.mPath = it
            this.mPlayerView.player?.let { player ->
                val uri = Uri.fromFile(File(it))
                val mediaItem = MediaItem.Builder().setUri(uri).build()
                player.setMediaItem(mediaItem)
                // 判断是否记录上次播放位置
                val position = IjkMediaPlayerAssist.getLastPosition(context, path)
                if (position > 0) {
                    player.seekTo(position)
                    this.mControllerView.showPositionTipsView()
                }
                player.playWhenReady = true
                player.prepare()
            }
            this.mControllerView.setTitle(title)
        }
    }

    fun onResume() {
        initializePlayer(false)
        setDataSource(this.mTitle, this.mPath)
        this.mPlayerView.onResume()
    }

    fun onPause() {
        this.mPlayerView.onPause()
        release()
    }

    fun release() {
        this.mPlayerView.player?.let {
            savePlayerPosition(it)
            it.stop()
            it.release()
            it.removeListener(this)
        }
        this.mPlayerView.player = null
    }

    /**
     * 设置横竖屏
     */
    fun requestedOrientation(orientation: Int) {
        if (Configuration.ORIENTATION_PORTRAIT == orientation) {
            // 通知视频控制器当前为竖屏模式
            this.mControllerView.requestedOrientation(true)
        } else if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
            // 通知视频控制器当前为横屏模式
            this.mControllerView.requestedOrientation(false)
        }
        this.mOrientation = orientation
    }

    fun unLock(): Boolean {
        return this.mControllerView.unlock()
    }

    /**
     * 获取横竖屏状态
     */
    fun getRequestedOrientation(): Int {
        return this.mOrientation
    }

    fun getWindowLayoutParams(): WindowManager.LayoutParams {
        return this.mParams
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_READY -> {
                this.mPlayerView.player?.let {
                    val controllerView = this.mControllerView
                    controllerView.player(it.isPlaying)
                    controllerView.setMax((it.duration / 1000).toInt())
                }
            }
            Player.STATE_ENDED -> {
                this.mPlayerView.player?.let {
                    this.mControllerView.player(it.isPlaying)
                }
            }
            else -> {

            }
        }
    }

}