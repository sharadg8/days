package com.sharad.days;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.View;
import android.view.animation.AnimationUtils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sharad on 13-Sep-15.
 */
public class ProgressView extends View {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float start_degree;
    private final float max_degree = 280;
    private float value_degree;
    private float target_degree;
    private int color_id;
    private Bitmap shadow;
    private RectF rectf;
    private RectF rectfc;
    private int innerStroke;
    private int outerStroke;

    public ProgressView(Context context, int color) {
        super(context);
        color_id = color;
        target_degree = max_degree * (28.6f / 100);
        start_degree = 90 + ((360 - max_degree) / 2);
    }

    public void setProgress(int value, int max, String unit) {
        target_degree = max_degree * ((float)value / max);
        invalidate();
    }

    public void setColor(int value) {
        color_id = value;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        value_degree = 0;
        animator.run();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Rect rect = new Rect();
        this.getDrawingRect(rect);
        rectf = new RectF(rect);
        float diameter = Math.min(rectf.width(), rectf.height());
        rectf.left = (rectf.right - rectf.left - diameter) / 2;
        rectf.top = (rectf.bottom - rectf.top - diameter) / 2;
        rectf.right = rectf.left + diameter;
        rectf.bottom = rectf.top + diameter;

        rectfc = new RectF(rectf);
        scale(rectfc, 0.8f);

        innerStroke = (int)Math.min(0.01f * diameter, 6f);
        outerStroke = (int)Math.min(0.04f * diameter, 24f);

        shadow = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Path path = new Path();
        path.arcTo(rectfc, start_degree, max_degree, true);

        Canvas canvas = new Canvas(shadow);
        paint.setColor(color_id);
        paint.setStrokeWidth(innerStroke);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, paint);
        paint.setStrokeWidth(outerStroke);
    }

    private void scale(RectF rect, float factor){
        float diffHorizontal = ((rect.width()) * (1f-factor)) / 2f;
        float diffVertical = ((rect.height()) * (1f-factor)) / 2f;
        rect.top += diffVertical;
        rect.bottom -= diffVertical;
        rect.left += diffHorizontal;
        rect.right -= diffHorizontal;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.STROKE);

        Path path1 = new Path();
        path1.arcTo(rectfc, start_degree, max_degree, true);
        paint.setStrokeWidth(innerStroke);
        canvas.drawPath(path1, paint);

        Path path2 = new Path();
        path2.arcTo(rectfc, start_degree, value_degree, true);
        paint.setStrokeWidth(outerStroke);
        canvas.drawPath(path2, paint);

        paint.setStyle(Paint.Style.FILL);
        PointF pt1 = calculatePointOnArc(start_degree);
        PointF pt2 = calculatePointOnArc(start_degree + value_degree);
        canvas.drawCircle(pt1.x, pt1.y, (outerStroke / 2f), paint);
        canvas.drawCircle(pt2.x, pt2.y, (outerStroke / 2f), paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(120);
        int percent = (int)Math.ceil(target_degree * 100 / max_degree);
        canvas.drawText(""+percent+"%", rectfc.centerX(), rectfc.centerY(), paint);
    }

    private PointF calculatePointOnArc(float angle) {
        PointF point = new PointF();
        double angleRadian = angle * (Math.PI / 180);
        point.x = (float) (rectfc.centerX() + (rectfc.width() / 2f) * Math.cos(angleRadian));
        point.y = (float) (rectfc.centerY() + (rectfc.width() / 2f) * Math.sin(angleRadian));
        return point;
    }

    private Runnable animator = new Runnable() {
        @Override
        public void run() {
            if (value_degree < target_degree) {
                value_degree += 1;
                postDelayed(this, 15);
            }
            invalidate();
        }
    };
}
