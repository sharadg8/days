package com.sharad.widget;

import android.app.Activity;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.sharad.days.DataProvider;
import com.sharad.days.Event;
import com.sharad.days.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The configuration screen for the {@link EventWidget EventWidget} AppWidget.
 */
public class EventWidgetConfigureActivity extends Activity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    EditText mAppWidgetText;
    private static final String PREFS_NAME = "com.sharad.widget.EventWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    public EventWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.event_widget_configure);
        mAppWidgetText = (EditText) findViewById(R.id.appwidget_text);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        mAppWidgetText.setText(loadTitlePref(EventWidgetConfigureActivity.this, mAppWidgetId));
        mAppWidgetText.clearFocus();

        WallpaperManager wm = WallpaperManager.getInstance(this);
        LinearLayout background = (LinearLayout)findViewById(R.id.screen_background);
        background.setBackground(wm.getDrawable());

        Spinner sp = (Spinner)findViewById(R.id.widget_select_spinner);
        SpinnerArrayAdapter adapter = new SpinnerArrayAdapter(this, R.layout.event_card, new ArrayList<Event>());
        sp.setAdapter(adapter);
        sp.requestFocus();

        DataProvider db = new DataProvider(this);
        db.open();
        if(db != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String strOrder = prefs.getString("sort_order", "0");
            boolean elapsed = prefs.getBoolean("show_elapsed", true);
            String where;
            if(!elapsed) {
                where = DataProvider.KEY_EVENT_DATE + " > " + System.currentTimeMillis();
            } else {
                where = null;
            }

            db.getEvents(adapter.getObjects(), where);
            Collections.sort(adapter.getObjects());

            if(strOrder.equals("1")) {
                Collections.reverse(adapter.getObjects());
            }
            adapter.notifyDataSetChanged();
        }
        db.close();
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = EventWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            String widgetText = mAppWidgetText.getText().toString();
            saveTitlePref(context, mAppWidgetId, widgetText);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            EventWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.commit();
    }
}

