package com.sharad.common;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sharad on 19-Sep-15.
 */
public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

    private OnFragmentInteractionListener mListener;
    private Date time;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        Calendar c = Calendar.getInstance();
        if(time != null) {  c.setTime(time); }
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of DatePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hourOfDay, minute, false);
    }

   @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (mListener != null) {
            mListener.timeSelected(hourOfDay, minute);
        }
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public interface OnFragmentInteractionListener {
        public void timeSelected(int hourOfDay, int minute);
    }
}