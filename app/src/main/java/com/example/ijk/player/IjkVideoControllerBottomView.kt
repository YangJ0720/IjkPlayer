package com.example.ijk.player

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout

class IjkVideoControllerBottomView : LinearLayout {

    constructor(context: Context?) : super(context) {
        initialize(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize(context)
    }

    private fun initialize(context: Context?) {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.view_ijk_video_controller_bottom, this)
    }

}