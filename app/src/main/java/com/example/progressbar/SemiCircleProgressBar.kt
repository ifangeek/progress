package com.example.progressbar

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Interpolator
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import kotlin.math.roundToInt

class SemiCircleProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint: Paint = Paint()
    private var rectangle: RectF? = null
    private var margin: Float
    private var arcProportion: Float = 0f
    private val progressForeground: Paint = Paint()

    companion object{
        private const val ANIMATION_BASE_DURATION_MS: Long = 500
    }


    init {
        backgroundPaint.isAntiAlias = true
        backgroundPaint.color = ContextCompat.getColor(context, R.color.Gray)
        backgroundPaint.style = Paint.Style.STROKE
        backgroundPaint.strokeWidth = dpToPx(1)
        progressForeground.isAntiAlias = true
        progressForeground.color = ContextCompat.getColor(context, R.color.GreenBlue)
        progressForeground.style = Paint.Style.STROKE
        progressForeground.strokeWidth = dpToPx(5)
        margin = dpToPx(5) // margin should be >= strokeWidth / 2 (otherwise the arc is cut)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (rectangle == null) {
            rectangle =
                RectF(0f + margin, 0f + margin, width.toFloat() - margin, height.toFloat() - margin)
        }
        canvas.drawArc(rectangle!!, 180f, 180f, false, backgroundPaint)

        canvas.drawArc(rectangle!!, -180f, (1 - arcProportion) * 180, false, progressForeground)
    }

    fun setArcProportion(arcProportion: Float) {
        ValueAnimator.ofFloat(0f, arcProportion).apply {
            interpolator = AccelerateDecelerateInterpolator()
            // The animation duration is longer for a larger arc
            duration = ANIMATION_BASE_DURATION_MS + (arcProportion * ANIMATION_BASE_DURATION_MS).toLong()
            addUpdateListener { animator ->
                this@SemiCircleProgressBar.arcProportion = animator.animatedValue as Float
                this@SemiCircleProgressBar.invalidate()
            }
            start()
        }
    }

    private fun dpToPx(dp: Int): Float {
        val displayMetrics = context.resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt().toFloat()
    }

}