package com.example.androidfinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;

import com.example.androidfinal.fragments.PasswordSet;
import com.example.androidfinal.fragments.PaymentSummary;
import com.example.androidfinal.fragments.ResidentPayments;
import com.example.androidfinal.fragments.VaadPayments;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FragmentManager frags;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ViewPager viewPager;
    private boolean isVaad;
    private FragmentStatePagerAdapter pagerAdapter;

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
        myRef = database.getReference("users").child(user.getUid()).child("userType");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                if (value.contentEquals("vaad")) {
                    isVaad = true;
                }
                createTabs();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });


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

    //Change fragment by button
    /*private void changeFragment(int id) {
        FragmentTransaction transaction = frags.beginTransaction();
        switch (id) {
            case 0: {
                transaction.add(R.id.fragmentFrame, new PasswordSet()).commit();
            }
            break;
            case 1: {
                if (isVaad)
                    transaction.add(R.id.fragmentFrame, new ResidentPayments()).commit();
                else
                    transaction.add(R.id.fragmentFrame, new VaadPayments()).commit();
            }
            break;
            case 2: {
                transaction.add(R.id.fragmentFrame, new ResidentPayments()).commit();
            }
            break;
        }
    }*/

    /*private void database() {
        myRef = database.getReference("users").child(user.getUid()).child("monthlyPayments");
        HashMap<String, Long> payments = new HashMap<>();
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                dataSnapshot.getChildren().forEach((month) -> {
                    payments.put(month.getKey(), month.getValue(Long.class))
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }*/
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