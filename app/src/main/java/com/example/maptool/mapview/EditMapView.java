package com.example.maptool.mapview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import com.example.maptool.R;
import com.example.maptool.listener.BaseMapViewTouchDetector;
import com.example.maptool.mapitem.BaseMapItem;
import com.example.maptool.mapitem.EditMapItem;
import com.example.maptool.mapitem.FenceLineItem;
import com.example.maptool.mapitem.FencePointItem;
import com.example.maptool.mapitem.MapOperation;
import com.example.maptool.mapitem.MarkMapItem;

import java.util.ArrayList;
import java.util.List;


public class EditMapView<T extends BaseMapItem> extends BaseMapView {
    public static final String TAG = "EditMapView";

    public ForegroundView mForegroundView;
    public BackgroundView mBackgroundView;

    public float mAngle;
    public float mLocationX;
    public float mLocationY;

    // 保存涂鸦操作，便于撤销
    private List<BaseMapItem> mItemStack = new ArrayList<>();
    // 已绘制的兴趣点栈
    private List<MarkMapItem> mMarkStack = new ArrayList<>();
    // 撤销的兴趣点栈
    private List<MarkMapItem> mMarkUndoStack = new ArrayList<>();
    // 文件保存的兴趣点栈
    private List<MarkMapItem> mStoragedMarkStack = new ArrayList<>();
    // 围栏初始点栈
    private List<FencePointItem> mFencePointStack = new ArrayList<>();

    // 撤销的初始点栈
    private List<FencePointItem> mFenceUndoPointStack = new ArrayList<>();
    // 围栏栈
    private List<FenceLineItem> mFenceLineStack = new ArrayList<>();
    // 当前已经绘制在画布上的围栏栈
    private List<FenceLineItem> mFenceLineGroup = new ArrayList<>();
    // 当前在画布上撤销的围栏栈
    private List<FenceLineItem> mFenceLineUndoGroup = new ArrayList<>();
    // 文件保存的围栏栈
    private List<FenceLineItem> mStoragedFenceLineStack = new ArrayList<>();
    // 已绘制的擦除栈
    private List<EditMapItem> mEditStack = new ArrayList<>();
    // 撤销的擦除栈
    private List<EditMapItem> mEditUndoStack = new ArrayList<>();
    private MapOperation mMapOperation;

    private boolean selected = false;

    public EditMapView(Context context) {
        super(context);
    }

