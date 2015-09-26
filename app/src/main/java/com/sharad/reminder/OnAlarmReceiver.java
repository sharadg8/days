package com.sharad.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.util.Log;

import com.sharad.days.DataProvider;
import com.sharad.days.Event;

public class OnAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = ComponentInfo.class.getCanonicalName();
    public OnAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received wake up from alarm manager.");

        long eventId = intent.getExtras().getLong(DataProvider.KEY_EVENT_ROWID);

        ReminderIntentService.acquireStaticLock(context);

        Intent i = new Intent(context, ReminderIntentService.class);
        i.putExtra(DataProvider.KEY_EVENT_ROWID, eventId);
        context.startService(i);
    }
}
