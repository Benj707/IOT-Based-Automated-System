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
    private TextView frequencyTextView;
    private TextView kwhTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_energy_meter);

        voltageTextView = findViewById(R.id.value1);
        currentTextView = findViewById(R.id.value2);
        frequencyTextView = findViewById(R.id.value3);
        kwhTextView = findViewById(R.id.value4);
        MaterialButton reading = (MaterialButton) findViewById(R.id.mReading);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Add a listener to check Firebase for changes
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get the value from Firebase
                String voltage = String.valueOf(dataSnapshot.child("voltage").getValue(Float.class));
                String current = String.valueOf(dataSnapshot.child("current").getValue(Float.class));
                String frequency = String.valueOf(dataSnapshot.child("power").getValue(Float.class));
                String kwh = String.valueOf(dataSnapshot.child("kWh").getValue(Float.class));

                voltageTextView.setText(voltage);
                currentTextView.setText(current);
                frequencyTextView.setText(frequency);
                kwhTextView.setText(kwh);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors
                voltageTextView.setText("Error loading data");
                currentTextView.setText("Error loading data");
                frequencyTextView.setText("Error loading data");
                kwhTextView.setText("Error loading data");
            }
        });

        reading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMonthlyMonitoring();
            }
        });

    }
    public void openMonthlyMonitoring(){
        Intent intent = new Intent(this, MonthlyMonitoring.class);
        startActivity(intent);
    }
}