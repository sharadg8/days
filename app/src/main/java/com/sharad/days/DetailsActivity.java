package com.sharad.days;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.sharad.common.DatePickerFragment;
import com.sharad.common.TimePickerFragment;

import java.util.ArrayList;
import java.util.List;


public class DetailsActivity extends AppCompatActivity
        implements EditorFragment.OnFragmentInteractionListener,
        DatePickerFragment.OnFragmentInteractionListener,
        TimePickerFragment.OnFragmentInteractionListener {
    public final static String ID_KEY = "DetailsActivity$idKey";
    private EditorFragment      _editor;
    private Event _event;

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

        initToolbar();
        initViews();
        initViewPager();
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
        TextView days = (TextView) findViewById(R.id.d_days);
        TextView date = (TextView) findViewById(R.id.d_date);
        TextView title = (TextView) findViewById(R.id.d_title);
        ImageView label = (ImageView) findViewById(R.id.d_label);
        ImageView agoTogo = (ImageView) findViewById(R.id.d_ago_togo);

        days.setText("" + _event.get_dayCount());
        date.setText(_event.get_dateText());
        title.setText(_event.get_title());
        label.setImageResource(_event.get_favorite());
        agoTogo.setImageResource(_event.get_agoTogo());
    }

    private void initViewPager() {
        ViewPager editorViewPager = (ViewPager) findViewById(R.id.editPager);
        PagerAdapter editorPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        _editor = EditorFragment.newInstance(0);
        editorPagerAdapter.addFragment(_editor, "Edit Event");
        editorViewPager.setAdapter(editorPagerAdapter);
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
    public void onBackPressed() {
        View add_view = findViewById(R.id.editPager);
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
