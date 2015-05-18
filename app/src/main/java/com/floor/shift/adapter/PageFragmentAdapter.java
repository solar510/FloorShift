package com.floor.shift.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.floor.shift.R;
import com.floor.shift.entity.Tutorial;
import com.floor.shift.fragment.CircleFragment;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.ArrayList;

public class PageFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {

    private ArrayList<Tutorial> mTutorials = new ArrayList<Tutorial>();
	public PageFragmentAdapter(FragmentManager fm, ArrayList<Tutorial> tutorials) {
		super(fm);
        mTutorials = tutorials;
	}

	@Override
	public Fragment getItem(int arg0) {
        if(mTutorials.size() > 0)
            return CircleFragment.newInstance(mTutorials.get(arg0));
        else
            return null;
	}

	@Override
	public int getCount() {
        return mTutorials.size();
	}

	@Override
	public int getIconResId(int index) {
        return R.drawable.ic_indicator_selected;
	}
}
