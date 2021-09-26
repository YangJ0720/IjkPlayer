package com.example.ijk.player.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.example.ijk.player.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author YangJ 视频信息
 */
class IjkVideoControllerInfoView: FrameLayout {

    private lateinit var mTvInfoName: TextView
    private lateinit var mTvInfoSize: TextView
    private lateinit var mTvInfoPath: TextView
    private lateinit var mTvInfoDate: TextView

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
        val view = inflater.inflate(R.layout.view_ijk_video_controller_info, this)
        this.mTvInfoName = view.findViewById(R.id.tv_info_name)
        this.mTvInfoSize = view.findViewById(R.id.tv_info_size)
        this.mTvInfoPath = view.findViewById(R.id.tv_info_path)
        this.mTvInfoDate = view.findViewById(R.id.tv_info_date)
        setOnTouchListener { v, _ ->
            removeView()
            v.performClick()
            true
        }
    }

    private fun removeView() {
        val viewParent = parent
        if (viewParent is ViewGroup) {
            viewParent.removeView(this)
        }
    }

    private fun getFileSize(length: Long): String {
        var size = length
        val unit = 1024
        // B
        if (size < unit) {
            return String.format("%dB", size)
        } else {
            size /= unit
        }
        // KB
        if (size < unit) {
            return String.format("%dKB", size)
        } else {
            size /= unit
        }
        // MB
        return if (size < unit) {
            size *= 100
            "${size / 100}.${size % 100}MB"
        } else {
            size = size * 100 / unit
            "${size / 100}.${size % 100}GB"
        }
    }

    fun setPath(path: String?) {
        path?.let {
            val file = File(it)
            this.mTvInfoName.text = file.name
            this.mTvInfoSize.text = getFileSize(file.length())
            this.mTvInfoPath.text = it
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            this.mTvInfoDate.text = format.format(Date(file.lastModified()))
        }
    }

}