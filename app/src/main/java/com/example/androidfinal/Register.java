package com.example.androidfinal;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidfinal.classes.Resident;
import com.example.androidfinal.classes.User;
import com.example.androidfinal.classes.Vaad;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.conn.ConnectTimeoutException;


public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private boolean isVaad;
    private TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        //Database
        database = FirebaseDatabase.getInstance();
        isVaad = true;
        error = findViewById(R.id.errorMessage);
    }

    //Select Vaad or Resident
    public void selectUserType(View view) {
        RadioButton button = (RadioButton) view;
        int id = button.getId();
        if (id == findViewById(R.id.vaad).getId()) {
            isVaad = true;
            findViewById(R.id.flatNumber).setVisibility(View.GONE);
            findViewById(R.id.seniority).setVisibility(View.VISIBLE);
        } else {
            isVaad = false;
            findViewById(R.id.flatNumber).setVisibility(View.VISIBLE);
            findViewById(R.id.seniority).setVisibility(View.GONE);
        }
    }

    //Add user
    public void Register(View view) {
        //Get Data from form
        int seniority = 0;
        int id;
        String firstName;
        String surname;
        String password;
        String username;
        int addressNumber = 0;
        try {
            TextView usertext = findViewById(R.id.usernameNew);
            username = usertext.getText().toString();
            TextView passtext = findViewById(R.id.passwordNew);
            password = passtext.getText().toString();

            TextView firstNameText = findViewById(R.id.firstName);
            TextView surnameText = findViewById(R.id.surname);
            firstName = firstNameText.getText().toString();
            surname = surnameText.getText().toString();
            TextView idText = findViewById(R.id.id);
            id = Integer.parseInt(idText.getText().toString());

            //Adds values to useless fields to prevent nulls. Will make the right object later.
            if (isVaad) {
                TextView seniorityText = findViewById(R.id.seniority);
                seniority = Integer.parseInt(seniorityText.getText().toString());
                addressNumber = 0;
            } else {
                TextView addressText = findViewById(R.id.flatNumber);
                addressNumber = Integer.parseInt(addressText.getText().toString());
                seniority = 0;
            }


            // Check if flat number exists already
            DatabaseReference myRef = database.getReference("users");
            int finalSeniority = seniority;
            int finalAddressNumber = addressNumber;
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    Iterable<DataSnapshot> userArray;
                    userArray = dataSnapshot.getChildren();
                    for (DataSnapshot user : userArray) {
                        //If it's a vaad
                        if (user.child("userType").getValue(String.class).contentEquals("resident")) {
                            if (user.child("addressNumber").getValue(Long.class).intValue() == finalAddressNumber) {
                                errorMessage("Flat number already registered.");
                                return;
                            } else if (user.child("id").getValue(Long.class).intValue() == id) {
                                errorMessage("ID already taken.");
                                return;
                            }
                        }
                    }
                    auth(username, password, firstName, surname, id, finalSeniority, finalAddressNumber);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    errorMessage("Couldn't connect. Please check your connection.");
                }
            });
        } catch (NumberFormatException e) {
            errorMessage("Please check validity of fields.");
        }

    }

    private void errorMessage(String errorMessage) {
        error.setVisibility(View.VISIBLE);
        error.setTextColor(Color.RED);
        error.setText(errorMessage);
    }

    private void auth(String username, String password, String firstName, String surname,
                      int id, int seniority, int addressNumber) {
        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference myRef = database.getReference("users").child(user.getUid());
                            //Create user for database
                            User userObj;
                            if (isVaad)
                                userObj = new Vaad(username, firstName, surname, id, seniority);
                            else
                                userObj = new Resident(username, firstName, surname, id, addressNumber);
                            myRef.setValue(userObj);

                            //Move to landing page
                            Intent intent = new Intent(Register.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            errorMessage("Registration failed.");
                        }
                        // ...
                    }
                });
    }

}