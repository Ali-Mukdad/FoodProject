package com.example.alialrida.foodproject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    public EditText Email,Password,FName,LName,PhoneNb,Longitude,Latitude,Username,hiden;
    public  Integer f;
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Email=(EditText)findViewById(R.id.EmailField);
        Password=(EditText)findViewById(R.id.PasswordField);
        FName=(EditText)findViewById(R.id.FNameField);
        LName=(EditText)findViewById(R.id.LNameField);
        PhoneNb=(EditText)findViewById(R.id.PhoneField);
        Longitude=(EditText)findViewById(R.id.LongitudeField);
        Latitude=(EditText)findViewById(R.id.latitudeField);
        Username=(EditText)findViewById(R.id.UsernameField);
        hiden=(EditText)findViewById(R.id.hiden);
        mAuth = FirebaseAuth.getInstance();
    }

    public void Submit(View view) throws ExecutionException, InterruptedException {
        if (!validateForm()) {
            return;
        }
        String email = Email.getText().toString();
        String password = Password.getText().toString();
        String username = Username.getText().toString();
        new PhpApis().execute(email, username).get();
        f=Integer.parseInt(hiden.getText().toString());
        if(f==3)
        {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
        Intent intent = new Intent(this, Entered.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }
        else if(f==0)
        {
            Email.setError("Already taken.");
            Username.setError("Already exists");
        }
        else if(f==1)
        {
            Email.setError("Already taken.");
        }
        else if(f==2)
        {
            Username.setError("Already exists");
        }

    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            //Password.setVisibility(View.GONE);
            Toast.makeText(this, "Succeeded", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(this,Entered.class);
            startActivity(intent);
            //findViewById(R.id.verifyEmailButton).setEnabled(!user.isEmailVerified());
        } else {
            Password.setVisibility(View.VISIBLE);
            //       SignIn.setVisibility(View.GONE);
        }
    }

    public void TurnBack(View view)
    {
        Intent intent=new Intent(this,MainActivity.class);
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

        String phoneNb = PhoneNb.getText().toString();
        if (TextUtils.isEmpty(phoneNb)) {
            PhoneNb.setError("Required.");
            valid = false;
        } else {
            PhoneNb.setError(null);
        }

        String username = Username.getText().toString();
        if (TextUtils.isEmpty(username)) {
            Username.setError("Required.");
            valid = false;
        } else {
            Username.setError(null);
        }

        return valid;
    }
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public class PhpApis extends AsyncTask<String,Integer,Integer> {

        private String Email,Username;

        public PhpApis() {

        }

        @Override
        protected Integer doInBackground(String...args) {
            Email=args[0];
            Username=args[1];

            String link="localhost:3307/checkExistEmail.php?email="+Email;
            String link2="localhost:3307/checkExistUsername.php?Username="+Username;
            try {
                Integer flag ;
                URL url = new URL(link);
                String answer="";
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while ((line = rd.readLine()) != null) {
                    answer += line;
                }
                URL url2=new URL(link2);
                String answer2="";
                HttpURLConnection connection2 = (HttpURLConnection)url2.openConnection();
                connection2.setRequestMethod("GET");
                connection2.connect();
                InputStream inputStream2 = connection2.getInputStream();
                BufferedReader rd2 = new BufferedReader(new InputStreamReader(inputStream2));
                String line2 = "";
                while ((line2 = rd2.readLine()) != null) {
                    answer2 += line2;
                }
                if(answer2.equals("") && answer.equals(""))
                    publishProgress(0);
                else if(answer.equals("") && !answer2.equals(""))
                    publishProgress(1);
                else if(!answer.equals("") && answer2.equals(""))
                    publishProgress(2);
                else
                    publishProgress(3);
                connection.disconnect();
                connection2.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {

            hiden.setText(integer);
        }
    }
}
