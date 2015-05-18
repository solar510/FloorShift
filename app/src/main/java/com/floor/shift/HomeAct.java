package com.floor.shift;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.floor.shift.adapter.PageFragmentAdapter;
import com.floor.shift.entity.Tutorial;
import com.floor.shift.net.NetLoadingDailog;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;


public class HomeAct extends FragmentActivity{

    private NetLoadingDailog loadingDlg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_home);

        initView();
    }

    private void initView(){
        ArrayList<Tutorial> mTutorialsList = new ArrayList<Tutorial>();
        TypedArray mTutorialImgs = getResources().obtainTypedArray(R.array.tutorial_imgs);
        String[] mTutorialTxts = getResources().getStringArray(R.array.tutorial_txts);

        for(int i = 0; i < mTutorialImgs.length() ; i++){
            Tutorial item = new Tutorial();
            item.setId(mTutorialImgs.getResourceId(i, 0));
            item.setName(mTutorialTxts[i]);
            mTutorialsList.add(item);
        }
        mTutorialImgs.recycle();

        PageFragmentAdapter mAdapter = new PageFragmentAdapter(getSupportFragmentManager(), mTutorialsList);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(mAdapter);
        CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(viewPager);
    }

    public void onLoginClick(View view){
        startActivity(new Intent(this, LoginAct.class));
    }

    public void onJoinNowClick(View view){
        startActivity(new Intent(this, SignUpAct.class));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == event.KEYCODE_BACK)
            return true;
        return super.onKeyDown(keyCode, event);
    }
}
