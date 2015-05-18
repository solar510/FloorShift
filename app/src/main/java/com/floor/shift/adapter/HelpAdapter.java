package com.floor.shift.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.floor.shift.R;
import com.floor.shift.entity.Messages;

import java.util.ArrayList;

/*
    Copyright (C) 2015 SeniorPanda
    Created by SeniorPanda on 2/15/2015.
*/

public class HelpAdapter extends ArrayAdapter<Messages> {

	Activity activity;
	int layoutResourceId;
	ArrayList<Messages> item;

	public HelpAdapter(Activity activity, int layoutId, ArrayList<Messages> items) {
		super(activity, layoutId, items);
		item = items;
		this.activity = activity;
		this.layoutResourceId = layoutId;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
        Messages item = getItem(position);

		final ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			LayoutInflater inflater = ((Activity) activity).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, null, false);
//			int mScreenH = activity.getWindow().getWindowManager().getDefaultDisplay().getHeight();
//			AbsListView.LayoutParams param = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, (int)activity.getResources().getDimension(R.dimen.artist_item_min_height));
//			convertView.setLayoutParams(param);
			
			viewHolder.checkbox = (CheckBox)convertView.findViewById(R.id.checkbox);
			viewHolder.txt_readmore = (TextView)convertView.findViewById(R.id.txt_readmore);
			viewHolder.txt_help = (TextView)convertView.findViewById(R.id.txt_help);
			convertView.setTag(viewHolder);
		}else
			viewHolder = (ViewHolder)convertView.getTag();

		return convertView;
	}
	
	class ViewHolder {
        CheckBox checkbox;
		TextView txt_readmore;
		TextView txt_help;
	}
}
