package com.sharad.days;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static String LOG_TAG = "MainActivity";

    DataProvider _db;

    private ImageButton mFavorite;
    private ImageButton mNotification;
    private ImageButton mPalette;
    private ImageButton mSave;
    private ImageButton mCancel;
    private EditText    mTitleText;
    private EditText    mDateText;
    int mYear;
    int mMonth;
    int mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initViewPager();
        initButtons();
    }

    private void initButtons() {
        mPalette = (ImageButton) findViewById(R.id.btn_palette);
        mPalette.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                // Show palette
            }
        });

        mFavorite = (ImageButton) findViewById(R.id.btn_favorite);
        mFavorite.setSelected(true);
        mFavorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                mFavorite.setSelected(!mFavorite.isSelected());
                int resId = mFavorite.isSelected() ? R.color.primary : R.color.primary_light;
                mFavorite.setColorFilter(getResources().getColor(resId));
            }
        });

        mNotification = (ImageButton) findViewById(R.id.btn_notification);
        mNotification.setSelected(true);
        mNotification.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                mNotification.setSelected(!mNotification.isSelected());
                int resId = mNotification.isSelected() ? R.color.primary : R.color.primary_light;
                mNotification.setColorFilter(getResources().getColor(resId));
            }
        });

        mSave = (ImageButton) findViewById(R.id.btn_save);
        mSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                String title = getItemTitle();
                if(title.length() > 0) {
                    _db.insertEvent(new Event(0, mTitleText.getText().toString(),
                            new Date(mYear, mMonth, mDay), 0,
                            mNotification.isSelected(), mFavorite.isSelected()));
                    hideAddView();
                }
            }
        });

        mCancel = (ImageButton) findViewById(R.id.btn_cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                hideAddView();
            }
        });

        mTitleText = (EditText) findViewById(R.id.txt_title);

        final SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy");
        Calendar cal = Calendar.getInstance();
        mDateText = (EditText) findViewById(R.id.txt_date);
        mDateText.setText(df.format(cal.getTime()));
        mDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                mYear = mcurrentDate.get(Calendar.YEAR);
                mMonth = mcurrentDate.get(Calendar.MONTH);
                mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog mDatePicker = new DatePickerDialog(getApplicationContext(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int year, int month, int day) {
                        EditText field = (EditText) findViewById(R.id.txt_date);
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month, day);
                        field.setText(df.format(cal.getTime()));
                        mYear = year;
                        mMonth = month;
                        mDay = day;
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.show();
            }
        });
    }

    private String getItemTitle() {
        mTitleText.setError(null);
        if(mTitleText.getText().toString().trim().length() == 0) {
            mTitleText.setError("Enter Title");
            return "";
        }
        return mTitleText.getText().toString();
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
        pagerAdapter.addFragment(EventFragment.createInstance(null), "List");
        viewPager.setAdapter(pagerAdapter);
    }

    private void hideAddView(){
        final View add_view = findViewById(R.id.view_add_new);
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

    private void showAddView() {
        View add_view = findViewById(R.id.view_add_new);
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
    public void onResume() {
        super.onResume();
        _db = new DataProvider(this);
        _db.open();
    }

    @Override
    public void onPause() {
        super.onPause();
        _db.close();
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
            View add_view = findViewById(R.id.view_add_new);
            if(add_view.getVisibility() == View.VISIBLE) {
                hideAddView();
            } else {
                mTitleText.setText("");
                mFavorite.setSelected(true);
                mNotification.setSelected(true);
                mFavorite.setColorFilter(getResources().getColor(R.color.primary));
                mNotification.setColorFilter(getResources().getColor(R.color.primary));
                showAddView();
            }
        }
        return super.onOptionsItemSelected(item);
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
