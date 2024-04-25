package com.example.asd;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private List<Meeting> meetingsList;
    private int currentIndex;

    private DatePicker datePicker;
    private TimePicker timePicker;
    private EditText meetingTitleEditText;
    private Button scheduleButton;
    private Button cancelButton;
    private Button previousButton;
    private Button nextButton;
    private Button viewMeetingsButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        meetingsList = new ArrayList<>();
        currentIndex = 0;

        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        meetingTitleEditText = findViewById(R.id.meetingTitleEditText);
        scheduleButton = findViewById(R.id.scheduleMeetingButton);
        cancelButton = findViewById(R.id.cancelMeetingButton);
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                scheduleMeeting();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                cancelMeeting();
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                showPreviousMeeting();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                showNextMeeting();
            }
        });

        viewMeetingsButton = findViewById(R.id.viewMeetingsButton);
        viewMeetingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScheduleMeetings();
            }
        });
        databaseHelper = new DatabaseHelper(this);
        List<Meeting> storedMeetings = databaseHelper.getAllMeetings();
        meetingsList.addAll(storedMeetings);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void scheduleMeeting() {
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();
        int hour, minute;

        if (Build.VERSION.SDK_INT >= 23) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }

        Calendar selectedDateTime = Calendar.getInstance();
        selectedDateTime.set(year, month, day, hour, minute, 0);

        Calendar currentDateTime = Calendar.getInstance();
        if (selectedDateTime.before(currentDateTime)) {
            Toast.makeText(this, "Cannot schedule a meeting in the past", Toast.LENGTH_SHORT).show();
            return;
        }
        String meetingTitle = meetingTitleEditText.getText().toString().trim();

        // Set the maximum character limit for the meeting title
        int maxTitleLength = 70;
        if (meetingTitle.length() > maxTitleLength) {
            Toast.makeText(this, "Meeting title exceeds the maximum character limit of " + maxTitleLength, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the meeting title contains any restricted characters
        String restrictedCharacters = "<>&";
        if (containsRestrictedCharacters(meetingTitle, restrictedCharacters)) {
            Toast.makeText(this, "Meeting title contains restricted characters: <, >, &", Toast.LENGTH_SHORT).show();
            return;
        }
        String meetingId = UUID.randomUUID().toString();

        Meeting meeting = new Meeting(meetingId, meetingTitle, selectedDateTime.getTime());
        meetingsList.add(meeting);
        databaseHelper.addMeeting(meeting);

        Toast.makeText(this, "Meeting Scheduled", Toast.LENGTH_SHORT).show();
        clearInputs();
    }
    private boolean containsRestrictedCharacters(String meetingTitle, String restrictedCharacters) {
        for (char restrictedChar : restrictedCharacters.toCharArray()) {
            if (meetingTitle.contains(String.valueOf(restrictedChar))) {
                return true;
            }
        }
        return false;
    }

    private void cancelMeeting() {
        if (meetingsList.size() > 0) {
            Meeting currentMeeting = meetingsList.get(currentIndex);
            meetingsList.remove(currentMeeting);
            Toast.makeText(this, "Meeting Cancelled", Toast.LENGTH_SHORT).show();
            clearInputs();
        }
    }

    private void showPreviousMeeting() {
        if (meetingsList.size() > 0) {
            currentIndex = (currentIndex - 1 + meetingsList.size()) % meetingsList.size();
            Meeting currentMeeting = meetingsList.get(currentIndex);
            showMeetingDetails(currentMeeting);
        }
    }

    private void showNextMeeting() {
        if (meetingsList.size() > 0) {
            currentIndex = (currentIndex + 1) % meetingsList.size();
            Meeting currentMeeting = meetingsList.get(currentIndex);
            showMeetingDetails(currentMeeting);
        }
    }

    private void showMeetingDetails(Meeting meeting) {
        meetingTitleEditText.setText(meeting.getTitle());

        if (meeting.isCancelled()) {
            // Clear the timePicker and datePicker for cancelled meetings
            if (Build.VERSION.SDK_INT >= 23) {
                timePicker.setHour(0);
                timePicker.setMinute(0);
            } else {
                timePicker.setCurrentHour(0);
                timePicker.setCurrentMinute(0);
            }
            datePicker.updateDate(0, 0, 0);
        } else {
            // Sort the meetings list by datetime
            Collections.sort(meetingsList, new Comparator<Meeting>() {
                @Override
                public int compare(Meeting m1, Meeting m2) {
                    return m1.getDateTime().compareTo(m2.getDateTime());
                }
            });

            // Find the index of the current meeting in the sorted list
            int index = meetingsList.indexOf(meeting);

            // Show the meeting details at the sorted index
            if (index != -1) {
                Meeting sortedMeeting = meetingsList.get(index);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(sortedMeeting.getDateTime());
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                if (Build.VERSION.SDK_INT >= 23) {
                    timePicker.setHour(hour);
                    timePicker.setMinute(minute);
                } else {
                    timePicker.setCurrentHour(hour);
                    timePicker.setCurrentMinute(minute);
                }

                datePicker.updateDate(year, month, day);
            }
        }
    }

    private void showScheduleMeetings() {
        // Filter the meetings based on the selected date from the DatePicker
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();

        List<Meeting> filteredMeetings = new ArrayList<>();

        for (Meeting meeting : meetingsList) {
            Calendar meetingDate = Calendar.getInstance();
            meetingDate.setTime(meeting.getDateTime());

            if (meetingDate.get(Calendar.YEAR) == year &&
                    meetingDate.get(Calendar.MONTH) == month &&
                    meetingDate.get(Calendar.DAY_OF_MONTH) == day) {
                filteredMeetings.add(meeting);
            }
        }

        // Sort the filtered meetings based on time
        Collections.sort(filteredMeetings, new Comparator<Meeting>() {
            @Override
            public int compare(Meeting meeting1, Meeting meeting2) {
                return meeting1.getDateTime().compareTo(meeting2.getDateTime());
            }
        });

        // Open the ScheduledMeetingsActivity and pass the filtered meetings as an extra
        Intent intent = new Intent(MainActivity.this, ScheduleMeetingsActivity.class);
        intent.putExtra("meetingsList", new ArrayList<>(filteredMeetings));
        startActivity(intent);
    }

    private void clearInputs() {
        meetingTitleEditText.setText("");

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if (Build.VERSION.SDK_INT >= 23) {
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        } else {
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minute);
        }

        datePicker.updateDate(year, month, day);
    }

}