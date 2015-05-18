package com.floor.shift.net;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.floor.shift.R;

/*
    Copyright (C) 2015 SeniorPanda
    Created by SeniorPanda on 1/15/2015.
*/

public class NetLoadingDailog {

	public Dialog mDialog;

	private Context context;

	private boolean isLDShow = false;

	public NetLoadingDailog(Context context) {
		this.context = context;
	}
	
	public boolean isShowing(){
		if(mDialog == null)
			createDialog();
		return mDialog.isShowing();
	}

	public void loading() {
		try {
			if (isLDShow) {
				hideLoadingDialog();
			}
			createDialog();
			mDialog.show();
			mDialog.setCancelable(false);
			isLDShow = true;
		} catch (Exception e) {
			if (isLDShow && mDialog != null) {
				hideLoadingDialog();
			}
		}
	}

	private void hideLoadingDialog() {
		isLDShow = false;
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}

	private void createDialog() {
		mDialog = null;
		mDialog = new Dialog(context, R.style.loading_dialog);
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_loading, null);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setCancelable(false);
		mDialog.setContentView(view);
	}

	public void dismissDialog() {
		hideLoadingDialog();
	}

    public void setCancelable(boolean flag){
        if(mDialog != null)
            mDialog.setCancelable(flag);
    }

}
