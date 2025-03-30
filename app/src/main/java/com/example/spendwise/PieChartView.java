package com.example.spendwise;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PieChartView extends View {

    public Map<String, Float> data = new HashMap<>();
    public int[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.CYAN, Color.YELLOW};
    public Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public PieChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(32f);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    // Sets the pie chart data and triggers a redraw
    public void setData(Map<String, Float> data) {
        this.data = data;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (data == null || data.isEmpty()) return;

        float total = 0f;
        for (float value : data.values()) total += value;

        float startAngle = 0f;
        int colorIndex = 0;

        // Chart dimensions
        int size = Math.min(getWidth(), getHeight());
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = size / 2f;
        RectF rect = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        // Draw each slice
        for (Map.Entry<String, Float> entry : data.entrySet()) {
            float sweepAngle = (entry.getValue() / total) * 360f;

            paint.setColor(colors[colorIndex % colors.length]);
            canvas.drawArc(rect, startAngle, sweepAngle, true, paint);

            // Draw label
            float medianAngle = (float) Math.toRadians(startAngle + sweepAngle / 2f);
            float labelX = (float) (centerX + (radius / 1.6) * Math.cos(medianAngle));
            float labelY = (float) (centerY + (radius / 1.6) * Math.sin(medianAngle));
            canvas.drawText(entry.getKey(), labelX, labelY, textPaint);

            startAngle += sweepAngle;
            colorIndex++;
        }
    }
}
