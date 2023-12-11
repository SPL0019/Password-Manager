package com.samcore.passwordmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.samcore.passwordmanager.components.AppSession;
import com.samcore.passwordmanager.fragment.DocumentUploadFragment;
import com.samcore.passwordmanager.fragment.HomeFragment;
import com.samcore.passwordmanager.fragment.PasswordGeneratorFragment;

public class MainActivity extends AppCompatActivity  implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment=new HomeFragment();
    PasswordGeneratorFragment passwordGeneratorFragment=new PasswordGeneratorFragment();
    DocumentUploadFragment documentUploadFragment=new DocumentUploadFragment();
    AppSession appSession;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appSession=new AppSession(getApplicationContext());
        bottomNavigationView
                = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
        firebaseAuth=FirebaseAuth.getInstance();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,homeFragment).commit();
                return true;
            case R.id.password_generator:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,passwordGeneratorFragment).commit();
                return true;
            case R.id.document_upload:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,documentUploadFragment).commit();
                return true;
            case R.id.logout:
                logout();
                return true;
        }
        return false;
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Would you like to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // logout
                    appSession.setKeyIsLoggedIn(false);
                    firebaseAuth.signOut();
                    Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // user doesn't want to logout
                    dialog.dismiss();
                })
                .show();
    }
}
