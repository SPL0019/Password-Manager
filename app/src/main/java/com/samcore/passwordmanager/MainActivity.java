package com.samcore.passwordmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.samcore.passwordmanager.components.AppSession;
import com.samcore.passwordmanager.fragment.DocumentUploadFragment;
import com.samcore.passwordmanager.fragment.HomeFragment;
import com.samcore.passwordmanager.fragment.NotesFragment;
import com.samcore.passwordmanager.fragment.PasswordGeneratorFragment;
import com.samcore.passwordmanager.model.UploadDocumentModel;

import java.util.Objects;

public class MainActivity extends AppCompatActivity  implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment=new HomeFragment();
    PasswordGeneratorFragment passwordGeneratorFragment=new PasswordGeneratorFragment();
    DocumentUploadFragment documentUploadFragment=new DocumentUploadFragment();
    NotesFragment notesFragment=new NotesFragment();
    AppSession appSession;
    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference mDatabase;
    private  static final String TAG="MainActivity";
    public static final String DATABASE_PATH_UPLOADS = "uploads";

    private static final int PICK_IMAGE_REQUEST = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appSession=new AppSession(getApplicationContext());
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
        firebaseAuth=FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference(DATABASE_PATH_UPLOADS);


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
            case R.id.more:
                //logout();
                showOptionsMenu(findViewById(R.id.more));
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!=null) {
            Uri fileUri = data.getData();
            // Now, you can upload this fileUri to Firebase Storage
            uploadFileToFirebaseStorage(Objects.requireNonNull(fileUri),"Aadhar");
            Log.e(TAG, "onActivityResult1: "+fileUri );
        }
        if (requestCode == 235 && resultCode == RESULT_OK && data!=null) {
            Uri fileUri = data.getData();
            // Now, you can upload this fileUri to Firebase Storage
            uploadFileToFirebaseStorage(Objects.requireNonNull(fileUri), "Pan");
            Log.e(TAG, "onActivityResult1: "+fileUri );
        }

        Toast.makeText(getApplicationContext(), "On Activity Result Called", Toast.LENGTH_SHORT).show();
    }
    private void showOptionsMenu(View view) {
        // Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(MainActivity.this, view);
        // Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.options_menu, popup.getMenu());

        // Registering the popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                // Handle menu item clicks
                switch (item.getItemId()) {
                    case R.id.notes_menu:
                        // Handle option 1
                        getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,notesFragment).commit();
                        return true;
                    case R.id.logout:
                        // Handle option 2
                        logout();
                        return true;
                    default:
                        return false;
                }
            }
        });

        // Showing the popup menu
        popup.show();
    }
    private void uploadFileToFirebaseStorage(Uri fileUri, String documentType) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child("files/" + fileUri.getLastPathSegment());

        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // File successfully uploaded
                    // Get the download URL
                    Toast.makeText(MainActivity.this, "Upload Successfully", Toast.LENGTH_SHORT).show();
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        UploadDocumentModel uploadDocumentModel = new UploadDocumentModel();
                        if (documentType.equals("Aadhar"))
                            uploadDocumentModel.setName("aadhar");
                        else
                            uploadDocumentModel.setName("PAN");
                        uploadDocumentModel.setImageUrl(downloadUrl);
                        progressDialog.show();
                        String  uploadId = mDatabase.push().getKey();
                        mDatabase.child(appSession.getKeyUid())
                                .child(Objects.requireNonNull(uploadId))
                                .setValue(uploadDocumentModel);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.dismiss();
                        // Now you can use the downloadUrl as needed
                        Log.e(TAG, "uploadFileToFirebaseStorage: "+downloadUrl );
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                    Toast.makeText(MainActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnProgressListener(snapshot -> {
                    double progress = (100.0*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage("Uploaded  " +(int)progress+"%");
                });
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
