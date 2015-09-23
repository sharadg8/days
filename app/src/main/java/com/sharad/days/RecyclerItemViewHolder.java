package com.sharad.days;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView _title;
    private final TextView _days;
    private final TextView _date;
    private final ImageView _label;
    private final ImageView _agoTogo;

    public RecyclerItemViewHolder(final View parent, TextView titleView,
                                  TextView daysView, TextView dateView,
                                  ImageView labelView, ImageView agoTogoView) {
        super(parent);
        _title = titleView;
        _days = daysView;
        _date = dateView;
        _label = labelView;
        _agoTogo = agoTogoView;
    }

    public static RecyclerItemViewHolder newInstance(View parent) {
        TextView titleView = (TextView) parent.findViewById(R.id.ec_title);
        TextView daysView = (TextView) parent.findViewById(R.id.ec_days);
        TextView dateView = (TextView) parent.findViewById(R.id.ec_date);
        ImageView labelView = (ImageView) parent.findViewById(R.id.ec_label);
        ImageView agoTogoView = (ImageView) parent.findViewById(R.id.ec_ago_togo);

        return new RecyclerItemViewHolder(parent, titleView,
                daysView, dateView, labelView, agoTogoView);
    }

    public void setupView(Event e){
        _title.setText(e.get_title());
        _days.setText(""+e.get_dayCount());
        _date.setText(e.get_dateText());
        _agoTogo.setImageResource(e.get_agoTogo());
        _label.setColorFilter(e.get_colorId());
        _label.setImageResource(e.get_favorite());
    }
}
