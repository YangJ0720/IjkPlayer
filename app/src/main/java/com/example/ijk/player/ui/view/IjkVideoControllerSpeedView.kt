package com.example.ijk.player.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ijk.player.R
import com.example.ijk.player.adapter.IjkVideoControllerSpeedAdapter

/**
 * @author YangJ 倍速
 */
class IjkVideoControllerSpeedView : FrameLayout {

    companion object {
        const val SPEED_0_5_0_X = 0.5f
        const val SPEED_0_7_5_X = 0.75f
        const val SPEED_0_1_0_X = 1.0f
        const val SPEED_0_1_5_X = 1.5f
        const val SPEED_0_2_0_X = 2.0f
        const val SPEED_0_3_0_X = 3.0f
    }

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
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.view_ijk_video_controller_speed, this)
        view.setOnClickListener {
            removeView()
        }
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = IjkVideoControllerSpeedAdapter(
            context,
            arrayListOf(
                SPEED_0_5_0_X,
                SPEED_0_7_5_X,
                SPEED_0_1_0_X,
                SPEED_0_1_5_X,
                SPEED_0_2_0_X,
                SPEED_0_3_0_X
            )
        )
        adapter.setOnItemClickListener(object : IjkVideoControllerSpeedAdapter.OnItemClickListener {
            override fun onItemClick(value: Float) {
                this@IjkVideoControllerSpeedView.mCallback?.callback(value)
                removeView()
            }
        })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun removeView() {
        val viewParent = parent
        if (viewParent is ViewGroup) {
            viewParent.removeView(this)
        }
    }

    fun setCallback(callback: Callback) {
        this.mCallback = callback
    }

    interface Callback {
        fun callback(value: Float)
    }
}