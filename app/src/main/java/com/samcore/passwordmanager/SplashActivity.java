package com.samcore.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.samcore.passwordmanager.components.AppSession;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    AppSession appSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        appSession=new AppSession(getApplicationContext());

        new Handler().postDelayed(() -> {
                    if (!appSession.isLoggedIN())
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    else
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                ,3000);

    }
}
