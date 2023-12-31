package com.samcore.passwordmanager.fragment;

import android.content.ClipboardManager;
import android.content.Context;
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
import android.widget.TextView;
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
import java.util.Random;

public class PasswordGeneratorFragment extends Fragment implements View.OnClickListener{
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference,passwordReference;
    FirebaseUser firebaseUser;
    PasswordTypeModel passwordTypeModel=new PasswordTypeModel();
    PasswordModel passwordModel=new PasswordModel();
    EditText addPasswordType;
    ImageView addPasswordTypeButton;
    Spinner passwordTypeSpinner;
    ArrayList<String> passwordTypeList=new ArrayList<>();
    TextInputLayout name_txt,username_txt,password_txt,password_length;
    AppCompatButton saveButton,generatePasswordButton;
    TextView generatePassword;
    private static final String TAG = "Password fragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_password_generator, container, false);
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        // instance of our FIrebase database.
        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference("Password_type");
        passwordReference=firebaseDatabase.getReference("Password");
        firebaseUser=firebaseAuth.getCurrentUser();
        findViewById(view);
        return view;
    }
    private void findViewById(View view) {
        addPasswordType=view.findViewById(R.id.add_password_type_text);
        addPasswordTypeButton=view.findViewById(R.id.add_passwordType_image);
        passwordTypeSpinner=view.findViewById(R.id.password_type_spinner);

        name_txt=view.findViewById(R.id.add_password_name);
        username_txt=view.findViewById(R.id.add_password_username);
        password_txt=view.findViewById(R.id.add_password_password);

        password_length=view.findViewById(R.id.add_password_length);
        generatePassword=view.findViewById(R.id.password_generator);

        saveButton=view.findViewById(R.id.save_password_button);
        generatePasswordButton=view.findViewById(R.id.password_generator_button);



        addPasswordTypeButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        generatePasswordButton.setOnClickListener(this);
        generatePassword.setOnClickListener(this);

        addSpinnerData();
    }
    @Override
    public void onClick(View view) {
        if (view==addPasswordTypeButton)
        {
            if (addPasswordType.getText().toString().trim().isEmpty())
                Toast.makeText(getContext(), "Please enter password type..", Toast.LENGTH_SHORT).show();
            else{

                passwordTypeModel.setPasswordType(addPasswordType.getText().toString().trim());
                passwordTypeModel.setUpdateUserID(firebaseUser.getUid());
                addDataToFirebase();
            }
        } else if (view==saveButton) {
            if (validation()){
                passwordModel.setId(firebaseUser.getUid());
                passwordModel.setName(Objects.requireNonNull(name_txt.getEditText()).getText().toString().trim());
                passwordModel.setUsername(Objects.requireNonNull(username_txt.getEditText()).getText().toString().trim());
                passwordModel.setPassword(Objects.requireNonNull(password_txt.getEditText()).getText().toString().trim());
                passwordModel.setPassword_type(Objects.requireNonNull(passwordTypeSpinner.getSelectedItem().toString()));
                addPasswordToFirebase();
            }
        } else if (view==generatePasswordButton) {
            int length= Integer.parseInt(Objects.requireNonNull(password_length.getEditText()).getText().toString());
            if (password_length.getEditText().getText().toString().isEmpty())
                Toast.makeText(getActivity(), "please enter password length", Toast.LENGTH_SHORT).show();
            else {
                String generatedPassword = generateRandomPassword(length, true, true, true, true);
                generatePassword.setText(generatedPassword);
            }
            password_length.getEditText().setText("");
            password_length.getEditText().clearFocus();
        } else if (view==generatePassword) {
            ClipboardManager cm =(ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setText(generatePassword.getText());
            Toast.makeText(getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean validation(){
        boolean validate=false;
        if (Objects.requireNonNull(name_txt.getEditText()).getText().toString().trim().isEmpty())
            Toast.makeText(getContext(), "Please enter name..", Toast.LENGTH_SHORT).show();
        else if (Objects.requireNonNull(username_txt.getEditText()).getText().toString().trim().isEmpty())
            Toast.makeText(getContext(), "Please enter username or email", Toast.LENGTH_SHORT).show();
        else if (Objects.requireNonNull(password_txt.getEditText()).getText().toString().trim().isEmpty())
            Toast.makeText(getContext(), "Please enter password", Toast.LENGTH_SHORT).show();
        else
            validate=true;
        return validate;
    }

    private void addSpinnerData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                List<String> dataList = new ArrayList<>();
                passwordTypeList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Assuming your data is stored as strings
                    String passwordType = (String) snapshot.child("passwordType").getValue();

                    Log.d(TAG, "onDataChange: "+passwordType);
                    passwordTypeList.add(passwordType);
                }

                // Populate the Spinner with the data
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, passwordTypeList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                passwordTypeSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addDataToFirebase() {
        databaseReference.orderByChild("passwordType").equalTo(addPasswordType.getText().toString().trim()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    Toast.makeText(getContext(), "This Password type already exist...", Toast.LENGTH_SHORT).show();
                else
                {
                    databaseReference.child(addPasswordType.getText().toString().trim()).setValue(passwordTypeModel);
                    Toast.makeText(getContext(), "Password type added successfully", Toast.LENGTH_SHORT).show();
                    addPasswordType.setText("");
                    addPasswordType.clearFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void addPasswordToFirebase() {
        String uname=Objects.requireNonNull(username_txt.getEditText()).getText().toString().trim().replaceAll("[^a-zA-Z0-9_]", "_");
        passwordReference.child(firebaseUser.getUid())
                .child(passwordTypeSpinner.getSelectedItem().toString())
                .orderByChild("username")
                .equalTo(Objects.requireNonNull(username_txt.getEditText()).getText().toString().trim())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                            Toast.makeText(getContext(), "This username already Added...", Toast.LENGTH_SHORT).show();
                        else
                        {
                            passwordReference.child(firebaseUser.getUid())
                                    .child(passwordTypeSpinner.getSelectedItem().toString())
                                    .child(uname)
                                    .setValue(passwordModel);
                            Toast.makeText(getContext(), "Password added successfully", Toast.LENGTH_SHORT).show();
                            Objects.requireNonNull(name_txt.getEditText()).setText("");
                            Objects.requireNonNull(password_txt.getEditText()).setText("");
                            Objects.requireNonNull(username_txt.getEditText()).setText("");
                            name_txt.clearFocus();
                            password_txt.clearFocus();
                            username_txt.clearFocus();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private static String generateRandomPassword(int max_length, boolean upperCase, boolean lowerCase, boolean numbers, boolean specialCharacters)
    {
        String upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseChars = "abcdefghijklmnopqrstuvwxyz";
        String numberChars = "0123456789";
        String specialChars = "!@#$%^&*()_-+=<>?/{}~|";
        String allowedChars = "";

        Random rn = new Random();
        StringBuilder sb = new StringBuilder(max_length);

        //this will fulfill the requirements of atleast one character of a type.
        if(upperCase) {
            allowedChars += upperCaseChars;
            sb.append(upperCaseChars.charAt(rn.nextInt(upperCaseChars.length()-1)));
        }

        if(lowerCase) {
            allowedChars += lowerCaseChars;
            sb.append(lowerCaseChars.charAt(rn.nextInt(lowerCaseChars.length()-1)));
        }

        if(numbers) {
            allowedChars += numberChars;
            sb.append(numberChars.charAt(rn.nextInt(numberChars.length()-1)));
        }

        if(specialCharacters) {
            allowedChars += specialChars;
            sb.append(specialChars.charAt(rn.nextInt(specialChars.length()-1)));
        }


        //fill the allowed length from different chars now.
        for(int i=sb.length();i < max_length;++i){
            sb.append(allowedChars.charAt(rn.nextInt(allowedChars.length())));
        }

        return  sb.toString();
    }
}
