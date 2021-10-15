package com.example.ijk.player.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

/**
 * @author YangJ 电池控件
 */
class ExoVideoBatteryView : View {

    private var mWidth: Float = 0.0f
    private var mHeight: Float = 0.0f
    private val mRadius = 5.0f

    // 电池电量
    private var mLevel: Float = 0.0f

    private lateinit var mPaint: Paint

    // 电池正极
    private lateinit var mRectFAnode: RectF

    // 电池外壳
    private lateinit var mRectFShell: RectF

    // 电池满电容量
    private var mBatteryMax = 0.0f

    //
    private lateinit var mRectF: RectF

    // 电池电芯
    private lateinit var mRectFCapacity: RectF

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
        val width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30.0f, metrics)
        this.mWidth = width
        val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16.0f, metrics)
        this.mHeight = height
        this.mPaint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
        }
        var paddingHorizontal = 20.0f
        var paddingVertical = 10.0f
        this.mRectFAnode = RectF(width - paddingHorizontal, height / 2 - paddingVertical + 3, width - paddingVertical, height / 2 + paddingVertical - 3)
        this.mRectFShell = RectF(paddingHorizontal, paddingVertical, width - paddingHorizontal, height - paddingVertical)
        paddingHorizontal += 4.0f
        paddingVertical += 4.0f
        this.mRectF = RectF(paddingHorizontal, paddingVertical, width - paddingHorizontal, height - paddingVertical)
        paddingHorizontal += 4.0f
        paddingVertical += 4.0f
        this.mRectFCapacity = RectF(paddingHorizontal, paddingVertical, width - paddingHorizontal, height - paddingVertical)
        //
        this.mBatteryMax = width - paddingHorizontal
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(mWidth.toInt(), mHeight.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        val paint = this.mPaint
        paint.color = Color.parseColor("#66FFFFFF")
        val radius = this.mRadius
        // 绘制电池正极
        paint.color = Color.WHITE
        canvas.drawArc(mRectFAnode, 270.0f, 180.0f, true, paint)
        // 绘制电池外壳
        canvas.drawRoundRect(mRectFShell, radius, radius, paint)
        //
        paint.color = Color.BLACK
        canvas.drawRoundRect(mRectF, radius, radius, paint)
        // 绘制电池电芯
        val level: Float = this.mLevel
        paint.color = if (level >= 0.2f) {
            Color.WHITE
        } else {
            Color.RED
        }
        val r = radius / 2
        canvas.drawRoundRect(mRectFCapacity, r, r, paint)
    }

    fun setLevel(level: Int) {
        val l = level / 100.0f
        this.mLevel = l
        val rectFCapacity = this.mRectFCapacity
        val left = rectFCapacity.left
        rectFCapacity.right = left + (mBatteryMax - left) * l
        invalidate()
    }
}