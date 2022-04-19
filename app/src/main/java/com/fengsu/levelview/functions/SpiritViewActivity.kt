package com.fengsu.levelview.functions

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.fengsu.levelview.R
import kotlin.math.*

/**
 * author: liuyu
 * data: 2022-4-14
 * desc: 添加水平仪
 */
class SpiritViewActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var layout: ConstraintLayout
    private lateinit var view: SpiritView
    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null

    private var currentTime: Long = System.currentTimeMillis() //当前时间
    private var lastTime: Long = 0 //最后移动存储的时间
    private var diffTime: Long? = null //时间差
    private var maxSpeed: Int = 1500 //最大晃动速度
    private var angle: Double = 0.0 //倾斜角度
    private var cos: Double = 0.0 // y/√(x^2 + y^2)
    private var rotation: Int = 0 //倾斜的角度
    private var showText: String = "0°" //初始展示的度数
    private var currx: Float = 0f //平移的X距离
    private var curry: Float = 0f //平移的Y距离

    private var x: Float = 0f //加速度x轴方向
    private var y: Float = 0f //加速度y轴方向
    private var z: Float = 0f //加速度z轴方向
    private var lastx: Float = 0f //上次加速度x轴方向
    private var lasty: Float = 0f //上次加速度y轴方向
    private var lastz: Float = 0f //上次加速度z轴方向

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spirit_view)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        initView()
    }

    private fun initView() {
        layout = findViewById(R.id.spirit_view)
        view = SpiritView(this, attrs = null, 0)
        view.invalidate()
        layout.addView(view)
    }

    /**
     * 传感器变动
     */
    override fun onSensorChanged(event: SensorEvent) {
        /**
         * 加速度传感器算倾斜角度
         * 改变的时间和位置
         */
        diffTime = currentTime - lastTime
        val value = event.values
        x = value[0]
        y = value[1]
        z = value[2]
        Log.e("坐标", "x=" + x + " y=" + y + " z=" + z)
        val deltax: Float = x - lastx
        val deltay: Float = y - lasty
        val deltaz: Float = z - lastz
        //存储本次坐标
        lastx = x
        lasty = y
        lastz = z
        Log.e("存储后的坐标", "lastx=" + lastx + " lasty=" + lasty + " lastz" + lastz)
        //速度：不超过设置的最大速度
        val speed: Double =
            (sqrt(deltax * deltax + deltay * deltay + deltaz * deltaz) / diffTime!! * 10000).toDouble()
        if (speed > maxSpeed) {
//            Toast.makeText(this,"晃动速度过快",Toast.LENGTH_SHORT).show()
        } else {
            //算整体偏移角度
            cos = sqrt((x * x + y * y).toDouble()) / sqrt((x * x + y * y + z * z).toDouble())
            if (cos > 1) {
                cos = 1.0
            } else if (cos < -1) {
                cos = -1.0
            }
            angle = acos(cos)
            rotation = round((180 * (angle / PI)).toFloat() - 90).toInt() //三维抬起角度

            //算两球的运动轨迹
            val dirCos: Double = x / sqrt(x * x + z * z).toDouble()
//            val oblCos: Double = y / sqrt(x*x + y*y).toDouble()
//            val rotCos: Double = x / sqrt(y*y + z*z).toDouble()
            val dirAngle: Double = acos(dirCos)
//            val oblAngle: Double = acos(oblCos)
//            val rotAngle: Double = acos(rotCos)
            val dirRotation: Float = round((360 * (dirAngle / PI)).toFloat() - 180) //方向角（绕z轴旋转）
//            val oblRotation: Float = round((360 * (oblAngle / PI)).toFloat() - 90) //倾斜角（绕y轴旋转）
//            val rotRotation: Float = round((180 * (rotAngle / PI)).toFloat() - 180) //旋转角（绕x轴旋转）
//            currDistance = (view.height / 45.0) * (oblRotation * oblRotation) //斜边距离
            currx = x
            curry = y
            //传感器监听事件后传值刷新页面
            view.onDraws()
            showText = "${rotation}°"
            view.show(rotation.toFloat(), showText, currx, curry, dirRotation)
        }
        lastTime = currentTime
        currentTime = System.currentTimeMillis()
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    /**
     * 暂停传感器
     */
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    /**
     * 传感器监听
     */
    override fun onResume() {
        super.onResume()
        sensor?.also { light ->
            sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }
}