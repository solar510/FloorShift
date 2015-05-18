package com.floor.shift.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.floor.shift.FloorShiftApp;
import com.floor.shift.R;
import com.floor.shift.entity.Messages;
import com.floor.shift.utils.AppConstants;
import com.floor.shift.utils.CommonMethods;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/*
    Copyright (C) 2015 SeniorPanda
    Created by SeniorPanda on 2/15/2015.
*/

public class MessageAdapter extends ArrayAdapter<Messages> {

	Activity activity;
	int layoutResourceId;
	ArrayList<Messages> item;

	public MessageAdapter(Activity activity, int layoutId, ArrayList<Messages> items) {
		super(activity, layoutId, items);
		item = items;
		this.activity = activity;
		this.layoutResourceId = layoutId;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
        Messages item = getItem(position);

		final ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			LayoutInflater inflater = ((Activity) activity).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, null, false);
//			int mScreenH = activity.getWindow().getWindowManager().getDefaultDisplay().getHeight();
//			AbsListView.LayoutParams param = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, (int)activity.getResources().getDimension(R.dimen.artist_item_min_height));
//			convertView.setLayoutParams(param);
			viewHolder.img_map = (ImageView)convertView.findViewById(R.id.img_map);
            viewHolder.lay_img = (RelativeLayout) convertView.findViewById(R.id.lay_img);
            viewHolder.lay_times = (RelativeLayout) convertView.findViewById(R.id.lay_times);
            viewHolder.lay_txt = (LinearLayout) convertView.findViewById(R.id.lay_txt);
            viewHolder.lay_message = (LinearLayout) convertView.findViewById(R.id.lay_message);
            viewHolder.lay_public = (LinearLayout) convertView.findViewById(R.id.lay_public);
            viewHolder.img_public = (ImageView) convertView.findViewById(R.id.img_public);
			viewHolder.txt_location = (TextView)convertView.findViewById(R.id.txt_location);
            viewHolder.txt_public_location = (TextView) convertView.findViewById(R.id.txt_public_location);
            viewHolder.txt_message = (TextView)convertView.findViewById(R.id.txt_message);
            viewHolder.txt_public_message = (TextView) convertView.findViewById(R.id.txt_public_message);
            viewHolder.txt_time = (TextView)convertView.findViewById(R.id.txt_time);
			convertView.setTag(viewHolder);
		}else
			viewHolder = (ViewHolder)convertView.getTag();

        viewHolder.lay_message.setBackgroundColor(activity.getResources().getColor(android.R.color.white));
        viewHolder.lay_times.setBackgroundColor(activity.getResources().getColor(R.color.third_time));

        if(item.getTime() != null){
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
            String strDate = dateFormat.format(item.getTime());
            viewHolder.txt_time.setText(strDate);
        }

        if(item.isType()){
            viewHolder.lay_img.setVisibility(View.VISIBLE);
            viewHolder.lay_txt.setVisibility(View.VISIBLE);
            viewHolder.lay_public.setVisibility(View.GONE);
            if(item.getImg_url() != null){
                String imageUrl = String.format(AppConstants.URL_IMG_CROP, 250, 250, item.getImg_url());
                ImageLoader.getInstance().displayImage(imageUrl, viewHolder.img_map, FloorShiftApp.thumbnailOptions);
            }

            if(item.getMessage() != null){
                if(item.getTime() != null){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
                    String strDate = dateFormat.format(item.getTime());
                    strDate = String.format("Customer Assist %s", strDate);
                    viewHolder.txt_message.setText(strDate);
                }
            }
            if(item.getTo_loc() != null){
                ParseQuery<ParseObject> query = ParseQuery.getQuery("FloorMap");
                query.getInBackground(item.getTo_loc(), new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, com.parse.ParseException e) {
                        if(e == null){
                            if(parseObject.has("name")) {
                                viewHolder.txt_location.setText(String.format("@%s", parseObject.getString("name")));
                            }
                        }else{
                            CommonMethods.showMessage(activity, e.getMessage());
                        }
                    }
                });
            }

            if(item.getTime() != null){
                Date time = item.getTime();

                Calendar currentDate = Calendar.getInstance();
                long diffUpdatedMillis = System.currentTimeMillis() - time.getTime();
                currentDate.setTimeInMillis(Math.abs(diffUpdatedMillis));
                int mins = currentDate.get(Calendar.MINUTE);
                int seconds = currentDate.get(Calendar.SECOND);
                if(seconds < 30){
                    viewHolder.lay_message.setBackgroundColor(activity.getResources().getColor(R.color.second));
                    viewHolder.lay_times.setBackgroundColor(activity.getResources().getColor(R.color.second_time));
                    viewHolder.txt_time.setText(String.format(" :%s", seconds));
                }
                if(mins >= 5){
                    viewHolder.lay_message.setBackgroundColor(activity.getResources().getColor(R.color.first));
                    viewHolder.lay_times.setBackgroundColor(activity.getResources().getColor(R.color.first_time));
                    viewHolder.txt_time.setText(String.format("%s:%s", mins, seconds));
                }
            }

        }else{
            viewHolder.lay_img.setVisibility(View.INVISIBLE);
            viewHolder.lay_txt.setVisibility(View.GONE);
            viewHolder.lay_public.setVisibility(View.VISIBLE);
            if(item.getImg_url() != null){
                String imageUrl = String.format(AppConstants.URL_IMG_CROP, 250, 250, item.getImg_url());
                ImageLoader.getInstance().displayImage(imageUrl, viewHolder.img_public, FloorShiftApp.thumbnailOptions);
            }
            if(item.getMessage() != null){
                viewHolder.txt_public_location.setText(item.getMessage());
            }

            if(item.getTo_loc() != null){

                ParseQuery<ParseObject> query = ParseQuery.getQuery("FloorMap");
                query.getInBackground(item.getTo_loc(), new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, com.parse.ParseException e) {
                        if(e == null){
                            if(parseObject.has("name")) {
                                String message = String.format("Arrived @%s to help", parseObject.getString("name"));
                                viewHolder.txt_public_message.setText(message);
                            }
                        }else{
                            CommonMethods.showMessage(activity, e.getMessage());
                        }
                    }
                });
            }

        }

		return convertView;
	}
	
	class ViewHolder {
        RelativeLayout lay_img, lay_times;
        LinearLayout lay_txt, lay_public, lay_message;
		ImageView img_map, img_public;
		TextView txt_location, txt_public_location;
		TextView txt_message, txt_public_message;
        TextView txt_time;

	}
}
