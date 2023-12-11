package com.samcore.passwordmanager;

import static com.samcore.passwordmanager.ValidationCheck.validateEmail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.samcore.passwordmanager.model.UserModel;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    TextInputLayout name,email,password;
    AppCompatCheckBox rememberMeCheck;
    TextView forgotPassword,signUpText,desc,name_txt;
    AppCompatButton loginButton;
    ImageButton googleButton;
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    // Firebase Database.
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    UserModel userModel=new UserModel();
    String name_s="",email_s="",password_S="";
    AppSession appSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        appSession=new AppSession(getApplicationContext());

        findViewById();

        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
            // Initialize Firebase Auth
            firebaseAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        googleSignInClient=GoogleSignIn.getClient(this,googleSignInOptions);
        // instance of our FIrebase database.
        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference("UserInfo");


    }

    private void findViewById() {
        email=findViewById(R.id.loginEmail);
        password=findViewById(R.id.login_password);
        name=findViewById(R.id.loginName);
        forgotPassword=findViewById(R.id.login_forgot_password);
        rememberMeCheck=findViewById(R.id.login_remember_me);
        loginButton=findViewById(R.id.login_button);
        googleButton=findViewById(R.id.google_button);
        signUpText=findViewById(R.id.signup_text_link);
        desc=findViewById(R.id.desc);
        name_txt=findViewById(R.id.name_txt);

        googleButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        signUpText.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        addDataToFirebase(user);
                        appSession.setKeyIsLoggedIn(true);
                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                });
    }
    // [END auth_with_google]

    public void sendEmailVerificationWithContinueUrl() {
        // [START send_email_verification_with_continue_url]

        FirebaseUser user = firebaseAuth.getCurrentUser();

        String url = "http://www.example.com/verify?uid=" + user.getUid();
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setUrl(url)
                .setIOSBundleId("com.example.ios")
                // The default for this is populated with the current android package name.
                .setAndroidPackageName("com.example.android", false, null)
                .build();

        user.sendEmailVerification(actionCodeSettings)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Email sent.");
                    }
                });

        // [END send_email_verification_with_continue_url]
        // [START localize_verification_email]
        firebaseAuth.setLanguageCode("fr");
        // To apply the default app language instead of explicitly setting it.
        firebaseAuth.useAppLanguage();
        // [END localize_verification_email]
    }
    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        Objects.requireNonNull(user).sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    // Email sent
                });
        // [END send_email_verification]
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
        if(currentUser != null){
            reload();
        }
    }
    private void reload() { }
    private void updateUI(FirebaseUser user) {

    }

    @Override
    public void onClick(View v) {
        if (v==googleButton){
            //initialize sign in intent
            Intent intent=googleSignInClient.getSignInIntent();
//            startActivity for result
            startActivityForResult(intent, RC_SIGN_IN);
        } else if (v==loginButton) {
            if (loginButton.getText().equals(getString(R.string.login))){
                if (validation())
                {
                    signIn(email_s,password_S);
                }
            }else {
                if (validation())
                {
                    createAccount(email_s,password_S);
                }
            }
        } else if (v==signUpText) {
            if (signUpText.getText().equals(getString(R.string.signup))){
                name.setVisibility(View.VISIBLE);
                name_txt.setVisibility(View.VISIBLE);
                loginButton.setText(getString(R.string.signup));
                signUpText.setText(getString(R.string.login));
                desc.setText(R.string.have_an_account);
                forgotPassword.setVisibility(View.GONE);
            } else if (signUpText.getText().equals(getString(R.string.login))) {
                name.setVisibility(View.GONE);
                name_txt.setVisibility(View.GONE);
                loginButton.setText(getString(R.string.login));
                signUpText.setText(getString(R.string.signup));
                desc.setText(R.string.don_t_have_an_account);
                forgotPassword.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean validation() {
        boolean validation = false;
        email_s= Objects.requireNonNull(email.getEditText()).getText().toString().trim();
        password_S= Objects.requireNonNull(password.getEditText()).getText().toString().trim();
        name_s= Objects.requireNonNull(name.getEditText()).getText().toString().trim();

        if (!email_s.isEmpty() && !password_S.isEmpty()){
            if (!validateEmail(email_s))
                Toast.makeText(this, "Please enter valid email..", Toast.LENGTH_SHORT).show();
            else
                validation= true;
        }
        else
            Toast.makeText(this, "Please enter email or password..", Toast.LENGTH_SHORT).show();

        return validation;
    }

    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        updateUI(user);
                        appSession.setKeyIsLoggedIn(true);
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
        // [END sign_in_with_email]
    }
    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        addDataToFirebase(user);
                        startActivity(new Intent(LoginActivity.this,LoginActivity.class));
                    } else {
                        // If sign in fails, display a message to the user.
                        // If sign in fails, display a message to the user.
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthWeakPasswordException e) {
                            // Handle weak password exception
                            String errorCode = e.getErrorCode();
                            String errorMessage = e.getMessage();

                            if (errorCode.equals("ERROR_WEAK_PASSWORD")) {
                                // Password is too weak
                                Toast.makeText(LoginActivity.this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
                            } else {
                                // Other weak password exception, handle accordingly
                                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            // Handle other exceptions
                            Toast.makeText(LoginActivity.this, "Authentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        updateUI(null);
                    }
                });
        // [END create_user_with_email]
    }
    private void addDataToFirebase( FirebaseUser user) {
        // below 3 lines of code is used to set
        // data in our object class.
        if (name_s.isEmpty())
            userModel.setName(user.getDisplayName());
        else
            userModel.setName(name_s);

        userModel.setEmail(user.getEmail());
        userModel.setId(user.getUid());
        // we are use add value event listener method
        // which is called with database reference.
        databaseReference.orderByChild("id").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // inside the method of on Data change we are setting
                // our object class to our database reference.
                // data base reference will sends data to firebase.
                if (snapshot.exists())
                    Toast.makeText(LoginActivity.this, "This user already exist", Toast.LENGTH_SHORT).show();
                else {
                    databaseReference.child(user.getUid()).setValue(userModel);
                    // after adding this data we are showing toast message.
                    Toast.makeText(LoginActivity.this, "SingIn successfully", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // if the data is not added or it is cancelled then
                // we are displaying a failure toast message.
                Toast.makeText(LoginActivity.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
