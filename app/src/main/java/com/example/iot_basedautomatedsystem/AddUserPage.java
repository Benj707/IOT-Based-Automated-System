package com.example.iot_basedautomatedsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class AddUserPage extends AppCompatActivity {

    EditText username, password, confirmpass;
    MaterialButton register_btn;
    DBManager DB;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_page);

        username = (EditText) findViewById(R.id.username1);
        password = (EditText) findViewById(R.id.password1);
        confirmpass = (EditText) findViewById(R.id.confirmpass);
        register_btn = (MaterialButton) findViewById(R.id.register_btn);
        DB = new DBManager(this);

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();
                String confirm = confirmpass.getText().toString();

                if(user.equals("")||pass.equals("")||confirm.equals(""))
                    Toast.makeText(AddUserPage.this, "Please Fill-up All Fields", Toast.LENGTH_SHORT).show();
                else
                if(pass.equals(confirm)){
                    Boolean checkuser = DB.checkUserName(user);
                    if(checkuser==false){
                        Boolean insert = DB.insertData(user, pass);
                        if(insert==true){
                            Toast.makeText(AddUserPage.this, "Succesfully Added", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(AddUserPage.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(AddUserPage.this, "User Already Exists! Try Logging In", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(AddUserPage.this, "Password Doesn't Match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}