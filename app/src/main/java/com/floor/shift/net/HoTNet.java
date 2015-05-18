package com.floor.shift.net;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.floor.shift.FloorShiftApp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
    Copyright (C) 2015 SeniorPanda
    Created by SeniorPanda on 1/15/2015.
*/

public class HoTNet {
	private static final String CHARSET = "UTF-8";
	private static final String TAG = "Http";

	private static final int POST_STARTING = 0;
	private static final int POST_STOPED = 2;

	private HttpPost httpPost = null;
	private HttpGet httpGet = null;
	private DefaultHttpClient defaultHttpClient = null;
	private int state = POST_STARTING;

	public void closePost() {
		try {
			state = POST_STOPED;
			if (httpPost != null) {
				if (!httpPost.isAborted()) {
					httpPost.abort();
					if (defaultHttpClient != null) {
						defaultHttpClient.getConnectionManager().shutdown();
					}
				}
			}

			if(httpGet != null) {
				if(!httpGet.isAborted()){
					httpGet.abort();
					if(defaultHttpClient != null){
						defaultHttpClient.getConnectionManager().shutdown();
					}
				}
			}
			httpGet = null;
			httpPost = null;
			defaultHttpClient = null;
		} catch (Exception e) {
			Log.e(TAG, "closePost error");
		}
	}

	public void startPost(final Context context, final String url,
			final Map<String, String> map, final boolean IS_POST, final INetCallBack callback) {
		closePost();
		new Thread(new Runnable() {
			public void run() {
				state = POST_STARTING;
				final InputStream result = doPost(url, map, IS_POST);
				if (state == POST_STARTING) {
					state = POST_STOPED;
					((Activity) context).runOnUiThread(new Runnable() {
						public void run() {
							callback.onComplete(result, null);
						}
					});
				}
			}
		}).start();
	}

	private InputStream doPost(String url, Map<String, String> map, boolean IS_POST) {
		Log.e("do post", "url:" + url);
		try {
			HttpResponse httpResponse = null;
			defaultHttpClient = FloorShiftApp.getInstance().getDefaultHttpClient();
			if(IS_POST){
				httpPost = new HttpPost(url);
				List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
				if (map != null) {
					for (Map.Entry<String, String> entry : map.entrySet()) {
						Log.e(TAG,  entry.getKey());
						Log.e(TAG,  entry.getValue());
						BasicNameValuePair valuePair = new BasicNameValuePair(
								entry.getKey(), entry.getValue());
						nameValuePairs.add(valuePair);
					}
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				}
				
				httpResponse = defaultHttpClient.execute(httpPost);
			} else{
				httpGet = new HttpGet(url);
				httpResponse = defaultHttpClient.execute(httpGet);
			}
			
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();
			return is;
		} catch (ConnectTimeoutException e) {
			Log.e(TAG, "connection time out exception");
		} catch (ClientProtocolException e) {
			Log.e(TAG, "client protocol exception" + e.getMessage());
		} catch (Exception e) {
			Log.e(TAG, "exception" + e.getMessage());
		}
		return null;
	}

}
