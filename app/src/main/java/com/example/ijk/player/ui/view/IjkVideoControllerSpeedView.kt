package com.example.ijk.player.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.ijk.player.R

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
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val value = when (checkedId) {
                R.id.rb_3_0 -> {
                    SPEED_0_3_0_X
                }
                R.id.rb_2_0 -> {
                    SPEED_0_2_0_X
                }
                R.id.rb_1_5 -> {
                    SPEED_0_1_5_X
                }
                R.id.rb_1_0 -> {
                    SPEED_0_1_0_X
                }
                R.id.rb_0_7_5 -> {
                    SPEED_0_7_5_X
                }
                R.id.rb_0_5 -> {
                    SPEED_0_5_0_X
                }
                else -> {
                    SPEED_0_1_0_X
                }
            }
            this.mCallback?.callback(value)
            removeView()
        }
        view.findViewById<RadioButton>(R.id.rb_3_0).text = convert(SPEED_0_3_0_X)
        view.findViewById<RadioButton>(R.id.rb_2_0).text = convert(SPEED_0_2_0_X)
        view.findViewById<RadioButton>(R.id.rb_1_5).text = convert(SPEED_0_1_5_X)
        view.findViewById<RadioButton>(R.id.rb_1_0).text = convert(SPEED_0_1_0_X)
        view.findViewById<RadioButton>(R.id.rb_0_7_5).text = convert(SPEED_0_7_5_X)
        view.findViewById<RadioButton>(R.id.rb_0_5).text = convert(SPEED_0_5_0_X)
    }

    private fun convert(float: Float): String {
        val value = if (SPEED_0_7_5_X == float) {
            String.format("%.2f", float)
        } else {
            String.format("%.1f", float)
        }
        return context.getString(R.string.video_speed_x, value)
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