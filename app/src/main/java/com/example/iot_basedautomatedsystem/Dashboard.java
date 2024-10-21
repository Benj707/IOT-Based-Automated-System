package com.example.iot_basedautomatedsystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;

public class Dashboard extends AppCompatActivity {

    MaterialButton logout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        CardView controlRoom = (CardView) findViewById(R.id.controlRoom);
        CardView energyMeter = (CardView) findViewById(R.id.energyMeter);
        logout = (MaterialButton) findViewById(R.id.logout);

        controlRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openControlRoom();
            }
        });

        energyMeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEnergyMeter();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainActivity();
            }
        });


    }
    public void openControlRoom(){
        Intent intent = new Intent(this, ControlRoom.class);
        startActivity(intent);
    }
    public void openEnergyMeter(){
        Intent intent = new Intent(this, EnergyMeter.class);
        startActivity(intent);
    }
    public void openMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}