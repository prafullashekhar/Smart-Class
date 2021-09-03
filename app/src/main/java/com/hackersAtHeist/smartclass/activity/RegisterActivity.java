package com.hackersAtHeist.smartclass.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hackersAtHeist.smartclass.Constants;
import com.hackersAtHeist.smartclass.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private TextInputEditText inputName, inputEmail, inputPassword, inputBatchBranch, inputRoll;
    private Button btnRegister;
    private TextView tvLogin;

    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // initiating
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        inputBatchBranch = findViewById(R.id.inputBatchBranch);
        inputRoll = findViewById(R.id.inputRoll);
        inputPassword = findViewById(R.id.inputPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chechUserData();
            }
        });
    }

    private void chechUserData(){
        String userEmail = inputEmail.getText().toString().trim();
        String userPass = inputPassword.getText().toString();
        String userName = inputName.getText().toString();
        String userBatchBranch = inputBatchBranch.getText().toString();
        String userRoll = inputRoll.getText().toString();

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

        if(userPass.length() < 6){
            inputPassword.setError("Password too short");
            inputPassword.requestFocus();
            return;
        }

        if(userBatchBranch.isEmpty()){
            inputBatchBranch.setError("Enter Batch/Branch");
            inputBatchBranch.requestFocus();
            return;
        }

        if(userRoll.isEmpty()){
            inputRoll.setError("Enter Roll No.");
            inputRoll.requestFocus();
            return;
        }

        if(userName.isEmpty()){
            inputName.setError("Enter Name");
            inputName.requestFocus();
            return;
        }


        // adding user
        mAuth.createUserWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Registered successfully \nPlease check your email to verify", Toast.LENGTH_LONG).show();
                                userId = mAuth.getCurrentUser().getUid();

                                registerUser(userName, userEmail, userBatchBranch, userRoll);
                            }
                        }
                    });
                }else{
                    String errorMsg = task.getException().getMessage();
                    Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void registerUser(String Name, String Email, String BatchBranch, String Roll){
        DocumentReference documentReference = mStore.collection("Users").document(userId);
        Map<String, Object> user = new HashMap<>();
        user.put(Constants.name, Name);
        user.put(Constants.email, Email);
        user.put(Constants.BatchBranch, BatchBranch);
        user.put(Constants.roll, Roll);

        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("MSG", "user profile is created");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("MSG", "user profile is not created");
            }
        });
    }

}