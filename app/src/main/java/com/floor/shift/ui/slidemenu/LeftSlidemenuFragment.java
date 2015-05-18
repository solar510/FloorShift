/**
 * LeftSlidemenuFragment.java 
 * FEast
 *
 * Created by @author Siddesh Bingi on Sep 6, 2013
 */
package com.floor.shift.ui.slidemenu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.floor.shift.FloorShiftApp;
import com.floor.shift.R;
import com.floor.shift.customview.PolygonImageView;
import com.floor.shift.entity.User;
import com.floor.shift.utils.CommonMethods;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class LeftSlidemenuFragment extends ListFragment {
	private final static String TAG = LeftSlidemenuFragment.class.getSimpleName();
    private View mHeaderView = null;
    ArrayList<String> mMenuItems = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = null;
		view = inflater.inflate(R.layout.fragment_leftslidemenu, container, false);
        if(!FloorShiftApp.getInstance().getSkipState())
            mHeaderView = inflater.inflate(R.layout.slidemenu_headerup, null, false);

        initView(view);
		return view;
	}

    private PolygonImageView imgProfile;
    private TextView txt_profilename;

    private void initView(View view){
        TextView txtHeader = (TextView)view.findViewById(R.id.txt_header);
        txtHeader.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "smudger_let_plain.TTF"));

        if(!FloorShiftApp.getInstance().getSkipState()) {
            txt_profilename = (TextView) mHeaderView.findViewById(R.id.txt_profilename);
            imgProfile = (PolygonImageView) mHeaderView.findViewById(R.id.img_profile);
        }
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        if(mHeaderView != null){
            if(getListView().getHeaderViewsCount() == 0)
                getListView().addHeaderView(mHeaderView);
        }

        getListView().setDivider(null);
		getListView().setBackgroundColor(Color.TRANSPARENT);
		getListView().setCacheColorHint(Color.TRANSPARENT);

		mMenuItems.add("Playlist");
		mMenuItems.add("Recording");
//		mMenuItems.add("Studio");
        mMenuItems.add("DJs");
        mMenuItems.add("Contact");
        mMenuItems.add("Media");
        mMenuItems.add("Notifications");
        mMenuItems.add("About");
        mMenuItems.add("Help");
        mMenuItems.add("Log Out");

		for (String item : mMenuItems)
			getMenuListAdapter().add(item);
	}

	private MenuListAdapter getMenuListAdapter() {
		if (getListAdapter() == null) {
			setListAdapter(new MenuListAdapter(getActivity()));
		}

		return (MenuListAdapter) getListAdapter();
	}

	public class MenuListAdapter extends ArrayAdapter<String> {

		public MenuListAdapter(Context context) {
			super(context, 0);
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_leftmenu, null);
			}

			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position));

            return convertView;
		}
	}

    @Override
    public void onResume() {
        super.onResume();
        if(!FloorShiftApp.getInstance().getSkipState())
            updateProfile();
    }

    private void updateProfile(){
        User user = FloorShiftApp.getInstance().getCurrentUser();
        if(user != null){
            txt_profilename.setText(user.getNickname());
            if(user.getProfile_url() == null || user.getProfile_url().isEmpty()){
                if(user.getGender() == null || user.getGender().isEmpty())
                    imgProfile.setImageResource(R.drawable.man_pictogram);
                else{
                    if(user.getGender().equalsIgnoreCase("male"))
                        imgProfile.setImageResource(R.drawable.man_pictogram);
                    else
                        imgProfile.setImageResource(R.drawable.woman_pictogram);
                }
            }else{
                ImageLoader.getInstance().displayImage(user.getProfile_url(), imgProfile, FloorShiftApp.thumbnailOptions);
            }
        }
    }
}
