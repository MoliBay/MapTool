package com.example.maptool.listener;

import android.content.Context;

public class BaseMapViewTouchDetector extends TouchGestureDetector implements IMapViewTouchDetector {
    public BaseMapViewTouchDetector(Context context, IOnTouchGestureListener listener) {
        super(context, listener);
        // 下面两行绘画场景下应该设置间距为大于等于1，否则设为0双指缩放后抬起其中一个手指仍然可以移动
        this.setScaleSpanSlop(1); // 手势前识别为缩放手势的双指滑动最小距离值
        this.setScaleMinSpan(1); // 缩放过程中识别为缩放手势的双指最小距离值

        this.setIsLongpressEnabled(false);
        this.setIsScrollAfterScaled(false);
    }
}
