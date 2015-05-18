package com.floor.shift;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.floor.shift.entity.Tutorial;


public class WelcomeAct extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_welcome);

    }

    public void onTutorialClick(View view){
        startActivity(new Intent(this, TutorialAct.class));
    }

    public void onGetStartedClick(View view){
        startActivity(new Intent(this, HomeAct.class));
    }

}
