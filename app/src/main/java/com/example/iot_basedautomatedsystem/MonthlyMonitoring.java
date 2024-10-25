package com.example.iot_basedautomatedsystem;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import androidx.core.content.ContextCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonthlyMonitoring extends AppCompatActivity {

    private LineChart lineChart;
    private List<String> monthLabels;
    private List<Entry> entries;  // To store the entries for the graph
    private Map<Integer, String> monthMap; // For dynamic month mapping

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_monitoring);

        // Initialize LineChart
        lineChart = findViewById(R.id.lineChart);

        // Initialize entries list
        entries = new ArrayList<>();

        // Initialize month labels and monthMap
        monthLabels = new ArrayList<>();
        monthMap = new HashMap<>();

        // Fetch data from Firebase and plot the graph
        fetchDataFromFirebase();
    }

    private void fetchDataFromFirebase() {
        // Reference to Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("monthlyReading");

        // Attach a listener to read the data for each month
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int monthIndex = 0;  // Index for placing entries in the graph

                // Iterate over the months in the Firebase "readings" node
                for (DataSnapshot monthSnapshot : dataSnapshot.getChildren()) {
                    // Example: monthKey = "2024-08"
                    String monthKey = monthSnapshot.getKey();  // Get the key like "2024-08"
                    if (monthKey != null) {
                        String[] parts = monthKey.split("-");  // Split "2024-08" into ["2024", "08"]
                        int monthNumber = Integer.parseInt(parts[1]);  // Extract the month number (e.g., 8 for August)

                        // Convert the month number into month name
                        String monthName = getMonthName(monthNumber);
                        monthLabels.add(monthName);  // Add the month name (e.g., "August") to the labels

                        // Fetch the "voltage" value for the month
                        Float energy = monthSnapshot.child("totalEnergy").getValue(Float.class);

                        // Add the data to the graph entries
                        if (energy != null) {
                            entries.add(new Entry(monthIndex, energy));  // Use the monthIndex as the X value and voltage as Y value
                            monthIndex++;  // Increment the month index for the next entry
                            Toast.makeText(getApplicationContext(), "DATA SUCCESSFULLY RETRIEVED", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                // Once data is fetched, plot the graph
                plotDataOnGraph();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching data", databaseError.toException());
                Toast.makeText(getApplicationContext(), "FAILED TO RETRIEVE DATA", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getMonthName(int monthNumber) {
        // A method to convert month number to month name
        switch (monthNumber) {
            case 1: return "January";
            case 2: return "February";
            case 3: return "March";
            case 4: return "April";
            case 5: return "May";
            case 6: return "June";
            case 7: return "July";
            case 8: return "August";
            case 9: return "September";
            case 10: return "October";
            case 11: return "November";
            case 12: return "December";
            default: return "";
        }
    }

    private void plotDataOnGraph() {
        // Create dataset
        LineDataSet dataSet = new LineDataSet(entries, "Monthly Reading Data");
        dataSet.setColor(ContextCompat.getColor(this, R.color.red)); // Set color
        dataSet.setValueTextSize(12f);

        // Create a data object with the dataset
        LineData lineData = new LineData(dataSet);

        // Set the data to the chart
        lineChart.setData(lineData);
        lineChart.invalidate();  // Refresh the chart

        // Set X-axis behavior
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(monthLabels));  // Set dynamic month labels
        xAxis.setGranularity(1f);  // Ensure steps of 1
        xAxis.setGranularityEnabled(true);  // Ensure granularity is applied
        xAxis.setLabelCount(monthLabels.size());  // Set label count dynamically based on months
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  // Position the X-axis at the bottom

    }
}