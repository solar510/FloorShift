package com.floor.shift.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.floor.shift.R;
import com.floor.shift.entity.FloorMap;

import java.util.ArrayList;

/*
    Copyright (C) 2015 SeniorPanda
    Created by SeniorPanda on 2/15/2015.
*/

public class PhotoAdapter extends ArrayAdapter<FloorMap> {

	Activity activity;
	int layoutResourceId;
	ArrayList<FloorMap> item;

	public PhotoAdapter(Activity activity, int layoutId, ArrayList<FloorMap> items) {
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

			int mScreenW = activity.getWindow().getWindowManager().getDefaultDisplay().getWidth();
//			AbsListView.LayoutParams param = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, (int)activity.getResources().getDimension(R.dimen.artist_item_min_height));
//			convertView.setLayoutParams(param);
			
			viewHolder.img_media = (ImageView)convertView.findViewById(R.id.img_media);
			viewHolder.txt_info = (TextView)convertView.findViewById(R.id.txt_info);

            viewHolder.img_media.getLayoutParams().height = mScreenW;
            viewHolder.img_media.getLayoutParams().width = mScreenW;

			convertView.setTag(viewHolder);
		}else
			viewHolder = (ViewHolder)convertView.getTag();

		return convertView;
	}
	
	class ViewHolder {
        ImageView img_media;
		TextView txt_info;
	}
}
