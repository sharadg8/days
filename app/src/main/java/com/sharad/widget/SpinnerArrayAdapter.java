package com.sharad.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sharad.days.Event;
import com.sharad.days.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sharad on 11-Oct-15.
 */
public class SpinnerArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> objects;
    private Context context;

    public SpinnerArrayAdapter(Context context, int resourceId, ArrayList<Event> objects) {
        super(context, resourceId, objects);
        this.objects = objects;
        this.context = context;
    }

    public ArrayList<Event> getObjects() { return this.objects; }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        Event thisEvent = objects.get(position);
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(  Context.LAYOUT_INFLATER_SERVICE );
        View row = inflater.inflate(R.layout.widget_spinner_item, parent, false);

        TextView title = (TextView)row.findViewById(R.id.ec_title);
        title.setText(thisEvent.get_title());

        TextView date = (TextView)row.findViewById(R.id.ec_date);
        date.setText(thisEvent.get_dateText());

        TextView days = (TextView)row.findViewById(R.id.ec_days);
        days.setText(String.valueOf(thisEvent.get_dayCount()));

        ImageView label = (ImageView)row.findViewById(R.id.ec_label);
        label.setColorFilter(thisEvent.get_colorId());
        label.setImageResource(thisEvent.get_favorite());

        ImageView agoTogo = (ImageView)row.findViewById(R.id.ec_ago_togo);
        agoTogo.setImageResource(thisEvent.get_agoTogo());
        return row;
    }
}
