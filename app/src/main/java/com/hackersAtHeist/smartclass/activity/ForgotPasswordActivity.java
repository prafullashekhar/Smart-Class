package com.hackersAtHeist.smartclass.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.hackersAtHeist.smartclass.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputEditText inputEmail;
    private Button btnForgotPassword;
    private TextView tvLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // initiating
        mAuth = FirebaseAuth.getInstance();
        inputEmail = findViewById(R.id.inputEmail);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.INVISIBLE);

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResetLink();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            }
        });
    }


    private void sendResetLink(){
        String userEmail = inputEmail.getText().toString().trim();

        if(userEmail.isEmpty()){
            inputEmail.setError("Enter Email");
            inputEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
            inputEmail.setError("Enter correct email");
            inputEmail.requestFocus();
            return;
        }

        // sending reset link
        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(userEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ForgotPasswordActivity.this, "Reset link is sent to your email", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}