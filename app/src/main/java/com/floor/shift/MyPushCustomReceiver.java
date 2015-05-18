package com.floor.shift;

import java.util.Iterator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.floor.shift.utils.AppConstants;
import com.floor.shift.utils.CommonMethods;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class MyPushCustomReceiver extends ParsePushBroadcastReceiver {

	private static final String TAG = "MyCustomReceiver";
    public static final String IntentAction = "com.floor.shift.UPDATE_STATUS";

	@Override
	public void onReceive(Context context, Intent intent) {
        if (intent == null){
            Log.e(TAG, "Receiver intent null");
        }else{
            String action = intent.getAction();
            if(action.equals(IntentAction)) {
                String channel = intent.getExtras().getString("com.parse.Channel");
                try {
                    JSONObject obj = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                    if (obj.has("to_loc")) {
                        String loc = obj.getString("to_loc");
                        if(FloorShiftApp.getInstance().getFloorMapId() != null){
                            if(FloorShiftApp.getInstance().getFloorMapId().equals(loc)){
                                CommonMethods.displayMessage(context, "UPDATE_MESSAGE");
                            }
                        }
                    }else{
                        CommonMethods.displayMessage(context, "UPDATE_MESSAGE");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
	}
}
