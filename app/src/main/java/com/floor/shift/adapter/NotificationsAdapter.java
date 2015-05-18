package com.floor.shift.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.floor.shift.R;
import com.floor.shift.customview.PolygonImageView;
import com.floor.shift.entity.Notification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
    Copyright (C) 2015 SeniorPanda
    Created by SeniorPanda on 2/15/2015.
*/

public class NotificationsAdapter extends ArrayAdapter<Notification> {

	Activity activity;
	int layoutResourceId;
	ArrayList<Notification> item;

	public NotificationsAdapter(Activity activity, int layoutId, ArrayList<Notification> items) {
		super(activity, layoutId, items);
		item = items;
		this.activity = activity;
		this.layoutResourceId = layoutId;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
        Notification item = getItem(position);

		final ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			LayoutInflater inflater = ((Activity) activity).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, null, false);
//			int mScreenH = activity.getWindow().getWindowManager().getDefaultDisplay().getHeight();
//			AbsListView.LayoutParams param = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, (int)activity.getResources().getDimension(R.dimen.artist_item_min_height));
//			convertView.setLayoutParams(param);
			
			viewHolder.img_artist = (PolygonImageView)convertView.findViewById(R.id.img_artist);
			viewHolder.txt_notificationName = (TextView)convertView.findViewById(R.id.txt_notificationName);
			viewHolder.txt_notiInfo = (TextView)convertView.findViewById(R.id.txt_notiInfo);
            viewHolder.txt_notification = (TextView) convertView.findViewById(R.id.txt_notification);
			convertView.setTag(viewHolder);
		}else
			viewHolder = (ViewHolder)convertView.getTag();

        if(item.getTitle() != null)
            viewHolder.txt_notificationName.setText(item.getTitle());
        if(item.getMessage() != null)
            viewHolder.txt_notiInfo.setText(item.getMessage());
        if(item.getSend_date() != null){
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd"); // 2015-03-03
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMM dd");// Jan 15
            try{

                Date date = dateFormat1.parse(item.getSend_date());
                String strDate = dateFormat2.format(date);
                strDate = String.format("(%s)", strDate);
                viewHolder.txt_notification.setText(strDate);
            }catch (ParseException e){
                e.printStackTrace();
            }
        }



		return convertView;
	}
	
	class ViewHolder {
        PolygonImageView img_artist;
		TextView txt_notificationName;
		TextView txt_notiInfo;
        TextView txt_notification;
	}
}
