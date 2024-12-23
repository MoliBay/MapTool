package com.example.maptool.util;

import android.os.Environment;

public class Constants {

    public static final String STROAGE_DIR = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES).getAbsolutePath();
    //建图工具文件夹
    public static final String MAP_ROOT_DIR = "/MapTool";
    //栅格地图层文件夹
    public static final String MAP_LAYER = "/grid_map_layer";
    //地图图片名称及格式
    public static final String MAP_PNG_NAME = "/map.png";
    //语义兴趣点文件夹
    public static final String POI_LAYER = "/poi_layer";
    //兴趣点json文件名
    public static final String POI = "/poi.json";
    //语义虚拟墙文件夹
    public static final String FORBIDDENLINE_LAYER = "/forbiddenline_layer";
    //虚拟墙json文件名
    public static final String FORBIDDEN = "/forbiddenline.json";
    //地图描述文件
    public static final String MAPINFO = "/info.json";

}
