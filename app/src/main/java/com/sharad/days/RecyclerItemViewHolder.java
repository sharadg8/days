package com.sharad.days;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView _title;
    private final TextView _days;
    private final RelativeLayout _buttonGroup;

    public RecyclerItemViewHolder(final View parent, TextView titleView,
                                  TextView daysView, RelativeLayout btnGroup) {
        super(parent);
        _title = titleView;
        _days = daysView;
        _buttonGroup = btnGroup;
    }

    public static RecyclerItemViewHolder newInstance(View parent) {
        TextView titleView = (TextView) parent.findViewById(R.id.e_title);
        TextView daysView = (TextView) parent.findViewById(R.id.e_days);
        RelativeLayout btnGroup = (RelativeLayout) parent.findViewById(R.id.e_e_buttons);

        return new RecyclerItemViewHolder(parent, titleView, daysView, btnGroup);
    }

    public void setTitle(CharSequence text) {   _title.setText(text);   }
    public void setDays(CharSequence text)  {   _days.setText(text);    }
    public void showButtons(boolean toggle)  {
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
}
