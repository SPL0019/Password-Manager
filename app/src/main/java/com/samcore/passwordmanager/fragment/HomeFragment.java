package com.samcore.passwordmanager.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.samcore.passwordmanager.R;
import com.samcore.passwordmanager.model.PasswordModel;
import com.samcore.passwordmanager.model.PasswordTypeModel;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment  {
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference,passwordReference;
    FirebaseUser firebaseUser;
    PasswordTypeModel passwordTypeModel=new PasswordTypeModel();
    PasswordModel passwordModel=new PasswordModel();

    private static final String TAG = "Home fragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        return view;
    }





}
