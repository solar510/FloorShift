package com.floor.shift;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.floor.shift.net.NetLoadingDailog;
import com.floor.shift.utils.AppConstants;
import com.floor.shift.utils.CommonMethods;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

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
import java.util.ArrayList;
import java.util.List;


public class ForgotPasswdAct extends Activity implements View.OnClickListener{

    private NetLoadingDailog loadingDlg = null;
    private EditText editEmail;
    private String mErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_forgotpasswd);

        loadingDlg = new NetLoadingDailog(this);

        initView();
    }

    private void initView(){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        CommonMethods.setupUI(this, getWindow().getDecorView().findViewById(android.R.id.content));

        TextView txt_title = (TextView)findViewById(R.id.txt_title);
        txt_title.setText("Forgot Password");
        txt_title.setTypeface(Typeface.createFromAsset(getAssets(), "smudger_let_plain.TTF"));
        findViewById(R.id.btn_setting).setVisibility(View.GONE);
        findViewById(R.id.btn_back).setOnClickListener(this);

        editEmail = (EditText) findViewById(R.id.edit_email);
    }

    public void onOkClick(View view){
        String strEmail = editEmail.getText().toString().trim();

        if (!CommonMethods.isValidEmail(strEmail) && strEmail.length() < 1) {
            CommonMethods.showMessage(this, "Please enter a valid user email.");
            return;
        }
        if(!loadingDlg.isShowing())
            loadingDlg.loading();

        ParseUser.requestPasswordResetInBackground(strEmail, new RequestPasswordResetCallback() {
            @Override
            public void done(ParseException e) {
                if (loadingDlg.isShowing())
                    loadingDlg.dismissDialog();
                if (e == null)
                    CommonMethods.showMessage(ForgotPasswdAct.this, "Email Address", getResources().getString(R.string.forgot_password_success));
                else {
                    CommonMethods.showMessage(ForgotPasswdAct.this, "Email Address", getResources().getString(R.string.forgot_password_no_email));
                }
            }
        });
    }

    public void onCancelClick(View view){
        finish();
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
