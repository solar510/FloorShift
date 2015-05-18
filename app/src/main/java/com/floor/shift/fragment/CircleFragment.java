package com.floor.shift.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.floor.shift.FloorShiftApp;
import com.floor.shift.R;
import com.floor.shift.entity.Tutorial;
import com.floor.shift.utils.AppConstants;
import com.floor.shift.utils.CommonMethods;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CircleFragment extends Fragment {

	private static final String KEY_CONTENT = "CircleFragment:Content";
	private Tutorial tutorial;

	public CircleFragment() {
	}

	public static CircleFragment newInstance(Tutorial tutorial) {
		CircleFragment testfragment = new CircleFragment();
		testfragment.tutorial = tutorial;
		return testfragment;
	}

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		if (bundle != null && bundle.containsKey(KEY_CONTENT))
            tutorial = (Tutorial)bundle.getSerializable(KEY_CONTENT);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = null;
        view = inflater.inflate(R.layout.item_tutorial, container, false);

		ImageView imgView = (ImageView) view.findViewById(R.id.img_tutorial);
        int mScreenW = CommonMethods.getWindowWidth(getActivity());
        imgView.getLayoutParams().height = mScreenW;
        imgView.setImageResource(tutorial.getId());
        TextView txt = (TextView) view.findViewById(R.id.txt_tutorial);
        txt.setText(tutorial.getName());

		return view;
	}

	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putSerializable(KEY_CONTENT, tutorial);
	}
}
