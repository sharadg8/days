package com.sharad.reminder;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import com.sharad.days.DataProvider;
import com.sharad.days.Event;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ReminderIntentService extends IntentService {

    public static final String LOCK_NAME_STATIC="com.sharad.reminder.Static";
    private static PowerManager.WakeLock lockStatic=null;

    public static void acquireStaticLock(Context context) {
        getLock(context).acquire();
    }

    synchronized private static PowerManager.WakeLock getLock(Context context) {
        if (lockStatic==null) {
            PowerManager mgr=(PowerManager)context.getSystemService(Context.POWER_SERVICE);
            lockStatic=mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_STATIC);
            lockStatic.setReferenceCounted(true);
        }
        return(lockStatic);
    }

    public ReminderIntentService() {
        super("ReminderIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            doReminderWork(intent);
            getLock(this).release();
        }
    }

    private void doReminderWork(Intent intent) {
        Log.d("ReminderIntentService", "Doing work.");

        Long eventId = intent.getExtras().getLong(DataProvider.KEY_EVENT_ROWID);
        DataProvider db = new DataProvider(this);
        db.open();
        Event e = db.getEvent(eventId);
        db.close();

        EventNotification.notify(this, e);
    }
}
