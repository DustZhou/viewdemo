package com.hudunzht.cropimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * author: ZHT
 * date: 2022/4/13
 * desc:自定义图片裁剪ImageView
 */
public class CropImageView extends AppCompatImageView {
    /**
     * 触摸事件相关属性
     */
    //touch 用的点；
    private float mX_1 = 0;
    private float mY_1 = 0;
    //当前触摸得到的点
    private PointF mNowPoint;
    //触摸事件的判断；
    private final int STATUS_SINGLE = 1;
    private final int STATUS_MULTI_START = 2;
    private final int STATUS_MULTI_TOUCHING = 3;
    //当前状态；
    private int mStatus = STATUS_SINGLE;
    //八个关键点的静态属性
    private final int EDGE_LT = 1;
    private final int EDGE_RT = 2;
    private final int EDGE_LB = 3;
    private final int EDGE_RB = 4;
    private final int EDGE_L = 5;
    private final int EDGE_T = 6;
    private final int EDGE_B = 7;
    private final int EDGE_R = 8;
    private final int EDGE_MOVE_IN = 9;
    private final int EDGE_MOVE_OUT = 10;
    private final int EDGE_NONE = 11;
    /**
     * 裁剪框属性
     */
    //判断裁剪框是否填充
    private boolean isCropRectFill;
    //线的属性
    private final int lineColor;//颜色
    private final int partitionWidth;//分割线宽度
    private final int frameWidth;//边框宽度
    //各点的属性
    private final int interval;//点与裁剪线间隔
    private final int lengthVertex;//顶点两边的距离
    private final int lengthMidpoint;//中点两边的距离
    //默认裁剪的宽高；
    private int cropWidth;
    private int cropHeigh;
    //最大缩小放大；
    protected float oriRationWH = 0;
    protected final float maxZoomOut = 5.0f;
    protected final float minZoomIn = 0.333333f;
    /**
     * 画笔paint
     * 裁剪框rectf
     * 路径path：画八个点时用到的。
     */
    private Path mPath;
    private Paint mPaint;
    private RectF mRectF;
    private RectF mRectFOri;
    private PointF[] mPoint;
    //图片铺满imageview
    private float[] mValue;

    //上下文；
    protected Context mContext;

//    public CropImageView(Context context){
//        this(context,null,0);
//    }

