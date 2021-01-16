package com.example.androidfinal.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.androidfinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PasswordSet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PasswordSet extends Fragment {

    private static TextView message;
    private static TextView passwordText;
    private static FirebaseUser user;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PasswordSet() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PasswordSet.
     */
    // TODO: Rename and change types and number of parameters
    public static PasswordSet newInstance(String param1, String param2) {
        PasswordSet fragment = new PasswordSet();
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
        return inflater.inflate(R.layout.fragment_password_set, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        message = view.findViewById(R.id.passwordChangedMessage);
        passwordText = view.findViewById(R.id.passwordSet);
    }

    private static void errorMessage(String errorMessage) {
        message.setVisibility(View.VISIBLE);
        message.setTextColor(Color.RED);
        message.setText(errorMessage);
    }

    public static void ChangePassword(View view) {
        try {
            user = FirebaseAuth.getInstance().getCurrentUser();
            String password = passwordText.getText().toString();

            user.updatePassword(password)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            message.setVisibility(View.VISIBLE);
                            if (task.isSuccessful()) {
                                message.setTextColor(Color.WHITE);
                                message.setText("Password Changed");
                            } else {
                                errorMessage("Failed to change password.");
                            }
                        }
                    });
        } catch (Exception e) {
            errorMessage("Failed to change password.");
        }


    }
}