package com.example.sergey.tgplibrary.telegram.passport

import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.*
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import com.example.sergey.tgplibrary.R

class TelegramLoginButton(context: Context, attrs: AttributeSet? = null) : Button(context, attrs) {

    private val dp = resources.displayMetrics.density
    private var background: DynamicRoundRectDrawable? = null
    private var overlay: DynamicRoundRectDrawable? = null

    init {
        isAllCaps = false
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
        setTextColor(0xFFFFFFFF.toInt())
        gravity = Gravity.CENTER

        initBackground()
        setPadding(Math.round(16 * dp), 0, Math.round(21 * dp), 0)
        initCorners(attrs)
        initCompoundDrawables()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec,
                Math.round(47 * dp) or MeasureSpec.EXACTLY)
    }

    fun setCornerRoundness(roundness: Float) {
        background?.radiusPercent = roundness
        overlay?.radiusPercent = roundness
    }

    private fun initBackground() {
        background = DynamicRoundRectDrawable()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            elevation = 0f
            stateListAnimator = null
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)

            val stateList = ColorStateList(
                    arrayOf(
                            intArrayOf(android.R.attr.state_pressed),
                            intArrayOf()
                    ),
                    intArrayOf(0x20000000, 0x20000000)
            )
            setBackgroundDrawable(RippleDrawable(stateList, background, background))
        } else {
            typeface = Typeface.DEFAULT_BOLD

            overlay = DynamicRoundRectDrawable().apply { setColor(0x18000000) }

            val stateList = StateListDrawable().apply {
                addState(intArrayOf(android.R.attr.state_pressed), overlay)
                addState(intArrayOf(), ColorDrawable(0))
                setExitFadeDuration(250)
                setEnterFadeDuration(100)
            }
            setBackgroundDrawable(LayerDrawable(arrayOf(background, stateList)))
        }
    }

    private fun initCorners(attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(
                attrs, R.styleable.TelegramLoginButton, 0, 0)
        try {
            setCornerRoundness(a.getFloat(
                    R.styleable.TelegramLoginButton_cornerRoundness, .3f))
        } finally {
            a.recycle()
        }
    }

    private fun initCompoundDrawables() {
        if (Build.VERSION.SDK_INT >= 17) {
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.telegram_logo, 0, 0, 0)
        } else {
            setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.telegram_logo, 0, 0, 0)
        }
        compoundDrawablePadding = Math.round(16 * dp)
    }

    private class DynamicRoundRectDrawable : Drawable() {

        private val rect = RectF()
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF349ff3.toInt()
        }

        var radiusPercent = 0f
            set(value) {
                field = value
                invalidateSelf()
            }

        private var initialAlpha = 255

        override fun draw(canvas: Canvas) {
            rect.set(bounds)
            val radius = rect.height() / 2f * radiusPercent
            canvas.drawRoundRect(rect, radius, radius, paint)
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = Math.round(initialAlpha * alpha / 255f)
            invalidateSelf()
        }

        override fun getOpacity() = PixelFormat.UNKNOWN

        override fun setColorFilter(colorFilter: ColorFilter?) {}

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun getOutline(outline: Outline) {
            outline.setRoundRect(bounds, bounds.height() / 2f * radiusPercent)
        }

        fun setColor(color: Int) {
            paint.color = color
            initialAlpha = paint.alpha
        }
    }
}