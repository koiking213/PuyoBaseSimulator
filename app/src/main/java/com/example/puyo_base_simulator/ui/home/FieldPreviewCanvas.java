package com.example.puyo_base_simulator.ui.home;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;

public class FieldPreviewCanvas extends View {
    Paint mPaint;
    String mField;

    public FieldPreviewCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //canvas.drawColor(Color.argb(127, 0, 127, 63));
        //mPaint.setColor(Color.RED);
        // debug
        for (int idx=0; idx<mField.length(); idx++) {
            int row = idx/6;
            int column = idx%6;
            mPaint.setColor(getColor(mField.charAt(idx)));
            canvas.drawCircle(10+20*column, 120-(10+20*row), 10, mPaint);
        }
    }

    private int getColor(char c) {
        switch (c) {
            case 'r':
                return Color.rgb(255,0,0);
            case 'b':
                return Color.rgb(0,0,225);
            case 'y':
                return Color.rgb(224,224,0);
            case 'g':
                return Color.rgb(0,128,128);
            case 'p':
                return Color.rgb(160, 64, 192);
            default:
                return Color.WHITE;

        }
    }

    // should be receive Field class
    public void setField(String str) {
        mField = str;
    }
}
