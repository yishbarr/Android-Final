package com.example.androidfinal.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.androidfinal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IndividualPayments#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IndividualPayments extends Fragment {

    private static Context context;
    private static int px;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private static HashMap<Long, HashMap<String, Long>> allPayments;
    private static TableLayout table;
    private static TextView flatChoiceText;
    private static TextView message;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public IndividualPayments() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IndividualPayments.
     */
    // TODO: Rename and change types and number of parameters
    public static IndividualPayments newInstance(String param1, String param2) {
        IndividualPayments fragment = new IndividualPayments();
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
        return inflater.inflate(R.layout.fragment_individual_payments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        table = view.findViewById(R.id.individualTable);
        flatChoiceText = view.findViewById(R.id.flatChoice);
        message = view.findViewById(R.id.flatErrorMessage);
        allPayments = new HashMap<>();
        //Convert dp to px
        int dip = 30;
        Resources r = getResources();
        px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );

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
                Iterable<DataSnapshot> myUsersSnap = dataSnapshot.child(user.getUid()).child("residents").getChildren();
                ArrayList<String> myUsers = new ArrayList<>();
                myUsersSnap.forEach(user -> myUsers.add(user.getKey()));
                //Get flat numbers
                dataSnapshot.getChildren().forEach(user -> {
                    if (myUsers.contains(user.getKey())) {
                        HashMap<String, Long> payments = new HashMap<>();
                        user.child("monthlyPayments").getChildren().forEach(month -> {
                            payments.put(month.getKey(), month.getValue(Long.class));
                        });
                        allPayments.put(user.child("addressNumber").getValue(Long.class), payments);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void getPayments(View view) {
        int flatChoice = 0;
        try {
            flatChoice = Integer.parseInt(flatChoiceText.getText().toString());

            HashMap<String, Long> payments = null;
            for (Map.Entry<Long, HashMap<String, Long>> entry : allPayments.entrySet()) {
                int flat = Math.toIntExact(entry.getKey());
                if (flat == flatChoice) {
                    payments = entry.getValue();
                    break;
                }
            }
            if (payments != null) {
                message.setVisibility(View.INVISIBLE);
                addInfoToPayments(payments);
            } else
                errorMessage("Flat doesn't exist.");
        } catch (NumberFormatException e) {
            errorMessage("Flat must be number.");
        }
    }

    public static void errorMessage(String errorMessage) {
        message.setVisibility(View.VISIBLE);
        message.setTextColor(Color.RED);
        message.setText(errorMessage);
    }

    //Put payments in
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void addInfoToPayments(HashMap<String, Long> payments) {
        while (table.getChildAt(1) != null)
            table.removeViewAt(1);

        TableRow[] items = new TableRow[12];

        payments.entrySet().stream().forEach(entry -> {
            TableRow row = new TableRow(context);
            TextView key = new TextView(context);
            key.setText(entry.getKey());
            key.setTextSize(24);
            TextView value = new TextView(context);
            value.setText(entry.getValue() + "");
            value.setTextSize(24);
            row.addView(key);
            row.addView(value);


            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) value.getLayoutParams();
            params.setMargins(px, 0, 0, 0);
            value.setLayoutParams(params);

            switch (entry.getKey()) {
                case "January":
                    items[0] = row;
                    break;
                case "Febuary":
                    items[1] = row;
                    break;

                case "March":
                    items[2] = row;
                    break;

                case "April":
                    items[3] = row;
                    break;

                case "May":
                    items[4] = row;
                    break;

                case "June":
                    items[5] = row;
                    break;

                case "July":
                    items[6] = row;
                    break;

                case "August":
                    items[7] = row;
                    break;

                case "September":
                    items[8] = row;
                    break;

                case "October":
                    items[9] = row;
                    break;

                case "November":
                    items[10] = row;
                    break;

                case "December":
                    items[11] = row;
                    break;

            }
        });

        for (TableRow row : items) {
            if (row.getParent() != null)
                ((ViewGroup) row.getParent()).removeView(row);
            table.addView(row);
        }
    }
}