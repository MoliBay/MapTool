package com.example.maptool.mapitem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.example.maptool.mapview.EditMapView;

/**
 * 围栏端点类
 */
public class FencePointItem extends BaseMapItem{
    private static final String TAG = "FencePointItem";
    private EditMapView mMapView;
    private PointF mPoint; //（手指点击）映射后的坐标
    private static final int DEFAULT_RADIUS = 4;

    public FencePointItem(Context context, EditMapView mapView, PointF point, long color, int size) {
        super(context);
        mMapView = mapView;
        mPoint = point;
        mColor = color;
        mSize = DEFAULT_PAINT_SIZE;
    }

    @Override
    public void drawGraphics(Canvas canvas) {
        float scale = mMapView.getAllScale();

        mPaint.reset();
        mPaint.setColor((int)mColor);
        mPaint.setStrokeWidth(mSize/scale);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);// 线冒样式：圆角
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        canvas.save();
        canvas.scale(mMapView.getAllScale(), mMapView.getAllScale());
        canvas.clipRect(0, 0, mMapView.getMapViewBitmap().getWidth(), mMapView.getMapViewBitmap().getHeight());
        canvas.drawCircle(mPoint.x, mPoint.y, DEFAULT_RADIUS/scale, mPaint);
        canvas.restore();
    }

    @Override
    public boolean itemClicked(PointF start, PointF end, PointF point) {
        return false;
    }

    public PointF getPoint() {
        return mPoint;
    }
}
