package com.fengsu.levelview.functions

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.fengsu.levelview.R

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
    private var xRotation: Float = 0f //x球旋转角度
    private var yRotation: Float = 0f //y球旋转角度
    private var backColor: Int = Color.BLACK //背景颜色
    private var xballColor: Int = Color.WHITE //x球的颜色
    private var yballColor: Int = Color.WHITE //y球的颜色
    private var xballMargin: Float = width/2f //x球的颜色
    private var yballMargin: Float = 250f //y球的颜色
    private var textColor: Int = Color.WHITE //文字颜色
    private var showText: String = "" //文本
    private lateinit var canvas: Canvas
    private var bitmap: Bitmap=Bitmap.createBitmap(10,10,Bitmap.Config.ARGB_8888)
    private lateinit var canvas1: Canvas
    private var bitmap1: Bitmap=Bitmap.createBitmap(10,10,Bitmap.Config.ARGB_8888)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        initView(context, attrs = null)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val pxBall = Paint()
        pxBall.color = xballColor
        val pText = Paint(Paint.ANTI_ALIAS_FLAG)
        pText.textAlign = Paint.Align.CENTER
        pText.color = Color.WHITE
        pText.textSize = 200f
        /**
         * drawcircle: 左上角为顶点，x向下，y向右
         * cx: 距顶部距离，cy: 距左边距离，radius: 圆的半径
         */
//        canvas.rotate(rotationText, width/2f, height/2f) //旋转整个屏幕
//        val matrixValues: FloatArray = floatArrayOf(1f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 1f)
//        matrix.setValues(matrixValues)
//        matrix.reset()
//        matrix.setRotate(190f)
//        matrix.preTranslate(100f, 800f)
//        canvas.setMatrix(matrix)
//        val bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
//        val ret = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
//        canvas.drawBitmap(bitmap, matrix, pp)
        bitmap1 = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        canvas1 = Canvas(bitmap1)
        canvas1.save()
        canvas1.rotate(xRotation,width/2f,height/2f)
        canvas1.drawCircle(width/2f, 220f, 220f, pxBall)
        canvas1.restore()
        canvas.drawText(showText, width/2f, height/2f, pText)
        canvas.drawBitmap(bitmap,0f,0f,null)
        canvas.drawBitmap(bitmap1,0f,0f,null)
    }

    fun onDraws(){
        val pyBall = Paint()
        pyBall.color = yballColor
        bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
        canvas.save()
        canvas.rotate(yRotation, width/2f, height/2f)
        canvas.drawCircle(width/2f, 220f, 220f, pyBall)
        canvas.restore()
        invalidate()
    }



    fun show(showText: String, rotationX: Float, rotationY: Float) {
        this.showText = showText
        this.xRotation = rotationX
        this.yRotation = rotationY
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