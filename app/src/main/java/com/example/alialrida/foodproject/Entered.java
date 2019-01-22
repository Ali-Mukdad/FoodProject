package com.example.alialrida.foodproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Entered extends AppCompatActivity {

    private TextView Hello;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entered);

        Hello=(TextView)findViewById(R.id.Hello);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

        Intent intent=getIntent();
        String name = intent.getStringExtra("email");
        if(user != null)
            Hello.setText("Hello "+ user.getEmail());
        else
            Hello.setText("Hello " + name);
    }

    public void signout(View v)
    {
        mAuth.signOut();
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
