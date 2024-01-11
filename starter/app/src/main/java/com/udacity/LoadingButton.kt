package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates


private enum class ButtonColor( val label: Int){
    RED(R.string.red),
    GREEN(R.string.green),
    BLUE(R.string.blue);

    fun next() = when (this){
        RED -> GREEN
        GREEN-> BLUE
        BLUE -> RED
    }
}

private var buttonColorRed = 0
private var buttonColorGreen = 0
private var buttonColorBlue = 0

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    private var buttonColor: ButtonColor = ButtonColor.RED


    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonColorBlue = getColor(R.styleable.LoadingButton_buttonColorBlue, 0)
            buttonColorGreen = getColor(R.styleable.LoadingButton_buttonColorGreen, 0)
            buttonColorRed = getColor(R.styleable.LoadingButton_buttonColorRed, 0)
        }

    }


    override fun performClick(): Boolean {
        if( super.performClick() ) return true // why?!

        buttonColor = buttonColor.next()
        invalidate()
        val animator = ObjectAnimator.ofFloat(this, View.ROTATION, -360f, 0f)
        animator.duration = 2000
        animator.disableViewDuringAnimation(this)
        animator.start()
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = when(buttonColor){
            ButtonColor.RED -> buttonColorRed
            ButtonColor.GREEN -> buttonColorGreen
            ButtonColor.BLUE -> buttonColorBlue
        } as Int

        canvas?.drawRoundRect(0.0f,0.0f,widthSize.toFloat(), heightSize.toFloat(),25.0f, 25.0f, paint)
        canvas?.drawText(resources.getString(buttonColor.label), widthSize.toFloat()/2.0f, heightSize.toFloat()/2.0f + 55.0f/2.0f, textPaint)
    }
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.BOLD)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.BOLD)
        color=Color.WHITE
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}
private fun ObjectAnimator.disableViewDuringAnimation(view: View) {

    // This extension method listens for start/end events on an animation and disables
    // the given view for the entirety of that animation.

    addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator) {
            view.isEnabled = false
        }

        override fun onAnimationEnd(animation: Animator) {
            view.isEnabled = true
        }
    })
}
