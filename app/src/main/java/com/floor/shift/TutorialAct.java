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


public class TutorialAct extends Activity{

    private NetLoadingDailog loadingDlg = null;
    private String mErrorMessage, strPasswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_tutorial);
        loadingDlg = new NetLoadingDailog(this);

        initView();

    }

    private void initView(){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        CommonMethods.setupUI(this, getWindow().getDecorView().findViewById(android.R.id.content));

        TextView txt_title = (TextView)findViewById(R.id.txt_title);
        txt_title.setText("Tutorial");
        txt_title.setTypeface(Typeface.createFromAsset(getAssets(), "smudger_let_plain.TTF"));
        findViewById(R.id.btn_setting).setVisibility(View.GONE);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void onLoginClick(View view){
    }

    public void onForgotPasswdClick(View view){
        startActivity(new Intent(this, ForgotPasswdAct.class));
    }
}
