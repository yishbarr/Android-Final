package com.example.androidfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Firebase
        mAuth = FirebaseAuth.getInstance();

        //Message
        message = findViewById(R.id.loginMessage);
    }

    //Login
    public void Login(View view) {
        try {
            final boolean[] error = new boolean[1];
            //Get Data from form
            TextView usertext = findViewById(R.id.username);
            String email = usertext.getText().toString();
            TextView passtext = findViewById(R.id.passwordVaad);
            String password = passtext.getText().toString();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        errorMessage("Login credentials incorrect.");
                                    }
                                }
                                // ...

                            }
                    );


        } catch (IllegalArgumentException e) {
            errorMessage("Login credentials incorrect.");
        } catch (Exception e) {
            errorMessage(e.getMessage());
        }

    }

    private void errorMessage(String errorMessage) {
        message.setVisibility(View.VISIBLE);
        message.setTextColor(Color.RED);
        message.setText(errorMessage);
    }

    //Register
    public void Register(View view) {
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
    }

    //Reset Password email
    public void resetPassword(View view) {
        try {
            TextView usertext = findViewById(R.id.username);
            String email = usertext.getText().toString();
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            message.setVisibility(View.VISIBLE);
                            if (task.isSuccessful()) {
                                message.setTextColor(Color.WHITE);
                                message.setText("An email with a link to reset your password has been sent to your address.");
                            } else {
                                errorMessage("Email is not registered. Please rewrite your email address or sign up with new account.");
                            }
                        }

                    });
        } catch (IllegalArgumentException e) {
            errorMessage("Email is invalid.");
        }

    }
}