    public EditMapView(Context context, Bitmap bitmap) {
        super(context);
        setClipChildren(false);

        mBitmap = bitmap;
        mForegroundView = new ForegroundView(context);
        mBackgroundView = new BackgroundView(context);
        addView(mBackgroundView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(mForegroundView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initView(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mBitmap.isRecycled()) {
            return;
        }

        refreshMapViewBitmap();

        mBackgroundView.invalidate();
        super.dispatchDraw(canvas);
    }

    private void refreshMapViewBitmap() {
        initMapViewBitmap();

//        List<MarkMapItem> items = getMarkItem();
//        for (BaseMapItem item : items) {
//            item.drawGraphics(mMapViewCanvas);
//        }
    }

    @Override
    public void initMapViewBitmap() {
        if (mMapViewBitmap != null) {
            mMapViewBitmap.recycle();
        }
        mMapViewBitmap = mBitmap.copy(mBitmap.getConfig(), true);
        mMapViewCanvas = new Canvas(mMapViewBitmap);
    }

    private void initPaintStyle(Paint paint) {
        paint.setColor(mPaintColor);
        paint.setStrokeWidth(mSize);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);// 线冒样式：圆角
        paint.setStrokeCap(Paint.Cap.ROUND);// 线段连接处样式：圆角
    }

    private void initView(Context context) {
        //手势监听
        mDefaultTouchDetector = new BaseMapViewTouchDetector(context, null);
        mPaint = new Paint();
        initPaintStyle(mPaint);
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setAngle(float mAngle) {
        this.mAngle = mAngle;
    }

    public float getAngle() {
        return mAngle;
    }

    public void setLocationX(float locationX) {
        this.mLocationX = locationX;
    }

    public float getLocationX() {
        return mLocationX;
    }

    public void setLocationY(float locationY) {
        this.mLocationY = locationY;
    }

    public float getLocationY() {
        return mLocationY;
    }

    @Override
    public void refresh() {
        mItemStack.clear();
        mItemStack.addAll(getEditItem());
        mItemStack.addAll(getStoragedMarkItem());
        mItemStack.addAll(getStoragedFenceItem());
        mItemStack.addAll(getMarkItem());
        mItemStack.addAll(getFencePointItem());
        mItemStack.addAll(getFenceLineGroup());
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.invalidate();
            mForegroundView.invalidate();
        } else {
            super.postInvalidate();
            mForegroundView.postInvalidate();
        }
    }

    public void setOperation(MapOperation shape) {
        this.mMapOperation = shape;
    }

    public MapOperation getOperation() {
        return mMapOperation;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void addItem(T item) {
        mItemStack.add(item);
    }

    public void removeItem(T item) {
        mItemStack.remove(item);
    }

    public void addAllItem(List<? extends BaseMapItem> itemList) {
        mItemStack.addAll(itemList);
    }

    public void addMarkItem(MarkMapItem markMapItem) {
        mMarkStack.add(markMapItem);
    }

    public void addStoragedMarkItem(MarkMapItem markMapItem) {
        mStoragedMarkStack.add(markMapItem);
    }

    public void removeMarkItem(MarkMapItem markMapItem) {
        mMarkStack.remove(markMapItem);
        mStoragedMarkStack.remove(markMapItem);
    }

    public void undoMarkItem() {
        if (mMarkStack.size() == 0) {
            return;
        }
        mMarkUndoStack.add(mMarkStack.get(mMarkStack.size() - 1));
        mMarkStack.remove(mMarkStack.size() - 1);
        refresh();
    }

    public void redoMarkItem() {
        if (mMarkUndoStack.size() == 0) {
            return;
        }
        mMarkStack.add(mMarkUndoStack.get(mMarkUndoStack.size() - 1));
        refresh();
    }

    public void restoreMarkLayer() {
        mMarkStack.clear();
        refresh();
    }

    public void addFencePointItem(FencePointItem fencePointItem) {
        mFencePointStack.add(fencePointItem);
    }

    public void addFenceLineItem(FenceLineItem fenceLineItem) {
        mFenceLineStack.add(fenceLineItem);
        mFenceLineGroup.add(fenceLineItem);
        mFencePointStack.clear();
        mFenceUndoPointStack.clear();
    }

    public void addFenceLineGroup(FenceLineItem fenceLineItem) {
        mFenceLineGroup.add(fenceLineItem);
    }

    public void addStoragedLineItem(FenceLineItem fenceLineItem) {
        mStoragedFenceLineStack.add(fenceLineItem);
    }

    public void removeFenceLineGroup(FenceLineItem fenceLineItem) {
        mFenceLineGroup.remove(fenceLineItem);
        mStoragedFenceLineStack.remove(fenceLineItem);
        createNewFenceGroup();
    }

    public void undoFenceItem() {
        if (mFencePointStack.size() != 0) {
            mFenceUndoPointStack.add(mFencePointStack.remove(0));

        } else if (mFenceLineGroup.size() == 0) {
            mFenceLineStack.clear();
        } else {
            mFenceLineUndoGroup.add(mFenceLineGroup.get(mFenceLineGroup.size() - 1));
            FenceLineItem remove = mFenceLineGroup.remove(mFenceLineGroup.size() - 1);
            mFenceLineStack.remove(remove);

        }
        refresh();
    }

    public void redoFenceItem() {
        if (mFenceLineUndoGroup.size() != 0) {
            FenceLineItem fenceLineItem = mFenceLineUndoGroup.get(mFenceLineUndoGroup.size() - 1);
            mFenceLineUndoGroup.remove(fenceLineItem);
            mFenceLineGroup.add(fenceLineItem);
        } else if (mFenceUndoPointStack.size() != 0) {
            mFencePointStack.add(mFenceUndoPointStack.remove(0));

        }

        refresh();
    }

    public void restoreFenceLayer() {
        mFenceLineGroup.clear();
        refresh();
    }

    public void addEditItem(EditMapItem editMapItem) {
        mEditStack.add(editMapItem);
    }

    public void removeEditItem(EditMapItem editMapItem) {
        mEditStack.remove(editMapItem);
    }

    public void undoEditItem() {
        if (mEditStack.size() == 0) {
            return;
        }
        mEditUndoStack.add(mEditStack.get(mEditStack.size() - 1));
        mEditStack.remove(mEditStack.size() - 1);
        refresh();
    }

    public void redoEditItem() {
        if (mEditUndoStack.size() == 0) {
            return;
        }
        mEditStack.add(mEditUndoStack.get(mEditUndoStack.size() - 1));
        refresh();
    }

    public void restoreEditLayer() {
        mEditStack.clear();
        refresh();
    }

    public List<FencePointItem> getFencePointItem() {
        return mFencePointStack;
    }

    public List<FenceLineItem> getFenceLineItem() {
        return mFenceLineStack;
    }

    public void createNewFenceGroup() {
        mFencePointStack.clear();
        mFenceLineStack.clear();
        mFencePointStack.clear();
        mFenceUndoPointStack.clear();
    }

    public List<FenceLineItem> getFenceLineGroup() {
        return mFenceLineGroup;
    }

    public List<MarkMapItem> getStoragedMarkItem() {
        return mStoragedMarkStack;
    }

    public List<FenceLineItem> getStoragedFenceItem() {
        return mStoragedFenceLineStack;
    }

    public List<MarkMapItem> getMarkItem() {
        return mMarkStack;
    }

    public List<EditMapItem> getEditItem() {
        return mEditStack;
    }

    public List<BaseMapItem> getItem() {
        return mItemStack;
    }

    public void saveEditItem()
    {
        Canvas canvas = new Canvas(mBitmap);
        float scale = getAllScale();
        canvas.scale(1/scale, 1/scale);
        for (EditMapItem item : mEditStack) {
            item.drawGraphics(canvas);
        }
    }

    // 前景图层，每次刷新都会绘制，用于绘制正在创建或选中的item
    public class ForegroundView extends View {

        public ForegroundView(Context context) {
            super(context);
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            foregoundDoDraw(canvas);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return mDefaultTouchDetector.onTouchEvent(event);
        }

        protected void foregoundDoDraw(Canvas canvas) {
            float left = getAllTranX();
            float top = getAllTranY();
            float scale = getAllScale();
            List<BaseMapItem> items = mItemStack;

            Log.d(TAG, "foregournd scale=" + scale);

            canvas.translate(left, top); // 偏移画布

            //canvas.save();
            for (BaseMapItem item : items) {
                if (item != null) {
                    item.drawGraphics(canvas);
                }
            }
            //canvas.restore();
        }
    }

    // 背景图层，只在背景发生变化时绘制， 用于绘制原始图片或在优化绘制时的非编辑状态的item
    public class BackgroundView extends View {

        public BackgroundView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            backgroundDoDraw(canvas);
        }

        protected void backgroundDoDraw(Canvas canvas) {
            Log.d(TAG, "backgroundDoDraw");
            float left = getAllTranX();
            float top = getAllTranY();
            float scale = getAllScale();

            // 画布和图片共用一个坐标系，只需要处理屏幕坐标系到图片（画布）坐标系的映射关系
            canvas.translate(left, top); // 偏移画布

            canvas.save();
            canvas.scale(scale, scale); // 缩放画布
            Bitmap bitmap = mMapViewBitmap;
            canvas.drawBitmap(bitmap, 0, 0, null);
            canvas.restore();

        }
    }
}
