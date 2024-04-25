package com.example.asd;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.List;

public class ScheduleMeetingsActivity extends AppCompatActivity {

    private List<Meeting> meetingsList;
    private RecyclerView recyclerView;
    private MeetingAdapter meetingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_meetings);

        // Retrieve the scheduled meetings from the main activity
        meetingsList = (List<Meeting>) getIntent().getSerializableExtra("meetingsList");

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.meetingsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create the adapter and set it to the RecyclerView
        meetingAdapter = new MeetingAdapter(meetingsList);
        recyclerView.setAdapter(meetingAdapter);
    }
}
