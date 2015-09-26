package com.sharad.reminder;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import com.sharad.days.DataProvider;
import com.sharad.days.Event;

public class OnBootReceiver extends BroadcastReceiver {
	
	private static final String TAG = ComponentInfo.class.getCanonicalName();  
	
	@Override
	public void onReceive(Context context, Intent intent) {

		ReminderManager reminderMgr = new ReminderManager(context);
		
		DataProvider db = new DataProvider(context);
		db.open();

		ArrayList<Event> events = new ArrayList<>();
		db.getEvents(events, DataProvider.KEY_EVENT_DATE + ">" + System.currentTimeMillis());
		for(int i = 0; i<events.size(); i++) {
			Calendar c = Calendar.getInstance();
			c.setTime(events.get(i).get_startDate());
			reminderMgr.setReminder(events.get(i).get_id(), c);
		}
		db.close();
	}
}

