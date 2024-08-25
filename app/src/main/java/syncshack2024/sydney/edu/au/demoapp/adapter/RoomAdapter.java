package syncshack2024.sydney.edu.au.demoapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import syncshack2024.sydney.edu.au.demoapp.R;
import syncshack2024.sydney.edu.au.demoapp.model.Room;


/** Custom adapter for Room Class*/

public class RoomAdapter extends ArrayAdapter<Room> {
    private final Context context;

    public RoomAdapter(Context context, List<Room> items) {
        super(context, 0, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Room room = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.room_list, parent, false);
        }

        // Lookup view for data population
        TextView titleTextView = convertView.findViewById(R.id.txtName);
        TextView dueDateTextView = convertView.findViewById(R.id.txtStartDate);

        // Populate the data into the template view using the data object
        titleTextView.setText(room.getTitle());
        dueDateTextView.setText(room.getStartDate().toString());

        //Long timeUntilDue = item.getDueDate().getTime() - new Date().getTime(); // Time to due date in milliseconds
        // Adapted from https://stackoverflow.com/questions/9027317/how-to-convert-milliseconds-to-hhmmss-format
        //String timeUntilDueStr = String.format("%02d days, %02d hours, %02d minutes",
        //        TimeUnit.MILLISECONDS.toDays(timeUntilDue),
        //        TimeUnit.MILLISECONDS.toHours(timeUntilDue) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeUntilDue)),
        //        TimeUnit.MILLISECONDS.toMinutes(timeUntilDue) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeUntilDue)));
        //timeUntilDueStr = "Due in: " + timeUntilDueStr;
        //timeUntilDueTextView.setText(timeUntilDueStr);

        //if (item.getDueDate().before(new Date())) {
        //    dueDateTextView.setText(R.string.overdue);
        //}

        // Set a listener for the checkbox to update the isComplete field
        //isCompleteCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
        //    item.setIsComplete(isChecked);
        //    if (context instanceof MainActivity) {
        //        ((MainActivity) context).updateRoomIsCompleteToDatabase(item);
        //    }
        //    Log.i("Room checked: ", item.toString());
        //});

        //isCompleteCheckBox.setChecked(item.getIsComplete());

        // Return the completed view to render on screen
        return convertView;
    }
}
