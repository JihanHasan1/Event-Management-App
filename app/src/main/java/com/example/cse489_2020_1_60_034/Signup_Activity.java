package com.example.cse489_2020_1_60_034;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Signup_Activity extends AppCompatActivity {
    private LinearLayout layoutName, layoutEmail, layoutPhone, layoutRePassword;
    private TextView tvTitle, tvAccountInfo;
    private EditText etName, etEmail, etPhone, etUserId, etPassword, etRePassword;
    private CheckBox cbRememberUserId, cbRememberLogin;
    private Button btnToggle;
    private String storedUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        layoutName = findViewById(R.id.layoutName);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutPhone = findViewById(R.id.layoutPhone);
        layoutRePassword = findViewById(R.id.layoutRePassword);

        tvAccountInfo = findViewById(R.id.tvAccountInfo);
        tvTitle = findViewById(R.id.tvTitle);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etUserId = findViewById(R.id.etUserId);
        etPassword = findViewById(R.id.etPassword);
        etRePassword = findViewById(R.id.etRePassword);

        cbRememberUserId = findViewById(R.id.cbRememberUserId);
        cbRememberLogin = findViewById(R.id.cbRememberLogin);
        btnToggle = findViewById(R.id.btnToggle);

        SharedPreferences sp = getSharedPreferences("user_account", MODE_PRIVATE);
        String storedUserId = sp.getString("signupUserId", "");
        String storedEmail = sp.getString("email", "");

        boolean isUserIdChecked = sp.getBoolean("rem_userId", false);
        boolean isLoginChecked = sp.getBoolean("rem_login", false);

        if(isLoginChecked){
            Intent i = new Intent(Signup_Activity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
        if (isUserIdChecked) {
            //Now user is at Login Page
            tvTitle.setText("Login");
            btnToggle.setText("SignUp");
            tvAccountInfo.setText("Don't have an account?");
            layoutName.setVisibility(View.GONE);
            layoutEmail.setVisibility(View.GONE);
            layoutPhone.setVisibility(View.GONE);
            layoutRePassword.setVisibility(View.GONE);
            etUserId.setText(storedUserId);
            etPassword.setText("");
        }

        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toggleValue = btnToggle.getText().toString();

                if (toggleValue.equalsIgnoreCase("Login")) {
                    // At SignUp Page now. But will go to Login Page
                    tvTitle.setText("Login");
                    btnToggle.setText("SignUp");
                    tvAccountInfo.setText("Don't have an account?");
                    layoutName.setVisibility(View.GONE);
                    layoutEmail.setVisibility(View.GONE);
                    layoutPhone.setVisibility(View.GONE);
                    layoutRePassword.setVisibility(View.GONE);
                } else {
                    // At Login Page now. But will go to SignUp page
                    tvTitle.setText("SignUp");
                    btnToggle.setText("Login");
                    tvAccountInfo.setText("Already have account?");
                    layoutName.setVisibility(View.VISIBLE);
                    layoutEmail.setVisibility(View.VISIBLE);
                    layoutPhone.setVisibility(View.VISIBLE);
                    layoutRePassword.setVisibility(View.VISIBLE);
                    etName.setText("");
                    etEmail.setText("");
                    etPhone.setText("");
                    etRePassword.setText("");
                    etUserId.setText("");
                    etPassword.setText("");
                }
            }
        });

        findViewById(R.id.btnExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btnGo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toggleValue = btnToggle.getText().toString();
                String userId = etUserId.getText().toString();
                String password = etPassword.getText().toString();
                boolean isUserIdChecked = cbRememberUserId.isChecked();
                boolean isLoginChecked = cbRememberLogin.isChecked();

                if (toggleValue.equalsIgnoreCase("Login")) {
                    // Process all data for signup
                    String name = etName.getText().toString();
                    String email = etEmail.getText().toString();
                    String phone = etPhone.getText().toString();
                    String rePass = etRePassword.getText().toString();

                    // Data Validation

                    String errMsg = "";

                    if (name.equals("")) {
                        errMsg += "Name Missing !\n";
                    }
                    if (email.equals("")) {
                        errMsg += "Email Missing !\n";
                    }
                    if (phone.equals("")) {
                        errMsg += "Phone No. Missing !\n";
                    }
                    if (userId.equals("")) {
                        errMsg += "User ID Missing !\n";
                    }
                    if (password.equals("")) {
                        errMsg += "Password Missing !\n";
                    }
                    if (rePass.equals("")) {
                        errMsg += "Please Re-enter Password !";
                    }
                    if (!password.equals(rePass) && !rePass.equals("")) {
                        errMsg += "Password Didn't Match !";
                    }

                    if (errMsg.isEmpty()) {
                        // Store data to SharedPreference
                        SharedPreferences sp = getSharedPreferences("user_account", MODE_PRIVATE);
                        SharedPreferences.Editor spEditor = sp.edit();
                        spEditor.putString("name", userId);
                        spEditor.putString("email", password);
                        spEditor.putString("phone", email);
                        spEditor.putString("signupUserId", userId);
                        spEditor.putString("signupPassword", password);

                        spEditor.putBoolean("rem_userId", isUserIdChecked);
                        spEditor.putBoolean("rem_login", isLoginChecked);
                        spEditor.apply();

                        Intent i = new Intent(Signup_Activity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(Signup_Activity.this, errMsg, Toast.LENGTH_LONG).show();
                    }

                } else {
                    // Process all data for login
                    SharedPreferences sp = getSharedPreferences("user_account", MODE_PRIVATE);
                    String storedUserId = sp.getString("signupUserId", "");
                    String storedPassword = sp.getString("signupPassword", "");
                    String errMsg = "";

                    if (userId.equals("")) {
                        errMsg += "Please Input User ID !\n";
                    }
                    if (!userId.equals(storedUserId) && !userId.equals("")) {
                        errMsg += "UserId didn't exist\n";
                    }
                    if (password.equals("")) {
                        errMsg += "Please Input Password !";
                    }
                    if (!password.equals(storedPassword) && !password.equals("")) {
                        errMsg += "Password didn't match";
                    }
                    // if everything OKAY
                    if (errMsg.isEmpty()) {
                        SharedPreferences.Editor spEditor = sp.edit();
                        spEditor.putBoolean("rem_login", isLoginChecked);
                        spEditor.apply();

                        Intent i = new Intent(Signup_Activity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(Signup_Activity.this, errMsg, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }
    public void onStart(){
        super.onStart();

    }
}