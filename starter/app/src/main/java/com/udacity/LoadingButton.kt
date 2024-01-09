package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import kotlin.properties.Delegates


private enum class ButtonColor( val c: Int){
    RED(Color.RED),
    GREEN(Color.GREEN),
    BLUE(Color.BLUE);

    fun next() = when (this){
        RED -> GREEN
        GREEN-> BLUE
        BLUE -> RED
    }
}

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
    }

    override fun performClick(): Boolean {
        if( super.performClick() ) return true // why?!

        buttonColor = buttonColor.next()
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = buttonColor.c
        canvas?.drawRect(0.0f,0.0f,100.0f,100.0f, paint)
    }
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.BOLD)
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