package com.fengsu.levelview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
    private long lastTime; //上一次存储的时间
    private String showText = "0°";  //中间显示的文字
    private float currx; //水平方向平移的距离
    private float curry; //垂直方向平移的距离
    private float lastx; //存储后的传感器坐标x
    private float lasty; //存储后的传感器坐标y
    private float lastz; //存储后的传感器坐标z
    private int textColor = Color.WHITE; //中间文字颜色

    private float textRotation; //文字旋转的角度
    private float rotations; //三维抬起角度
    private float lastox; //存储的圆心x
    private float lastoy; //存储的圆心y

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

        //获取传感器坐标值
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

            //三维抬起角度
            int rotation = (int) Math.round((180 * (angle / Math.PI)) - 90);
            double dirCos = x / Math.sqrt(x * x + z * z);
            double dirAngle = Math.acos(dirCos);

            //方向角（绕z轴旋转）
            float dirRotation = Math.round((360 * (dirAngle / Math.PI)) - 180);
            currx = x;
            curry = y;
            showText = rotation + "°";
            rotations = rotation;
            textRotation = dirRotation;
            invalidate();
        }
        else {
//            Toast.makeText(this, "晃动速度过快", Toast.LENGTH_SHORT).show();
        }
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
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
            canvas.drawColor(Color.BLACK);

            //上面球
            canvas.save();
            canvas.drawCircle(ox, oy, 220f, paint);
            canvas.restore();

            //对称球
            canvas.save();
            paint.setColor(Color.BLACK);
            canvas.rotate(180f, getWidth() / 2f, getHeight() / 2f);
            canvas.drawCircle(ox, oy, 220f, paint);
            canvas.restore();

            //文本样式
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(textColor);
            paint.setTextSize(200f);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
            canvas.save();
            textColor = Color.BLACK;
            canvas.rotate(textRotation, getWidth() / 2f, getHeight() / 2f);
            canvas.drawText(showText, getWidth() / 2f + 40, getHeight() / 2f + 70, paint);
            canvas.restore();
            canvas.restoreToCount(layerId);
            setLayerType(LAYER_TYPE_HARDWARE, null);
        } else {
            canvas.drawColor(Color.GREEN);

            //0°
            canvas.save();
            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(200f);
            canvas.rotate(textRotation, getWidth() / 2f, getHeight() / 2f);
            canvas.drawText(showText, getWidth() / 2f + 40, getHeight() / 2f + 43, paint);
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
