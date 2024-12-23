package com.example.maptool.listener;

import com.example.maptool.mapview.EditMapView;

public class FenceMapGestureListener extends EditMapViewGestureListener {
    public FenceMapGestureListener(EditMapView mapView, ISelectionListener listener) {
        super(mapView, listener);
    }
}
