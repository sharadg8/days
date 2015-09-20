package com.sharad.days;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.sharad.common.DatePickerFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements EditorFragment.OnFragmentInteractionListener,
        DatePickerFragment.OnFragmentInteractionListener {
    private static String LOG_TAG = "MainActivity";
    private EventFragment       _eventList;
    private EditorFragment      _editor;

    ViewPager _editorViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initViewPager();
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
        _eventList = EventFragment.createInstance(null);
        pagerAdapter.addFragment(_eventList, "List");
        viewPager.setAdapter(pagerAdapter);

        _editorViewPager = (ViewPager) findViewById(R.id.addPager);
        PagerAdapter editorPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        _editor = EditorFragment.newInstance(0);
        editorPagerAdapter.addFragment(_editor, "Add Event");
        _editorViewPager.setAdapter(editorPagerAdapter);
    }

    private void hideEditorView(){
        final View add_view = findViewById(R.id.addPager);
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
    }

    private void showEditorView() {
        _editor.setNewEvent(null);
        View add_view = findViewById(R.id.addPager);
        View add_icon = findViewById(R.id.action_add);
        int[] location = new int[2];
        add_icon.getLocationOnScreen(location);
        Point center = new Point(location[0] + 20, 0);

        int finalRadius = Math.max(add_view.getWidth(), add_view.getHeight());
        Animator anim = ViewAnimationUtils.createCircularReveal(add_view, center.x, center.y, 0, finalRadius);
        anim.setDuration(300);
        add_view.setVisibility(View.VISIBLE);
        anim.start();
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
    public void onEditorUpdate(int next) {
        switch (next) {
            case EditorFragment.NEXT_CLOSE_EDITOR:
                hideEditorView();
                break;
            case EditorFragment.NEXT_ADDED_EDITOR:
                // notify recycler
                _eventList.insertEvent(null);
                hideEditorView();
                break;
        }
    }

    @Override
    public void dateSelected(int year, int month, int day) {
        _editor.dateSelected(year, month, day);
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
