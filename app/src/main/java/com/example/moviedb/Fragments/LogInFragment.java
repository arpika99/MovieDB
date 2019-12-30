package com.example.moviedb.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.moviedb.Activities.HomeActivity;
import com.example.moviedb.Database.SqliteHelper;
import com.example.moviedb.Database.User;
import com.example.moviedb.R;

public class LogInFragment extends Fragment {

    private static final String TAG = "LogInFragment";
    private Button logInButton;
    private EditText username;
    private EditText password;
    private TextView register;
    private SqliteHelper sqliteHelper;
    private boolean isEmptyEditText = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initComponents(view);
        initListeners();
    }

    //initializing components
    private void initComponents(View view)
    {
        logInButton = view.findViewById(R.id.logInButton);
        username = view.findViewById(R.id.usernameEditText);
        password = view.findViewById(R.id.passwordEditText);
        register = view.findViewById(R.id.registerTextView);
        sqliteHelper = new SqliteHelper(getContext());
    }

    private void initListeners(){
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEmptyEditText = verifyEmptyStrings();
                if (isEmptyEditText) {
                    Toast.makeText(getContext(), "Username or password is missing", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (sqliteHelper.Authenticate(new User(null,String.valueOf(username.getText()).trim(),
                            String.valueOf(password.getText()).trim()))) {

                        password.setText("");
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        getActivity().startActivity(intent);
                    }
                    else{
                        Toast.makeText(getContext(), "Wrong username or password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toRegisterFragment();
            }
        });
    }

    private boolean verifyEmptyStrings(){
        if ((username.getText().toString().trim().isEmpty()) ||
                (password.getText().toString().trim().isEmpty())) {
            return true;
        }else {
            return false;
        }
    }

    private void toRegisterFragment() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frameLayout, new RegisterFragment(), null);
        fragmentTransaction.commit();
    }
}
