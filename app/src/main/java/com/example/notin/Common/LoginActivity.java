package com.example.notin.Common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Intent;

import com.example.notin.R;
import com.example.notin.Student.Home;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int RC_SIGN_IN = 123;
    Spinner spinnerSem;
    Spinner spinnerDept;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    EditText et_pno, et_verificationCode;
    String phoneNumber, otp;
//    String codesent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //--for mobile sign in
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_pno = findViewById(R.id.et_pno);
                phoneNumber = et_pno.getText().toString();
//                et_verificationCode = findViewById(R.id.et_verificationCode);
//                otp = et_verificationCode.getText().toString();

                if (phoneNumber.isEmpty()) {
                    et_pno.setError("Phone number is required");
                    et_pno.requestFocus();
                    return;
                }
                if (phoneNumber.length() < 10) {
                    et_pno.setError("Please enter a valid phone number");
                    et_pno.requestFocus();
                    return;
                }
                sendVerificationCode();
            }
        });


        //--for google sign in
        mAuth = FirebaseAuth.getInstance();
        createRequest();
        findViewById(R.id.googleLoginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        // Spinner element
        spinnerSem = (Spinner) findViewById(R.id.spinnerSem);
        spinnerDept = (Spinner) findViewById(R.id.spinnerDept);

        // Spinner click listener
        spinnerSem.setOnItemSelectedListener(this);
        spinnerDept.setOnItemSelectedListener(this);

        // Spinner Drop down elements - for semester
        List<String> categoriesSem = new ArrayList<String>();
        categoriesSem.add("1");
        categoriesSem.add("2");
        categoriesSem.add("3");
        categoriesSem.add("4");
        categoriesSem.add("5");
        categoriesSem.add("6");
        categoriesSem.add("7");
        categoriesSem.add("8");

        // Spinner Drop down elements - for department
        List<String> categoriesDept = new ArrayList<String>();
        categoriesDept.add("Computer Science & Engineering");
        categoriesDept.add("Mechanical Engineering");
        categoriesDept.add("Civil Engineering");
        categoriesDept.add("Electrical & Electronics Engineering");
        categoriesDept.add("Electronics & Communication Engineering");
        categoriesDept.add("Industrial Engineering & Management");
        categoriesDept.add("Electronics & Telecommunication Engineering");
        categoriesDept.add("Information Science & Engineering");
        categoriesDept.add("Electronics & Instrumentation Engineering");
        categoriesDept.add("Medical Electronics Engineering");
        categoriesDept.add("Bio Technology");
        categoriesDept.add("Chemical Engineering");
        categoriesDept.add("Aerospace Engineering");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterSem = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesSem);
        ArrayAdapter<String> dataAdapterDept = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesDept);

        // Drop down layout style - list view with radio button
        dataAdapterSem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapterDept.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerSem.setAdapter(dataAdapterSem);
        spinnerDept.setAdapter(dataAdapterDept);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {


        if (parent.getId() == R.id.spinnerSem) {
            // On selecting a spinner item
            String item = parent.getItemAtPosition(position).toString();

        } else if (parent.getId() == R.id.spinnerDept) {
            // On selecting a spinner item
            String item = parent.getItemAtPosition(position).toString();
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    //-------------------------------Google Sign in --------------------------------//

    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {

        mGoogleSignInClient.signOut();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                toast(e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(getApplicationContext(), Home.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            toast("failed");

                        }

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
//        if(mAuth.getCurrentUser() != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                startActivity(new Intent(this, Home.class));
            }
        }


    //------------------------------- End Google Sign in --------------------------------//


    //------------------------------- Mobile Number Sign in --------------------------------//

    private void sendVerificationCode() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phoneNumber,  // Phone number to verify
                60,         // Timeout and unit
                TimeUnit.SECONDS,
                this,
                mCallbacks
        );
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            toast(e.getMessage());
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            otp = s;
            showOtpDialog();
        }
    };

    private void showOtpDialog() {
        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter OTP:");

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.pno_otp_alert_dialog, null);
        builder.setView(customLayout);

        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText otp1 = customLayout.findViewById(R.id.et_verificationCode);
                        String otp2 = otp1.getText().toString();
                        verifyCode(otp2);
                    }
        });

        // create and show
        // the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void verifyCode(String codeByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otp, codeByUser);
        signInByUserCredential(credential);
    }

    private void signInByUserCredential(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(), Home.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            toast(task.getException().getMessage());
                        }
                    }
                });
    }
}