package com.example.maptool.adpter;

public class MapListItem
{
    String mPath;
    String mMapName;
    long mModifiedTime;
    String mLocation;
    String mUuid;

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public String getMapName() {
        return mMapName;
    }

    public void setMapName(String mMapName) {
        this.mMapName = mMapName;
    }


    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String mLocation) {
        this.mLocation = mLocation;
    }


    public String getUuid() {
        return mUuid;
    }

    public void setUuid(String uuid) {
        this.mUuid = uuid;
    }
}
