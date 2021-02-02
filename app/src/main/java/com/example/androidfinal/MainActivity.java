package com.example.androidfinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.androidfinal.fragments.IndividualPayments;
import com.example.androidfinal.fragments.PasswordSet;
import com.example.androidfinal.fragments.PaymentSummary;
import com.example.androidfinal.fragments.ResidentPayments;
import com.example.androidfinal.fragments.VaadPayments;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private FragmentManager frags;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ViewPager viewPager;
    private static boolean isVaad;
    private FragmentStatePagerAdapter pagerAdapter;
    private ValueEventListener listener;

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

        listener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.child(user.getUid()).child("userType").getValue(String.class);
                if (value.contentEquals("vaad")) {
                    isVaad = true;
                } else
                    isVaad = false;
                createTabs();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        };
        // Read from the database
        myRef.addValueEventListener(listener);


    }

    public static boolean isVaad() {
        return isVaad;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void createTabs() {
        //Create appropriate tabs.
        TabLayout tabs;
        if (isVaad) {
            tabs = findViewById(R.id.vaadTabs);
            tabs.setVisibility(View.VISIBLE);
            findViewById(R.id.residentTabs).setVisibility(View.GONE);
        } else {
            tabs = findViewById(R.id.residentTabs);
            tabs.setVisibility(View.VISIBLE);
            findViewById(R.id.vaadTabs).setVisibility(View.GONE);

        }

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
                if (tab.getPosition() == 2)
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                else
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        myRef.removeEventListener(listener);
    }

    public void ChangePassword(View view) {
        PasswordSet.ChangePassword(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void ConfirmPayment(View view) {
        VaadPayments.ConfirmPayment(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getPayments(View view) {
        IndividualPayments.getPayments(view);
    }

}

class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private boolean isVaad;
    private int tabCount;
    private Activity activity;


    public ViewPagerAdapter(@NonNull FragmentManager fm, int tabCount, boolean isVaad) {
        super(fm);
        this.isVaad = isVaad;
        this.tabCount = tabCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                if (isVaad)
                    return new VaadPayments();
                else
                    return new ResidentPayments();
            case 2:
                return new PaymentSummary();
            case 3:
                return new IndividualPayments();
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
            case 3:
                return "Individual Payments";
        }
        return "";
    }

}