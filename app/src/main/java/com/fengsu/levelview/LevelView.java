package com.fengsu.levelview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * author: liuyu
 * data: 2022-4-19
 * desc: 自定义View水平仪
 */
public class LevelView extends View implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;

    private long currentTime = System.currentTimeMillis();
    private long lastTime;
    private String showText = "0°";
    private float currx;
    private float curry;
    private float lastx;
    private float lasty;
    private float lastz;
    private int textColor = Color.WHITE;
    private int xballColor = Color.WHITE;

    private float textRotation;
    private float rotations;
    private float lastox;
    private float lastoy;

    public LevelView(Context context) {
        super(context);
        init(context);
    }

    public LevelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LevelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public void init(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }

    public void openSensor() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void closeSensor() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        /*
         * 重力传感器算倾斜角度
         * 改变的时间和位置
         */
        long diffTime = currentTime - lastTime;
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];
        Float deltax = x - lastx;
        Float deltay = y - lasty;
        Float deltaz = z - lastz;
        lastx = x;
        lasty = y;
        lastz = z;
        double speed = (Math.sqrt(deltax * deltax + deltay * deltay + deltaz * deltaz) / diffTime * 10000);
        int maxSpeed = 1500;
        if (speed < maxSpeed) {
            double cos = Math.sqrt(x * x + y * y) / Math.sqrt(x * x + y * y + z * z);
            if (cos > 1) {
                cos = 1.0;
            } else if (cos < -1) {
                cos = -1.0;
            }
            double angle = Math.acos(cos);
            int rotation = (int) Math.round((180 * (angle / Math.PI)) - 90); //三维抬起角度
            double dirCos = x / Math.sqrt(x * x + z * z);
            double dirAngle = Math.acos(dirCos);
            float dirRotation = Math.round((360 * (dirAngle / Math.PI)) - 180); //方向角（绕z轴旋转）
            currx = x;
            curry = y;
            showText = rotation + "°";
            rotations = rotation;
            textRotation = dirRotation;
            Log.e("aaaaa",""+rotation);
            invalidate();
        }  //            else: Toast.makeText(this,"晃动速度过快", Toast.LENGTH_SHORT).show();
        lastTime = currentTime;
        currentTime = System.currentTimeMillis();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        Paint paintText = new Paint();
        Paint pText = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint pxBall = new Paint();
        pxBall.setColor(xballColor);
        pText.setTextAlign(Paint.Align.CENTER);
        pText.setColor(textColor);
        pText.setTextSize(200f);
        pxBall.setAntiAlias(true);
        pxBall.setDither(true);
        Log.e("角度",""+rotations);
        if (rotations != 0.0f) {
            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);
            paint.setDither(true);
            float ox = getWidth() / 2f - currx * 150;
            float oy = getHeight() / 2f - curry * 150;
            Float deltax = ox - lastox;
            Float deltay = oy - lastoy;
            float s = (float) Math.sqrt(deltax * deltax + deltay * deltay);
            lastox = ox;
            lastoy = oy;

            int layerId = canvas.saveLayer(0f, 0f, getWidth(), getHeight(), null);
            pxBall.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
            canvas.drawColor(Color.BLACK);

            //上面球
            canvas.save();
            canvas.drawCircle(ox, oy, 220f, pxBall);
            canvas.restore();

            //对称球
            canvas.save();
            xballColor = Color.BLACK;
            canvas.rotate(180f, getWidth() / 2f, getHeight() / 2f);
            canvas.drawCircle(ox, oy, 220f, pxBall);
            canvas.restore();

            //文本样式
            pText.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
            canvas.save();
            textColor = Color.BLACK;
            canvas.rotate(textRotation, getWidth() / 2f, getHeight() / 2f);
            canvas.drawText(showText, getWidth() / 2f + 40, getHeight() / 2f + 70, pText);
            canvas.restore();
            canvas.restoreToCount(layerId);
            setLayerType(LAYER_TYPE_HARDWARE, null);
        } else {
            paintText.setColor(Color.WHITE);
            paintText.setTextAlign(Paint.Align.CENTER);
            paintText.setTextSize(200f);
            canvas.drawColor(Color.GREEN);

            //0°
            canvas.save();
            canvas.rotate(textRotation, getWidth() / 2f, getHeight() / 2f);
            canvas.drawText(showText, getWidth() / 2f + 40, getHeight() / 2f + 43, paintText);
            canvas.restore();
            setLayerType(LAYER_TYPE_HARDWARE, null);

            //空心圆
            canvas.save();
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE); // Style 修改为画线模式
            paint.setStrokeWidth(8f); // 线条宽度为 8 像素
            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, 220f, paint);
            canvas.restore();
        }
    }

}
