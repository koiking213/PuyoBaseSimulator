package com.example.puyo_base_simulator.ui.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class FieldPreviewCanvas(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var mPaint = Paint()
    private var mField = ""
    override fun onDraw(canvas: Canvas) {
        //canvas.drawColor(Color.argb(127, 0, 127, 63));
        //mPaint.setColor(Color.RED);
        // debug
        for (idx in mField.indices) {
            val row = idx / 6
            val column = idx % 6
            mPaint.color = getColor(mField[idx])
            canvas.drawCircle((10 + 20 * column).toFloat(), (120 - (10 + 20 * row)).toFloat(), 10f, mPaint)
        }
    }

    private fun getColor(c: Char): Int {
        return when (c) {
            'r' -> Color.rgb(255, 0, 0)
            'b' -> Color.rgb(0, 0, 225)
            'y' -> Color.rgb(224, 224, 0)
            'g' -> Color.rgb(0, 128, 128)
            'p' -> Color.rgb(160, 64, 192)
            else -> Color.WHITE
        }
    }

    // should be receive Field class
    fun setField(str: String) {
        mField = str
    }

}