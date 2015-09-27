package com.sharad.days;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sharad on 28-Aug-15.
 */
public class Event implements Comparable<Event>{
    private long   _id;
	private Date   _startDate;
    private String _title;
    private int    _repeat, _favorite, _favoriteIndex;
    private int    _colorId;
    private int    _dayCount, _agoTogo;
    private boolean _updated;

    public static final int REPEAT_YEARLY   = -1;
    public static final int REPEAT_MONTHLY  = -2;
    public static final int REPEAT_WEEKLY   = -3;
    public static final int REPEAT_DAYS     = -4;
    public static final int REPEAT_NEVER    = 0;

    public static final int[] LabelArray = {
            R.drawable.ic_favorite_black_24dp,
            R.drawable.ic_cake_black_24dp,
            R.drawable.ic_star_black_24dp,
            R.drawable.ic_directions_bus_black_24dp,
            R.drawable.ic_school_black_24dp,
            R.drawable.ic_timer_black_24dp,
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_shopping_cart_black_24dp,
            R.drawable.ic_redeem_black_24dp,
            R.drawable.ic_work_black_24dp,
            R.drawable.ic_flight_takeoff_black_24dp,
            R.drawable.ic_attach_money_black_24dp,
            R.drawable.ic_thumb_up_black_24dp,
            R.drawable.ic_thumb_down_black_24dp,
            R.drawable.ic_movie_black_24dp,
            R.drawable.ic_insert_emoticon_black_24dp,
            R.drawable.ic_style_black_24dp,
            R.drawable.ic_call_black_24dp,
    };

    public static final int[] LabelArrayLarge = {
            R.drawable.ic_favorite_black_48dp,
            R.drawable.ic_cake_black_48dp,
            R.drawable.ic_star_black_48dp,
            R.drawable.ic_directions_bus_black_48dp,
            R.drawable.ic_school_black_48dp,
            R.drawable.ic_timer_black_48dp,
            R.drawable.ic_home_black_48dp,
            R.drawable.ic_shopping_cart_black_48dp,
            R.drawable.ic_redeem_black_48dp,
            R.drawable.ic_work_black_48dp,
            R.drawable.ic_flight_takeoff_black_48dp,
            R.drawable.ic_attach_money_black_48dp,
            R.drawable.ic_thumb_up_black_48dp,
            R.drawable.ic_thumb_down_black_48dp,
            R.drawable.ic_movie_black_48dp,
            R.drawable.ic_insert_emoticon_black_48dp,
            R.drawable.ic_style_black_48dp,
            R.drawable.ic_call_black_48dp,
    };

    Event(long id, String title, Date stDate, int colId, int repeat, int favorite) {
        _updated = false;
        _id = id;
        _title = title;
        _startDate = stDate;
        _colorId = colId;
        _repeat = repeat;
        _favoriteIndex = favorite;
        _favorite = favorite;
        if(favorite >= 0 && favorite < LabelArray.length) {
            _favorite = LabelArray[favorite];
        }

        Date date = _startDate;
        if(repeat != REPEAT_NEVER) {
            while((System.currentTimeMillis() - _startDate.getTime()) < 0) {
                Calendar c = Calendar.getInstance();
                c.setTime(_startDate);
                if(repeat == REPEAT_YEARLY) {
                    c.add(Calendar.YEAR, -1);
                } else if(repeat == REPEAT_MONTHLY) {
                    c.add(Calendar.MONTH, -1);
                } else if(repeat == REPEAT_WEEKLY) {
                    c.add(Calendar.DATE, -7);
                } else {
                    c.add(Calendar.DATE, -repeat);
                }
                _startDate = c.getTime();
            }
            while((System.currentTimeMillis() - _startDate.getTime()) > 0) {
                Calendar c = Calendar.getInstance();
                c.setTime(_startDate);
                if(repeat == REPEAT_YEARLY) {
                    c.add(Calendar.YEAR, 1);
                } else if(repeat == REPEAT_MONTHLY) {
                    c.add(Calendar.MONTH, 1);
                } else if(repeat == REPEAT_WEEKLY) {
                    c.add(Calendar.DATE, 7);
                } else {
                    c.add(Calendar.DATE, repeat);
                }
                _startDate = c.getTime();
            }

            _updated |= (date.getTime() != _startDate.getTime());
        }

        long diff = System.currentTimeMillis() - _startDate.getTime();
        float days = (float) diff / (24 * 60 * 60 * 1000);
        _dayCount = (int) Math.abs(days);
        _agoTogo = (diff > 0) ? R.drawable.ic_previous_black_24dp : R.drawable.ic_next_black_24dp;
    }

    public static int getLabelId(int labelResId) {
        int id = 0;
        for(int i=0; i<LabelArray.length; i++) {
            if(labelResId == LabelArray[i]) {
                id = i;
                break;
            }
        }
        return id;
    }

    public int getLabelLarge() {
        int index = 0;
        if(_favoriteIndex < LabelArrayLarge.length) { index = _favoriteIndex; }
        return LabelArrayLarge[index];
    }

	public void set_id(long id) {             _id = id;       }
    public long get_id() {             return _id;            }

    public Date get_startDate() {      return _startDate;     }
    public String get_title() {        return _title;         }
    public int get_favorite() {        return _favorite;      }
    public int get_favoriteIndex() {   return _favoriteIndex; }
    public int get_repeat() {          return _repeat;        }
    public int get_colorId() {         return _colorId;       }
    public int get_dayCount() {        return _dayCount;      }
    public int get_agoTogo() {         return _agoTogo;       }

    public boolean checkAndClearUpdated() {
        boolean updated = _updated;
        _updated = false;
        return updated;
    }

    public String get_dateText() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(_startDate);
        final SimpleDateFormat df;
        if(cal.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
            df = new SimpleDateFormat("dd MMMM");
        } else {
            df = new SimpleDateFormat("MMMM dd, yyyy");
        }
        String txt;
        if(_repeat == REPEAT_YEARLY) {
            txt = " [Y]";
        } else if(_repeat == REPEAT_MONTHLY) {
            txt = " [M]";
        } else if(_repeat == REPEAT_WEEKLY) {
            txt = " [W]";
        } else if(_repeat == REPEAT_NEVER) {
            txt = "";
        } else {
            txt = " ["+_repeat+" D]";
        }
        return df.format(cal.getTime()) + txt;
    }

    @Override
    public int compareTo(Event another) {
        return (this._dayCount < another._dayCount) ? -1 : 1;
    }
}
