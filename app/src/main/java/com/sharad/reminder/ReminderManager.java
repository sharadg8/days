package com.sharad.reminder;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.sharad.days.DataProvider;

public class ReminderManager {

	private Context mContext; 
	private AlarmManager mAlarmManager;
	
	public ReminderManager(Context context) {
		mContext = context; 
		mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	}
	
	public void setReminder(long eventId, Calendar when) {
		
        Intent intent = new Intent(mContext, OnAlarmReceiver.class);
		intent.putExtra(DataProvider.KEY_EVENT_ROWID, eventId);

        PendingIntent pi = PendingIntent.getBroadcast(mContext, (int)eventId, intent, PendingIntent.FLAG_ONE_SHOT);
        
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(), pi);
	}
}
