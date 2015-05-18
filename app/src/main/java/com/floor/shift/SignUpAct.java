package com.floor.shift;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.internal.widget.FitWindowsLinearLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.floor.shift.entity.User;
import com.floor.shift.net.NetLoadingDailog;
import com.floor.shift.utils.AppConstants;
import com.floor.shift.utils.CommonMethods;
import com.floor.shift.utils.GPSTracker;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.floor.shift.utils.CommonMethods.TAG;


public class SignUpAct extends Activity implements View.OnClickListener{

    private NetLoadingDailog loadingDlg = null;
    private EditText editNickname, editEmail, editPasswd, editConfirm, editPhone;
    private User mUser;
    private String mErrorMessage, strPasswd;
    private CheckBox checkTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_signup);

        FloorShiftApp.getInstance().setSkipState(false);

        loadingDlg = new NetLoadingDailog(this);

        mUser = new User();

        initView();

    }

    private void initView(){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        CommonMethods.setupUI(this, getWindow().getDecorView().findViewById(android.R.id.content));

        TextView txt_title = (TextView)findViewById(R.id.txt_title);
        txt_title.setText("Register");
        txt_title.setTypeface(Typeface.createFromAsset(getAssets(), "smudger_let_plain.TTF"));
        findViewById(R.id.btn_setting).setVisibility(View.GONE);
        findViewById(R.id.btn_back).setOnClickListener(this);

        editNickname = (EditText) findViewById(R.id.edit_nickname);
        editEmail = (EditText) findViewById(R.id.edit_email);
        editPasswd = (EditText) findViewById(R.id.edit_password);
        editConfirm = (EditText) findViewById(R.id.edit_confirm);
        editPhone = (EditText) findViewById(R.id.edit_phone);

        checkTerm = (CheckBox) findViewById(R.id.check_term);
        checkTerm.setOnClickListener(this);
    }

    public void onSignUpClick(View view){
        final String strNickname = editNickname.getText().toString().trim();
        final String strEmail = editEmail.getText().toString().trim();
        strPasswd = editPasswd.getText().toString();
        String strConfirm = editConfirm.getText().toString();
        String phone = editPhone.getText().toString();

        if(!checkTerm.isChecked()) {
            CommonMethods.showMessage(this, "Please agree the terms and conditions.");
            return;
        }
        if (CommonMethods.isSpecialChar(strNickname) && strNickname.isEmpty()){
            CommonMethods.showMessage(this, "Please enter a valid user name.");
            return;
        }

        if(!CommonMethods.isValidEmail(strEmail) && strEmail.length() < 1) {
            CommonMethods.showMessage(this, "Please enter a valid user email.");
            return;
        }

        if (strPasswd.isEmpty()) {
            CommonMethods.showMessage(this, "Password is empty. Please type a password.");
            return;
        }

        if (strConfirm.isEmpty()) {
            CommonMethods.showMessage(this, "Reenter Password can't blank. Please retype password to confirm.");
            return;
        }

        if (!strPasswd.equalsIgnoreCase(strConfirm)) {
            CommonMethods.showMessage(this, "Your passwords don't match. Please try again.");
            return;
        }

        mUser.setNickname(strNickname);
        mUser.setEmail(strEmail);

        try{
            strPasswd = URLEncoder.encode(strPasswd, "UTF-8");
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e);
        }
        mUser.setPassword(strPasswd);

        double latitude = 0.0D, longitude = 0.0D;
        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
            return;
        }

        if(!loadingDlg.isShowing())
            loadingDlg.loading();
//
//        if(!CommonMethods.isValidPhoneNumberNational(phone)){
//            CommonMethods.showMessage(this, "Input Valid Phone Number", "Please enter a valid phone number!");
//        }else{
        ParseGeoPoint geoPoint = new ParseGeoPoint(latitude, longitude);
        ParseUser user = new ParseUser();
        user.setUsername(strNickname);
        user.setPassword(strPasswd);
        user.setEmail(strEmail);
        user.put("mobile", phone);
        user.put("geopoint", geoPoint);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(loadingDlg.isShowing())
                    loadingDlg.dismissDialog();

                if(e == null){
                    FloorShiftApp.getInstance().setUserEmail(strEmail);
                    FloorShiftApp.getInstance().setUserPasswd(strPasswd);
                    FloorShiftApp.getInstance().setLoginState(true);
                    FloorShiftApp.getInstance().setUserName(strNickname);

                    startActivity(new Intent(SignUpAct.this, MainAct.class));
                }else{
                    CommonMethods.showMessage(SignUpAct.this, e.getMessage());
                }

            }
        });

//        }
    }

    private class UsersSignupAsync extends AsyncTask<String, Integer, User> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!SignUpAct.this.isFinishing()) {
                if (!loadingDlg.isShowing())
                    loadingDlg.loading();
            }
        }

        @Override
        protected User doInBackground(String... params) {
            String url = params[0];
            try {
                if (!CommonMethods.isInternetAvailable(SignUpAct.this)) {
                    CommonMethods.showCloseMessage(SignUpAct.this);
                }

                DefaultHttpClient httpClient = FloorShiftApp.getInstance().getDefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("action", "signup"));
                nameValuePairs.add(new BasicNameValuePair("username", mUser.getNickname()));
                nameValuePairs.add(new BasicNameValuePair("email", mUser.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("password", mUser.getPassword()));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                InputStream is = httpEntity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                    sb.append(line + "\n");
                is.close();
                final JSONObject obj = new JSONObject(sb.toString());
                if (obj.has("success")) {
                    if (!obj.getBoolean("success")) {
                        if(obj.has("error"))
                            mErrorMessage = obj.getString("error");
                        mUser = null;
                    }else{
                        if(obj.has("userid")){
                            mUser.setId(obj.getInt("userid"));
                        }
                    }
                }
                return mUser;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(User result) {
            super.onPostExecute(result);
            try {
                if (!SignUpAct.this.isFinishing()) {
                    if (loadingDlg.isShowing())
                        loadingDlg.dismissDialog();
                }
                if (result == null) {
                    if(mErrorMessage == null)
                        CommonMethods.showCloseMessage(SignUpAct.this);
                    else
                        CommonMethods.showMessage(SignUpAct.this, mErrorMessage);
                } else {
                    FloorShiftApp.getInstance().setCurrentUser(mUser);
                    FloorShiftApp.getInstance().setUserID(mUser.getId());
                    FloorShiftApp.getInstance().setLoginState(true);
                    FloorShiftApp.getInstance().setUserEmail(result.getEmail());
                    FloorShiftApp.getInstance().setUserPasswd(strPasswd);

                    startActivity(new Intent(SignUpAct.this, MainAct.class));
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == event.KEYCODE_BACK)
            return true;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                finish();
                break;
        }
    }
}
