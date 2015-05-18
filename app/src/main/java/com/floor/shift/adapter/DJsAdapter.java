package com.floor.shift.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.floor.shift.FloorShiftApp;
import com.floor.shift.R;
import com.floor.shift.customview.PolygonImageView;
import com.floor.shift.entity.DJ;
import com.floor.shift.utils.AppConstants;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/*
    Copyright (C) 2015 SeniorPanda
    Created by SeniorPanda on 2/15/2015.
*/

public class DJsAdapter extends ArrayAdapter<DJ> {

	Activity activity;
	int layoutResourceId;
	ArrayList<DJ> item;

	public DJsAdapter(Activity activity, int layoutId, ArrayList<DJ> items) {
		super(activity, layoutId, items);
		item = items;
		this.activity = activity;
		this.layoutResourceId = layoutId;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
        DJ item = getItem(position);

		final ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			LayoutInflater inflater = ((Activity) activity).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, null, false);
//			int mScreenH = activity.getWindow().getWindowManager().getDefaultDisplay().getHeight();
//			AbsListView.LayoutParams param = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, (int)activity.getResources().getDimension(R.dimen.artist_item_min_height));
//			convertView.setLayoutParams(param);
			
			viewHolder.img_artist = (PolygonImageView)convertView.findViewById(R.id.img_artist);
			viewHolder.txt_artistname = (TextView)convertView.findViewById(R.id.txt_artistname);
			viewHolder.txt_info = (TextView)convertView.findViewById(R.id.txt_info);
			convertView.setTag(viewHolder);
		}else
			viewHolder = (ViewHolder)convertView.getTag();

        if(item.getName() != null)
            viewHolder.txt_artistname.setText(item.getName());
        if(item.getInfo() != null)
            viewHolder.txt_info.setText(item.getInfo());
        if(item.getImage_url() != null){
            String imageUrl = String.format(AppConstants.URL_IMG_CROP, 250, 250, item.getImage_url());
            ImageLoader.getInstance().displayImage(imageUrl, viewHolder.img_artist, FloorShiftApp.thumbnailOptions);
        }

		return convertView;
	}
	
	class ViewHolder {
        PolygonImageView img_artist;
		TextView txt_artistname;
		TextView txt_info;
	}
}
