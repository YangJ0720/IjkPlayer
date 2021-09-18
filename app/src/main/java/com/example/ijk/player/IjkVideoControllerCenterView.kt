package com.example.ijk.player

import android.app.Activity
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference

class IjkVideoControllerCenterView : LinearLayout {

    companion object {
        // 屏幕亮度默认值
        private const val DEF_LIGHT = 0.16f
        // 屏幕亮度最大值
        private const val MAX_LIGHT = 100
        // 媒体音量最大值
        private const val MAX_VOICE = 300
    }

    private lateinit var mHandler: MainHandler
    private lateinit var mImageView: ImageView
    private lateinit var mProgressBar: ProgressBar

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
        val drawable = GradientDrawable()
        drawable.setColor(ContextCompat.getColor(context, R.color.color_player_controller_bg_center))
        drawable.cornerRadius = 10.0f
        background = drawable
        //
        this.mHandler = MainHandler(this)
        //
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.view_ijk_video_controller_center, this)
        this.mImageView = view.findViewById(R.id.imageView)
        this.mProgressBar = view.findViewById(R.id.progressBar)
    }

    /**
     * 设置亮度（0 ~ 1）
     */
    fun seekByLight(progress: Int) {
        val context = context
        if (context is Activity) {
            val window = context.window
            val params: WindowManager.LayoutParams = window.attributes
            val screenBrightness = if (params.screenBrightness == -1.0f) {
                DEF_LIGHT
            } else {
                params.screenBrightness
            }
            //
            val unit = 0.01f
            val brightness = if (progress > 1.0f) {
                if (screenBrightness + unit > 1) {
                    1.0f
                } else {
                    screenBrightness + unit
                }
            } else {
                if (screenBrightness - unit < 0.0f) {
                    0.0f
                } else {
                    screenBrightness - unit
                }
            }
            params.screenBrightness = brightness
            window.attributes = params
            //
            seekTo((params.screenBrightness * MAX_LIGHT).toInt(), MAX_LIGHT)
            this.mImageView.setImageResource(R.drawable.ic_light)
        }
    }

    /**
     * 设置音量（0 ~ 15）
     */
    fun seekByVoice(progress: Int) {
        //
        val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        // val max = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        // val cur = manager.getStreamVolume(AudioManager.STREAM_MUSIC) * 20
        val cur = this.mProgressBar.progress
        //
        val unit = 5
        val value = if (progress > 0) {
            if (cur + unit > MAX_VOICE) {
                MAX_VOICE
            } else {
                cur + unit
            }
        } else {
            if (cur - unit < 0) {
                0
            } else {
                cur - unit
            }
        }
        //
        val flags = AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
        manager.setStreamVolume(AudioManager.STREAM_MUSIC, value / 20, flags)
        //
        seekTo(value, MAX_VOICE)
        this.mImageView.setImageResource(R.drawable.ic_voice)
    }

    private fun seekTo(progress: Int, max: Int) {
        show()
        val seekBar = this.mProgressBar
        seekBar.max = max
        seekBar.progress = progress
    }

    private fun show() {
        visibility = View.VISIBLE
        this.mHandler.sendMessage()
    }

    private fun hide() {
        visibility = View.GONE
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        this.mHandler.removeMessage()
    }

    private class MainHandler(view: IjkVideoControllerCenterView) :
        Handler(Looper.getMainLooper()) {
        companion object {
            private const val HANDLER_WHAT_HIDE = 1
            private const val HANDLER_DELAY_MILLIS_HIDE = 500L
        }

        private var mReference = WeakReference(view)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            this.mReference.get()?.hide()
        }

        fun sendMessage() {
            removeMessage()
            sendEmptyMessageDelayed(HANDLER_WHAT_HIDE, HANDLER_DELAY_MILLIS_HIDE)
        }

        fun removeMessage() {
            removeMessages(HANDLER_WHAT_HIDE)
        }
    }
}