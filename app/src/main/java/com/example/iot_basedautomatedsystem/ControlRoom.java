package com.example.iot_basedautomatedsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CompoundButton;

import android.annotation.SuppressLint;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ControlRoom extends AppCompatActivity {

    // Initialize Toggle Switch
    private Switch toggleSwitch1;
    private Switch toggleSwitch2;
    private Switch toggleSwitch3;
    private Switch toggleSwitch4;
//    private Switch toggleSwitch5;
//    private Switch toggleSwitch6;
//    private Switch toggleSwitch7;
//    private Switch toggleSwitch8;

    private DatabaseReference databaseReference;
    private TextView data1TextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_room);

        toggleSwitch1 = findViewById(R.id.toggleSwitch1);
        toggleSwitch2 = findViewById(R.id.toggleSwitch2);
        toggleSwitch3 = findViewById(R.id.toggleSwitch3);
        toggleSwitch4 = findViewById(R.id.toggleSwitch4);
//        toggleSwitch5 = findViewById(R.id.toggleSwitch5);
//        toggleSwitch6 = findViewById(R.id.toggleSwitch6);
//        toggleSwitch7 = findViewById(R.id.toggleSwitch7);
//        toggleSwitch8 = findViewById(R.id.toggleSwitch8);
        //data1TextView = findViewById(R.id.data1TextView);

        // Get reference to Firebase Database (for "toggle_value" node)
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Add a listener to check Firebase for changes
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get the value from Firebase
                //String data1 = dataSnapshot.child("sensorValue").getValue(String.class);
                String room1Status = dataSnapshot.child("room1").getValue(String.class);
                String room2Status = dataSnapshot.child("room2").getValue(String.class);
                String room3Status = dataSnapshot.child("room3").getValue(String.class);
//                String compLab1Status = dataSnapshot.child("compLab1").getValue(String.class);
//                String compLab2Status = dataSnapshot.child("compLab2").getValue(String.class);
                String facultyStatus = dataSnapshot.child("faculty").getValue(String.class);
//                String libraryStatus = dataSnapshot.child("library").getValue(String.class);
//                String lobbyStatus = dataSnapshot.child("lobby").getValue(String.class);

                // Toggle the switch based on the Firebase value
                if ("1".equals(room1Status)) {
                    toggleSwitch1.setChecked(true);
                } else {
                    toggleSwitch1.setChecked(false);
                }
                //toggleSwitch2
                if ("1".equals(room2Status)) {
                    toggleSwitch2.setChecked(true);
                } else {
                    toggleSwitch2.setChecked(false);
                }
                //toggleSwitch3
                if ("1".equals(room3Status)) {
                    toggleSwitch3.setChecked(true);
                } else {
                    toggleSwitch3.setChecked(false);
                }
                //toggleSwitch4
                if ("1".equals(facultyStatus)) {
                   toggleSwitch4.setChecked(true);
                } else {
                    toggleSwitch4.setChecked(false);
                }
//                //toggleSwitch5
//                if ("1".equals(compLab1Status)) {
//                    toggleSwitch5.setChecked(true);
//                } else {
//                    toggleSwitch5.setChecked(false);
//                }
//                //toggleSwitch6
//                if ("1".equals(compLab2Status)) {
//                    toggleSwitch6.setChecked(true);
//                } else {
//                    toggleSwitch6.setChecked(false);
//                }
//                //toggleSwitch7
//                if ("1".equals(libraryStatus)) {
//                    toggleSwitch7.setChecked(true);
//                } else {
//                    toggleSwitch7.setChecked(false);
//                }
//                //toggleSwitch8
//                if ("1".equals(lobbyStatus)) {
//                    toggleSwitch8.setChecked(true);
//                } else {
//                    toggleSwitch8.setChecked(false);
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors
                //data1TextView.setText("Error loading data");
            }
        });

        // Listen for user interaction with the Toggle Switch
        toggleSwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Update Firebase switchStatus to "1"
                    databaseReference.child("room1").setValue("1");
                } else {
                    // Update Firebase switchStatus to "2"
                    databaseReference.child("room1").setValue("2");
                }
            }
        });
        //toggleSwitch2
        toggleSwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Update Firebase switchStatus to "1"
                    databaseReference.child("room2").setValue("1");
                } else {
                    // Update Firebase switchStatus to "2"
                    databaseReference.child("room2").setValue("2");
                }
            }
        });
        //toggleSwitch3
        toggleSwitch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Update Firebase switchStatus to "1"
                    databaseReference.child("room3").setValue("1");
                } else {
                    // Update Firebase switchStatus to "2"
                    databaseReference.child("room3").setValue("2");
                }
            }
        });
        //toggleSwitch4
        toggleSwitch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Update Firebase switchStatus to "1"
                    databaseReference.child("faculty").setValue("1");
                } else {
                    // Update Firebase switchStatus to "2"
                    databaseReference.child("faculty").setValue("2");
                }
            }
        });
        //toggleSwitch5
//        toggleSwitch5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    // Update Firebase switchStatus to "1"
//                    databaseReference.child("compLab1").setValue("1");
//                } else {
//                    // Update Firebase switchStatus to "2"
//                    databaseReference.child("compLab1").setValue("2");
//                }
//            }
//        });
//        //toggleSwitch6
//        toggleSwitch6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    // Update Firebase switchStatus to "1"
//                    databaseReference.child("compLab2").setValue("1");
//                } else {
//                    // Update Firebase switchStatus to "2"
//                    databaseReference.child("compLab2").setValue("2");
//                }
//            }
//        });
//        //toggleSwitch7
//        toggleSwitch7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    // Update Firebase switchStatus to "1"
//                    databaseReference.child("library").setValue("1");
//                } else {
//                    // Update Firebase switchStatus to "2"
//                    databaseReference.child("library").setValue("2");
//                }
//            }
//        });
//        //toggleSwitch8
//        toggleSwitch8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    // Update Firebase switchStatus to "1"
//                    databaseReference.child("lobby").setValue("1");
//                } else {
//                    // Update Firebase switchStatus to "2"
//                    databaseReference.child("lobby").setValue("2");
//                }
//            }
//        });
    }
}