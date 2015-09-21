package com.sharad.days;

import android.content.Intent;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;


public class DetailsActivity extends ActionBarActivity {
    public final static String ID_KEY = "DetailsActivity$idKey";
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

        initViews();
    }

    private void initViews() {
        TextView days = (TextView) findViewById(R.id.d_days);
        TextView date = (TextView) findViewById(R.id.d_date);
        TextView title = (TextView) findViewById(R.id.d_title);
        ImageView label = (ImageView) findViewById(R.id.d_label);
        ImageView agoTogo = (ImageView) findViewById(R.id.d_ago_togo);

        days.setText(""+_event.get_dayCount());
        date.setText(_event.get_dateText());
        title.setText(_event.get_title());
        label.setImageResource(_event.get_favorite());
        agoTogo.setImageResource(_event.get_agoTogo());
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
                DataProvider db = new DataProvider(this);
                db.open();
                db.deleteEvent(_event.get_id());
                db.close();
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
