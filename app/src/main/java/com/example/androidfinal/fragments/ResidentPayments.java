package com.example.androidfinal.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.androidfinal.MainActivity;
import com.example.androidfinal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Month;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResidentPayments#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResidentPayments extends Fragment {

    private ArrayList<LinkedHashMap<String, Long>> allPayments = new ArrayList<>();
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ResidentPayments() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResidentPayments.
     */
    // TODO: Rename and change types and number of parameters
    public static ResidentPayments newInstance(String param1, String param2) {
        ResidentPayments fragment = new ResidentPayments();
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
        return inflater.inflate(R.layout.fragment_resident_payments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        //Read from database.
        myRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                //Get payment info from database.
                if (MainActivity.isVaad()) {
                    dataSnapshot.getChildren().forEach(user -> {
                        LinkedHashMap<String, Long> payments = new LinkedHashMap<>();
                        user.child("monthlyPayments").getChildren().forEach(month -> {
                            payments.put(month.getKey(), month.getValue(Long.class));
                        });
                        allPayments.add(payments);
                    });
                } else {
                    LinkedHashMap<String, Long> payments = new LinkedHashMap<>();
                    dataSnapshot.child(user.getUid()).child("monthlyPayments").getChildren().forEach(month -> {
                        payments.put(month.getKey(), month.getValue(Long.class));
                    });
                    allPayments.add(payments);
                    addInfoToResidentPayments(allPayments);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    //Put payments in
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addInfoToResidentPayments(ArrayList<LinkedHashMap<String, Long>> allPayments) {
        View view = this.getView();
        TableLayout table = view.findViewById(R.id.table);
        allPayments.forEach(paymentList -> {
            paymentList.entrySet().stream().forEach(entry -> {
                TableRow row = new TableRow(this.getContext());
                TextView key = new TextView(this.getContext());
                key.setText(entry.getKey());
                key.setTextSize(24);
                TextView value = new TextView(this.getContext());
                value.setText(entry.getValue() + "");
                value.setTextSize(24);
                row.addView(key);
                row.addView(value);

                int dip = 30;
                Resources r = getResources();
                int px = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        dip,
                        r.getDisplayMetrics()
                );
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) value.getLayoutParams();
                params.setMargins(px, 0, 0, 0);
                value.setLayoutParams(params);

                table.addView(row);
            });
        });
    }
}

