package com.example.androidfinal;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidfinal.classes.Resident;
import com.example.androidfinal.classes.User;
import com.example.androidfinal.classes.Vaad;
import com.example.androidfinal.interfaces.Authentication;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity implements Authentication {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private boolean isVaad;
    private TextView error;
    private HashMap<String, User> userArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        userArray = new HashMap<>();
        //Firebase
        mAuth = FirebaseAuth.getInstance();
        //Database
        database = FirebaseDatabase.getInstance();
        isVaad = true;
        error = findViewById(R.id.errorMessage);
        DatabaseReference myRef = database.getReference("users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Iterable<DataSnapshot> userArrayIterator = dataSnapshot.getChildren();
                for (DataSnapshot user : userArrayIterator) {
                    if (user.child("userType").getValue(String.class).contentEquals("resident"))
                        userArray.put(user.getKey(), user.getValue(Resident.class));
                    else
                        userArray.put(user.getKey(), user.getValue(Vaad.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                errorMessage("Couldn't connect. Please check your connection.");
            }
        });
    }

    //Select Vaad or Resident
    public void selectUserType(View view) {
        RadioButton button = (RadioButton) view;
        int id = button.getId();
        if (id == findViewById(R.id.vaad).getId()) {
            isVaad = true;
            findViewById(R.id.flatNumber).setVisibility(View.GONE);
            findViewById(R.id.vaadEmail).setVisibility(View.GONE);
            findViewById(R.id.seniority).setVisibility(View.VISIBLE);
        } else {
            isVaad = false;
            findViewById(R.id.flatNumber).setVisibility(View.VISIBLE);
            findViewById(R.id.vaadEmail).setVisibility(View.VISIBLE);
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
        String vaadEmail = "";
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
                TextView vaadEmailText = findViewById(R.id.vaadEmail);
                vaadEmail = vaadEmailText.getText().toString();
                seniority = 0;
            }


            // Check if flat number exists already and if the vaad email is valid
            int finalSeniority = seniority;
            int finalAddressNumber = addressNumber;
            String finalVaadEmail = vaadEmail;

            //If it's a resident
            //Check if vaad email exists.
            boolean emailExists = false;
            String vaadUid = null;

            //Check if id taken
            String idTaken = "ID already taken.";
            for (Map.Entry<String, User> mapEntry : userArray.entrySet()) {
                User user = mapEntry.getValue();
                String key = mapEntry.getKey();
                if (user.getId() == id) {
                    errorMessage(idTaken);
                    return;
                }
                if (!isVaad) {
                    if (user.getUserType().contentEquals("resident")) {
                        if (((Resident) user).getAddressNumber() == finalAddressNumber) {
                            errorMessage("Flat number already registered.");
                            return;
                        }
                    }
                    String email = user.getUserName();
                    if (email.contentEquals(finalVaadEmail)) {
                        emailExists = true;
                        vaadUid = key;
                        break;
                    }
                }
            }
            if (!emailExists) {
                errorMessage("Committee email isn't registered");
                return;
            }


            if (firstName.contentEquals("") || surname.contentEquals("")) {
                errorMessage("Please check validity of fields.");
                return;
            }
            auth(username, password, firstName, surname, id, finalSeniority, finalAddressNumber, finalVaadEmail, vaadUid);


        } catch (NumberFormatException e) {
            errorMessage("Please check validity of fields.");
        }

    }

    public void errorMessage(String errorMessage) {
        error.setVisibility(View.VISIBLE);
        error.setTextColor(Color.RED);
        error.setText(errorMessage);
    }

    private void auth(String username, String password, String firstName, String surname,
                      int id, int seniority, int addressNumber, String vaadEmail, String vaadUid) {
        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference myRef = database.getReference("users");
                            //Create user for database
                            User userObj;
                            if (isVaad) {
                                userObj = new Vaad(username, firstName, surname, id, seniority);
                                myRef.child(user.getUid()).setValue(userObj);
                            } else {
                                userObj = new Resident(username, firstName, surname, id, addressNumber, vaadEmail, vaadUid);
                                myRef.child(vaadUid).child("residents").child(user.getUid()).setValue(user.getUid());
                                myRef.child(user.getUid()).setValue(userObj);
                            }

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