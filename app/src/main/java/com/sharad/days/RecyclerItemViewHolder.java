package com.sharad.days;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView _title;
    private final TextView _days;
    private final RelativeLayout _buttonGroup;
    private final LinearLayout _background;

    public RecyclerItemViewHolder(final View parent, TextView titleView,
                                  TextView daysView, RelativeLayout btnGroup,
                                  LinearLayout background) {
        super(parent);
        _title = titleView;
        _days = daysView;
        _buttonGroup = btnGroup;
        _background = background;
    }

    public static RecyclerItemViewHolder newInstance(View parent) {
        TextView titleView = (TextView) parent.findViewById(R.id.e_title);
        TextView daysView = (TextView) parent.findViewById(R.id.e_days);
        RelativeLayout btnGroup = (RelativeLayout) parent.findViewById(R.id.e_e_buttons);
        LinearLayout background = (LinearLayout) parent.findViewById(R.id.event_color);

        return new RecyclerItemViewHolder(parent, titleView, daysView, btnGroup, background);
    }

    public void showButtons(boolean toggle)  {
        toggle = false;
        if(toggle) {
            if(_buttonGroup.getVisibility() == View.VISIBLE) {
                _buttonGroup.setVisibility(View.GONE);
            } else {
                _buttonGroup.setVisibility(View.VISIBLE);
            }
        } else {
            _buttonGroup.setVisibility(View.GONE);
        }
    }

    public void setupView(Event event){
        _background.setBackgroundColor(event.get_colorId());
        _title.setText(event.get_title());
        _days.setText(""+event.get_dayCount());
    }
}
