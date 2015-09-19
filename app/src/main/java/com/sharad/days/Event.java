package com.sharad.days;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sharad on 28-Aug-15.
 */
public class Event {
    private long   _id;
	private Date   _startDate;
    private String _title;
    private boolean _notify, _favorite;
    private int    _colorId;
    private int    _dayCount;

    private int    _state;
    
	public static final boolean NOTIFICATION_OFF = false;
    public static final boolean NOTIFICATION_ON  = true;

    public static final boolean FEVORITE_OFF = false;
    public static final boolean FEVORITE_ON  = true;

    public static final int STATE_NORMAL     = 0;
    public static final int STATE_EXPANDED   = 1;
    public static final int STATE_EDIT       = 2;
    public static final int STATE_DELETE     = 3;
    public static final int STATE_COLOR_PICK = 4;

    Event(long id, String title, Date stDate, int colId, boolean notify, boolean favorite) {
        _id = id;
        _title = title;
        _startDate = stDate;
        _colorId = colId;
        _notify = notify;
        _favorite = favorite;

        _dayCount = 36;

        _state = STATE_NORMAL;
    }

	public void set_id(long id) {             _id = id;      }
    public long get_id() {             return _id;           }

    public Date get_startDate() {      return _startDate;    }
    public String get_title() {        return _title;        }
    public boolean is_favorite() {     return _favorite;     }
    public boolean is_notify() {       return _notify;       }
    public int get_colorId() {         return _colorId;      }
    public int get_dayCount() {        return _dayCount;     }

    public int get_state() {           return _state;        }
    public void set_state(int state) {        _state = state;}

    public String get_info() {
        return " Sample info text";
    }

    public String get_startDateText() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(_startDate);
        final SimpleDateFormat df;
        if(cal.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
            df = new SimpleDateFormat("dd MMM");
        } else {
            df = new SimpleDateFormat("dd/MM/yyyy");
        }
        return df.format(cal.getTime());
    }
}
