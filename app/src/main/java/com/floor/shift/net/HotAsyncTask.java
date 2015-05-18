package com.floor.shift.net;

import android.os.AsyncTask;

/*
    Copyright (C) 2015 SeniorPanda
    Created by SeniorPanda on 2/15/2015.
*/

public class HotAsyncTask extends AsyncTask<Request, Void, Object> {
	
	private RequestTaskCompleteListener mRequestTaskCompleteListener;
    //SoapObject pSoapObject,String pAction
	
    public HotAsyncTask(RequestTaskCompleteListener pRequestTaskCompleteListener) {
		this.mRequestTaskCompleteListener = pRequestTaskCompleteListener;
	}

	@Override
	protected Object doInBackground(Request... pRequest) {
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Object pResult) {
		//super.onPostExecute(pResult);
		mRequestTaskCompleteListener.onTaskComplete(pResult);
	}
}
