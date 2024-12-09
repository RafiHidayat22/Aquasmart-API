package com.example.aquasmart.ui.predict.predict_result

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.ImageView
import com.example.aquasmart.R
import kotlin.random.Random

class BubbleAnimator(
    private val context: Context,
    private val container: ViewGroup
) {

    private val handler = Handler(Looper.getMainLooper())

    fun startAnimating() {
        val bubbleRunnable = object : Runnable {
            override fun run() {
                createBubble()
                handler.postDelayed(this, Random.nextLong(500, 1000)) // Interval antar gelembung
            }
        }
        handler.post(bubbleRunnable)
    }

    private fun createBubble() {
        if (container.width == 0 || container.height == 0) return

        // Randomize bubble size
        val bubbleSize = Random.nextInt(50, 400) // Ukuran gelembung antara 30dp dan 100dp

        // Create bubble
        val bubble = ImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(bubbleSize, bubbleSize)
            setImageResource(R.drawable.bubble_shape)
            alpha = Random.nextFloat().coerceIn(0.5f, 1.0f) // Transparansi
            x = Random.nextInt(0, container.width - bubbleSize).toFloat()
            y = container.height.toFloat()
        }

        container.addView(bubble)

        // Animate bubble upwards
        ObjectAnimator.ofFloat(bubble, "translationY", -bubbleSize.toFloat()).apply {
            duration = Random.nextLong(2000, 5000) // Durasi animasi
            addUpdateListener {
                if (bubble.translationY <= -bubble.height) {
                    container.removeView(bubble) // Hapus gelembung setelah keluar layar
                }
            }
            start()
        }
    }

    fun stopAnimating() {
        handler.removeCallbacksAndMessages(null) // Hentikan semua animasi
    }
}