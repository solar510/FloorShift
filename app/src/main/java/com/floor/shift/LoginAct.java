package com.floor.shift;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.floor.shift.entity.User;
import com.floor.shift.net.NetLoadingDailog;
import com.floor.shift.utils.AppConstants;
import com.floor.shift.utils.CommonMethods;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

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


public class LoginAct extends Activity implements View.OnClickListener{

    private NetLoadingDailog loadingDlg = null;
    private EditText editEmial, editPasswd;
    private User mUser;
    private String mErrorMessage, strPasswd, strEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);

        FloorShiftApp.getInstance().setSkipState(false);
        mUser = new User();
        loadingDlg = new NetLoadingDailog(this);

        initView();

    }

    private void initView(){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        CommonMethods.setupUI(this, getWindow().getDecorView().findViewById(android.R.id.content));

        TextView txt_title = (TextView)findViewById(R.id.txt_title);
        txt_title.setText("Login");
        txt_title.setTypeface(Typeface.createFromAsset(getAssets(), "smudger_let_plain.TTF"));
        findViewById(R.id.btn_setting).setVisibility(View.GONE);
        findViewById(R.id.btn_back).setOnClickListener(this);

        editEmial = (EditText) findViewById(R.id.edit_login);
        editPasswd = (EditText) findViewById(R.id.edit_password);

    }

    public void onLoginClick(View view){
        strEmail = editEmial.getText().toString().trim();
        strPasswd = editPasswd.getText().toString();

        if (!CommonMethods.isValidEmail(strEmail) && strEmail.length() < 1) {
            CommonMethods.showMessage(this, "Please enter a valid user email.");
            return;
        }
        if (strPasswd.length() < 1 || strPasswd.isEmpty()) {
            CommonMethods.showMessage(this, "Password is empty. Please type a password.");
            return;
        }

        mUser.setEmail(strEmail);
        try{
            strPasswd = URLEncoder.encode(strPasswd, "UTF-8");
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e);
        }
        mUser.setPassword(strPasswd);

        if(!loadingDlg.isShowing())
            loadingDlg.loading();

        ParseUser.logInInBackground(strEmail, strPasswd, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (loadingDlg.isShowing())
                    loadingDlg.dismissDialog();

                if (parseUser != null) {
                    FloorShiftApp.getInstance().setUserEmail(strEmail);
                    FloorShiftApp.getInstance().setUserPasswd(strPasswd);
                    FloorShiftApp.getInstance().setLoginState(true);

                    parseUser.setUsername(strEmail);
                    parseUser.setPassword(strPasswd);
                    parseUser.saveInBackground();

                    startActivity(new Intent(LoginAct.this, MainAct.class));
                    finish();
                } else {
                    CommonMethods.showMessage(LoginAct.this, "No such user exist, Please sign up.");
                    FloorShiftApp.getInstance().clearUserInfo();
                }
            }
        });
    }

    public void onForgotPasswdClick(View view){
        startActivity(new Intent(this, ForgotPasswdAct.class));
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
                startActivity(new Intent(this, WelcomeAct.class));
                break;
        }
    }
}
