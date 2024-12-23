package com.example.maptool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.maptool.adpter.MapListAdapter;
import com.example.maptool.adpter.MapListItem;
import com.example.maptool.util.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.home_map_list)
    RecyclerView mMapList;

    private final String TAG = "MainActivity";
    private String mMapRootDir = Constants.STROAGE_DIR + Constants.MAP_ROOT_DIR;


    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private List<MapListItem> mMapItems = new ArrayList<>();
    private MapListAdapter mAdapter;
    private MediaScanner MediaScanner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mMapRootDir = getApplication().getExternalFilesDir(null).getAbsolutePath();

        // 检查并请求权限
        if (checkPermissions()) {
            Toast.makeText(this, getText(R.string.permission_granted), Toast.LENGTH_SHORT).show();
        } else {
            requestPermissions();
        }

        initMapList();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapItems.clear();
        startScan();
    }

    private boolean checkPermissions() {
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result!= PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                Toast.makeText(this, getText(R.string.permission_granted), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getText(R.string.permission_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void initMapList()
    {
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        mMapList.setLayoutManager(manager);
        mAdapter = new MapListAdapter();
        mMapList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(view -> {
            if (mAdapter.isSelecteMode()) {
                Log.d(TAG, "selected mode click");
                CheckBox checkBox = view.findViewById(R.id.map_thumb_checkbox);
                checkBox.setChecked(!checkBox.isChecked());
            } else {
                Intent intent = new Intent(this, EditMapActivity.class);
                intent.putExtra("map_path", mMapItems.get((int) view.getTag()).getPath());
                intent.putExtra("map_name", mMapItems.get((int) view.getTag()).getMapName());
                intent.putExtra("uuid", mMapItems.get((int) view.getTag()).getUuid());
                startActivity(intent);
            }
        });
    }

    private void startScan() {

        if (MediaScanner == null) {
            MediaScanner = new MediaScanner(this, mMapRootDir);
        }
        else {
            MediaScanner.mConnection.disconnect();
        }
        MediaScanner.startScan();

    }

    private void scanFile() {
        Thread thread = new Thread() {

            @Override
            public void run() {
                    Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon()
                            .appendPath(MediaStore.Images.Media.RELATIVE_PATH)
                            .appendPath(Constants.STROAGE_DIR + Constants.MAP_ROOT_DIR)
                            .build();
                    try {
                        if (getContentResolver() == null)
                        {
                            Cursor cursor = getContentResolver().query(
                                    uri,
                                    new String[]{MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                                            MediaStore.Files.FileColumns.TITLE,
                                            MediaStore.Files.FileColumns.MIME_TYPE,
                                            MediaStore.Files.FileColumns.DATA,
                                            MediaStore.Files.FileColumns.DATE_MODIFIED},
                                    MediaStore.Files.FileColumns.MIME_TYPE + "=?"+
                                            " AND " + MediaStore.Files.FileColumns.TITLE + "=?" ,
                                    new String[]{"image/png","map"},
                                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER
                            );
                            Log.d(TAG, "cursor.count=" + cursor.getCount());
                            //DatabaseUtils.dumpCursor(cursor);
                            if (cursor != null && cursor.moveToFirst()) {
                                do {
                                    int dataIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                                    String path = cursor.getString(dataIndex);
                                    String path1=path.replaceAll("/","").toLowerCase();
                                    String indexText = mMapRootDir.replaceAll("/","").toLowerCase();
                                    Log.d(TAG,"path1 is:"+path1+",indexText is:"+indexText);
                                    boolean need = path1.contains(indexText);
                                    if(need) {
                                        File file = new File(path);
                                        if (file.exists()) {
                                            MapListItem item = new MapListItem();
                                            item.setPath(path);
                                            String[] splits = path.split("/");
                                            String name = splits[splits.length - 3];
                                            item.setMapName(name);
                                            Log.d(TAG, "name is:" + name);
                                            mMapItems.add(item);
                                        }
                                    }
                                } while (cursor.moveToNext());
                            }
                            cursor.close();
                        }
                    } catch (RuntimeException e)
                    {
                        e.printStackTrace();
                    }
                    finally {
                        addMapTest();
                        updateUI();
                    }
                }


        };
        thread.start();
    }

    private void updateUI()
    {
        mAdapter.setListItems(mMapItems);
        mAdapter.notifyDataSetChanged();
    }
    
    /***
     * 添加一张预制图片，可用于编辑界面使用
     */
    private void addMapTest()
    {
        MapListItem item = new MapListItem();
        item.setMapName("map_test");
        mMapItems.add(item);
    }

    public class MediaScanner {
        private static final String TAG = "MediaScanner";
        private MediaScannerConnection mConnection;
        private String mFilePath;

        public MediaScanner(Context context, String filePath) {
            mFilePath = filePath;
            mConnection = new MediaScannerConnection(context, new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {
                    Log.d(TAG, "Media scanner connected.");
                    mConnection.scanFile(mFilePath, "image/png");
                }

                @Override
                public void onScanCompleted(String path, Uri uri) {
                    Log.d(TAG, "Scanned file: " + path + " -> " + (uri!= null? uri.toString() : "null"));
                    mConnection.disconnect();
                    scanFile();
                }
            });
        }

        public void startScan() {
            mConnection.connect();
        }
    }


}