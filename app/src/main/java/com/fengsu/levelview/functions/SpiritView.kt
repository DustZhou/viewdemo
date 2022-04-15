package com.fengsu.levelview.functions

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.fengsu.levelview.R
import org.w3c.dom.Text

/**
 * author: liuyu
 * data: 2022-4-13
 * desc: 自定义水平仪
 */
class SpiritView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * 背景颜色默认黑色，角度为0时绿色
     * x球和y球各在一边时，两球都为白色
     * x球和y球相交时，交集为黑色，其他部分为白色
     * 文字默认白色，与其中一个球相交部分为黑色，其他为白色
     * x球和y球重合时，球为绿色，且外环为白色
     */
    private val degrees: Float = 180f
    private var backColor: Int = Color.BLACK //背景颜色
    private var xballColor: Int = Color.WHITE //x球的颜色
    private var yballColor: Int = Color.WHITE //y球的颜色
    private var textColor: Int = Color.WHITE //文字颜色
    private var showText:String =""


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        initView(context, attrs = null)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val px = Paint()
        val py = Paint()
        val pp = Paint()
        px.setColor(xballColor)
        py.setColor(yballColor)
        pp.setColor(Color.RED)
        val pText = Paint(Paint.ANTI_ALIAS_FLAG)
        pText.textAlign = Paint.Align.CENTER
        pText.color = Color.WHITE
        pText.textSize = 200f
        /**
         * rotate: 旋转degrees:旋转角度，(x,y)绘制在该点旋转
         * drawcircle: 左上角为顶点，x向下，y向右
         * cx: 距顶部距离，cy: 距左边距离，radius: 圆的半径
         */
//        canvas.rotate(degrees, width/2f, height/2f)
        canvas.drawText(showText, width/2f, height/2f, pText)
        canvas.drawCircle(300f, 250f, 220f, px)
        canvas.drawCircle(800f, 250f, 220f, py)
    }

    fun show(showText: String) {
        this.showText= showText
        invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    @SuppressLint("Recycle", "CustomViewStyleable")
    private fun initView(context: Context, attrs: AttributeSet?) {
        val mTypedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.spiritview)
    }

}