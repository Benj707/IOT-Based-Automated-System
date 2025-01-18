package com.example.iot_basedautomatedsystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EnergyMeter extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private TextView voltageTextView;
    private TextView currentTextView;
    private TextView powerTextView;
    private TextView kwhTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_energy_meter);

        voltageTextView = findViewById(R.id.value1);
        currentTextView = findViewById(R.id.value2);
        powerTextView = findViewById(R.id.value3);
        kwhTextView = findViewById(R.id.value4);
        MaterialButton reading = (MaterialButton) findViewById(R.id.mReading);
        MaterialButton wreading = (MaterialButton) findViewById(R.id.wReading);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Add a listener to check Firebase for changes
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get the value from Firebase
                Float voltage = dataSnapshot.child("voltage").getValue(Float.class);
                Float current = dataSnapshot.child("current").getValue(Float.class);
                Float power = dataSnapshot.child("power").getValue(Float.class);
                Float kwh = dataSnapshot.child("kWh").getValue(Float.class);

                String formattedVoltageValue = String.format("%.1f", voltage);
                voltageTextView.setText(formattedVoltageValue);
                String formattedCurrentValue = String.format("%.1f", current);
                currentTextView.setText(formattedCurrentValue);
                String formattedPowerValue = String.format("%.1f", power);
                powerTextView.setText(formattedPowerValue);
                String formattedKwhValue = String.format("%.1f", kwh);
                kwhTextView.setText(formattedKwhValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors
                voltageTextView.setText("Error loading data");
                currentTextView.setText("Error loading data");
                powerTextView.setText("Error loading data");
                kwhTextView.setText("Error loading data");
            }
        });

        reading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMonthlyMonitoring();
            }
        });

        wreading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWeeklyMonitoring();
            }
        });

    }
    public void openWeeklyMonitoring(){
        Intent intent = new Intent(this, WeeklyMonitoring.class);
        startActivity(intent);
    }
    public void openMonthlyMonitoring(){
        Intent intent = new Intent(this, MonthlyMonitoring.class);
        startActivity(intent);
    }
}