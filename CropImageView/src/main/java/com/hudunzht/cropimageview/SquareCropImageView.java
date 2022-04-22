package com.hudunzht.cropimageview;

import static java.lang.Math.abs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
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
 * date: 2022/4/20
 * desc:自定义正方形imageview SquareCropImageView
 */
public class SquareCropImageView extends AppCompatImageView {
    private boolean isCropRectFill;
    /**
     * 触摸事件的相关属性
     */
    private float mX = 0;
    private float mY = 0;
    //当前触摸的点和上一个点
    private PointF mNowPoint;
    private PointF mLastPoint;
    //触摸事件的判断
    private final int Radius;
    private int mType = 10;
    /**
     * 裁剪框属性
     */
    private final int lineColor; //颜色
    private final int interval;//点与裁剪线间隔
    private final int frameWidth;//边框宽度
    private final int lengthVertex;//顶点两边的距离
    //默认裁剪的宽高；
    private final int cropWidth;
    private final int cropHeigh;
    /**
     * 画笔paint
     * 裁剪框rectf
     * 路径path：画八个点时用到的。
     */
    //路径path
    private Path mPath;
    //画笔
    private Paint mPaint;
    //裁剪框
    private RectF mRectF;
    //呈图框
    private RectF mRectFOri;
    //原图上的裁剪框。
    private RectF mRectFImg;
    //关键点
    private PointF[] mPoint;
    //图片铺满imageview
    private float[] mValue;
    //原图的Bitmap。
    private Bitmap imageOri = null;

    /**
     * 在activity中new一个cropimageview需要此方法
     */
    public SquareCropImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquareCropImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CropImageView);
        //触摸半径
        Radius = array.getInteger(R.styleable.CropImageView_Radius, 60);
        //间隔
        interval = array.getInteger(R.styleable.CropImageView_interval, 0);
        //线的颜色
        lineColor = array.getColor(R.styleable.CropImageView_lineColor, Color.BLACK);
        //顶点距离
        lengthVertex = array.getInteger(R.styleable.CropImageView_lengthVertex, 20);
        //边框宽度
        frameWidth = array.getInteger(R.styleable.CropImageView_frameWidth, 5);
        //默认裁剪宽高
        cropHeigh = array.getInteger(R.styleable.CropImageView_cropHeigh, 200);
        cropWidth = array.getInteger(R.styleable.CropImageView_cropWidth, 200);
        array.recycle();
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(lineColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mRectF = new RectF();
        mRectFOri = new RectF();
        mRectFImg = new RectF();
        mPath = new Path();
        mValue = new float[9];
        mPoint = new PointF[4];
        for (int i = 0; i < 4; ++i) {
            mPoint[i] = new PointF();
        }
        mNowPoint = new PointF();
        mLastPoint = new PointF();
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
                //裁剪框最开始与view中图片一样大
                mRectFOri.set(startX, startY, startX + scaleX * rect.width(), startY + scaleY * rect.height());
                isCropRectFill = true;
                mRectF.set(getWidth() / 2 - 100, getHeight() / 2 - 100, getWidth() / 2 + 100, getHeight() / 2 + 100);
                cropRectPoints();
            }
            //绘制边框；
            mPaint.setStrokeWidth(frameWidth);
            canvas.drawRect(mRectF, mPaint);
            //绘制各点
            mPath.reset();
            //左下点
            mPath.moveTo(mRectF.left + interval + lengthVertex, mRectF.bottom - interval);
            mPath.lineTo(mRectF.left + interval, mRectF.bottom - interval);
            mPath.lineTo(mRectF.left + interval, mRectF.bottom - interval - lengthVertex);
            //左上点
            mPath.moveTo(mRectF.left + interval, mRectF.top + interval + lengthVertex);
            mPath.lineTo(mRectF.left + interval, mRectF.top + interval);
            mPath.lineTo(mRectF.left + interval + lengthVertex, mRectF.top + interval);
            //右上点
            mPath.moveTo(mRectF.right - interval - lengthVertex, mRectF.top + interval);
            mPath.lineTo(mRectF.right - interval, mRectF.top + interval);
            mPath.lineTo(mRectF.right - interval, mRectF.top + interval + lengthVertex);
            //右下点
            mPath.moveTo(mRectF.right - interval, mRectF.bottom - interval - lengthVertex);
            mPath.lineTo(mRectF.right - interval, mRectF.bottom - interval);
            mPath.lineTo(mRectF.right - interval - lengthVertex, mRectF.bottom - interval);
            canvas.drawPath(mPath, mPaint);
        }
    }

    /**
     * 点击拖动事件
     * 判断关键点位置
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:{
                    setNowPoint(event);
                    mType = 10;
                    for (int i = 0; i < mPoint.length; ++i) {
                        if (Radius >= distance(mPoint[i], mNowPoint)) {
                            mType = i;
                            Log.e("zht", "type" + mType);
                            break;
                        }
                    }
                        if (mType == 10) {
                            if (mRectF.contains(event.getX(), event.getY())) {
                                mType = 4;
                            }
                        }
                        mLastPoint.set(mNowPoint.x, mNowPoint.y);
                    }
                case MotionEvent.ACTION_MOVE: {
                    onTouchMove(event);
                    invalidate();
                    mLastPoint.set(mNowPoint.x, mNowPoint.y);
                }
                case MotionEvent.ACTION_UP: {
                    cropRectPoints();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 裁剪框变换逻辑
     *
     * @param event
     */
    private void onTouchMove(MotionEvent event) {
        mNowPoint.set(event.getX(), event.getY());
        float dx = mNowPoint.x - mLastPoint.x;
        float dy = mNowPoint.y - mLastPoint.y;
        float ds = 0;
        if (abs(dx) > abs(dy)){
            ds = abs(dx);
        }else {
            ds = abs(dy);
        }

        if (mType == 4) {
//            到达边界的判断
            if (mRectFOri.left > mRectF.left + dx) {
                dx = mRectFOri.left - mRectF.left;
            }
            if (mRectFOri.right < mRectF.right + dx) {
                dx = mRectFOri.right - mRectF.right;
            }
            if (mRectFOri.top > mRectF.top + dy) {
                dy = mRectFOri.top - mRectF.top;
            }
            if (mRectFOri.bottom < mRectF.bottom + dy) {
                dy = mRectFOri.bottom - mRectF.bottom;
            }
            //移动整个裁剪框
            mRectF.offset(dx, dy);
        } else {
            switch (mType) {
                case 0:
                    if (dx < 0) {
                        mRectF.left = mRectF.left - ds;
                        mRectF.bottom = mRectF.bottom + ds;
                    }else {
                        mRectF.left = mRectF.left + ds;
                        mRectF.bottom = mRectF.bottom - ds;
                    }
                    break;
            }
        }
    }

    /**
     * 设置lastpoint的坐标
     */
    private void setNowPoint(MotionEvent event) {
        mNowPoint.x = event.getX();
        mNowPoint.y = event.getY();
    }

    /**
     * 定义点的位置，一共有八个点，用于确定裁剪框位置大小。
     */
    private void cropRectPoints() {
        mPoint[0].set(mRectF.left, mRectF.bottom);//左下点
        mPoint[1].set(mRectF.left, mRectF.top);
        mPoint[2].set(mRectF.right, mRectF.top);
        mPoint[3].set(mRectF.right, mRectF.bottom);
    }

    /**
     * 两点之间的距离
     *
     * @param p1
     * @param p2
     * @return
     */
    private float distance(PointF p1, PointF p2) {
        float dx = p1.x - p2.x;
        float dy = p1.y - p2.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

}

