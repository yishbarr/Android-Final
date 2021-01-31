package com.example.androidfinal.fragments;

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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaymentSummary#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentSummary extends Fragment {
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

    public PaymentSummary() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PaymentSummary.
     */
    // TODO: Rename and change types and number of parameters
    public static PaymentSummary newInstance(String param1, String param2) {
        PaymentSummary fragment = new PaymentSummary();
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
        return inflater.inflate(R.layout.fragment_payment_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TableLayout table = view.findViewById(R.id.vaadPaymentTable);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        HashMap<Long, HashMap<String, Long>> allPayments = new HashMap<>();
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                //Get flat numbers
                dataSnapshot.getChildren().forEach(user -> {
                    if (user.child("userType").getValue(String.class).contentEquals("resident")) {
                        HashMap<String, Long> payments = new HashMap<>();
                        user.child("monthlyPayments").getChildren().forEach(month -> {
                            payments.put(month.getKey(), month.getValue(Long.class));
                        });
                        allPayments.put(user.child("addressNumber").getValue(Long.class), payments);
                    }
                });
                addInfoToPayments(allPayments);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addInfoToPayments(HashMap<Long, HashMap<String, Long>> allPayments) {
        View view = this.getView();
        TableLayout table = view.findViewById(R.id.allPaymentTable);
        //Count sums for each month
        int[] sums = new int[12];
        for (int i = 0; i < sums.length; i++)
            sums[i] = 0;

        //Convert dp to px
        int dip = 10;
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );

        //Sort by flat numbers
        ArrayList<TableRow> sorter = new ArrayList<>();
        allPayments.keySet().stream().forEach(topKey -> {
            TextView[] orderPayments = new TextView[13];
            TableRow row = new TableRow(this.getContext());
            allPayments.get(topKey).keySet().forEach(innerKey -> {
                TextView value = new TextView(this.getContext());
                int num = Math.toIntExact(allPayments.get(topKey).get(innerKey));
                value.setText(num + "");

                switch (innerKey) {
                    case "January":
                        orderPayments[1] = value;
                        sums[0] += num;
                        break;
                    case "Febuary":
                        orderPayments[2] = value;
                        sums[1] += num;
                        break;

                    case "March":
                        orderPayments[3] = value;
                        sums[2] += num;
                        break;

                    case "April":
                        orderPayments[4] = value;
                        sums[3] += num;
                        break;

                    case "May":
                        orderPayments[5] = value;
                        sums[4] += num;
                        break;

                    case "June":
                        orderPayments[6] = value;
                        sums[5] += num;
                        break;

                    case "July":
                        orderPayments[7] = value;
                        sums[6] += num;
                        break;

                    case "August":
                        orderPayments[8] = value;
                        sums[7] += num;
                        break;

                    case "September":
                        orderPayments[9] = value;
                        sums[8] += num;
                        break;

                    case "October":
                        orderPayments[10] = value;
                        sums[9] += num;
                        break;

                    case "November":
                        orderPayments[11] = value;
                        sums[10] += num;
                        break;

                    case "December":
                        orderPayments[12] = value;
                        sums[11] += num;
                        break;
                }

            });
            TextView flatNum = new TextView(this.getContext());

            flatNum.setText(topKey + "");
            orderPayments[0] = flatNum;
            for (TextView value : orderPayments) {
                if (value.getParent() != null)
                    ((ViewGroup) value.getParent()).removeView(value);
                row.addView(value);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) value.getLayoutParams();
                params.setMargins(px, 0, 0, 0);
                value.setLayoutParams(params);
                value.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
            if (row.getParent() != null)
                ((ViewGroup) row.getParent()).removeView(row);

            int flatNumIndex = Math.toIntExact(topKey);
            while (sorter.size() < flatNumIndex)
                sorter.add(null);
            sorter.add(flatNumIndex, row);
        });

        for (TableRow row : sorter) {
            if (row != null)
                table.addView(row);
        }

        //Add sums to table
        TableRow sumsRow = new TableRow(this.getContext());
        TextView[] sumsRowTexts = new TextView[13];
        TextView all = new TextView(this.getContext());
        all.setText("Sums");
        sumsRowTexts[0] = all;
        for (int i = 0; i < sums.length; i++) {
            TextView num = new TextView(this.getContext());
            num.setText(sums[i] + "");
            sumsRowTexts[i + 1] = num;
        }
        for (TextView num : sumsRowTexts) {
            if (num.getParent() != null)
                ((ViewGroup) num.getParent()).removeView(num);
            sumsRow.addView(num);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) num.getLayoutParams();
            params.setMargins(px, 0, 0, 0);
            num.setLayoutParams(params);
            num.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        table.addView(sumsRow);

    }
}