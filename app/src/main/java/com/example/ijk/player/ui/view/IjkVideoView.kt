package com.example.ijk.player.ui.view

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.ijk.player.R
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ui.PlayerView
import com.tencent.mmkv.MMKV
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
        val player = SimpleExoPlayer.Builder(context).build()
        player.addListener(this)
        player.setAudioAttributes(AudioAttributes.DEFAULT, true)
        playerView.player = player
        this.mPlayerView = playerView
        // 视频控制器
        val controllerView = view.findViewById<IjkVideoControllerView>(R.id.controllerView)
        controllerView.setupMediaPlayer(IjkMediaPlayerProxy(player))
        this.mControllerView = controllerView
    }

    private fun savePlayerPosition(player: Player) {
        // 记录播放位置
        val currentPosition = player.currentPosition
        val position = if (currentPosition >= player.duration - 5000) {
            0
        } else {
            currentPosition
        }
        MMKV.defaultMMKV().encode(this.mPath, position)
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
                val kv = MMKV.defaultMMKV()
                val position = kv.decodeLong(it)
                if (position > 0) {
                    player.seekTo(position)
                }
                player.playWhenReady = true
                player.prepare()
            }
            this.mControllerView.setTitle(title)
        }
    }

    fun onResume() {
        this.mPlayerView.onResume()
    }

    fun onPause() {
        this.mPlayerView.onPause()
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

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_BUFFERING -> {
                println("正在缓冲")
            }
            Player.STATE_READY -> {
                println("开始播放")
                this.mPlayerView.player?.let {
                    val controllerView = this.mControllerView
                    controllerView.player(it.isPlaying)
                    controllerView.setMax((it.duration / 1000).toInt())
                }
            }
            Player.STATE_ENDED -> {
                println("结束播放")
            }
            else -> {

            }
        }
    }

}