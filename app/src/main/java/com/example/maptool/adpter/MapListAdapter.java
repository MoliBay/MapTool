package com.example.maptool.adpter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maptool.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapListAdapter extends RecyclerView.Adapter<MapListAdapter.MapHolder>
        implements View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "MapListAdapter";
    private OnItemClickListener mOnItemClickListener = null;
    private OnItemLongClickListener mOnItemLongClickListener = null;
    private OnItemSelectedListener mOnItemSelectedListener = null;
    private List<MapListItem> mListItems = new ArrayList<>();
    private List<MapListItem> mSelectedItems = new ArrayList<>();
    private Map<Integer, Boolean> mSelectedStatus = new HashMap<>();
    private boolean mSelecteMode = false;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.mOnItemSelectedListener = listener;
    }

    @Override
    public void onClick(View view) {
        mOnItemClickListener.onItemClick(view);
    }

    @Override
    public boolean onLongClick(View view) {
        mOnItemLongClickListener.onItemLongClick(view);
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mOnItemSelectedListener.onItemSelected(buttonView, isChecked);
    }

    public interface OnItemClickListener {
        void onItemClick(View view);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view);
    }

    public interface OnItemSelectedListener {
        void onItemSelected(CompoundButton buttonView, boolean isChecked);
    }

    @NonNull
    @Override
    public MapHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_map, parent, false);
        return new MapHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MapHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        String path = mListItems.get(position).getPath();

        if (!TextUtils.isEmpty(path))
        {
            File file = new File(path);
            if(file.exists() && file.isFile()) {
                Bitmap bitmap = BitmapFactory.decodeFile(path).copy(Bitmap.Config.RGB_565, true);
                holder.mapThumb.setImageBitmap(bitmap);
            }
        }
        else {
            try {
                Bitmap bitmap = BitmapFactory.decodeResource(holder.boarderLayout.getContext().getResources(),
                        R.drawable.map_thumb_demo);
                holder.mapThumb.setImageBitmap(bitmap);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        holder.mapName.setText(mListItems.get(position).getMapName());
        //holder.mapAddress.setVisibility(View.GONE);
        holder.mItemLayout.setTag(position);
        holder.mItemLayout.setOnClickListener(this::onClick);
        holder.mItemLayout.setOnLongClickListener(this::onLongClick);

        MapListItem item = mListItems.get(position);
        holder.boarderLayout.setBackground(
                holder.boarderLayout.getContext().getDrawable(R.drawable.map_thumb_inactive_boarder));

        if (mSelecteMode) {
            holder.selectedBox.setVisibility(View.VISIBLE);
            holder.selectedBox.setTag(position);
            holder.selectedBox.setOnCheckedChangeListener(this::onCheckedChanged);
            holder.selectedBox.setChecked(mSelectedStatus.get(position));
        } else {
            holder.selectedBox.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }

    public void initSelectStatus() {
        for (int i = 0; i < mListItems.size(); i++) {
            mSelectedStatus.put(i, false);// 默认所有的checkbox都是没选中
        }
    }

    public void updateSelectStatus(int position, boolean checked) {
        mSelectedStatus.put(position, checked);
    }

    public List<MapListItem> getListItems() {
        return mListItems;
    }

    public void setListItems(List<MapListItem> mListItems) {
        this.mListItems = mListItems;
    }

    public List<MapListItem> getSelectedItems() {
        return mSelectedItems;
    }

    public void addSelectItem(MapListItem item) {
        mSelectedItems.add(item);
    }

    public void removeSelectItem(MapListItem item) {
        mSelectedItems.remove(item);
    }

    public void clearSelectItem() {
        mSelectedItems.clear();
    }

    public boolean isSelecteMode() {
        return mSelecteMode;
    }

    public void setSelecteMode(boolean mSelecteMode) {
        this.mSelecteMode = mSelecteMode;
    }

    static class MapHolder extends RecyclerView.ViewHolder {
        ImageView mapThumb;
        ImageView loadState;
        CheckBox selectedBox;
        TextView mapName;
        LinearLayout mItemLayout;
        FrameLayout boarderLayout;
        OnItemClickListener itemClickListener;
        OnItemLongClickListener itemLongClickListener;

        public MapHolder(@NonNull View itemView) {
            super(itemView);
            mItemLayout = itemView.findViewById(R.id.map_item_layout);
            boarderLayout= itemView.findViewById(R.id.map_thumb_boarder);
            mapThumb = itemView.findViewById(R.id.map_thumb);
            loadState = itemView.findViewById(R.id.map_thumb_state);
            selectedBox = itemView.findViewById(R.id.map_thumb_checkbox);
            mapName = itemView.findViewById(R.id.map_name);
        }
    }
 }
