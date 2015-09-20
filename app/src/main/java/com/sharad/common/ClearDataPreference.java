package com.sharad.common;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.sharad.days.DataProvider;

/**
 * Created by Sharad on 16-Sep-15.
 */
public class ClearDataPreference extends DialogPreference {
    public ClearDataPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText("Ok");
        setNegativeButtonText("Cancel");

        setDialogIcon(null);
        setDialogTitle(null);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        // When the user selects "OK", persist the new value
        if (positiveResult) {
            // User selected OK
            DataProvider db = new DataProvider(getContext());
            db.open();
            db.deleteAllEvents();
            db.close();
        } else {
            // User selected Cancel
        }
    }
}
