package com.example.maptool.mapitem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;

import com.example.maptool.mapview.EditMapView;
import com.example.maptool.util.DrawUtil;

/**
 * 围栏线段类
 */
public class FenceLineItem extends BaseMapItem{
    private static final String TAG = "FenceLineItem";
    private static final int DEFAULT_RADIUS = 4;
    private EditMapView mMapView;
    private final Path mPath = new Path(); // 画笔的路径
    private PointF mSxy ; // 映射后的起始坐标，（手指点击）
    private PointF mDxy ; // 映射后的终止坐标，（手指抬起）
    private FencePointItem mSpoint; //起点对应的pointItem
    private FencePointItem mDpoint; //终点对应的pointItem

    public FenceLineItem(Context context, PointF sxy, PointF dxy, long color,
                         EditMapView mapView, FencePointItem spoint, FencePointItem dpoint) {
        super(context);
        mMapView = mapView;
        mSxy = sxy;
        mDxy = dxy;
        mColor = color;
        mSize = DEFAULT_PAINT_SIZE;
        mSpoint = spoint;
        mDpoint = dpoint;
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
        canvas.drawCircle(mSpoint.getPoint().x, mSpoint.getPoint().y, DEFAULT_RADIUS/scale, mPaint);
        canvas.drawPath(getPath(), mPaint);
        canvas.drawCircle(mDpoint.getPoint().x, mDpoint.getPoint().y, DEFAULT_RADIUS/scale, mPaint);
        canvas.restore();
    }

    @Override
    public boolean itemClicked(PointF start, PointF end, PointF point) {
        double d = calculateDistance(start, end, point);
        Log.d(TAG, "start:" + start.x + "," + start.y);
        Log.d(TAG, "end:" + end.x + "," + end.y);
        Log.d(TAG, "point:" + point.x + "," + point.y);
        if ( DrawUtil.dip2px(mContext, (float) d) < DrawUtil.dip2px(mContext,20)) {
            return true;
        } else {
            return false;
        }
    }

    public void updateLine(Path path) {
        path.moveTo(mSxy.x, mSxy.y);
        path.lineTo(mDxy.x, mDxy.y);
        addLinePath(path);
    }

    public void addLinePath(Path path) {
        mPath.reset();
        mPath.addPath(path);
    }

    public boolean containsPoint(PointF point) {
        if (point.x == mSxy.x && point.y == mSxy.y) {
            return true;
        }
        if (point.x == mDxy.x && point.y == mDxy.y) {
            return true;
        }
        return false;
    }

    private double calculateDistance(PointF start, PointF end, PointF point) {
        double distance = 0.0;
        distance = Math.abs((end.y - start.y) * point.x + (start.x - end.x) * point.y + start.x * (start.y - end.y) + start.y * (end.x - start.x))
                /Math.sqrt(Math.pow((end.y - start.y),2) + Math.pow((start.x - start.y),2));

        return distance;
    }

    public PointF getSxy() {
        return mSxy;
    }

    public PointF getDxy() {
        return mDxy;
    }

    public Path getPath() {
        return mPath;
    }

    public FencePointItem getSpoint() {
        return  mSpoint;
    }

    public FencePointItem getDpoint() {
        return mDpoint;
    }
}
