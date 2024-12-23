package com.example.maptool.mapitem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import com.example.maptool.mapview.EditMapView;


//地图编辑工具，用于擦除噪点、擦除未知区域
public class EditMapItem extends BaseMapItem {
    private EditMapView mMapView;
    private final Path mPath = new Path(); // 画笔的路径
    private final Path mOriginPath = new Path();

    public EditMapItem(Context context, EditMapView mapView) {
        super(context);
        mMapView = mapView;
    }

    @Override
    public void drawGraphics(Canvas canvas) {
        mPaint.reset();
        mPaint.setColor((int)getColor());
        mPaint.setStrokeWidth(getSize());
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);// 线冒样式：圆角
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        canvas.save();
        //缩放画布，使绘制的图像和地图的缩放一致
        canvas.scale(mMapView.getAllScale(), mMapView.getAllScale());
        //裁剪画布，以免绘制超出地图区域
        canvas.clipRect(0, 0, mMapView.getMapViewBitmap().getWidth(), mMapView.getMapViewBitmap().getHeight());
        canvas.drawPath(getPath(), mPaint);
        canvas.restore();
    }

    public static EditMapItem toPath(EditMapView mapView, Path p) {
        EditMapItem item = new EditMapItem(mapView.getContext(), mapView);
        item.setColor(mapView.getColor());
        item.setSize(mapView.getSize());

        item.updatePath(p);
        return item;
    }

    public void updatePath(Path path) {
        mOriginPath.reset();
        this.mOriginPath.addPath(path);
        //adjustPath();
    }

    public void adjustPath() {
        mPath.reset();
        mPath.addPath(mOriginPath);
    }

    @Override
    public boolean itemClicked(PointF start, PointF end, PointF point) {
        return false;
    }


    public Path getPath() {
        //return mPath;
        return mOriginPath;
    }
}
