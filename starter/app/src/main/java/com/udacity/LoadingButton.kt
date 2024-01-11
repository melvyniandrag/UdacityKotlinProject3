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
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
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
private const val ANIMATOR_MAX = 1270

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var animationWidth = 0
    private val valueAnimator = ValueAnimator.ofInt(ANIMATOR_MAX)

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    public var savedPlayTime : Long = 0

    private var buttonColor: ButtonColor = ButtonColor.RED

    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonColorBlue = getColor(R.styleable.LoadingButton_buttonColorBlue, 0)
            buttonColorGreen = getColor(R.styleable.LoadingButton_buttonColorGreen, 0)
            buttonColorRed = getColor(R.styleable.LoadingButton_buttonColorRed, 0)
        }
        valueAnimator.addUpdateListener{
            //buttonColor = buttonColor.next()
            animationWidth = (valueAnimator.animatedValue as Int) % ANIMATOR_MAX
            savedPlayTime = valueAnimator.currentPlayTime
            invalidate()
        }
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 2000

    }


    fun restartSavedAnimation() : Unit{
        if(savedPlayTime != 0L){
            valueAnimator.currentPlayTime = savedPlayTime
            valueAnimator.start()
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        //if( super.performClick() ) return true // why?!
        valueAnimator.start()
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = when(buttonColor){
            ButtonColor.RED -> buttonColorRed
            ButtonColor.GREEN -> buttonColorGreen
            ButtonColor.BLUE -> buttonColorBlue
        }

        canvas?.drawRoundRect(0.0f,0.0f,widthSize.toFloat(), heightSize.toFloat(),25.0f, 25.0f, paint)
        canvas?.drawRoundRect(0.0f,0.0f,widthSize.toFloat() * animationWidth * 1.0f / ANIMATOR_MAX , heightSize.toFloat(),25.0f, 25.0f, loadingPaint)
        val arcLeftSide = widthSize.toFloat() / 2.0f + 150.0f // how to get the width of the drawn text? I just use 150.0f based on experimentation

        canvas?.drawArc(arcLeftSide, 3.0f, arcLeftSide+ heightSize.toFloat() - 6.0f,  heightSize.toFloat() - 3.0f, 0.0f, (animationWidth * 1.0f / ANIMATOR_MAX )* 360.0f, true, arcPaint )
        if(animationWidth == 0) {
            canvas?.drawText(
                resources.getString(R.string.button_name),
                widthSize.toFloat() / 2.0f,
                heightSize.toFloat() / 2.0f + 55.0f / 2.0f,
                textPaint
            )
        } else {
            canvas?.drawText(
                resources.getString(R.string.button_loading),
                widthSize.toFloat() / 2.0f - 35.0f,
                heightSize.toFloat() / 2.0f + 55.0f / 2.0f,
                textPaint
            )

        }
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

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        style = Paint.Style.FILL
        color = Color.GREEN
    }

    private val loadingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        style = Paint.Style.FILL
        color = Color.BLACK
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
