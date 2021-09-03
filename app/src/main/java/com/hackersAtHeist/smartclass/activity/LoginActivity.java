package com.hackersAtHeist.smartclass.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.hackersAtHeist.smartclass.MainActivity;
import com.hackersAtHeist.smartclass.R;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private TextInputEditText inputEmail, inputPassword;
    private TextView tvRegister,tvForgotPassword;
    private Button btnLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initiating
        mAuth = FirebaseAuth.getInstance();

        inputEmail = (TextInputEditText) findViewById(R.id.inputEmail);
        inputPassword = (TextInputEditText) findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyUser();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        progressBar.setVisibility(View.INVISIBLE);
    }

    private void verifyUser(){
        String userEmail = inputEmail.getText().toString().trim();
        String userPass = inputPassword.getText().toString();

        if(userEmail.isEmpty()){
            inputEmail.setError("Enter Email");
            inputEmail.requestFocus();
            return;
        }

        if(userPass.isEmpty()){
            inputPassword.setError("Enter Password");
            inputPassword.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
            inputEmail.setError("Enter correct email");
            inputEmail.requestFocus();
            return;
        }

        // initiating verifying
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.INVISIBLE);

                if(task.isSuccessful()){
                    if(mAuth.getCurrentUser().isEmailVerified()){
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else{
                        Toast.makeText(LoginActivity.this, "Please first verify your email address through your email", Toast.LENGTH_LONG).show();
                    }
                }else{
                    String errorMsg = task.getException().getMessage();
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}