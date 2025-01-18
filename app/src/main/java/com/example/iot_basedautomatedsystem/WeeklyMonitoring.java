package com.example.iot_basedautomatedsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import androidx.core.content.ContextCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WeeklyMonitoring extends AppCompatActivity {

    private BarChart barChart;
    private List<String> weekLabels;
    private List<BarEntry> entries;
    private TextView monthTextView; // Display the current month
    private static final String PREFS_NAME = "AppPrefs";
    private static final String LAST_PROCESSED_MONTH_KEY = "LastProcessedMonth";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_monitoring);

        // Initialize BarChart
        barChart = findViewById(R.id.barChart);

        // Initialize entries list
        entries = new ArrayList<>();

        // Initialize week labels
        weekLabels = new ArrayList<>();

        // Initialize month text view
        monthTextView = findViewById(R.id.monthTextView);

        // Add this inside your onCreate method
        Spinner monthSpinner = findViewById(R.id.monthSpinner);

        // Create an array of month names
        String[] months = {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };

        // Set up the adapter to populate the Spinner
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        // Set the current month below the chart
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Months are 0-indexed
        String currentMonthName = getMonthName(currentMonth);
        monthTextView.setText("Month: " + currentMonthName);

        // Check for month change
        if (isNewMonth(currentMonth)) {
            resetTableData();
            saveLastProcessedMonth(currentMonth);
        }

        // Set the Spinner listener to detect month selection
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected month
                String selectedMonth = months[position];

                // Update the TextView to display the selected month
                monthTextView.setText("Month: " + selectedMonth);

                // Fetch data for the selected month
                fetchDataFromFirebase(selectedMonth);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Optional: Handle the case when no month is selected
            }
        });



        // Fetch data from Firebase and plot the graph
        //fetchDataFromFirebase();
    }

    private boolean isNewMonth(int currentMonth) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int lastProcessedMonth = prefs.getInt(LAST_PROCESSED_MONTH_KEY, -1);
        return lastProcessedMonth != currentMonth;
    }

    private void saveLastProcessedMonth(int currentMonth) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(LAST_PROCESSED_MONTH_KEY, currentMonth);
        editor.apply();
    }

    private void resetTableData() {
        // Reset Firebase data for the table
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("weeklyReading");
        databaseReference.setValue(null) // This clears all data under "weeklyReading"
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Data reset for the new month", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to reset data", Toast.LENGTH_SHORT).show());
    }

    // Modify the fetchDataFromFirebase method to work with the new key format
    private void fetchDataFromFirebase(String selectedMonth) {
        // Clear previous data
        entries.clear();
        weekLabels.clear();

        // Reference to Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("weeklyReading");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int weekIndex = 0;

                    // Iterate over each week in the data
                    for (DataSnapshot weekSnapshot : dataSnapshot.getChildren()) {
                        // Fetch the month directly from the snapshot
                        String month = weekSnapshot.child("month").getValue(String.class);

                        // Only process data if it matches the selected month
                        if (month != null && month.equalsIgnoreCase(selectedMonth)) {
                            String weekKey = weekSnapshot.getKey(); // e.g., "Jan-Week1", "Feb-Week2"

                            // Extract and format week number for display
                            String displayLabel = weekKey.replaceAll("^(\\D+)-", "");  // Removes month part
                            displayLabel = "Week " + displayLabel.substring(displayLabel.indexOf("Week") + 4);

                            // Add the formatted week label to the list
                            weekLabels.add(displayLabel);

                            // Fetch the "totalEnergy" value for the week
                            Float energy = weekSnapshot.child("totalEnergy").getValue(Float.class);

                            // Add data to graph entries
                            if (energy != null) {
                                entries.add(new BarEntry(weekIndex, energy));
                                weekIndex++;
                            }
                        }
                    }

                    // If no data for the selected month
                    if (weekLabels.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "No data available for the selected month", Toast.LENGTH_LONG).show();
                    }

                    // Update the graph with new data
                    plotDataOnGraph();
                } else {
                    Toast.makeText(getApplicationContext(), "No data available", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching data", databaseError.toException());
                Toast.makeText(getApplicationContext(), "Failed to retrieve data", Toast.LENGTH_LONG).show();
            }
        });
    }








    private String getMonthName(int monthNumber) {
        switch (monthNumber) {
            case 1: return "Jan";
            case 2: return "Feb";
            case 3: return "Mar";
            case 4: return "Apr";
            case 5: return "May";
            case 6: return "Jun";
            case 7: return "Jul";
            case 8: return "Aug";
            case 9: return "Sep";
            case 10: return "Oct";
            case 11: return "Nov";
            case 12: return "Dec";
            default: return "";
        }
    }

    private void plotDataOnGraph() {
        // Create BarDataSet
        BarDataSet barDataSet = new BarDataSet(entries, "Weekly Reading Data");

        // Define assorted colors for each week
        List<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(this, R.color.red));
        colors.add(ContextCompat.getColor(this, R.color.blue));
        colors.add(ContextCompat.getColor(this, R.color.green));
        colors.add(ContextCompat.getColor(this, R.color.yellow));
        colors.add(ContextCompat.getColor(this, R.color.orange));

        // Apply colors to dataset
        barDataSet.setColors(colors);
        barDataSet.setValueTextSize(12f);

        // Create BarData with the dataset
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);

        // Set data to the chart
        barChart.setData(barData);
        barChart.setFitBars(true);  // Ensure the bars fit within the X-axis
        barChart.invalidate();  // Refresh the chart

        // Set X-axis behavior
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(weekLabels));  // Set dynamic week labels
        xAxis.setGranularity(1f);  // Ensure steps of 1
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelCount(weekLabels.size());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }
}
