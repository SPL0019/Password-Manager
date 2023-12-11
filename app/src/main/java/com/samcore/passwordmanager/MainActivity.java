package com.samcore.passwordmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.samcore.passwordmanager.fragment.DocumentUploadFragment;
import com.samcore.passwordmanager.fragment.HomeFragment;
import com.samcore.passwordmanager.fragment.PasswordGeneratorFragment;

public class MainActivity extends AppCompatActivity  implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment=new HomeFragment();
    PasswordGeneratorFragment passwordGeneratorFragment=new PasswordGeneratorFragment();
    DocumentUploadFragment documentUploadFragment=new DocumentUploadFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView
                = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
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
    }
}
