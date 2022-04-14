package com.hudunzht.cropimageview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.hardware.camera2.params.BlackLevelPattern;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * author: ZHT
 * date: 2022/4/13
 * desc:
 */
public class CropImageView extends AppCompatImageView {
    //touch 用的点；
    private float mX_1 = 0;
    private float mY_1 = 0;
    //触摸事件的判断；
    private final int STATUS_SINGLE = 1;
    private final int STATUS_MULTI_START = 2;
    private final int STATUS_MULTI_TOUCHING = 3;
    //当前状态；
    private int mStatus = STATUS_SINGLE;
    //默认裁剪的宽高；
    private int cropWidth;
    private int cropHeigh;
    //最大缩小放大；
    protected float oriRationWH = 0;
    protected final float maxZoomOut = 5.0f;
    protected final float minZoomIn = 0.333333f;

    private Path mPath;
    private Paint mPaint;
    private RectF mRectF;
    //上下文；
    protected Context mContext;

    public CropImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mRectF = new RectF();
        mPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null != getDrawable()) {
            //绘制剪切框中的分割线；
            mPaint.setStrokeWidth(2);
            for (int i = 1; i < 3; ++i) {
                //水平分割线；
                float XHorizontal = mRectF.left + i * mRectF.width() / 3;
                canvas.drawLine(XHorizontal, mRectF.top, XHorizontal, mRectF.bottom, mPaint);
                //竖直分割线；
                float YVertical = mRectF.top + i * mRectF.height() / 3;
                canvas.drawLine(mRectF.left, YVertical, mRectF.right, YVertical, mPaint);
            }
            //绘制边框；
            mPaint.setStrokeWidth(5);
            canvas.drawRect(mRectF, mPaint);
            //绘制各顶点
            mPath.reset();
            //左下点
            mPath.moveTo(mRectF.left + 8 + 20, mRectF.bottom - 8);
            mPath.lineTo(mRectF.left + 8, mRectF.bottom - 8);
            mPath.lineTo(mRectF.left + 8, mRectF.bottom - 8 - 20);
            //左中点
            mPath.moveTo(mRectF.left + 8, mRectF.top + mRectF.height() / 2 - 20);
            mPath.moveTo(mRectF.left + 8, mRectF.top + mRectF.height() / 2 + 20);
            //左上点
            mPath.moveTo(mRectF.left + 8, mRectF.top + 8 + 20);
            mPath.lineTo(mRectF.left + 8, mRectF.top + 8);
            mPath.lineTo(mRectF.left + 8 + 20, mRectF.top + 8);
            //上中点
            mPath.moveTo(mRectF.left + mRectF.width() / 2 - 20, mRectF.top + 8);
            mPath.moveTo(mRectF.left + mRectF.width() / 2 - 20, mRectF.top + 8);
            //右上点
            mPath.moveTo(mRectF.right - 8 - 20, mRectF.top + 8);
            mPath.lineTo(mRectF.right - 8, mRectF.top + 8);
            mPath.lineTo(mRectF.right - 8, mRectF.top + 8 + 20);
            //右中点
            mPath.moveTo(mRectF.right - 8, mRectF.top + mRectF.height() / 2 - 20);
            mPath.moveTo(mRectF.right - 8, mRectF.top + mRectF.height() / 2 + 20);
            //右下点
            mPath.moveTo(mRectF.right - 8, mRectF.bottom - 8 - 20);
            mPath.lineTo(mRectF.right - 8, mRectF.bottom - 8);
            mPath.lineTo(mRectF.right - 8 - 20, mRectF.bottom - 8);
            //下中点
            mPath.moveTo(mRectF.left + mRectF.width() / 2 - 20, mRectF.bottom - 8);
            mPath.moveTo(mRectF.left + mRectF.width() / 2 - 20, mRectF.bottom - 8);
        }
    }
}

