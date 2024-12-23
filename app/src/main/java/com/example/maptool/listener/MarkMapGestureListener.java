package com.example.maptool.listener;


import com.example.maptool.mapview.EditMapView;

public class MarkMapGestureListener extends EditMapViewGestureListener {
    public MarkMapGestureListener(EditMapView mapView, ISelectionListener listener) {
        super(mapView, listener);
    }
}
