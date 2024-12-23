package com.example.maptool;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import com.example.maptool.listener.BaseMapViewGestureListener;
import com.example.maptool.listener.BaseMapViewTouchDetector;
import com.example.maptool.listener.EditMapViewGestureListener;
import com.example.maptool.listener.IMapViewTouchDetector;
import com.example.maptool.mapitem.FenceLineItem;
import com.example.maptool.mapitem.FencePointItem;
import com.example.maptool.mapitem.MapOperation;
import com.example.maptool.mapitem.MarkMapItem;
import com.example.maptool.mapview.EditMapView;
import com.example.maptool.util.Constants;
import com.example.maptool.util.DrawUtil;
import com.example.maptool.util.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class EditMapActivity extends Activity {

    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    //relayout
    @BindView(R.id.mark_layer_layout)
    RelativeLayout mMarkLayerLayout;
    @BindView(R.id.mark_btn)
    TextView mMarkBtn;
    @BindView(R.id.mark_layer_restore)
    ImageView mMarkLayerRestore;
    @BindView(R.id.fence_layer_layout)
    RelativeLayout mFenceLayerLayout;
    @BindView(R.id.fence_btn)
    TextView mFenceBtn;
    @BindView(R.id.fence_layer_restore)
    ImageView mFenceLayerRestore;
    @BindView(R.id.edit_layer_layout)
    RelativeLayout mEditLayerLayout;
    @BindView(R.id.edit_btn)
    TextView mEditBtn;
    @BindView(R.id.edit_layer_restore)
    ImageView mEditLayerRestore;
    @BindView(R.id.mapview_container)
    FrameLayout mMapViewContainer;
    //地图编辑View
    @BindView(R.id.edit_map_tool_layout)
    LinearLayout mEditMapToolLayout;
    @BindView(R.id.edit_map_tool_move)
    TextView mEditMoveBtn;
    @BindView(R.id.edit_map_tool_brush)
    TextView mEidtBrushBtn;
    @BindView(R.id.edit_map_tool_eraser)
    TextView mEditEraseBtn;
    @BindView(R.id.edit_map_undo)
    FrameLayout mEditUndoLayout;
    @BindView(R.id.edit_map_undo_view)
    ImageView mEditUndoView;
    @BindView(R.id.edit_map_redo)
    FrameLayout mEditRedoLayout;
    @BindView(R.id.edit_map_redo_view)
    ImageView mEditRedoView;
    @BindView(R.id.edit_map_tool_brush_layout)
    RadioGroup mEditBrushLayout;
    @BindView(R.id.edit_map_brush_black)
    RadioButton mEditBrushBlackBtn;
    @BindView(R.id.edit_map_brush_white)
    RadioButton mEditBrushWhiteBtn;
    @BindView(R.id.edit_map_tool_eraser_layout)
    RadioGroup mEditEraseLayout;
    @BindView(R.id.edit_map_eraser_small)
    RadioButton mEditEraseSmall;
    @BindView(R.id.edit_map_eraser_middle)
    RadioButton mEditEraseMiddle;
    @BindView(R.id.edit_map_eraser_large)
    RadioButton mEditEraseLarge;
    @BindView(R.id.edit_map_eraser_exlarge)
    RadioButton mEditEraseExLarge;
    //地图围栏View
    @BindView(R.id.fence_map_tool_layout)
    LinearLayout mFenceMapToolLayout;
    @BindView(R.id.fence_map_tool_move)
    TextView mFenceMoveBtn;
    @BindView(R.id.fence_map_tool_operation)
    TextView mFenceOperationBtn;
    @BindView(R.id.fence_map_undo)
    FrameLayout mFenceMapUndoLayout;
    @BindView(R.id.fence_map_undo_view)
    ImageView mFenceMapUndoView;
    @BindView(R.id.fence_map_redo)
    FrameLayout mFenceMapRedoLayout;
    @BindView(R.id.fence_map_redo_view)
    ImageView mFenceMapRedoView;
    @BindView(R.id.fence_map_delete)
    FrameLayout mFenceMapDeleteBtn;
    @BindView(R.id.fence_map_tool_colors_layout)
    RadioGroup mFenceMapColorsLayout;
    @BindView(R.id.fence_map_color_red)
    RadioButton mFenceMapRedBtn;
    @BindView(R.id.fence_map_color_orange)
    RadioButton mFenceMapOgangeBtn;
    @BindView(R.id.fence_map_color_green)
    RadioButton mFenceMapGreenBtn;
    @BindView(R.id.fence_map_color_blue)
    RadioButton mFenceMapBlueBtn;
    @BindView(R.id.fence_map_color_purple)
    RadioButton mFenceMapPurpleBtn;
    @BindView(R.id.fence_map_color_grey)
    RadioButton mFenceMapGreyBtn;
    //地图标记View
    @BindView(R.id.mark_map_tool_layout)
    LinearLayout mMarkMapToolLayout;
    @BindView(R.id.mark_map_tool_move)
    TextView mMarkMoveBtn;
    @BindView(R.id.mark_map_tool_operation)
    TextView mMarkOperationBtn;
    @BindView(R.id.mark_map_undo)
    FrameLayout mMarkMapUndoLayout;
    @BindView(R.id.mark_map_undo_view)
    ImageView mMarkMapUndoView;
    @BindView(R.id.mark_map_redo)
    FrameLayout mMarkMapRedoLayout;
    @BindView(R.id.mark_map_redo_view)
    ImageView mMarkMapRedoView;
    @BindView(R.id.mark_map_tool_colors_layout)
    RadioGroup mMarkMapColorsLayout;
    @BindView(R.id.mark_map_color_red)
    RadioButton mMarkMapRedBtn;
    @BindView(R.id.mark_map_color_orange)
    RadioButton mMarkMapOrangeBtn;
    @BindView(R.id.mark_map_color_green)
    RadioButton mMarkMapGreenBtn;
    @BindView(R.id.mark_map_color_blue)
    RadioButton mMarkMapBlueBtn;
    @BindView(R.id.mark_map_color_purple)
    RadioButton mMarkMapPurpleBtn;
    @BindView(R.id.mark_map_color_grey)
    RadioButton mMarkMapGreyBtn;

    EditText mPointTypeEx;
    EditText mPointX;
    EditText mPointY;
    EditText mPointAngle;

    private final String TAG = "EditMapActivity";
    private EditMapView mMapView;
    private BaseMapViewGestureListener mTouchGestureListener;
    private EditMapViewGestureListener.ISelectionListener mSelectionListener;
    private PopupWindow mPopupWindow;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;

    private String mFileName = "";
    private String mMapName = "";
    private String mMapDir = "";
    private String mGridDir = "";
    private String mPoiDir = "";
    private String mForbiddenDir = "";
    private String mMapRootDir = Constants.STROAGE_DIR + Constants.MAP_ROOT_DIR;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_map);

        ButterKnife.bind(EditMapActivity.this);
        mMapRootDir = getApplication().getExternalFilesDir(null).getAbsolutePath();

        mMapDir = getIntent().getStringExtra("map_path");
        mMapName = getIntent().getStringExtra("map_name");
        loadMapView(mMapDir, mMapName);
        initToolBar();

    }

    private void initToolBar() {
        mToolBar.inflateMenu(R.menu.title_edit_map);

        if (!TextUtils.isEmpty(mMapName)) {
            mToolBar.setTitle(mMapName);
        }

        mToolBar.setNavigationOnClickListener(v -> finish());
        mToolBar.setOnMenuItemClickListener((menuItem) -> {
            int id = menuItem.getItemId();
            switch (id) {
                case R.id.menu_finish:
                case R.id.menu_home:
                    ////切换到home
                    finishEditMap();
                    break;
                default:
                    break;
            }
            return false;
        });
    }


    /**
     * load map and mapview
     */
    private void loadMapView(String mapPath, String mapName)
    {
        Log.d(TAG, "load map mapPath:" + mapPath);
        Bitmap bitmap;

        if (mapPath != null && mapName != null) {
            Log.d(TAG, "load map exist map path: " + mapPath);
            mFileName = mapPath + mapName;
            Log.d(TAG, "mfilename=" + mFileName);
            bitmap = BitmapFactory.decodeFile(mapPath).copy(Bitmap.Config.RGB_565, true);
            mMapView = new EditMapView(this, bitmap);
            parsePoiData(mapPath);
            parseForbiddenData(mapPath);
            mMapView.setLocationX(bitmap.getWidth() / 2);
            mMapView.setLocationY(bitmap.getHeight() / 2);
            mMapView.refresh();
        }
        else
        {
            ///load bitmap
            Log.d(TAG, "load map map_test");

            mMapName = "map_test";
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map_thumb_demo);
            Log.d(TAG, "bitmap "+ bitmap);
            mMapView = new EditMapView(EditMapActivity.this, bitmap);
        }

        mMapView.setOperation(MapOperation.EDIT_MAP_ERASE);
        mMapViewContainer.addView(mMapView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSelectionListener = new EditMapViewGestureListener.ISelectionListener(){
            @Override
            public void onSelectedMarkItem(MarkMapItem item) {
                editMark(item);
            }

            @Override
            public void onSelectedFenceItem(FenceLineItem item) {
                deleteFence(item);
                mMapView.refresh();
            }
        };
        mTouchGestureListener = new EditMapViewGestureListener(mMapView, mSelectionListener);
        ((EditMapViewGestureListener) mTouchGestureListener).setSingleTapListener(event -> {
            Log.d(TAG, "event X: " + event.getX() + ",Y:" + event.getY());
            touchMapEvent(event.getX(), event.getY());
        });
        IMapViewTouchDetector detector = new BaseMapViewTouchDetector(this, mTouchGestureListener);
        mMapView.setDefaultTouchDetector(detector);
        onMarkClick();
    }

    private void touchMapEvent(float mTouchX, float mTouchY) {
        MapOperation mapOperation = mMapView.getOperation();
        switch (mapOperation) {
            case FENCE_MAP_MOVE:
                List<FenceLineItem> drawItems = mMapView.getFenceLineGroup();
                List<FenceLineItem> storagedItems = mMapView.getStoragedFenceItem();
                List<FenceLineItem> items = new ArrayList<>();
                items.addAll(drawItems);
                items.addAll(storagedItems);
                for (int i = 0; i < items.size(); i++) {
                    FenceLineItem item = items.get(i);

                    PointF start = item.getSxy();
                    PointF end = item.getDxy();
                    PointF point = new PointF(mMapView.toX(mTouchX), mMapView.toY(mTouchY));

                    boolean selected = item.itemClicked(start, end, point);
                    if (mSelectionListener != null && selected) {
                        mSelectionListener.onSelectedFenceItem(item);
                        break;
                    }
                }
                break;
            case FENCE_MAP:
                FencePointItem fencePointItem = new FencePointItem(mMapView.getContext(), mMapView,
                        new PointF(mMapView.toX(mTouchX), mMapView.toY(mTouchY)),
                        mMapView.getColor(), mMapView.getSize());

                List<FenceLineItem> fenceLineItems = mMapView.getFenceLineItem();
                FencePointItem startPointItem;
                if (fenceLineItems.size() == 0) {
                    if (mMapView.getFencePointItem().size() == 1) {
                        startPointItem = (FencePointItem) mMapView.getFencePointItem().get(0);

                        FenceLineItem lineItem = new FenceLineItem(mMapView.getContext(), startPointItem.getPoint(), fencePointItem.getPoint(),
                                mMapView.getColor(), mMapView, startPointItem, fencePointItem);
                        lineItem.updateLine(new Path());

                        mMapView.addFenceLineItem(lineItem);
                    }
                } else {
                    FenceLineItem lastLineItem = (FenceLineItem) mMapView.getFenceLineItem()
                            .get(mMapView.getFenceLineItem().size() - 1);
                    startPointItem = lastLineItem.getDpoint();

                    FenceLineItem lineItem = new FenceLineItem(mMapView.getContext(), startPointItem.getPoint(), fencePointItem.getPoint(),
                            mMapView.getColor(), mMapView, startPointItem, fencePointItem);
                    lineItem.updateLine(new Path());

                    mMapView.addFenceLineItem(lineItem);
                }

                if (mMapView.getFencePointItem().size() == 0) {
                    mMapView.addFencePointItem(fencePointItem);
                }
                break;
            case MARK_MAP_MOVE:
                break;
            case MARK_MAP:
                List<MarkMapItem> stackItems = mMapView.getMarkItem();
                List<MarkMapItem> storageItems = mMapView.getStoragedMarkItem();
                List<MarkMapItem> markItems = new ArrayList<>();
                markItems.addAll(stackItems);
                markItems.addAll(storageItems);
                PointF markPoint = new PointF(mMapView.toX(mTouchX), mMapView.toY(mTouchY));

                for (int i = 0; i < markItems.size(); i++) {
                    MarkMapItem item = markItems.get(i);
                    if (item instanceof MarkMapItem) {
                        boolean selected = item.itemClicked(null, null, markPoint);
                        if (mSelectionListener != null && selected) {
                            mSelectionListener.onSelectedMarkItem(item);
                            break;
                        }
                    }
                }
                addMark(markPoint);
                break;

            default:
                break;
        }
    }


    @OnClick(R.id.edit_layer_layout)
    void onEditClick() {
        mMarkMapToolLayout.setVisibility(View.GONE);
        mFenceMapToolLayout.setVisibility(View.GONE);
        mEditMapToolLayout.setVisibility(View.VISIBLE);

        mMarkLayerLayout.setSelected(false);
        mFenceLayerLayout.setSelected(false);
        mEditLayerLayout.setSelected(true);
        mEditBtn.setTextColor(getResources().getColor(R.color.black));
        mMarkBtn.setTextColor(getResources().getColor(R.color.light_black));
        mFenceBtn.setTextColor(getResources().getColor(R.color.light_black));

        mMarkLayerRestore.setVisibility(View.GONE);
        mFenceLayerRestore.setVisibility(View.GONE);
        mEditLayerRestore.setVisibility(View.VISIBLE);

        onEditBrushClick();
    }

    @OnClick(R.id.edit_layer_restore)
    void onEditRestoreClick() {
        mMapView.restoreEditLayer();
    }

    @OnClick(R.id.mark_layer_layout)
    void onMarkClick() {
        mMarkMapToolLayout.setVisibility(View.VISIBLE);
        mFenceMapToolLayout.setVisibility(View.GONE);
        mEditMapToolLayout.setVisibility(View.GONE);

        mMarkLayerLayout.setSelected(true);
        mFenceLayerLayout.setSelected(false);
        mEditLayerLayout.setSelected(false);
        mEditBtn.setTextColor(getResources().getColor(R.color.light_black));
        mMarkBtn.setTextColor(getResources().getColor(R.color.black));
        mFenceBtn.setTextColor(getResources().getColor(R.color.light_black));

        mMarkLayerRestore.setVisibility(View.VISIBLE);
        mFenceLayerRestore.setVisibility(View.GONE);
        mEditLayerRestore.setVisibility(View.GONE);

        onMarkOperationClick();
    }

    @OnClick(R.id.mark_layer_restore)
    void onMarkRestoreClick() {
        mMapView.restoreMarkLayer();
    }

    @OnClick(R.id.fence_layer_layout)
    void onFenceClick() {
        mMarkMapToolLayout.setVisibility(View.GONE);
        mFenceMapToolLayout.setVisibility(View.VISIBLE);
        mEditMapToolLayout.setVisibility(View.GONE);


        mMarkLayerLayout.setSelected(false);
        mFenceLayerLayout.setSelected(true);
        mEditLayerLayout.setSelected(false);
        mEditBtn.setTextColor(getResources().getColor(R.color.light_black));
        mMarkBtn.setTextColor(getResources().getColor(R.color.light_black));
        mFenceBtn.setTextColor(getResources().getColor(R.color.black));

        mMarkLayerRestore.setVisibility(View.GONE);
        mFenceLayerRestore.setVisibility(View.VISIBLE);
        mEditLayerRestore.setVisibility(View.GONE);
        onFenceOperationClick();
    }

    @OnClick(R.id.fence_layer_restore)
    void onFenceRestoreClick() {
        mMapView.restoreFenceLayer();
    }

    @OnClick(R.id.edit_map_tool_move)
    void onEditMoveClick() {
        mMapView.setOperation(MapOperation.EDIT_MAP_MOVE);

        mEditBrushLayout.setVisibility(View.GONE);
        mEditEraseLayout.setVisibility(View.GONE);

        mEditMoveBtn.setSelected(true);
        mEidtBrushBtn.setSelected(false);
        mEditEraseBtn.setSelected(false);
    }

    @OnClick(R.id.edit_map_tool_brush)
    void onEditBrushClick() {
        mMapView.setOperation(MapOperation.EDIT_MAP_BRUSH);

        mEditBrushWhiteBtn.setChecked(true);
        mMapView.setColor(Color.WHITE);
        mEditBrushLayout.setVisibility(View.VISIBLE);
        mEditEraseLayout.setVisibility(View.GONE);

        mEditMoveBtn.setSelected(false);
        mEidtBrushBtn.setSelected(true);
        mEditEraseBtn.setSelected(false);
    }

    @OnClick(R.id.edit_map_tool_eraser)
    void onEditEraseClick() {
        mMapView.setOperation(MapOperation.EDIT_MAP_ERASE);
        mMapView.setColor(R.color.map_background_color);
        mMapView.setSize(4);

        mEditEraseSmall.setChecked(true);
        mEditBrushLayout.setVisibility(View.GONE);
        mEditEraseLayout.setVisibility(View.VISIBLE);

        mEditMoveBtn.setSelected(false);
        mEidtBrushBtn.setSelected(false);
        mEditEraseBtn.setSelected(true);
    }

    @OnClick(R.id.edit_map_undo)
    void onEditUndoClick() {
        mMapView.undoEditItem();
    }

    @OnClick(R.id.edit_map_redo)
    void onEditRedoClick() {
        mMapView.redoEditItem();
    }

    @OnCheckedChanged({R.id.edit_map_brush_black, R.id.edit_map_brush_white})
    void onEditBrushChange(CompoundButton view, boolean selected){
        switch (view.getId()) {
            case R.id.edit_map_brush_black:
                if (selected) {
                    mMapView.setColor(Color.BLACK);
                    mMapView.setSize(1);
                }
                break;
            case R.id.edit_map_brush_white:
                if (selected) {
                    mMapView.setColor(Color.WHITE);
                    mMapView.setSize(1);
                }
                break;
        }
    }

    @OnCheckedChanged({R.id.edit_map_eraser_small, R.id.edit_map_eraser_middle,
            R.id.edit_map_eraser_large, R.id.edit_map_eraser_exlarge})
    void onEditEraseChange(CompoundButton view, boolean selected){
        switch (view.getId()) {
            case R.id.edit_map_eraser_small:
                if (selected) {
                    mMapView.setColor(getResources().getColor(R.color.map_background_color));
                    mMapView.setSize(4);
                }
                break;
            case R.id.edit_map_eraser_middle:
                if (selected) {
                    mMapView.setColor(getResources().getColor(R.color.map_background_color));
                    mMapView.setSize(8);
                }
                break;
            case R.id.edit_map_eraser_large:
                if (selected) {
                    mMapView.setColor(getResources().getColor(R.color.map_background_color));
                    mMapView.setSize(12);
                }
                break;
            case R.id.edit_map_eraser_exlarge:
                if (selected) {
                    mMapView.setColor(getResources().getColor(R.color.map_background_color));
                    mMapView.setSize(20);
                }
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.fence_map_tool_move)
    void onFenceMoveClick() {
        mMapView.setOperation(MapOperation.FENCE_MAP_MOVE);
        mFenceMapColorsLayout.setVisibility(View.GONE);
        mFenceMoveBtn.setSelected(true);
        mFenceOperationBtn.setSelected(false);
    }

    @OnClick(R.id.fence_map_tool_operation)
    void onFenceOperationClick() {
        mMapView.setOperation(MapOperation.FENCE_MAP);
        mFenceMapColorsLayout.setVisibility(View.VISIBLE);
        mFenceOperationBtn.setSelected(true);
        mFenceMoveBtn.setSelected(false);
        mFenceMapRedBtn.setChecked(true);
        mMapView.setColor(getResources().getColor(R.color.red));
    }

    @OnClick(R.id.fence_map_undo)
    void onFenceUndoClick() {
        mMapView.undoFenceItem();
    }

    @OnClick(R.id.fence_map_redo)
    void onFenceRedoClick() {
        mMapView.redoFenceItem();
    }

    @OnClick(R.id.fence_map_delete)
    void onFenceNewGroup() {
        mMapView.createNewFenceGroup();
    }

    @OnCheckedChanged({R.id.fence_map_color_red, R.id.fence_map_color_orange, R.id.fence_map_color_green,
            R.id.fence_map_color_blue, R.id.fence_map_color_purple, R.id.fence_map_color_grey})
    void onFenceColorChange(CompoundButton view, boolean selected){
        switch (view.getId()) {
            case R.id.fence_map_color_red:
                if (selected) {
                    mMapView.setColor(getResources().getColor(R.color.red));
                }
                break;
            case R.id.fence_map_color_orange:
                if (selected) {
                    mMapView.setColor(getResources().getColor(R.color.orange));
                }
                break;
            case R.id.fence_map_color_green:
                if (selected) {
                    mMapView.setColor(getResources().getColor(R.color.green));
                }
                break;
            case R.id.fence_map_color_blue:
                if (selected) {
                    mMapView.setColor(getResources().getColor(R.color.blue));
                }
                break;
            case R.id.fence_map_color_purple:
                if (selected) {
                    mMapView.setColor(getResources().getColor(R.color.purple));
                }
                break;
            case R.id.fence_map_color_grey:
                if (selected) {
                    mMapView.setColor(getResources().getColor(R.color.grey));
                }
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.mark_map_tool_move)
    void onMarkMoveClick() {
        mMapView.setOperation(MapOperation.MARK_MAP_MOVE);
        mMarkMapColorsLayout.setVisibility(View.GONE);
        mMarkMoveBtn.setSelected(true);
        mMarkOperationBtn.setSelected(false);
    }

    @OnClick(R.id.mark_map_tool_operation)
    void onMarkOperationClick() {
        mMapView.setOperation(MapOperation.MARK_MAP);
        mMarkMapColorsLayout.setVisibility(View.VISIBLE);
        mMarkMoveBtn.setSelected(false);
        mMarkOperationBtn.setSelected(true);
        mMarkMapColorsLayout.clearCheck();
    }

    @OnClick(R.id.mark_map_undo)
    void onMarkUndoClick() {
        mMapView.undoMarkItem();
    }

    @OnClick(R.id.mark_map_redo)
    void onMarkRedoClick() {
        mMapView.redoMarkItem();
    }

    @OnCheckedChanged({R.id.mark_map_color_red, R.id.mark_map_color_orange, R.id.mark_map_color_green,
            R.id.mark_map_color_blue, R.id.mark_map_color_purple, R.id.mark_map_color_grey})
    void onMarkColorChange(CompoundButton view, boolean selected){
        switch (view.getId()) {
            case R.id.mark_map_color_red:
                if (selected) {
                    mMapView.setColor(getResources().getColor(R.color.red));
                    Toast.makeText(EditMapActivity.this, getString(R.string.add_mark_point_toast),
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.mark_map_color_orange:
                if (selected) {
                    mMapView.setColor(getResources().getColor(R.color.orange));
                    Toast.makeText(EditMapActivity.this, getString(R.string.add_mark_point_toast),
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.mark_map_color_green:
                if (selected) {
                    mMapView.setColor(getResources().getColor(R.color.green));
                    Toast.makeText(EditMapActivity.this, getString(R.string.add_mark_point_toast),
                            Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.mark_map_color_blue:
                if (selected) {
                    mMapView.setColor(getResources().getColor(R.color.blue));
                    Toast.makeText(EditMapActivity.this, getString(R.string.add_mark_point_toast),
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.mark_map_color_purple:
                if (selected) {
                    mMapView.setColor(getResources().getColor(R.color.purple));
                    Toast.makeText(EditMapActivity.this, getString(R.string.add_mark_point_toast),
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.mark_map_color_grey:
                if (selected) {
                    mMapView.setColor(getResources().getColor(R.color.grey));
                    Toast.makeText(EditMapActivity.this, getString(R.string.add_mark_point_toast),
                            Toast.LENGTH_LONG).show();
                }

                break;
            default:
                break;
        }
    }

    private void addMark(PointF pointF) {
        Log.d(TAG, "add mark x:" + pointF.x + ";y:" + pointF.y);
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();

        ViewGroup container = (ViewGroup) View.inflate(this, R.layout.dialog_add_mark, null);
        //        container.setOnClickListener(view -> dialog.dismiss());;
        dialog.setContentView(container);

        mPointTypeEx = container.findViewById(R.id.point_type);
        mPointX = container.findViewById(R.id.point_x);
        mPointY = container.findViewById(R.id.point_y);
        mPointAngle = container.findViewById(R.id.point_angle);

        mPointX.setText(Integer.toString(Math.round(pointF.x)));
        mPointY.setText(Integer.toString(Math.round(pointF.y)));
        mPointAngle.setText("0");

        final EditText pointName = container.findViewById(R.id.point_name);
        final TextView titleText = container.findViewById(R.id.simple_dialog_ex_title);
        final TextView cancelBtn = container.findViewById(R.id.simple_dialog_ex_cancel);
        final TextView confirmBtn = container.findViewById(R.id.simple_dialog_ex_confirm);
        final ImageView pointTypeBtn = container.findViewById(R.id.iv_point_type);

        titleText.setText(getText(R.string.add_mark_title));

        cancelBtn.setOnClickListener(view -> dialog.dismiss());

        confirmBtn.setOnClickListener(view -> {
            if (TextUtils.isEmpty(pointName.getText())) {
                pointName.setText("Point_"+ FileUtils.randomString(4));
            }
            Log.d(TAG, "confirm add mark : " + pointName.getText().toString());

            //Point(x, y, angle)
            String text_x = mPointX.getText().toString();
            String text_y = mPointY.getText().toString();
            String text_angle = mPointAngle.getText().toString();
            final int point_x = TextUtils.isEmpty(text_x) ? 0 :Integer.parseInt(text_x);
            final int point_y = TextUtils.isEmpty(text_y) ? 0 :Integer.parseInt(text_y);
            final int point_angle = TextUtils.isEmpty(text_angle) ? 0 :Integer.parseInt(text_angle);


            mMapView.addMarkItem(
                    new MarkMapItem(mMapView, new PointF(point_x, point_y), pointName.getText().toString(), point_angle,
                            mMapView.getColor(), Integer.parseInt(mPointTypeEx.getText().toString())));
            mMapView.refresh();
            dialog.dismiss();
        });

        pointTypeBtn.setOnClickListener(view -> {
            selectActivationType();
        });
    }

    protected void selectActivationType() {
        String[] activationPointTypeArray = getApplicationContext().getResources().getStringArray(
                R.array.arr_activation_point_type); //array.arr_activation_point_type

        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(EditMapActivity.this);
            mListView = new ListView(EditMapActivity.this);
            mAdapter = new ArrayAdapter<String>(EditMapActivity.this,
                    R.layout.item_simple_select_view, R.id.tv_select_item, activationPointTypeArray);
            //该方法虽然过时，但是可以避免旧设备外围不可点击的问题
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setWidth(mPointTypeEx.getWidth());
            mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPointTypeEx.setText(Integer.toString(position));
                mPopupWindow.dismiss();
                mPopupWindow = null;
            }
        });
        mListView.setAdapter(mAdapter);
        mPopupWindow.setContentView(mListView);
        mPopupWindow.showAsDropDown(mPointTypeEx, 0, 10);
    }

    private void deleteFence(FenceLineItem item) {
        List<FenceLineItem> items = mMapView.getFenceLineGroup();
        for (FenceLineItem fenceLineItem : items) {
            if (item != fenceLineItem) {
                fenceLineItem.setColor(DrawUtil.lightenColor((int)fenceLineItem.getColor()));
            }
        }
        List<FenceLineItem> storagedItems = mMapView.getStoragedFenceItem();
        for (FenceLineItem storagedItem : storagedItems) {
            if (storagedItem != item) {
                storagedItem.setColor(DrawUtil.lightenColor((int)storagedItem.getColor()));
            }
        }


        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();

        ViewGroup container = (ViewGroup) View.inflate(this, R.layout.dialog_simple, null);
        dialog.setContentView(container);

        final TextView titleText = container.findViewById(R.id.simple_dialog_title);
        final TextView cancelBtn = container.findViewById(R.id.simple_dialog_cancel);
        final TextView confirmBtn = container.findViewById(R.id.simple_dialog_confirm);

        titleText.setText(getText(R.string.delete_fence_title));

        cancelBtn.setOnClickListener(view -> {
            restoreLineColor(item);
            dialog.dismiss();
        });

        confirmBtn.setOnClickListener(view -> {
            mMapView.removeFenceLineGroup(item);
            restoreLineColor(item);
            dialog.dismiss();
        });

        dialog.setOnDismissListener(dialog1 -> restoreLineColor(item));
    }

    private void restoreLineColor(FenceLineItem item) {
        List<FenceLineItem> items = mMapView.getFenceLineGroup();
        for (FenceLineItem fenceLineItem : items) {
            if (item != fenceLineItem) {
                fenceLineItem.setColor(DrawUtil.restoreColor((int)fenceLineItem.getColor()));
            }
        }
        List<FenceLineItem> storagedItems = mMapView.getStoragedFenceItem();
        for (FenceLineItem storagedItem : storagedItems) {
            if (storagedItem != item) {
                storagedItem.setColor(DrawUtil.restoreColor((int)storagedItem.getColor()));
            }
        }
        mMapView.refresh();
    }

    private void editMark(MarkMapItem item) {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();

        ViewGroup container = (ViewGroup) View.inflate(this, R.layout.dialog_edittext, null);
        //        container.setOnClickListener(view -> dialog.dismiss());;
        dialog.setContentView(container);

        final EditText textView = container.findViewById(R.id.dialog_edittext);
        final TextView titleText = container.findViewById(R.id.dialog_ex_title);
        final TextView cancelBtn = container.findViewById(R.id.dialog_ex_cancel);
        final TextView confirmBtn = container.findViewById(R.id.dialog_ex_confirm);
        final TextView deleteBtn = container.findViewById(R.id.dialog_ex_action);

        textView.setText(item.getName());
        titleText.setText(getText(R.string.edit_mark_title));

        cancelBtn.setOnClickListener(view -> dialog.dismiss());

        confirmBtn.setOnClickListener(view -> {
            //Toast.makeText(this, textView.getText(), Toast.LENGTH_SHORT).show();
            item.updateName(textView.getText().toString());
            dialog.dismiss();
            mMapView.refresh();
        });

        deleteBtn.setOnClickListener(view -> {
            dialog.dismiss();
            deleteMark(item);
        });
    }

    private void deleteMark(MarkMapItem item) {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();

        ViewGroup container = (ViewGroup) View.inflate(this, R.layout.dialog_simple, null);
//        container.setOnClickListener(view -> dialog.dismiss());
        dialog.setContentView(container);

        final TextView titleText = container.findViewById(R.id.simple_dialog_title);
        final TextView cancelBtn = container.findViewById(R.id.simple_dialog_cancel);
        final TextView confirmBtn = container.findViewById(R.id.simple_dialog_confirm);

        titleText.setText(getText(R.string.delete_mark_title));

        cancelBtn.setOnClickListener(view -> dialog.dismiss());

        confirmBtn.setOnClickListener(view -> {
            mMapView.removeMarkItem(item);
            dialog.dismiss();
            mMapView.refresh();
        });
    }

    private void parsePoiData(String mapPath) {
        String poiFileName = "";
        
        poiFileName = mapPath + Constants.POI_LAYER + Constants.POI;
        Log.e(TAG, "poi path=" + poiFileName );

        String jsonStr = FileUtils.readJsonFile(poiFileName);
        if (TextUtils.isEmpty(jsonStr)) {
            Log.e(TAG, "jsonStr is null");
            return;
        }

        try {
            JSONObject poiObject = new JSONObject(jsonStr);
            Iterator<String> keys = poiObject.keys();
            while (keys.hasNext()) {
                String poiName = keys.next();
                JSONObject poiDataObject = poiObject.getJSONObject(poiName);
                float x = Float.parseFloat(poiDataObject.get("x").toString());
                float y = Float.parseFloat(poiDataObject.get("y").toString());
                float yaw = Float.parseFloat(poiDataObject.get("yaw").toString());
                int color = Color.parseColor(poiDataObject.get("color").toString());
                int type = Integer.parseInt(poiDataObject.get("type").toString());
                Log.d(TAG, "parse x=" + x +";y=" + y + ";poiname=" + poiName);
                MarkMapItem markMapItem = new MarkMapItem(mMapView, new PointF(x, y),
                        poiName, yaw, color, type);
                mMapView.addStoragedMarkItem(markMapItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parseForbiddenData(String mapPath) {
        String forbiddenFileName = "";

        forbiddenFileName = mapPath + Constants.FORBIDDENLINE_LAYER + Constants.FORBIDDEN;
        Log.d(TAG, "forbiddenFileName=" + forbiddenFileName );

        String laststr = FileUtils.readJsonFile(forbiddenFileName);

        if (TextUtils.isEmpty(laststr)) {
            Log.e(TAG, "laststr is null");
            return;
        }

        try {
            JSONObject forbiddenObject = new JSONObject(laststr);
            Iterator<String> keys = forbiddenObject.keys();
            while (keys.hasNext()) {
                String lineName = keys.next();
                JSONObject foebiddenDataObject = forbiddenObject.getJSONObject(lineName);
                float sx = Float.parseFloat(foebiddenDataObject.get("startx").toString());
                float sy = Float.parseFloat(foebiddenDataObject.get("starty").toString());
                float dx = Float.parseFloat(foebiddenDataObject.get("endx").toString());
                float dy = Float.parseFloat(foebiddenDataObject.get("endy").toString());
                int color = Color.parseColor(foebiddenDataObject.get("color").toString());

                PointF endPoint = new PointF(dx, dy);
                PointF startPoint = new PointF(sx, sy);
                FenceLineItem lineItem = new FenceLineItem(mMapView.getContext(), startPoint, endPoint, color, mMapView,
                        new FencePointItem(mMapView.getContext(), mMapView, startPoint, color, 0),
                        new FencePointItem(mMapView.getContext(), mMapView, endPoint, color, 0));
                lineItem.updateLine(new Path());

                mMapView.addStoragedLineItem(lineItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void writePoi(String filePath) {

        Log.d(TAG, "write poi path=" + filePath );
        //删除老文件
        FileUtils.deleteFile(filePath);

        JSONObject poiObject = new JSONObject();
        List<MarkMapItem> markMapItems = mMapView.getMarkItem();
        for(MarkMapItem item : markMapItems) {
            JSONArray poiArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("x", item.getLocation().x);
                jsonObject.put("y",item.getLocation().y);
                jsonObject.put("z", 0);
                jsonObject.put("pitch", 0);
                jsonObject.put("yaw", 90.8);
                jsonObject.put("roll", item.getAngle());
                jsonObject.put("type", "1");
                jsonObject.put("color", DrawUtil.parseColor(item.getColor()));
                poiArray.put(jsonObject);
                poiObject.put(item.getName(), poiArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        FileUtils.writeFile(filePath, poiObject.toString());
    }

    private void writeForbidden(String filePath) {
        Log.d(TAG, "forbidden=" + filePath);

        //删除老文件
        FileUtils.deleteFile(filePath);

        JSONObject forbiddenObject = new JSONObject();
        List<FenceLineItem> fenceLineGroup = mMapView.getFenceLineGroup();
        int mapHeight = mMapView.getBitmap().getHeight();
        int i = 0;

        for (FenceLineItem item : fenceLineGroup) {

            try {
                i = i + 1;
                JSONArray forbiddenArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("startx", item.getSxy().x);
                jsonObject.put("starty",mapHeight - item.getSxy().y);
                jsonObject.put("endx",item.getDxy().x);
                jsonObject.put("endy", mapHeight - item.getDxy().y);
                jsonObject.put("color", DrawUtil.parseColor(item.getColor()));

                forbiddenArray.put(jsonObject);
                forbiddenObject.put("line" + i, forbiddenArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        FileUtils.writeFile(filePath, forbiddenObject.toString());
    }

    private void finishEditMap() {
        Log.d(TAG, "finish EditMapAc, and save map");
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();

        ViewGroup container = (ViewGroup) View.inflate(this, R.layout.dialog_save_map, null);
        //        container.setOnClickListener(view -> dialog.dismiss());;
        dialog.setContentView(container);

        final EditText mapName = container.findViewById(R.id.dialog_map_name);

        final TextView titleText = container.findViewById(R.id.dialog_ex_title);
        final TextView cancelBtn = container.findViewById(R.id.dialog_ex_cancel);
        final TextView saveBtn = container.findViewById(R.id.dialog_ex_action);


        mapName.setText(mMapName);

        cancelBtn.setOnClickListener(view -> dialog.dismiss());

        saveBtn.setOnClickListener(view -> {
            if (TextUtils.isEmpty(mapName.getText())) {
                Toast.makeText(this,
                        getString(R.string.map_name_cannot_empty), Toast.LENGTH_SHORT).show();
                return;
            }

            mMapName = mapName.getText().toString().replaceAll(" ","");

            Log.w(TAG, "saveBtn  mMapName=" + mMapName );
            mMapDir = mMapRootDir +"/" + mMapName;
            mGridDir = mMapDir + Constants.MAP_LAYER;
            mPoiDir = mMapDir + Constants.POI_LAYER;
            mForbiddenDir = mMapDir + Constants.FORBIDDENLINE_LAYER;

            if( Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            {
                createFolders();
                writePoi(mPoiDir + Constants.POI);
                writeForbidden(mForbiddenDir + Constants.FORBIDDEN);
                saveBitmap(mGridDir + Constants.MAP_PNG_NAME);
            }

            dialog.dismiss();
           finish();
        });

    }

    private void createFolders() {
        FileUtils.createDir(mMapDir);
        FileUtils.createDir(mGridDir);
        FileUtils.createDir(mPoiDir);
        FileUtils.createDir(mForbiddenDir);
    }

    private void saveBitmap(String filePath){

        Bitmap bitmap = mMapView.getBitmap();

        FileUtils.saveBitmap(filePath, bitmap);

    }

}
