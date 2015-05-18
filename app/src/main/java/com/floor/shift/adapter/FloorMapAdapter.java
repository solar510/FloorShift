package com.floor.shift.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.floor.shift.FloorShiftApp;
import com.floor.shift.R;
import com.floor.shift.entity.FloorMap;
import com.floor.shift.entity.Messages;
import com.floor.shift.utils.AppConstants;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class FloorMapAdapter extends ArrayAdapter<FloorMap> {

	Activity activity;
	int layoutResourceId;
	ArrayList<FloorMap> item;

	public FloorMapAdapter(Activity activity, int layoutId, ArrayList<FloorMap> items) {
		super(activity, layoutId, items);
		item = items;
		this.activity = activity;
		this.layoutResourceId = layoutId;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
        FloorMap item = getItem(position);

		final ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			LayoutInflater inflater = ((Activity) activity).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, null, false);
            viewHolder.img_cataglog = (ImageView) convertView.findViewById(R.id.img_catalog);
            viewHolder.txt_name = (TextView) convertView.findViewById(R.id.txt_name);
			convertView.setTag(viewHolder);
		}else
			viewHolder = (ViewHolder)convertView.getTag();

        if(item.getName() != null)
            viewHolder.txt_name.setText(item.getName());
        if(item.getImgUrl() != null){
            String imageUrl = String.format(AppConstants.URL_IMG_CROP, 250, 250, item.getImgUrl());
            ImageLoader.getInstance().displayImage(imageUrl, viewHolder.img_cataglog, FloorShiftApp.thumbnailOptions);
        }
		return convertView;
	}
	
	class ViewHolder {
		ImageView img_cataglog;
		TextView txt_name;
	}
}
