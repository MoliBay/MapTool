package com.example.maptool.listener;

import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.example.maptool.mapview.BaseMapView;


public class BaseMapViewGestureListener<T extends BaseMapView> extends TouchGestureDetector.OnTouchGestureListener {
    public static final float VALUE = 1f;

    // 触摸的相关信息
    public float mTouchX, mTouchY;
    public float mLastTouchX, mLastTouchY;
    public float mTouchDownX, mTouchDownY;

    // 缩放相关
    public Float mLastFocusX;
    public Float mLastFocusY;
    public float mTouchCentreX, mTouchCentreY;
    public float pendingX, pendingY, pendingScale = 1;

    // 动画相关
    public ValueAnimator mScaleAnimator;
    public float mScaleAnimTransX, mScaleAnimTranY;
    public ValueAnimator mTranslateAnimator;
    public float mTransAnimOldY, mTransAnimY;

    public float mStartX, mStartY;

    public Path mCurrPath; // 当前手写的路径
    public T mMapView;

    public BaseMapViewGestureListener(T mapView) {
        mMapView = mapView;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mTouchX = mTouchDownX = e.getX();
        mTouchY = mTouchDownY = e.getY();
        return true;
    }

    @Override
    public void onScrollBegin(MotionEvent e) {
        super.onScrollBegin(e);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    public void onScrollEnd(MotionEvent e) {
        super.onScrollEnd(e);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return super.onSingleTapUp(e);
    }

    @Override
    public boolean onScale(ScaleGestureDetectorApi27 detector) {
        // 屏幕上的焦点
        mTouchCentreX = detector.getFocusX();
        mTouchCentreY = detector.getFocusY();

        if (mLastFocusX != null && mLastFocusY != null) { // 焦点改变
            final float dx = mTouchCentreX - mLastFocusX;
            final float dy = mTouchCentreY - mLastFocusY;
            // 移动图片
            if (Math.abs(dx) > 1 || Math.abs(dy) > 1) {
                mMapView.setMapTranslationX(mMapView.getMapTranslationX() + dx + pendingX);
                mMapView.setMapTranslationY(mMapView.getMapTranslationY() + dy + pendingY);
                pendingX = pendingY = 0;
            } else {
                pendingX += dx;
                pendingY += dy;
            }
        }

        if (Math.abs(1 - detector.getScaleFactor()) > 0.005f) {
            // 缩放图片
            float scale = mMapView.getMapScale() * detector.getScaleFactor() * pendingScale;
            mMapView.setMapScale(scale, mMapView.toX(mTouchCentreX), mMapView.toY(mTouchCentreY));
            pendingScale = 1;
        } else {
            pendingScale *= detector.getScaleFactor();
        }

        mLastFocusX = mTouchCentreX;
        mLastFocusY = mTouchCentreY;
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetectorApi27 detector) {
        mLastFocusX = null;
        mLastFocusY = null;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetectorApi27 detector) {
        //limitBound(true);
        center();
    }

    public void center() {
        if (mMapView.getMapScale() < 1) {
            if (mScaleAnimator == null) {
                mScaleAnimator = new ValueAnimator();
                mScaleAnimator.setDuration(100);
                mScaleAnimator.addUpdateListener(animation -> {
                    float value = (float) animation.getAnimatedValue();
                    float fraction = animation.getAnimatedFraction();
                    mMapView.setMapScale(value, mMapView.toX(mTouchCentreX), mMapView.toY(mTouchCentreY));
                    mMapView.setMapTranslation(mScaleAnimTransX * (1 - fraction), mScaleAnimTranY * (1 - fraction));
                });
            }
            mScaleAnimator.cancel();
            mScaleAnimTransX = mMapView.getMapTranslationX();
            mScaleAnimTranY = mMapView.getMapTranslationY();
            mScaleAnimator.setFloatValues(mMapView.getMapScale(), 1);
            mScaleAnimator.start();
        } else {
            limitBound(true);
        }
    }

    /**
     * 限定边界
     *
     * @param anim 动画效果
     */
    public void limitBound(boolean anim) {
        final float oldX = mMapView.getMapTranslationX(), oldY = mMapView.getMapTranslationY();
        RectF bound = mMapView.getMapBound();
        float x = mMapView.getMapTranslationX(), y = mMapView.getMapTranslationY();
        float width = mMapView.getCenterWidth(), height = mMapView.getCenterHeight();

        // 上下都在屏幕内
        if (bound.height() <= mMapView.getHeight()) {
            y = (height - height * mMapView.getMapScale()) / 2;
        } else {
            float heightDiffTop = bound.top;
            // 只有上在屏幕内
            if (bound.top > 0 && bound.bottom >= mMapView.getHeight()) {
                y = y - heightDiffTop;
            } else if (bound.bottom < mMapView.getHeight() && bound.top <= 0) { // 只有下在屏幕内
                float heightDiffBottom = mMapView.getHeight() - bound.bottom;
                y = y + heightDiffBottom;
            }
        }

        // 左右都在屏幕内
        if (bound.width() <= mMapView.getWidth()) {
            x = (width - width * mMapView.getMapScale()) / 2;
        } else {
            float widthDiffLeft = bound.left;
            // 只有左在屏幕内
            if (bound.left > 0 && bound.right >= mMapView.getWidth()) {
                x = x - widthDiffLeft;
            } else if (bound.right < mMapView.getWidth() && bound.left <= 0) { // 只有右在屏幕内
                float widthDiffRight = mMapView.getWidth() - bound.right;
                x = x + widthDiffRight;
            }
        }
        if (anim) {
            if (mTranslateAnimator == null) {
                mTranslateAnimator = new ValueAnimator();
                mTranslateAnimator.setDuration(100);
                mTranslateAnimator.addUpdateListener(animation -> {
                    float value = (float) animation.getAnimatedValue();
                    float fraction = animation.getAnimatedFraction();
                    mMapView.setMapTranslation(value, mTransAnimOldY + (mTransAnimY - mTransAnimOldY) * fraction);
                });
            }
            mTranslateAnimator.setFloatValues(oldX, x);
            mTransAnimOldY = oldY;
            mTransAnimY = y;
            mTranslateAnimator.start();
        } else {
            mMapView.setMapTranslation(x, y);
        }
    }

}
