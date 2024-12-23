package com.example.maptool.mapview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.example.maptool.listener.IMapViewTouchDetector;
import com.example.maptool.util.DrawUtil;

public abstract class BaseMapView<MapOperation> extends FrameLayout {
    public static final String TAG = "BaseMapView";
    public final static float MAX_SCALE = 5f; // 最大缩放倍数
    public final static float MIN_SCALE = 0.25f; // 最小缩放倍数

    protected float mCenterScale; // 图片适应屏幕时的缩放倍数
    protected int mCenterHeight, mCenterWidth;// 图片适应屏幕时的大小（View窗口坐标系上的大小）
    protected float mCentreTranX, mCentreTranY;// 图片在适应屏幕时，位于居中位置的偏移（View窗口坐标系上的偏移）
    protected float mScale = 1; // 在适应屏幕时的缩放基础上的缩放倍数 （ 图片真实的缩放倍数为 mCenterScale*mScale ）
    protected float mTransX = 0, mTransY = 0; // 图片在适应屏幕且处于居中位置的基础上的偏移量（ 图片真实偏移量为mCentreTranX + mTransX，View窗口坐标系上的偏移）
    protected float mMinScale = MIN_SCALE; // 最小缩放倍数
    protected float mMaxScale = MAX_SCALE; // 最大缩放倍数

    protected RectF mMapBound = new RectF();
    protected PointF mTempPoint = new PointF();

    protected Bitmap mBitmap;
    protected Bitmap mMapViewBitmap;
    protected Canvas mMapViewCanvas;
    protected Paint mPaint;
    protected long mColor = Color.RED;
    protected int mPaintColor = Color.WHITE;
    protected int mSize = 10;

    protected IMapViewTouchDetector mDefaultTouchDetector;
    private MapOperation mMapOperation;

    public BaseMapView(@NonNull Context context) {
        super(context);
    }

    public BaseMapView(@NonNull Context context, Bitmap bitmap) {
        super(context);
        mBitmap = bitmap;
    }

    public abstract void refresh();

