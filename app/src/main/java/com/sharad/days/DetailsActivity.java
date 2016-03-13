package com.sharad.days;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.os.Bundle;
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
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sharad.common.DatePickerFragment;
import com.sharad.common.TimePickerFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class DetailsActivity extends AppCompatActivity
        implements EditorFragment.OnFragmentInteractionListener,
        DatePickerFragment.OnFragmentInteractionListener,
        TimePickerFragment.OnFragmentInteractionListener {
    public final static String ID_KEY = "DetailsActivity$idKey";
    private EditorFragment      _editor;
    private Event _event;
    private boolean _details;
    private BroadcastReceiver _broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            long id = extras.getLong(ID_KEY, 0);
            DataProvider db = new DataProvider(this);
            db.open();
            _event = db.getEvent(id);
            db.close();
        }

        _details = true;
        initToolbar();
        initViews();
        initViewPager();

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTextView();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }

    private void initToolbar() {
        int[] palette = getResources().getIntArray(R.array.palette);
        int index = 0;
        for(int i=0;i<palette.length;i++) {
            if(palette[i] == _event.get_colorId())  { index = i; break; }
        }
        int[] paletteDark = getResources().getIntArray(R.array.palette_dark);
        int[] paletteLight = getResources().getIntArray(R.array.palette_light);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(palette[index]);
        getSupportActionBar().setElevation(0);
        setTitle("");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(paletteDark[index]);
            window.setNavigationBarColor(paletteDark[index]);
        }

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        coordinatorLayout.setBackgroundColor(paletteLight[index]);
    }

    private void initViews() {
        final TextView date = (TextView) findViewById(R.id.d_date);
        final TextView time = (TextView) findViewById(R.id.d_time);
        TextView title = (TextView) findViewById(R.id.d_title);
        final ImageView label = (ImageView) findViewById(R.id.d_label);
        final ImageView labelLarge = (ImageView) findViewById(R.id.d_label_big);
        ImageView agoTogo = (ImageView) findViewById(R.id.d_ago_togo);


        updateDates(_details);

        date.setText(_event.get_dateText());
        date.setText(_event.get_dateText());
        title.setText(_event.get_title());
        label.setImageResource(_event.get_favorite());
        labelLarge.setImageResource(_event.getLabelLarge());
        agoTogo.setImageResource(_event.get_agoTogo());
        SimpleDateFormat tf = new SimpleDateFormat("hh:mm aa");
        time.setText(tf.format(_event.get_startDate()));

        date.setVisibility(View.INVISIBLE);
        time.setVisibility(View.INVISIBLE);
        labelLarge.setVisibility(View.INVISIBLE);
        labelLarge.clearAnimation();
        label.setVisibility(View.VISIBLE);

        ScaleAnimation labelAnim = new ScaleAnimation(1f, 0.8f, 1f, 0.8f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        labelAnim.setDuration(500);
        labelAnim.setRepeatCount(4);
        labelAnim.setRepeatMode(Animation.REVERSE);
        label.startAnimation(labelAnim);

        final ScaleAnimation labelScale = new ScaleAnimation(0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        labelScale.setDuration(800);
        labelScale.setFillAfter(true);

        labelAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                label.setVisibility(View.GONE);
                labelLarge.setVisibility(View.VISIBLE);
                labelLarge.startAnimation(labelScale);
            }
        });

        labelScale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                labelLarge.setVisibility(View.VISIBLE);
                date.setVisibility(View.VISIBLE);
                time.setVisibility(View.VISIBLE);
                ScaleAnimation labelAnim = new ScaleAnimation(1f, 0.85f, 1f, 0.85f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                labelAnim.setDuration(500);
                labelAnim.setRepeatCount(Animation.INFINITE);
                labelAnim.setRepeatMode(Animation.REVERSE);
                labelLarge.startAnimation(labelAnim);
            }
        });
    }

    private void updateDates(boolean showDetails) {
        TextView num1 = (TextView) findViewById(R.id.d_num1);
        TextView num2 = (TextView) findViewById(R.id.d_num2);
        TextView num3 = (TextView) findViewById(R.id.d_num3);
        TextView num1_accent = (TextView) findViewById(R.id.d_num1_accent);
        TextView num2_accent = (TextView) findViewById(R.id.d_num2_accent);
        TextView num3_accent = (TextView) findViewById(R.id.d_num3_accent);
        num1.setVisibility(View.GONE);
        num1_accent.setVisibility(View.GONE);
        num2.setVisibility(View.GONE);
        num2_accent.setVisibility(View.GONE);
        int dates[] = _event.get_diffInDates();
        if(_event.get_dayCount() > 7) {
            if(showDetails) {
                if (dates[0] > 0) {
                    num1.setText("" + dates[0]);
                    num1_accent.setText("Y");
                    num1.setVisibility(View.VISIBLE);
                    num1_accent.setVisibility(View.VISIBLE);
                }
                if (dates[1] > 0) {
                    num2.setText("" + dates[1]);
                    num2_accent.setText("M");
                    num2.setVisibility(View.VISIBLE);
                    num2_accent.setVisibility(View.VISIBLE);
                }
                num3.setText("" + dates[2]);
                num3_accent.setText("D");
            } else {
                num3.setText("" + _event.get_dayCount());
                num3_accent.setText("D");
            }
        } else {
            if(_event.get_dayCount() > 0) {
                num3.setText("" + dates[2]);
                num3_accent.setText("D");
                num3.setVisibility(View.VISIBLE);
                num3_accent.setVisibility(View.VISIBLE);
            } else {
                num2.setText("" + dates[3]);
                num2_accent.setText("H");
                num2.setVisibility(View.VISIBLE);
                num2_accent.setVisibility(View.VISIBLE);

                num3.setText("" + dates[4]);
                num3_accent.setText("M");
            }
        }
    }

    private void updateTextView() {
        TextView details = (TextView) findViewById(R.id.d_date_details);
        long diffmili = System.currentTimeMillis() - _event.get_startDate().getTime();
        diffmili = Math.abs(diffmili);
        int days = (int)TimeUnit.MILLISECONDS.toDays(diffmili);
        diffmili -= (long)days*24*60*60*1000;
        int hours = (int)TimeUnit.MILLISECONDS.toHours(diffmili);
        diffmili -= (long)hours*60*60*1000;
        int min = (int)TimeUnit.MILLISECONDS.toMinutes(diffmili);
        diffmili -= (long)min*60*1000;
        long sec = TimeUnit.MILLISECONDS.toSeconds(diffmili);
        details.setText("" + days + "." + String.format("%02d", hours)
                + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec));
    }

    private void initViewPager() {
        ViewPager editorViewPager = (ViewPager) findViewById(R.id.editPager);
        PagerAdapter editorPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        _editor = EditorFragment.newInstance(0);
        editorPagerAdapter.addFragment(_editor, "Edit Event");
        editorViewPager.setAdapter(editorPagerAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDates(_details);
    }

    @Override
    public void onStart() {
        super.onStart();
        _broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    updateDates(_details);
                }
            }
        };
        registerReceiver(_broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (_broadcastReceiver != null) {
            unregisterReceiver(_broadcastReceiver);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
                ActivityCompat.startActivity(this, intent, options.toBundle());
                return true;
            case R.id.action_delete:
                new AlertDialog.Builder(this)
                        .setMessage("Delete Event?")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DataProvider db = new DataProvider(getApplicationContext());
                                db.open();
                                db.deleteEvent(_event.get_id());
                                db.close();
                                onBackPressed();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
                return true;
            case R.id.action_edit:
                View add_view = findViewById(R.id.editPager);
                if(add_view.getVisibility() == View.VISIBLE) {
                    hideEditorView();
                } else {
                    showEditorView();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditorUpdate(int next) {
        switch (next) {
            case EditorFragment.NEXT_CLOSE_EDITOR:
                hideEditorView();
                break;
            case EditorFragment.NEXT_ADDED_EDITOR:
                DataProvider db = new DataProvider(this);
                db.open();
                _event = db.getEvent(_event.get_id());
                db.close();
                initToolbar();
                initViews();
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

    private void hideEditorView(){
        final View add_view = findViewById(R.id.editPager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View add_icon = findViewById(R.id.action_edit);
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
        _editor.setNewEvent(_event);
        View add_view = findViewById(R.id.editPager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View add_icon = findViewById(R.id.action_edit);
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

    public void swipeViewClick(View view) {
        _details = !_details;
        updateDates(_details);
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
