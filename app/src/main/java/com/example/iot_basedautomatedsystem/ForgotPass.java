package com.example.iot_basedautomatedsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class ForgotPass extends AppCompatActivity {

    EditText username, new_password, confirm_new_pass;
    MaterialButton done_btn;
    DBManager DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        username = (EditText) findViewById(R.id.username2);
        new_password = (EditText) findViewById(R.id.new_password);
        confirm_new_pass = (EditText) findViewById(R.id.confirm_new_pass);
        done_btn = (MaterialButton) findViewById(R.id.done_btn);

        DB = new DBManager(this);

        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user = username.getText().toString();
                String new_pass = new_password.getText().toString();
                String confirm_pass = confirm_new_pass.getText().toString();

                if (user.equals("") || new_pass.equals("") || confirm_pass.equals(""))
                    Toast.makeText(ForgotPass.this, "Please Fill-up All Fields", Toast.LENGTH_SHORT).show();
                else if (new_pass.equals(confirm_pass)) {
                    Boolean checkuser = DB.checkUserName(user);
                    if (checkuser == true) {
                        Boolean checkpass = DB.updatePass(user, new_pass);
                        if (checkpass == true) {

                            openMainActivity();

                            Toast.makeText(ForgotPass.this, "Reset Sucessful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForgotPass.this, "Reset Failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ForgotPass.this, "User Does Not Exist", Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(ForgotPass.this, "Password Doesn't Match", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}