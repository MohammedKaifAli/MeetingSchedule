package com.example.asd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder> {

    private List<Meeting> meetingsList;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    public MeetingAdapter(List<Meeting> meetingsList) {
        this.meetingsList = meetingsList;
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        this.timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    }

    @NonNull
    @Override
    public MeetingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meeting, parent, false);
        return new MeetingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingViewHolder holder, int position) {
        Meeting meeting = meetingsList.get(position);

        holder.titleTextView.setText(meeting.getTitle());
        holder.dateTextView.setText(dateFormat.format(meeting.getDateTime()));
        holder.timeTextView.setText(timeFormat.format(meeting.getDateTime()));
    }

    @Override
    public int getItemCount() {
        return meetingsList.size();
    }

    static class MeetingViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView dateTextView;
        TextView timeTextView;

        MeetingViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }
}
