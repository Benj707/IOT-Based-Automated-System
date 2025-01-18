package com.example.iot_basedautomatedsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import androidx.core.content.ContextCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonthlyMonitoring extends AppCompatActivity {

    private BarChart barChart;
    private List<String> monthLabels;
    private List<BarEntry> entries;
    private Map<Integer, String> monthMap;
    private TextView yearTextView; // To display the year below the chart

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_monitoring);

        // Initialize BarChart
        barChart = findViewById(R.id.barChart);

        // Initialize entries list
        entries = new ArrayList<>();

        // Initialize month labels and monthMap
        monthLabels = new ArrayList<>();
        monthMap = new HashMap<>();

        // Initialize year text view
        yearTextView = findViewById(R.id.yearTextView);

        // Set the current year below the chart
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int lastYear = sharedPreferences.getInt("lastYear", -1);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearTextView.setText("Year: " + currentYear);

        // Check if the year has changed
        if (lastYear != currentYear) {
            resetFirebaseData();
            // Update the stored year
            sharedPreferences.edit().putInt("lastYear", currentYear).apply();
        }

        // Fetch data from Firebase and plot the graph
        fetchDataFromFirebase();
    }

    private void resetFirebaseData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("monthlyReading");

        // Clear all data in the "monthlyReading" node
        databaseReference.setValue(null).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Data reset for the new year", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to reset data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDataFromFirebase() {
        // Reference to Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("monthlyReading");

        // Attach a listener to read the data for each month
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int monthIndex = 0;

                // Iterate over the months in the Firebase "readings" node
                for (DataSnapshot monthSnapshot : dataSnapshot.getChildren()) {
                    String monthKey = monthSnapshot.getKey();  // Example: "2024-08"
                    if (monthKey != null) {
                        String[] parts = monthKey.split("-");
                        int monthNumber = Integer.parseInt(parts[1]);  // Get month number

                        // Convert the month number into month name
                        String monthName = getMonthName(monthNumber);
                        monthLabels.add(monthName);

                        // Fetch the "totalEnergy" value for the month
                        Float energy = monthSnapshot.child("totalEnergy").getValue(Float.class);

                        // Add data to graph entries
                        if (energy != null) {
                            entries.add(new BarEntry(monthIndex, energy));
                            monthIndex++;
                            Toast.makeText(getApplicationContext(), "DATA SUCCESSFULLY RETRIEVED", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                // Plot the data on the graph
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
        switch (monthNumber) {
            case 1: return "JAN";
            case 2: return "FEB";
            case 3: return "MAR";
            case 4: return "APR";
            case 5: return "MAY";
            case 6: return "JUN";
            case 7: return "JUL";
            case 8: return "AUG";
            case 9: return "SEP";
            case 10: return "OCT";
            case 11: return "NOV";
            case 12: return "DEC";
            default: return "";
        }
    }

    private void plotDataOnGraph() {
        // Create BarDataSet
        BarDataSet barDataSet = new BarDataSet(entries, "Monthly Reading Data");

        // Define assorted colors for each month
        List<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(this, R.color.red));
        colors.add(ContextCompat.getColor(this, R.color.blue));
        colors.add(ContextCompat.getColor(this, R.color.green));
        colors.add(ContextCompat.getColor(this, R.color.yellow));
        colors.add(ContextCompat.getColor(this, R.color.orange));
        colors.add(ContextCompat.getColor(this, R.color.purple));
        colors.add(ContextCompat.getColor(this, R.color.cyan));
        colors.add(ContextCompat.getColor(this, R.color.magenta));
        colors.add(ContextCompat.getColor(this, R.color.light_blue));
        colors.add(ContextCompat.getColor(this, R.color.brown));
        colors.add(ContextCompat.getColor(this, R.color.pink));
        colors.add(ContextCompat.getColor(this, R.color.teal));

        // Apply colors to dataset
        barDataSet.setColors(colors);
        barDataSet.setValueTextSize(12f);

        // Apply a custom formatter to display values with two decimal points
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return String.format("%.2f", barEntry.getY());
            }
        });

        // Create BarData with the dataset
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);

        // Set data to the chart
        barChart.setData(barData);
        barChart.setFitBars(true);  // Ensure the bars fit within the X-axis
        barChart.invalidate();  // Refresh the chart

        // Set X-axis behavior
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(monthLabels));  // Set dynamic month labels
        xAxis.setGranularity(1f);  // Ensure steps of 1
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelCount(monthLabels.size());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }
}
