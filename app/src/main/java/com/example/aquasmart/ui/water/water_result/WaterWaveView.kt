package com.example.aquasmart.ui.water.water_result

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.example.aquasmart.R

class WaterWaveView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint()
    private val wavePath = Path()

    private var waveOffset = 0f
    private var waveAmplitude = 50f
    private var waveFrequency = 2f
    private var waveSpeed = 3000L

    private var waveAnimator: ValueAnimator? = null

    init {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.shader = RadialGradient(
            0f, 0f, 500f,
            intArrayOf(0xAA00A1E4.toInt(), 0xFF00A1E4.toInt(), 0xAA00A1E4.toInt()),
            null, Shader.TileMode.MIRROR
        )
        setBackgroundResource(R.drawable.background_waterresult)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        paint.shader = RadialGradient(
            w / 2f, h / 2f, 500f,
            intArrayOf(0xAA00A1E4.toInt(), 0xFF00A1E4.toInt(), 0xAA00A1E4.toInt()),
            null, Shader.TileMode.MIRROR
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        wavePath.reset()
        wavePath.moveTo(0f, height / 2f)
        for (i in 0 until width step 10) {
            val y = (Math.sin(((i + waveOffset) * waveFrequency / width).toDouble()) * waveAmplitude).toFloat()
            wavePath.lineTo(i.toFloat(), height / 2f + y)
        }

        wavePath.lineTo(width.toFloat(), height.toFloat())
        wavePath.lineTo(0f, height.toFloat())
        wavePath.close()
        canvas.drawPath(wavePath, paint)
    }

    private fun updateWaveOffset(offset: Float) {
        waveOffset = offset
        invalidate()
    }

    fun startWaveAnimation() {
        stopWaveAnimation()
        waveAnimator = ValueAnimator.ofFloat(0f, width.toFloat())
        waveAnimator?.apply {
            duration = waveSpeed
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                updateWaveOffset(animatedValue)
            }
            start()
        }
    }

    fun stopWaveAnimation() {
        waveAnimator?.cancel()
        waveAnimator = null
    }

    fun setWaveAmplitude(amplitude: Float) {
        waveAmplitude = amplitude
        invalidate()
    }

    fun setWaveSpeed(speed: Long) {
        waveSpeed = speed
        startWaveAnimation()
    }

    fun setWaveFrequency(frequency: Float) {
        waveFrequency = frequency
        invalidate()
    }
}
