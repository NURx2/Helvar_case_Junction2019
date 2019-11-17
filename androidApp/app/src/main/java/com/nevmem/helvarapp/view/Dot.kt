package com.nevmem.helvarapp.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import com.nevmem.helvarapp.R

class Dot(ctx: Context) : View(ctx) {
    companion object {
        const val defaultSize = 50
        const val padding = 5f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(defaultSize, defaultSize)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let { canvasIt ->
            val width = defaultSize.toFloat()
            val height = defaultSize.toFloat()
            val paint = Paint()
            paint.color = resources.getColor(R.color.dotColor)
            canvasIt.drawOval(
                padding,
                padding,
                width - padding * 2,
                height - padding * 2,
                paint)
        }
    }
}