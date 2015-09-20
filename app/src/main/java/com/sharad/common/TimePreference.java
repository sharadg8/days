package com.sharad.common;


import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Sharad on 16-Sep-15.
 */
public class TimePreference extends DialogPreference {
    private int lastHour;
    private int lastMinute;
    private TimePicker picker;
    private SimpleDateFormat sdf;

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText("Ok");
        setNegativeButtonText("Cancel");

        sdf = new SimpleDateFormat("hh:mm a");
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());

        return(picker);
    }

    @Override
    protected void onBindDialogView(@NonNull View v) {
        super.onBindDialogView(v);

        picker.setCurrentHour(lastHour);
        picker.setCurrentMinute(lastMinute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            lastHour = picker.getCurrentHour();
            lastMinute = picker.getCurrentMinute();

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR, lastHour);
            cal.set(Calendar.MINUTE, lastMinute);

            String time = sdf.format(cal.getTime());
            setSummary(sdf.format(cal.getTime()));

            if (callChangeListener(time)) {
                persistString(time);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        Calendar cal = Calendar.getInstance();

        String time = "08:00 AM";
        if (restoreValue) {
            if (defaultValue == null) {
                time = getPersistedString(time);
            } else {
                time = defaultValue.toString();
            }
        } else {
            time = defaultValue.toString();
        }

        try {
            cal.setTime(sdf.parse(time));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        lastHour = cal.get(Calendar.HOUR);
        lastMinute = cal.get(Calendar.MINUTE);
        setSummary(sdf.format(cal.getTime()));
    }
}