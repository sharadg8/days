package com.sharad.days;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.sharad.common.DatePickerFragment;
import com.sharad.common.TimePickerFragment;
import com.sharad.reminder.ReminderManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.prefs.PreferencesFactory;


public class MainActivity extends AppCompatActivity
        implements EditorFragment.OnFragmentInteractionListener,
        DatePickerFragment.OnFragmentInteractionListener,
        TimePickerFragment.OnFragmentInteractionListener {
    //private static String LOG_TAG = "MainActivity";
    private EventsFragment _eventList;
    private EditorFragment _editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initViewPager();

        runOnce();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.primary_dark));
            window.setNavigationBarColor(getResources().getColor(R.color.primary_dark));
        }
    }

    private void runOnce() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        String version = "";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String currentVer = prefs.getString("appVersion", "0");
        if(!currentVer.equals(version)) {
            editor.putString("appVersion", version);
            editor.putBoolean("notifications_enable", true);
            editor.commit();
            ReminderManager reminderMgr = new ReminderManager(this);
            DataProvider db = new DataProvider(this);
            db.open();
            ArrayList<Event> events = new ArrayList<>();
            db.getEvents(events, DataProvider.KEY_EVENT_DATE + ">" + System.currentTimeMillis());
            for(int i = 0; i<events.size(); i++) {
                Calendar c = Calendar.getInstance();
                c.setTime(events.get(i).get_startDate());
                reminderMgr.setReminder(events.get(i).get_id(), c);
            }
            db.close();

            /*
            new AlertDialog.Builder(this)
                    .setTitle("What's new")
                    .setMessage("Version "+version+"\n"
                            +"1. \n"
                            +"Thanks for using app\n"
                            +"Happy counting days!!")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
                    */
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM");
        setTitle(sdf.format(cal.getTime()));
    }

    private void initViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        _eventList = EventsFragment.createInstance(null);
        pagerAdapter.addFragment(_eventList, "List");
        viewPager.setAdapter(pagerAdapter);

        ViewPager editorViewPager = (ViewPager) findViewById(R.id.addPager);
        PagerAdapter editorPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        _editor = EditorFragment.newInstance(0);
        editorPagerAdapter.addFragment(_editor, "Add Event");
        editorViewPager.setAdapter(editorPagerAdapter);
    }

    private void hideEditorView(){
        final View add_view = findViewById(R.id.addPager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View add_icon = findViewById(R.id.action_add);
            int[] location = new int[2];
            add_icon.getLocationOnScreen(location);
            Point center = new Point(location[0] + 20, 0);
            int initialRadius = add_view.getWidth();
            Animator anim = ViewAnimationUtils.createCircularReveal(add_view, center.x, center.y, initialRadius, 0);
            anim.setDuration(300);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    add_view.setVisibility(View.GONE);
                }
            });
            anim.start();
        } else {
            add_view.setVisibility(View.GONE);
        }

        InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e) {
            // do nothing
        }
    }

    private void showEditorView() {
        _editor.setNewEvent(null);
        View add_view = findViewById(R.id.addPager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View add_icon = findViewById(R.id.action_add);
            int[] location = new int[2];
            add_icon.getLocationOnScreen(location);
            Point center = new Point(location[0] + 20, 0);
            int finalRadius = Math.max(add_view.getWidth(), add_view.getHeight());
            Animator anim = ViewAnimationUtils.createCircularReveal(add_view, center.x, center.y, 0, finalRadius);
            anim.setDuration(300);
            add_view.setVisibility(View.VISIBLE);
            anim.start();
        } else {
            add_view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            hideEditorView();
            Intent intent = new Intent(this, SettingsActivity.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            ActivityCompat.startActivity(this, intent, options.toBundle());
            return true;
        } else if(id == R.id.action_add) {
            View add_view = findViewById(R.id.addPager);
            if(add_view.getVisibility() == View.VISIBLE) {
                hideEditorView();
            } else {
                showEditorView();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        View add_view = findViewById(R.id.addPager);
        if(add_view.getVisibility() == View.VISIBLE) {
            hideEditorView();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onEditorUpdate(int next) {
        switch (next) {
            case EditorFragment.NEXT_CLOSE_EDITOR:
                hideEditorView();
                break;
            case EditorFragment.NEXT_ADDED_EDITOR:
                _eventList.insertEvent(null);
                hideEditorView();
                break;
        }
    }

    @Override
    public void dateSelected(int year, int month, int day) {
        _editor.dateSelected(year, month, day);
    }

    @Override
    public void timeSelected(int hourOfDay, int minute) {
        _editor.timeSelected(hourOfDay, minute);
    }

    static class PagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();
        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }
        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }
        @Override
        public int getCount() {
            return fragmentList.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
}
