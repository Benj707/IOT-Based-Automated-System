package com.example.iot_basedautomatedsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    EditText username, password;
    TextView register, forgot;
    MaterialButton login_btn;
    DBManager DB;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login_btn = (MaterialButton) findViewById(R.id.login_btn);
        register = (TextView) findViewById(R.id.register);
        forgot = (TextView) findViewById(R.id.forgotpass);
        DB = new DBManager(this);

        //set click event
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user = username.getText().toString();
                String pass = password.getText().toString();

                if(user.equals("")||pass.equals(""))
                    Toast.makeText(MainActivity.this, "Please Fill-up All Fields", Toast.LENGTH_SHORT).show();
                else{
                    Boolean checkuserpass = DB.checkUsernamePass(user, pass);
                    if(checkuserpass==true){
                        Toast.makeText(MainActivity.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();

                        openDashboard();
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openForgotPass();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddUserPage();
            }
        });


    }

    public void openDashboard(){
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
        finish();
    }

    public void openForgotPass(){
        Intent intent = new Intent(this, ForgotPass.class);
        startActivity(intent);
    }

    public void openAddUserPage(){
        Intent intent = new Intent(this, AddUserPage.class);
        startActivity(intent);
    }
}