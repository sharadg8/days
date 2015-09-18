package com.sharad.days;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView _title;
    private final TextView _info;
    private final TextView _date;

    public RecyclerItemViewHolder(final View parent, TextView titleView,
                                  TextView infoView, TextView dateView) {
        super(parent);
        _title = titleView;
        _info = infoView;
        _date = dateView;
    }

    public static RecyclerItemViewHolder newInstance(View parent) {
        TextView titleView = (TextView) parent.findViewById(R.id.dc_title);
        TextView infoView = (TextView) parent.findViewById(R.id.dc_info);
        TextView dateView = (TextView) parent.findViewById(R.id.dc_date);

        return new RecyclerItemViewHolder(parent, titleView, infoView, dateView);
    }

    public void setTitle(CharSequence text) {   _title.setText(text);   }
    public void setInfo(CharSequence text)  {   _info.setText(text);    }
    public void setDate(CharSequence text)  {   _date.setText(text);    }
}