    /**
     * 在activity中new一个cropimageview需要此方法
     */
    public CropImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 在xml文件中需要用到此方法，xml文件中添加必须有attrs属性
     */
    public CropImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CropImageView);
        //间隔
        interval = array.getInteger(R.styleable.CropImageView_interval, 20);
        //顶点距离
        lengthVertex = array.getInteger(R.styleable.CropImageView_lengthVertex, 40);
        //中点距离
        lengthMidpoint = array.getInteger(R.styleable.CropImageView_lengthMidpoint, 40);
        //线的颜色
        lineColor = array.getColor(R.styleable.CropImageView_lineColor, Color.BLACK);
        //分割线的宽度
        partitionWidth = array.getInteger(R.styleable.CropImageView_partitionWidth, 5);
        //边框宽度
        frameWidth = array.getInteger(R.styleable.CropImageView_frameWidth, 10);

        array.recycle();

        init();
    }

    //初始化
    private void init() {
        mPaint = new Paint();
        mPaint.setColor(lineColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mRectF = new RectF();
        mRectFOri = new RectF();
        mPath = new Path();
        mValue = new float[9];
        mPoint = new PointF[7];
    }

    /**
     * 画布，裁剪框在此方法中画出。
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null != getDrawable()) {
            if (!isCropRectFill) {
                Rect rect = getDrawable().getBounds();
                getImageMatrix().getValues(mValue);
                //图片原始尺寸与view的比例。
                float scaleX = mValue[Matrix.MSCALE_X];//水平比例尺
                float scaleY = mValue[Matrix.MSCALE_Y];//竖直比例尺
                //图片在view中左顶点位置。
                float startX = mValue[Matrix.MTRANS_X];//起始x值
                float startY = mValue[Matrix.MTRANS_Y];//起始y值

                Log.e("mrchen",scaleX+":"+scaleY+"::"+startX+"::"+startY);
                //裁剪框最开始与view中图片一样大
                mRectF.set(startX,startY,startX + scaleX * rect.width(), startY + scaleY * rect.height());
                CropRectPoints();
                isCropRectFill = true;//mRectFOri只设置一次，mRectF根据onTouchEvent动态变化
                //固定的图片承载矩形的大小。
                mRectFOri.set(mRectF.left,mRectF.top,mRectF.right,mRectF.bottom);
            } else {
                Log.e("mrchen","11");
            }
            //绘制剪切框中的分割线；
            mPaint.setStrokeWidth(partitionWidth);
            for (int i = 1; i < 3; ++i) {
                //水平分割线；
                float XHorizontal = mRectF.left + i * mRectF.width() / 3;
                canvas.drawLine(XHorizontal, mRectF.top, XHorizontal, mRectF.bottom, mPaint);
                //竖直分割线；
                float YVertical = mRectF.top + i * mRectF.height() / 3;
                canvas.drawLine(mRectF.left, YVertical, mRectF.right, YVertical, mPaint);
            }
            //绘制边框；
            mPaint.setStrokeWidth(frameWidth);
            canvas.drawRect(mRectF, mPaint);
            //绘制各顶点
            mPath.reset();
            //左下点
            mPath.moveTo(mRectF.left + interval + lengthVertex, mRectF.bottom - interval);
            mPath.lineTo(mRectF.left + interval, mRectF.bottom - interval);
            mPath.lineTo(mRectF.left + interval, mRectF.bottom - interval - lengthVertex);
            //左中点
            mPath.moveTo(mRectF.left + interval, mRectF.top + mRectF.height() / 2 - lengthMidpoint);
            mPath.lineTo(mRectF.left + interval, mRectF.top + mRectF.height() / 2 + lengthMidpoint);
            //左上点
            mPath.moveTo(mRectF.left + interval, mRectF.top + interval + lengthVertex);
            mPath.lineTo(mRectF.left + interval, mRectF.top + interval);
            mPath.lineTo(mRectF.left + interval + lengthVertex, mRectF.top + interval);
            //上中点
            mPath.moveTo(mRectF.left + mRectF.width() / 2 - lengthMidpoint, mRectF.top + interval);
            mPath.lineTo(mRectF.left + mRectF.width() / 2 + lengthMidpoint, mRectF.top + interval);
            //右上点
            mPath.moveTo(mRectF.right - interval - lengthVertex, mRectF.top + interval);
            mPath.lineTo(mRectF.right - interval, mRectF.top + interval);
            mPath.lineTo(mRectF.right - interval, mRectF.top + interval + lengthVertex);
            //右中点
            mPath.moveTo(mRectF.right - interval, mRectF.top + mRectF.height() / 2 - lengthMidpoint);
            mPath.lineTo(mRectF.right - interval, mRectF.top + mRectF.height() / 2 + lengthMidpoint);
            //右下点
            mPath.moveTo(mRectF.right - interval, mRectF.bottom - interval - lengthVertex);
            mPath.lineTo(mRectF.right - interval, mRectF.bottom - interval);
            mPath.lineTo(mRectF.right - interval - lengthVertex, mRectF.bottom - interval);
            //下中点
            mPath.moveTo(mRectF.left + mRectF.width() / 2 - lengthMidpoint, mRectF.bottom - interval);
            mPath.lineTo(mRectF.left + mRectF.width() / 2 + lengthMidpoint, mRectF.bottom - interval);
            canvas.drawPath(mPath, mPaint);
        }
    }

    /**
     * 定义点的位置，一共有八个点，用于确定裁剪框位置大小。
     */
    private void CropRectPoints() {
    mPoint[0].set(mRectF.left,mRectF.bottom);//左下点
    mPoint[1].set(mRectF.left,mRectF.bottom - mRectF.height() / 2);
    mPoint[2].set(mRectF.left,mRectF.top);
    mPoint[3].set(mRectF.left + mRectF.width() / 2,mRectF.top);
    mPoint[4].set(mRectF.right,mRectF.top);
    mPoint[5].set(mRectF.right,mRectF.top + mRectF.height() / 2);
    mPoint[6].set(mRectF.right,mRectF.bottom);
    mPoint[7].set(mRectF.right - mRectF.width() / 2,mRectF.bottom);
    }

    /**
     * 两点之间的距离
     * @param p1
     * @param p2
     * @return
     */
    private float distance(PointF p1, PointF p2) {
        float dx = p1.x - p2.x;
        float dy = p1.y - p2.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){

        if (event.getPointerCount() > 1) {
            if (mStatus == STATUS_SINGLE) {
                mStatus = STATUS_MULTI_START;
            } else if (mStatus == STATUS_MULTI_START) {
                mStatus = STATUS_MULTI_TOUCHING;
            }
        } else {
            if (mStatus == STATUS_MULTI_START
                    || mStatus == STATUS_MULTI_TOUCHING) {
                mX_1 = event.getX();
                mY_1 = event.getY();
            }
            mStatus = STATUS_SINGLE;
        }
        try {
            mX_1 = event.getX();
            mY_1 = event.getY();
            mNowPoint.set(mX_1,mY_1);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

            }
        }catch (){

        }


    }
}

