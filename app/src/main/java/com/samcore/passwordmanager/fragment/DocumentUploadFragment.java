package com.samcore.passwordmanager.fragment;

import static com.samcore.passwordmanager.MainActivity.DATABASE_PATH_UPLOADS;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.samcore.passwordmanager.R;
import com.samcore.passwordmanager.components.AppSession;
import com.samcore.passwordmanager.model.UploadDocumentModel;

import java.util.ArrayList;
import java.util.List;


public class DocumentUploadFragment extends Fragment implements View.OnClickListener{
    ImageView aadharImageView,panImageView;
    //list to hold all the uploaded images
    List<UploadDocumentModel> uploadDocumentModelList=new ArrayList<>();
    RecyclerView recyclerView;
    FirebaseStorage storage;
    private ProgressDialog progressDialog;
    //database reference
    DatabaseReference mDatabase;
    RetriveDocumentAdapter retriveDocumentAdapter;
    private static final int PICK_IMAGE_REQUEST = 234;
    AppSession appSession;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_document_upload, container, false);
        storage = FirebaseStorage.getInstance();
        appSession=new AppSession(requireActivity());
        findViewById(view);
        return view;
    }

    private void findViewById(View view) {
        aadharImageView=view.findViewById(R.id.upload_aadhar_image_view);
        panImageView=view.findViewById(R.id.upload_pan_image_view);
        recyclerView = view.findViewById(R.id.recyclerView);

        progressDialog = new ProgressDialog(getContext());

        aadharImageView.setOnClickListener(this);
        panImageView.setOnClickListener(this);

        //displaying progress dialog while fetching images
        progressDialog.setMessage("Please wait...");
        progressDialog.show();


        loadImageData();
        //creating adapter
        retriveDocumentAdapter = new RetriveDocumentAdapter(uploadDocumentModelList,getContext());
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //adding adapter to recyclerview
        recyclerView.setAdapter(retriveDocumentAdapter);
        
    }

    private void loadImageData() {
        mDatabase = FirebaseDatabase.getInstance().getReference(DATABASE_PATH_UPLOADS);

        //adding an event listener to fetch values
        mDatabase.child(appSession.getKeyUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //dismissing the progress dialog
                progressDialog.dismiss();
                uploadDocumentModelList.clear();
                //iterating through all the values in database
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        UploadDocumentModel uploadDocumentModel = postSnapshot.getValue(UploadDocumentModel.class);
                        uploadDocumentModelList.add(uploadDocumentModel);
                }
                retriveDocumentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view==aadharImageView){
            showFileChooser(view);
        } else if (view==panImageView) {
            showFileChooser(view);
            
        }
    }
    private void showFileChooser(View view) {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (view==aadharImageView)
            getActivity().startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE_REQUEST);
        else if (view==panImageView) {
            getActivity().startActivityForResult(Intent.createChooser(intent, "Select Picture"),235);
        }
    }
}
