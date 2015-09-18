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
	private Date   _startDate, _endDate;
    private String _title;
    
	public static final int STATUS_INVALID = 0;
	public static final int STATUS_ACTIVE  = 1;
	public static final int STATUS_CLOSED  = 2;
	public static final int STATUS_CANCEL  = 3;
	
    Event(long id, String title, Date stDate, Date endDate) {
        _id = id;
        _title = title;
        _startDate = stDate;
        _endDate = endDate;
    }

	public void set_id(long id) {             _id = id;      }

    public Date get_endDate() {        return _endDate;      }
    public Date get_startDate() {      return _startDate;    }
    public long get_id() {             return _id;           }
    public String get_title() {        return _title;        }

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
