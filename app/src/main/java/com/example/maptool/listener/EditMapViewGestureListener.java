package com.example.maptool.listener;

import android.graphics.Path;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.example.maptool.mapitem.BaseMapItem;
import com.example.maptool.mapitem.EditMapItem;
import com.example.maptool.mapitem.FenceLineItem;
import com.example.maptool.mapitem.FencePointItem;
import com.example.maptool.mapitem.MapOperation;
import com.example.maptool.mapitem.MarkMapItem;
import com.example.maptool.mapview.EditMapView;

import java.util.ArrayList;
import java.util.List;

public class EditMapViewGestureListener extends BaseMapViewGestureListener<EditMapView> {
    private BaseMapItem mCurrentItem;
    private EditMapItem mEditItem;

    private ISingleTapListener mSingleTapListener;
    private ISelectionListener mSelectionListener;
    public EditMapViewGestureListener(EditMapView mapView, ISelectionListener listener) {
        super(mapView);
        mMapView = mapView;
        mSelectionListener = listener;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mTouchX = mTouchDownX = e.getX();
        mTouchY = mTouchDownY = e.getY();
        return true;
    }

    @Override
    public void onScrollBegin(MotionEvent e) {
        mLastTouchX = mTouchX = e.getX();
        mLastTouchY = mTouchY = e.getY();

        switch (mMapView.getOperation()) {
            case EDIT_MAP_MOVE:
            case MARK_MAP_MOVE:
            case FENCE_MAP_MOVE:
                mStartX = mMapView.getMapTranslationX();
                mStartY = mMapView.getMapTranslationY();
                break;
            case EDIT_MAP_ERASE:
            case EDIT_MAP_BRUSH:
                mCurrPath = new Path();
                mCurrPath.moveTo(mMapView.toX(mTouchX), mMapView.toY(mTouchY));

                mEditItem = EditMapItem.toPath(mMapView, mCurrPath);
                mMapView.addEditItem(mEditItem);
                break;
            case MARK_MAP:

            case FENCE_MAP:
                break;
            default:
                break;
        }

        mMapView.addItem(mCurrentItem);
    }

    @Override
    public void onScrollEnd(MotionEvent e) {
        mLastTouchX = mTouchX;
        mLastTouchY = mTouchY;
        mTouchX = e.getX();
        mTouchY = e.getY();

        switch (mMapView.getOperation()) {
            case EDIT_MAP_MOVE:
            case MARK_MAP_MOVE:
            case FENCE_MAP_MOVE:
                limitBound(true);
                break;
            case EDIT_MAP_ERASE:
            case EDIT_MAP_BRUSH:
                break;
            case MARK_MAP:

            case FENCE_MAP:
                break;
            default:
                break;
        }
        mMapView.refresh();
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mLastTouchX = mTouchX;
        mLastTouchY = mTouchY;
        mTouchX = e2.getX();
        mTouchY = e2.getY();

        switch (mMapView.getOperation()) {
            case EDIT_MAP_MOVE:
            case MARK_MAP_MOVE:
            case FENCE_MAP_MOVE:
                mMapView.setMapTranslation(mStartX + mTouchX - mTouchDownX,
                        mStartY + mTouchY - mTouchDownY);
                break;
            case EDIT_MAP_ERASE:
            case EDIT_MAP_BRUSH:
                mCurrPath.quadTo(
                        mMapView.toX(mLastTouchX),
                        mMapView.toY(mLastTouchY),
                        mMapView.toX((mTouchX + mLastTouchX) / 2),
                        mMapView.toY((mTouchY + mLastTouchY) / 2)
                );
                mEditItem.updatePath(mCurrPath);
                break;
            case MARK_MAP:

            case FENCE_MAP:
                break;
            default:
                    break;
        }

        mMapView.refresh();

        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        mLastTouchX = mTouchX;
        mLastTouchY = mTouchY;
        mTouchX = e.getX();
        mTouchY = e.getY();

        onScrollBegin(e);
        e.offsetLocation(VALUE, VALUE);
        onScroll(e, e, VALUE, VALUE);
        onScrollEnd(e);

        singleTap(e);
        mMapView.refresh();

        return true;
    }

    public interface ISelectionListener {
        /**
         * 被选中兴趣点时回调
         *
         * @param item
         */
        void onSelectedMarkItem(MarkMapItem item);

        /**
         * 被选中围栏点时回调
         * @param item
         */
        void onSelectedFenceItem(FenceLineItem item);
    }

    private void singleTap(MotionEvent event) {
        if (mSingleTapListener != null) {
            mSingleTapListener.onSingleTapListener(event);
        }
    }

    public void setSingleTapListener(ISingleTapListener singleTapListener) {
        mSingleTapListener = singleTapListener;
    }

    public interface ISingleTapListener {
        void onSingleTapListener(MotionEvent event);
    }

}
