package com.example.androidfinal.fragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.androidfinal.R;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VaadPayments#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VaadPayments extends Fragment implements AdapterView.OnItemSelectedListener {

    private static String monthChoice;
    private static TextView message;
    private static TextView flatNumberText;
    private static TextView costChoiceText;
    private static DatabaseReference myRef;
    private FirebaseUser user;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static HashMap<Long, String> validflats;

    public VaadPayments() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VaadPayments.
     */
    // TODO: Rename and change types and number of parameters
    public static VaadPayments newInstance(String param1, String param2) {
        VaadPayments fragment = new VaadPayments();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vaad_payments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Spinner spinner = view.findViewById(R.id.monthChoice);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.months, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        message = view.findViewById(R.id.paymentError);
        flatNumberText = view.findViewById(R.id.flatNumberChoice);
        costChoiceText = view.findViewById(R.id.costChoice);

        user = FirebaseAuth.getInstance().getCurrentUser();

        validflats = new HashMap<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Iterable<DataSnapshot> myUsersSnap = dataSnapshot.child(user.getUid()).child("residents").getChildren();
                ArrayList<String> myUsers = new ArrayList<>();
                myUsersSnap.forEach(user -> myUsers.add(user.getKey()));

                validflats.clear();
                Iterable<DataSnapshot> userArray = dataSnapshot.getChildren();
                for (DataSnapshot user : userArray) {
                    Long selectedFlat;
                    if (myUsers.contains(user.getKey())) {
                        selectedFlat = (user.child("addressNumber").getValue(Long.class));
                        validflats.put(selectedFlat, user.getKey());
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                errorMessage("Couldn't retrieve data from database.");
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        monthChoice = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Confirm payment change by month and flat number.
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void ConfirmPayment(View view) {
        try {
            int flatNumber = Integer.parseInt(flatNumberText.getText().toString());
            double costChoice = Double.parseDouble(costChoiceText.getText().toString());

            String userId = null;
            for (Map.Entry<Long, String> entry : validflats.entrySet()) {
                Long key = entry.getKey();
                String uid = entry.getValue();
                if (key.intValue() == flatNumber)
                    userId = uid;
            }
            if (userId == null) {
                errorMessage("Flat number doesn't exist.");
                return;
            }

            myRef.child(userId)
                    .child("monthlyPayments")
                    .child(monthChoice)
                    .setValue(costChoice);

            message.setVisibility(View.VISIBLE);
            message.setText("Successfully updated resident payment");
            message.setTextColor(Color.WHITE);


        } catch (NumberFormatException e) {
            errorMessage("Invalid input. Make sure you used numbers only.");
        } catch (Exception e) {
            errorMessage(e.getMessage());
        }
    }

    public static void errorMessage(String errorMessage) {
        message.setVisibility(View.VISIBLE);
        message.setTextColor(Color.RED);
        message.setText(errorMessage);
    }

}