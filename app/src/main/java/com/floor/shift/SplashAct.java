package com.floor.shift;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import com.floor.shift.net.NetLoadingDailog;
import com.floor.shift.utils.CommonMethods;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import static com.floor.shift.utils.CommonMethods.TAG;

public class SplashAct extends Activity {
    private static long SLEEP_TIME = 1; // Sleep for some time
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash);


        IntentLauncher launcher = new IntentLauncher();
        launcher.start();

    }

    private class IntentLauncher extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(SLEEP_TIME * 1000);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            autoLogin();
        }
    }
    private void autoLogin() {
        ParseUser user = ParseUser.getCurrentUser();
        if(user != null){
            startActivity(new Intent(this, MainAct.class));
        }else{
            if(FloorShiftApp.getInstance().getLoginState()){
                final String strEmail = FloorShiftApp.getInstance().getUserEmail();
                final String strPasswd = FloorShiftApp.getInstance().getUserPasswd();
                ParseUser.logInInBackground(strEmail, strPasswd, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (parseUser != null) {
                            parseUser.setUsername(strEmail);
                            parseUser.setPassword(strPasswd);
                            parseUser.saveInBackground();

                            startActivity(new Intent(SplashAct.this, MainAct.class));

                        } else {
                            startActivity(new Intent(SplashAct.this, WelcomeAct.class));
                        }
                    }
                });
            }else{
                startActivity(new Intent(this, WelcomeAct.class));
            }

        }
        finish();
    }
}
