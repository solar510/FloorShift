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
import com.floor.shift.entity.Video;
import com.floor.shift.utils.AppConstants;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/*
    Copyright (C) 2015 SeniorPanda
    Created by SeniorPanda on 2/15/2015.
*/

public class VideoAdapter extends ArrayAdapter<Video> {

	Activity activity;
	int layoutResourceId;
	ArrayList<Video> item;

	public VideoAdapter(Activity activity, int layoutId, ArrayList<Video> items) {
		super(activity, layoutId, items);
		item = items;
		this.activity = activity;
		this.layoutResourceId = layoutId;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
        Video item = getItem(position);
        int mScreenW = activity.getWindow().getWindowManager().getDefaultDisplay().getWidth();
		final ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			LayoutInflater inflater = ((Activity) activity).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, null, false);


//			AbsListView.LayoutParams param = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, (int)activity.getResources().getDimension(R.dimen.artist_item_min_height));
//			convertView.setLayoutParams(param);
			
			viewHolder.img_media = (ImageView)convertView.findViewById(R.id.img_media);
			viewHolder.txt_info = (TextView)convertView.findViewById(R.id.txt_info);
            viewHolder.txt_name = (TextView)convertView.findViewById(R.id.txt_name);

            viewHolder.img_media.getLayoutParams().height = mScreenW;
            viewHolder.img_media.getLayoutParams().width = mScreenW;

			convertView.setTag(viewHolder);
		}else
			viewHolder = (ViewHolder)convertView.getTag();

        if(item.getName() != null)
            viewHolder.txt_name.setText(item.getName());
        if(item.getInfo() != null)
            viewHolder.txt_info.setText(item.getInfo());
        if(item.getThumb_url() != null){
            String imageUrl = String.format(AppConstants.URL_IMG_CROP, mScreenW, mScreenW, item.getThumb_url());
            ImageLoader.getInstance().displayImage(imageUrl, viewHolder.img_media, FloorShiftApp.thumbnailOptions);
        }

		return convertView;
	}
	
	class ViewHolder {
        ImageView img_media;
		TextView txt_info;
        TextView txt_name;
    }
}
