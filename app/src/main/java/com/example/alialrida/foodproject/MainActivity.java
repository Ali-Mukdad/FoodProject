package com.example.alialrida.foodproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    private EditText Email,Password;
    private Button SignIn,SignUp,EmailVerification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Email =(EditText) findViewById(R.id.Email);
        Password =(EditText)findViewById(R.id.Password);
        SignUp =(Button) findViewById(R.id.SignUp);
        SignIn =(Button)findViewById(R.id.SignIn);
        EmailVerification=(Button)findViewById(R.id.EmailVerification);
        // Buttons
        SignIn.setOnClickListener(this);
        SignUp.setOnClickListener(this);
        EmailVerification.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void createAccount(String email,String password)
    {
        Intent intent=new Intent(this,SignUp.class);
        startActivity(intent);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = Email.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Email.setError("Required.");
            valid = false;
        }else if(!isEmailValid(email))
        {
            Email.setError("Incorrect Format.");
        } else {
            Email.setError(null);
        }

        String password = Password.getText().toString();
        if (TextUtils.isEmpty(password)) {
            Password.setError("Required.");
            valid = false;
        } else {
            Password.setError(null);
        }

        return valid;
    }
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
        // [END sign_in_with_email]
    }

    private void sendEmailVerification() {
        // Disable button
        EmailVerification.setEnabled(false);
        if (!validateForm()) {
            return;
        }
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        EmailVerification.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(MainActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private void updateUI(FirebaseUser user) {
        // hideProgressDialog();
        if (user != null) {
            //Password.setVisibility(View.GONE);
            SignIn.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Succeeded", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(this,Entered.class);
            startActivity(intent);
            //findViewById(R.id.verifyEmailButton).setEnabled(!user.isEmailVerified());
        } else {
            Password.setVisibility(View.VISIBLE);
            //       SignIn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.SignUp) {
            createAccount(Email.getText().toString(), Password.getText().toString());
        } else if (i == R.id.SignIn) {
            signIn(Email.getText().toString(), Password.getText().toString());
        }else if (i == R.id.EmailVerification) {
            sendEmailVerification();
        }
        /* else if (i == R.id.signOutButton) {
            signOut();
        } */
    }
}
