package com.example.maptool.mapitem;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.maptool.R;
import com.example.maptool.mapview.EditMapView;
import com.example.maptool.util.DrawUtil;

public class MarkMapItem extends BaseMapItem {
    private static final String TAG = "MarkMapItem";
    private final int DEFALUT_TEXT_PER_WIDTH = 6;
    private final int DEFALUT_TEXT_BACKGROUND_PADDING = 6;
    private final int DEFAULT_TEXT_START = 12;
    private final int DEFAULT_TEXT_BOTTOM = 18;
    private EditMapView mMapView;
    private PointF mLocation;
    private RectF mArea;
    private String mName;
    private float mAngle;
    private int mType;

    public MarkMapItem(EditMapView mapView, PointF pointF, String name,
                       float angle, long color, int type) {
        super(mapView.getContext());
        mMapView = mapView;
        mLocation = pointF;
        mName = name;
        mAngle = angle;
        mColor = color;
        mType = type;
    }

    /**
     * 绘制文字背景
     * @param count 字符个数
     * @param scale 图片缩放比例
     * @param offsetX x轴方向调整量，坐标对齐
     * @param offsetY y轴方向调整量，坐标对齐
     * @return
     */
    private RectF getTextBackgound(int count, float scale, int offsetX, int offsetY) {
        mArea = new RectF((mLocation.x * scale - offsetX) +  DrawUtil.dip2px(mContext,DEFAULT_TEXT_START),
                mLocation.y * scale - offsetY,
                (mLocation.x * scale - offsetX +  DrawUtil.dip2px(mContext,DEFAULT_TEXT_START))
                        + DrawUtil.dip2px(mContext,DEFALUT_TEXT_PER_WIDTH * count + DEFALUT_TEXT_BACKGROUND_PADDING) ,
                (mLocation.y * scale - offsetY)  + DrawUtil.dip2px(mContext, DEFAULT_TEXT_BOTTOM));
        return mArea;
    }

    private Paint getTextPaint() {
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(25);
        textPaint.setStyle(Paint.Style.FILL);
        //设置基线点到底为center
        textPaint.setTextAlign(Paint.Align.CENTER);

        return textPaint;
    }

    private int getBaseLineY(RectF rectF, Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float top = fontMetrics.top;//基线到字体上边框的距离
        float bottom = fontMetrics.bottom;//基线到字体下边框的距离

        int baseLineY = (int) (rectF.centerY() - top/2 - bottom/2);//基线中间点的y轴计算公式

        return baseLineY;
    }

    private void drawRectText(Canvas canvas, RectF rectF) {
        Paint rectPaint = new Paint();
        rectPaint.setColor(Color.GRAY);
        rectPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rectF, rectPaint);
        Paint textPaint = getTextPaint();
        canvas.drawText(mName, rectF.centerX(), getBaseLineY(rectF, textPaint), textPaint);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void drawGraphics(Canvas canvas) {
        Log.d(TAG, "draw mark item");
        RectF rectF;
        float scale = mMapView.getAllScale();
        canvas.save();
        Resources resource = mContext.getResources();
        Bitmap iconCo = BitmapFactory.decodeResource(resource, R.mipmap.bar_r)
                .copy(Bitmap.Config.ARGB_8888, true);

        if (getColor() == mContext.getColor(R.color.red)) {
            //绘制图标
            iconCo = BitmapFactory.decodeResource(resource, R.mipmap.bar_r)
                    .copy(Bitmap.Config.ARGB_8888, true);

        } else if (getColor() == mContext.getColor(R.color.orange)) {
            iconCo = BitmapFactory.decodeResource(resource, R.mipmap.bar_y)
                    .copy(Bitmap.Config.ARGB_8888, true);
        } else if (getColor() == mContext.getColor(R.color.green)) {
            iconCo = BitmapFactory.decodeResource(resource, R.mipmap.bar_g)
                    .copy(Bitmap.Config.ARGB_8888, true);
        } else if (getColor() == mContext.getColor(R.color.blue)) {
            iconCo = BitmapFactory.decodeResource(resource, R.mipmap.bar_b)
                    .copy(Bitmap.Config.ARGB_8888, true);
        } else if (getColor() == mContext.getColor(R.color.purple)) {
            iconCo = BitmapFactory.decodeResource(resource, R.mipmap.bar_p)
                    .copy(Bitmap.Config.ARGB_8888, true);
        } else if (getColor() == mContext.getColor(R.color.grey)) {
            iconCo = BitmapFactory.decodeResource(resource, R.mipmap.bar_black)
                    .copy(Bitmap.Config.ARGB_8888, true);
        } else {
            Log.e(TAG, "markitem no color, default color red");
        }

        canvas.drawBitmap(iconCo, (float) (mLocation.x * scale - iconCo.getWidth()/2.0),
                (mLocation.y * scale - iconCo.getHeight()), null);
        //绘制文字及背景
        rectF = getTextBackgound(calculateCharLength(), scale, iconCo.getWidth()/2, iconCo.getHeight());
        drawRectText(canvas, rectF);
        canvas.restore();
    }

    private int calculateCharLength() {
        int length = 0;

        String chinese = "[\u4e00-\u9fa5]";
        for (int i = 0; i < mName.length(); i++) {
            String temp = mName.substring(i, i + 1);
            if (temp.matches(chinese)) {
                length += 2;
            } else {
                length += 1;
            }
        }
        return length;
    }

    public void updateName(String name) {
        mName = name;
    }

    public PointF getLocation() {
        return mLocation;
    }

    public String getName() {
        return mName;
    }

    public float getAngle() {
        return mAngle;
    }

    public int getType() {
        return mType;
    }

    @Override
    public boolean itemClicked(PointF start, PointF end, PointF point) {

        if (calculateArea(mLocation, point)) {
            Log.d(TAG, "clicked markitem");
            return true;
        } else {
            Log.d(TAG, "not clicked markitem");
            return false;
        }
    }

    private boolean calculateArea(PointF targetPoint, PointF touchPoint) {
        double distance = (touchPoint.x - targetPoint.x)* (touchPoint.x - targetPoint.x)
                + (touchPoint.y - targetPoint.y)*(touchPoint.y - targetPoint.y);
        Log.d(TAG, "distance: " + distance);
        if (distance < 100) {
            Log.d(TAG, "touch poi: " + mName);
            return true;
        } else {
            return false;
        }
    }
}
