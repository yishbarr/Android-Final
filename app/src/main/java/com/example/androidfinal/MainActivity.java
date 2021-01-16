package com.example.androidfinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.androidfinal.fragments.PasswordSet;
import com.example.androidfinal.fragments.PaymentSummary;
import com.example.androidfinal.fragments.ResidentPayments;
import com.example.androidfinal.fragments.VaadPayments;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FragmentManager frags;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ViewPager viewPager;
    private boolean isVaad;
    private FragmentStatePagerAdapter pagerAdapter;
    private ArrayList<LinkedHashMap<String, Long>> allPayments = new ArrayList<>();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup Initial fragment
        frags = getSupportFragmentManager();
        FragmentTransaction transaction = frags.beginTransaction();
        transaction.add(R.id.fragmentFrame, new PasswordSet()).commit();

        //Check if user is Vaad or Resident
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.child(user.getUid()).child("userType").getValue(String.class);
                if (value.contentEquals("vaad")) {
                    isVaad = true;
                }
                createTabs();
                //Get payment info from database.
                /*if (isVaad) {
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
                }
                addInfoToFragments();*/
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });


    }

    private void addInfoToFragments() {

    }

    private void createTabs() {
        //Create appropriate tabs.
        TabLayout tabs;
        if (isVaad) {
            tabs = findViewById(R.id.vaadTabs);
            tabs.setVisibility(View.VISIBLE);
            findViewById(R.id.residentTabs).setVisibility(View.GONE);
        } else
            tabs = findViewById(R.id.residentTabs);

        //Setup fragments
        viewPager = findViewById(R.id.fragmentFrame);

        //Fragment manager
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabs.getTabCount(), isVaad);
        viewPager.setAdapter(pagerAdapter);
        tabs.setupWithViewPager(viewPager);
        //Add tab events
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    public void ChangePassword(View view) {
        PasswordSet.ChangePassword(view);
    }

}

class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private boolean isVaad;
    private int tabCount;


    public ViewPagerAdapter(@NonNull FragmentManager fm, int tabCount, boolean isVaad) {
        super(fm);
        this.isVaad = isVaad;
        this.tabCount = tabCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PasswordSet();
            case 1:
                if (isVaad)
                    return new VaadPayments();
                else
                    return new ResidentPayments();
            case 2:
                return new PaymentSummary();
            default:
                return new PasswordSet();
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Change Password";
            case 1:
                if (isVaad)
                    return "Resident Payments";
                else
                    return "Payments";
            case 2:
                return "Payment Summary";
        }
        return "";
    }
}