    public abstract void initMapViewBitmap();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initBound();
    }

    protected void initBound() {
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        float nw = w * 1f / getWidth();
        float nh = h * 1f / getHeight();
        if (nw > nh) {
            mCenterScale = 1 / nw;
            mCenterWidth = getWidth();
            mCenterHeight = (int) (h * mCenterScale);
        } else {
            mCenterScale = 1 / nh;
            mCenterWidth = (int) (w * mCenterScale);
            mCenterHeight = getHeight();
        }
        // 使图片居中
        mCentreTranX = (getWidth() - mCenterWidth) / 2f;
        mCentreTranY = (getHeight() - mCenterHeight) / 2f;

        // 居中适应屏幕
        mTransX = mTransY = 0;
        mScale = 1;

        initMapViewBitmap();

        refresh();
    }

    public int getCenterWidth() {
        return mCenterWidth;
    }

    public int getCenterHeight() {
        return mCenterHeight;
    }

    public float getMapScale() {
        return mScale;
    }

    public void setMapScale(float scale, float pivotX, float pivotY) {
        if (scale < mMinScale) {
            scale = mMinScale;
        } else if (scale > mMaxScale) {
            scale = mMaxScale;
        }

        float touchX = toTouchX(pivotX);
        float touchY = toTouchY(pivotY);
        this.mScale = scale;

        // 缩放后，偏移图片，以产生围绕某个点缩放的效果
        mTransX = toTransX(touchX, pivotX);
        mTransY = toTransY(touchY, pivotY);

        refresh();
    }

    /**
     * 获取当前图片在View坐标系中的矩型区域
     *
     * @return
     */
    public RectF getMapBound() {
        float width = mCenterWidth * mScale;
        float height = mCenterHeight * mScale;
        if (0 % 90 == 0) {
            mTempPoint.x = toTouchX(0);
            mTempPoint.y = toTouchY(0);

            DrawUtil.rotatePoint(mTempPoint, 0, mTempPoint.x, mTempPoint.y, getWidth() / 2, getHeight() / 2);
            mMapBound.set(mTempPoint.x, mTempPoint.y, mTempPoint.x + width, mTempPoint.y + height);
        } else {
            // 转换成屏幕坐标
            // 左上
            float ltX = toTouchX(0);
            float ltY = toTouchY(0);
            //右下
            float rbX = toTouchX(mBitmap.getWidth());
            float rbY = toTouchY(mBitmap.getHeight());
            // 左下
            float lbX = toTouchX(0);
            float lbY = toTouchY(mBitmap.getHeight());
            //右上
            float rtX = toTouchX(mBitmap.getWidth());
            float rtY = toTouchY(0);

            //转换到View坐标系
            DrawUtil.rotatePoint(mTempPoint, 0, ltX, ltY, getWidth() / 2, getHeight() / 2);
            ltX = mTempPoint.x;
            ltY = mTempPoint.y;
            DrawUtil.rotatePoint(mTempPoint, 0, rbX, rbY, getWidth() / 2, getHeight() / 2);
            rbX = mTempPoint.x;
            rbY = mTempPoint.y;
            DrawUtil.rotatePoint(mTempPoint, 0, lbX, lbY, getWidth() / 2, getHeight() / 2);
            lbX = mTempPoint.x;
            lbY = mTempPoint.y;
            DrawUtil.rotatePoint(mTempPoint, 0, rtX, rtY, getWidth() / 2, getHeight() / 2);
            rtX = mTempPoint.x;
            rtY = mTempPoint.y;

            mMapBound.left = Math.min(Math.min(ltX, rbX), Math.min(lbX, rtX));
            mMapBound.top = Math.min(Math.min(ltY, rbY), Math.min(lbY, rtY));
            mMapBound.right = Math.max(Math.max(ltX, rbX), Math.max(lbX, rtX));
            mMapBound.bottom = Math.max(Math.max(ltY, rbY), Math.max(lbY, rtY));
        }
        return mMapBound;
    }

    /**
     * 设置默认手势识别器
     *
     * @param touchGestureDetector
     */
    public void setDefaultTouchDetector(IMapViewTouchDetector touchGestureDetector) {
        mDefaultTouchDetector = touchGestureDetector;
    }

    public float getAllScale() {
        return mCenterScale * mScale;
    }

    public float getAllTranX() {
        return mCentreTranX + mTransX;
    }

    public float getAllTranY() {
        return mCentreTranY + mTransY;
    }

    /**
     * 将屏幕触摸坐标x转换成在图片中的坐标
     */
    public float toX(float touchX) {
        return (touchX - getAllTranX()) / getAllScale();
    }

    /**
     * 将屏幕触摸坐标y转换成在图片中的坐标
     */
    public float toY(float touchY) {
        return (touchY - getAllTranY()) / getAllScale();
    }

    /**
     * 将图片坐标x转换成屏幕触摸坐标
     */
    public float toTouchX(float x) {
        return x * getAllScale() + getAllTranX();
    }

    /**
     * 将图片坐标y转换成屏幕触摸坐标
     */
    public float toTouchY(float y) {
        return y * getAllScale() + getAllTranY();
    }

    /**
     * 坐标换算
     * （公式由toX()中的公式推算出）
     *
     * @param touchX  触摸坐标
     * @param doodleX 在涂鸦图片中的坐标
     * @return 偏移量
     */
    public float toTransX(float touchX, float doodleX) {
        return -doodleX * getAllScale() + touchX - mCentreTranX;
    }

    public float toTransY(float touchY, float doodleY) {
        return -doodleY * getAllScale() + touchY - mCentreTranY;
    }

    public void setMapTranslationX(float transX) {
        this.mTransX = transX;
        refresh();
    }

    public float getMapTranslationX() {
        return mTransX;
    }

    public void setMapTranslationY(float transY) {
        this.mTransY = transY;
        refresh();
    }

    public float getMapTranslationY() {
        return mTransY;
    }

    public void setMapTranslation(float transX, float transY) {
        mTransX = transX;
        mTransY = transY;
        refresh();
    }

    public void setOperation(MapOperation shape) {
        this.mMapOperation = shape;
    }

    public MapOperation getOperation() {
        return mMapOperation;
    }

    public long getColor() {
        return mColor;
    }

    public void setColor(long mColor) {
        this.mColor = mColor;
    }

    public int getSize() {
        return mSize;
    }

    public void setSize(int mSize) {
        this.mSize = mSize;
    }


    public Bitmap getMapViewBitmap() {
        return mMapViewBitmap;
    }
}
