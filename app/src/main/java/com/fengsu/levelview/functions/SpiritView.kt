package com.fengsu.levelview.functions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View


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
    private var textRotation: Float = 0f //文字旋转角度
    private var rotations: Float = 0f //旋转角度
    private var backColor: Int = Color.BLACK //背景颜色
    private var xballColor: Int = Color.WHITE //x球的颜色
    private var yballColor: Int = Color.WHITE //y球的颜色
    private var textColor: Int = Color.WHITE //文字颜色
    private var showText: String = "" //文本
    private var currx: Float = 0f //水平位移
    private var curry: Float = 0f //垂直位移

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (rotations != 0.0f) {
            val pxBall = Paint()
            val pyBall = Paint()
            pxBall.color = xballColor
            pyBall.color = yballColor
            pxBall.isAntiAlias = true
            pxBall.isDither = true
            pyBall.isAntiAlias = true
            pyBall.isDither = true
            val pText = Paint(Paint.ANTI_ALIAS_FLAG)
            pText.textAlign = Paint.Align.CENTER
            pText.color = textColor
            pText.textSize = 200f
            /**
             * drawcircle: 左上角为顶点，x向下，y向右
             * cx: 距顶部距离，cy: 距左边距离，radius: 圆的半径
             */
            var layerId: Int = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
            pxBall.xfermode = PorterDuffXfermode(PorterDuff.Mode.XOR)
            canvas.drawColor(backColor)
            //上面球
            canvas.save()
            canvas.drawCircle(width/2f - currx * 150, height/2f - curry * 150, 220f, pxBall)
            canvas.restore()
            //对称球
            canvas.save()
            xballColor = Color.BLACK
            canvas.rotate(180f,width/2f,height/2f)
            canvas.drawCircle(width/2f - currx * 150, height/2f - curry * 150, 220f, pxBall)
            canvas.restore()
            //文本样式
            pText.xfermode = PorterDuffXfermode(PorterDuff.Mode.XOR)
            canvas.save()
            textColor = Color.BLACK
            canvas.rotate(textRotation,width/2f,height/2f)
            canvas.drawText(showText, width/2f + 30, height/2f + 30, pText)
            canvas.restore()
            canvas.restoreToCount(layerId)
        } else {
            val paint = Paint()
            paint.color = Color.WHITE
            val paintText = Paint(Paint.ANTI_ALIAS_FLAG)
            paintText.textAlign = Paint.Align.CENTER
            paintText.textSize = 200f
            paintText.color = Color.WHITE
            canvas.drawColor(Color.GREEN)

            canvas.save()
            paint.style = Paint.Style.STROKE // Style 修改为画线模式
            paint.strokeWidth = 8f // 线条宽度为 20 像素
            canvas.drawCircle(width/2f, height/2f, 220f, paint)
            canvas.restore()

            canvas.save()
            canvas.rotate(textRotation,width/2f,height/2f)
            canvas.drawText(showText, width/2f + 40, height/2f + 43, paintText)
            canvas.restore()
        }

    }

    fun onDraws(){
        invalidate()
    }




    fun show(rotations: Float, showText: String, currx: Float, curry: Float, textRotation: Float) {
        this.rotations = rotations
        this.textRotation = textRotation
        this.showText = showText
        this.currx = currx
        this.curry = curry
        invalidate()
    }

}