package com.example.moviedb.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.moviedb.Database.SqliteHelper;
import com.example.moviedb.Database.User;
import com.example.moviedb.R;

public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragment";
    private EditText username;
    private EditText password;
    private Button register;
    private Boolean isEmptyString = true;
    private SqliteHelper sqliteHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initComponents(view);
    }

    private void initComponents(View view){
        username = view.findViewById(R.id.usernameEditText);
        password = view.findViewById(R.id.passwordEditText);
        register = view.findViewById(R.id.registerButton);
        sqliteHelper = new SqliteHelper(getContext());
        registerButtonOnClickListener();
    }

    private void registerButtonOnClickListener(){
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEmptyString = verifyEmptyStrings();
                if(isEmptyString){
                    Toast.makeText(getContext(),"Username or Password is missing", Toast.LENGTH_SHORT).show();
                }
                else {
                    Boolean exists = false;
                    exists = sqliteHelper.isUserNameExists(username.getText().toString().trim());
                    if(exists){
                        Toast.makeText(getContext(), "This username already exists", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Log.e(TAG, "addUser " );
                        sqliteHelper.addUser(new User(null,username.getText().toString().trim(),password.getText().toString().trim()));
                        toLogInFragment();
                    }
                }
            }
        });
    }

    private boolean verifyEmptyStrings() {
        if ((username.getText().toString().trim().isEmpty()) ||
                (password.getText().toString().trim().isEmpty())) {
            return true;
        } else {
            return false;
        }
    }

    private void toLogInFragment() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, new LogInFragment(), null);
        fragmentTransaction.commit();
    }
}
