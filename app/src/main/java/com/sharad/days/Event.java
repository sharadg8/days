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
    private int    _notify, _favorite;
    private int    _colorId;
    private int    _dayCount, _agoTogo;

    public static final int[] LabelArray = {
            R.drawable.ic_favorite_black_24dp,
            R.drawable.ic_cake_black_24dp,
            R.drawable.ic_star_black_24dp,
            R.drawable.ic_directions_bus_black_24dp,
            R.drawable.ic_school_black_24dp,
            R.drawable.ic_timer_black_24dp,
            R.drawable.ic_account_balance_black_24dp,
            R.drawable.ic_shopping_cart_black_24dp,
            R.drawable.ic_redeem_black_24dp,
            R.drawable.ic_event_seat_black_24dp,
            R.drawable.ic_flight_takeoff_black_24dp,
            R.drawable.ic_attach_money_black_24dp,
            R.drawable.ic_thumb_up_black_24dp,
            R.drawable.ic_thumb_down_black_24dp,
            R.drawable.ic_movie_black_24dp,
            R.drawable.ic_insert_emoticon_black_24dp,
            R.drawable.ic_style_black_24dp,
            R.drawable.ic_notifications_black_24dp,
    };

    Event(long id, String title, Date stDate, int colId, int notify, int favorite) {
        _id = id;
        _title = title;
        _startDate = stDate;
        _colorId = colId;
        _notify = notify;
        _favorite = favorite;

        long diff = System.currentTimeMillis() - _startDate.getTime();
        float days = (float) diff / (24 * 60 * 60 * 1000);
        _dayCount = (int) Math.abs(days);
        _agoTogo = (days > 0.9f) ? R.drawable.ic_previous_black_24dp :
                ((days < 0.9f) ? R.drawable.ic_next_black_24dp : 0);
    }

	public void set_id(long id) {             _id = id;      }
    public long get_id() {             return _id;           }

    public Date get_startDate() {      return _startDate;    }
    public String get_title() {        return _title;        }
    public int get_favorite() {         return _favorite;     }
    public int get_notify() {           return _notify;       }
    public int get_colorId() {         return _colorId;      }
    public int get_dayCount() {        return _dayCount;     }
    public int get_agoTogo() {        return _agoTogo;     }

    public String get_dateText() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(_startDate);
        final SimpleDateFormat df;
        if(cal.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
            df = new SimpleDateFormat("dd MMMM");
        } else {
            df = new SimpleDateFormat("MMMM dd, yyyy");
        }
        return df.format(cal.getTime());
    }

    @Override
    public int compareTo(Event another) {
        return (this._dayCount < another._dayCount) ? -1 : 1;
    }
}
