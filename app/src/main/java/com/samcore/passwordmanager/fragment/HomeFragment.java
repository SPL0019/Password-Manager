package com.samcore.passwordmanager.fragment;

import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.samcore.passwordmanager.components.AppSession;
import com.samcore.passwordmanager.R;
import com.samcore.passwordmanager.model.PasswordModel;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment  {
    DatabaseReference databaseReference;

    private static final String TAG = "Home fragment";
    ImageView userImage;
    TextView userName;
    AppSession appSession;
    RecyclerView passwordRecycler;
    // Fetch data from Firebase
    List<PasswordModel> passwordList=new ArrayList<>();
    HomeFragmentAdapter homeFragmentAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        appSession=new AppSession(requireContext());
        findViewById(view);

//        fetchData(appSession.getKeyUid());
        return view;
    }

    private void findViewById(View view) {
        userImage=view.findViewById(R.id.user_image);
        userName=view.findViewById(R.id.home_username);
        passwordRecycler=view.findViewById(R.id.recyclerView);

        Glide.with(HomeFragment.this)
                .load(R.drawable.logo)
                .centerCrop()
                .placeholder(R.drawable.logo)
                .into(userImage);

        if (!appSession.getKeyUsername().isEmpty())
            userName.setText(appSession.getKeyUsername());
        else
            Toast.makeText(getContext(), "no user name found", Toast.LENGTH_SHORT).show();

        // Set up RecyclerView

        fetchData(appSession.getKeyUid());
        homeFragmentAdapter=new HomeFragmentAdapter(passwordList,getContext());
        passwordRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        passwordRecycler.setAdapter(homeFragmentAdapter);


    }
    public void fetchData(String path) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Password");
        databaseReference.child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    // Get the child name (key)
//                    String childName = childSnapshot.getKey();

                    for (DataSnapshot typeSnapshot: childSnapshot.getChildren()){
                        String childName = typeSnapshot.getKey();
                        PasswordModel passwordModel = typeSnapshot.getValue(PasswordModel.class);
                        passwordList.add(passwordModel);
                        Log.e("TAG1", "Password list: "+passwordModel.getPassword_type());

                    }

//
                }

                // Update your RecyclerView adapter with the new data
                // For example, passwordAdapter.setPasswords(passwordList);
                // passwordAdapter.notifyDataSetChanged();
                homeFragmentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

}
