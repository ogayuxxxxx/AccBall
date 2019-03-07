package com.example.gateway_pc.accball

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.util.Log
import android.view.SurfaceHolder
import com.example.gateway_pc.accball.R.id.surfaceView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity() : AppCompatActivity(), SensorEventListener, SurfaceHolder.Callback {
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
         //To change body of created functions use File | Settings | File Templates.
    }


    private var surfaceWidth: Int = 0 //サーフェイスビューの幅
    private var surfaceHeight: Int = 0 //サーフェイスビューの高さ

    private var radius = 10.5f    //ボールの半径を表す定数
    private var coef = 100.0f     //ボールの移動量を表す定数

    private var ballX: Float = 0f //ボールの現在のx座標
    private var ballY: Float = 0f //ボールの現在のy座標
    private var vx: Float = 0f    //ボールの現在のx方向への加速
    private var vy: Float = 0f    //ボールの現在のy方向への加速
    private var time: Long = 0L   //前回時間の保持

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)
        val holder = surfaceView.holder
        holder.addCallback(this)

    }


    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (time == 0L) time = System.currentTimeMillis()
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = -event.values[0]
            val y = event.values[1]

            var t = (System.currentTimeMillis() - time).toFloat()
            time = System.currentTimeMillis()
            t /= 1000.0f

            val dx = vx * t + x * t * t / 2.0f
            val dy = vy * t + y * t * t / 2.0f
            ballX += dx * coef
            ballY += dy * coef
            vx += x * t
            vy += y * t

            if (ballX - radius < 0 && vx < 0) {
                vx = -vx / 1.5f
                ballX = radius
            } else if (ballX + radius > surfaceWidth && vx > 0) {
                vx = -vx / 1.5f
                ballX = surfaceWidth - radius
            }
            if (ballY - radius < 0 && vy < 0) {
                vy = -vy / 1.5f
                ballY = radius
            } else if (ballY + radius > surfaceHeight && vy > 0) {
                vy = -vy / 1.5f
                ballY = surfaceHeight - radius
            }
            drawCanvas()
        }

    }


    override fun surfaceCreated(holder: SurfaceHolder?) {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE)
                as SensorManager
        val accSensor = sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(
                this, accSensor,
                SensorManager.SENSOR_DELAY_GAME)
    }

    override fun surfaceChanged(holder: SurfaceHolder?,
                                format: Int, width: Int, height: Int) {
        surfaceWidth = width
        surfaceHeight = height
        ballX = (width / 2).toFloat()
        ballY = (height / 2).toFloat()
    }


    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE)
                as SensorManager
        sensorManager.unregisterListener(this)
    }

    private fun drawCanvas() {
        val canvas = surfaceView.holder.lockCanvas()
        canvas.drawColor(Color.YELLOW)
        canvas.drawCircle(ballX, ballY, radius, Paint().apply {
            color = Color.MAGENTA
        })
        surfaceView.holder.unlockCanvasAndPost(canvas)
    }
}















