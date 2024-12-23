package com.example.maptool.mapitem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public abstract class BaseMapItem {
    protected static int DEFAULT_PAINT_SIZE = 8;
    private MapOperation mOperation = MapOperation.EDIT_MAP_ERASE;
    protected float mSize;//画笔像素
    protected long mColor;//画笔颜色
    protected Paint mPaint = new Paint();

    protected Context mContext;

    public BaseMapItem(Context context) {
        mContext = context;
    }

    public abstract void drawGraphics(Canvas canvas);
    public abstract boolean itemClicked(PointF start, PointF end, PointF point);

    public MapOperation getOperation() {
        return mOperation;
    }

    public void setOperation(MapOperation mOperation) {
        this.mOperation = mOperation;
    }

    public float getSize() {
        return mSize;
    }

    public void setSize(float mSize) {
        this.mSize = mSize;
    }

    public long getColor() {
        return mColor;
    }

    public void setColor(long mColor) {
        this.mColor = mColor;
    }
}
