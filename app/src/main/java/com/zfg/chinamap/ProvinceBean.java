package com.zfg.chinamap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.text.TextUtils;
import android.util.Log;

public class ProvinceBean {

    private Path path;
    private int drawColor;
    private String name;
    private Paint paintText;

    public ProvinceBean(Path path, String name) {
        this.path = path;
        this.name = name;
        paintText = new Paint();
        paintText.setColor(Color.BLACK);
        paintText.setAntiAlias(true);
        paintText.setStyle(Paint.Style.STROKE);
        paintText.setStrokeWidth(1);
    }

    public void setDrawColor(int drawColor) {
        this.drawColor = drawColor;
    }

    public void drawItem(Canvas canvas, Paint paint, boolean isSelect) {

        RectF rectF = new RectF();
        path.computeBounds(rectF, true);

        if (isSelect) {
            //绘制内部的颜色
            paint.clearShadowLayer();
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.YELLOW);
            canvas.drawPath(path, paint);

            //绘制边界
            paint.setStyle(Paint.Style.STROKE);
            int strokeColor = Color.BLACK;
            paint.setColor(strokeColor);
            canvas.drawPath(path, paint);
        } else {

            //绘制内部的颜色
            paint.clearShadowLayer();
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(drawColor);
            canvas.drawPath(path, paint);

            //绘制边界
//            paint.setStyle(Paint.Style.STROKE);
//            int strokeColor = Color.BLACK;
//            paint.setColor(strokeColor);
//            canvas.drawPath(path, paint);
        }

        if (!TextUtils.isEmpty(name)) {
            float widthText = paint.measureText(name);
            if ("内蒙古".equals(name)) {
                canvas.drawText(name, rectF.centerX() - widthText / 2, (rectF.centerY() + rectF.centerY() / 2) - paint.getStrokeWidth(), paintText);
            } else {
                canvas.drawText(name, rectF.centerX() - widthText / 2, rectF.centerY() - paint.getStrokeWidth(), paintText);
            }
        }
    }

    public boolean isTouch(float x, float y) {

        boolean result = false;
        RectF rectF = new RectF();
        path.computeBounds(rectF, true);
        Region region = new Region();
        region.setPath(path, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
        if (region.contains((int) x, (int) y)) {
            result = true;
        }
        return result;
    }


}
