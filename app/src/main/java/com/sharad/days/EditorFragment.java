package com.sharad.days;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sharad.common.CircleButton;
import com.sharad.common.DatePickerFragment;
import com.sharad.common.TimePickerFragment;
import com.sharad.reminder.EventNotification;
import com.sharad.reminder.ReminderManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditorFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_COLOR_ID = "color_id";

    private ImageButton mFavorite;
    private ImageButton mRepeat;
    private ImageButton mPalette;
    private ImageButton mSave;
    private ImageButton mCancel;
    private EditText    mTitleText;
    private TextView    mDateText;
    private TextView    mTimeText;
    private RelativeLayout  mColorPicker;
    private RelativeLayout  mLabelPicker;
    private RelativeLayout  mEditorView;
    private LinearLayout    mRepeatView;
    private FrameLayout     mMaskView;
    private int             mColorId;
    private int             mLabelId;
    private int             mRepeatType;
    DataProvider _db;
    private Date mDate;
    private long _eventId;

    private TextView    mRepeatYearly;
    private TextView    mRepeatMonthly;
    private TextView    mRepeatWeekly;
    private TextView    mRepeatDays;
    private EditText    mRepeatDaysX;

    private ImageButton labelButtons[];

    public final static int NEXT_CLOSE_EDITOR = 0;
    public final static int NEXT_ADDED_EDITOR = 1;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param colorId  - color Id to be selected.
     * @return A new instance of fragment EditorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditorFragment newInstance(int colorId) {
        EditorFragment fragment = new EditorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLOR_ID, colorId);
        fragment.setArguments(args);
        return fragment;
    }

    public EditorFragment() {
        //
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mColorId = getArguments().getInt(ARG_COLOR_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_editor, container, false);
        initView(view);
        return view;
    }

    private void initView(final View rootView) {
        RelativeLayout background = (RelativeLayout) rootView.findViewById(R.id.close_me);
        background.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                showNextView(NEXT_CLOSE_EDITOR);
            }
        });

        mPalette = (ImageButton) rootView.findViewById(R.id.btn_palette);
        mPalette.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                showColorPicker();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[] attrs = new int[]{android.R.attr.selectableItemBackgroundBorderless};
            mPalette.setBackground(getActivity().obtainStyledAttributes(attrs).getDrawable(0));
        }

        mLabelId = 0;
        mFavorite = (ImageButton) rootView.findViewById(R.id.btn_favorite);
        mFavorite.setImageResource(Event.LabelArray[0]);
        mFavorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                showLabelPicker();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[] attrs = new int[]{android.R.attr.selectableItemBackgroundBorderless};
            mFavorite.setBackground(getActivity().obtainStyledAttributes(attrs).getDrawable(0));
        } else {
            int[] attrs = new int[]{android.R.attr.selectableItemBackground};
            mFavorite.setBackground(getActivity().obtainStyledAttributes(attrs).getDrawable(0));
        }

        mRepeat = (ImageButton) rootView.findViewById(R.id.btn_repeat);
        mRepeatView = (LinearLayout) rootView.findViewById(R.id.repeat_days);
        mRepeat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                mRepeat.setSelected(!mRepeat.isSelected());
                float alpha = mRepeat.isSelected() ? 1.0f : 0.3f;
                mRepeat.setAlpha(alpha);
                if(mRepeat.isSelected()) {
                    mRepeatView.setVisibility(View.VISIBLE);
                } else {
                    mRepeatView.setVisibility(View.GONE);
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[] attrs = new int[]{android.R.attr.selectableItemBackgroundBorderless};
            mRepeat.setBackground(getActivity().obtainStyledAttributes(attrs).getDrawable(0));
        } else {
            int[] attrs = new int[]{android.R.attr.selectableItemBackground};
            mRepeat.setBackground(getActivity().obtainStyledAttributes(attrs).getDrawable(0));
        }

        mSave = (ImageButton) rootView.findViewById(R.id.btn_save);
        mSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                String title = mTitleText.getText().toString();
                if(title.equals("")) { title = "(No Title)"; }
                int repeatId = Event.REPEAT_NEVER;
                if(mRepeat.isSelected()) {
                    if (mRepeatType == Event.REPEAT_DAYS) {
                        if (mRepeatDaysX.getText().toString().length() > 0) {
                            repeatId = Integer.valueOf(mRepeatDaysX.getText().toString());
                        } else {
                            repeatId = 0;
                        }
                    } else {
                        repeatId = mRepeatType;
                    }
                }
                long eventId;
                if(_eventId != -1) {
                    eventId = _eventId;
                    _db.updateEvent(new Event(_eventId, title, mDate, mColorId, repeatId, mLabelId));
                } else {
                    eventId = _db.insertEvent(new Event(0, title, mDate, mColorId, repeatId, mLabelId));
                }
                showNextView(NEXT_ADDED_EDITOR);
                Calendar c = Calendar.getInstance();
                c.setTime(mDate);
                new ReminderManager(getActivity()).setReminder(eventId, c);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[] attrs = new int[]{android.R.attr.selectableItemBackgroundBorderless};
            mSave.setBackground(getActivity().obtainStyledAttributes(attrs).getDrawable(0));
        } else {
            int[] attrs = new int[]{android.R.attr.selectableItemBackground};
            mSave.setBackground(getActivity().obtainStyledAttributes(attrs).getDrawable(0));
        }

        mCancel = (ImageButton) rootView.findViewById(R.id.btn_cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                showNextView(NEXT_CLOSE_EDITOR);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[] attrs = new int[]{android.R.attr.selectableItemBackgroundBorderless};
            mCancel.setBackground(getActivity().obtainStyledAttributes(attrs).getDrawable(0));
        } else {
            int[] attrs = new int[]{android.R.attr.selectableItemBackground};
            mCancel.setBackground(getActivity().obtainStyledAttributes(attrs).getDrawable(0));
        }

        mTitleText = (EditText) rootView.findViewById(R.id.txt_title);

        SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy");
        mDate = getCurrentTime();
        mDateText = (TextView) rootView.findViewById(R.id.txt_date);
        mDateText.setText(df.format(mDate));
        mDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setTime(mDate);
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        SimpleDateFormat tf = new SimpleDateFormat("hh:mm a");
        mTimeText = (TextView) rootView.findViewById(R.id.txt_time);
        mTimeText.setText(tf.format(mDate));
        mTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment newFragment = new TimePickerFragment();
                newFragment.setTime(mDate);
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });

        mEditorView = (RelativeLayout) rootView.findViewById(R.id.view_add_new);
        mEditorView.setVisibility(View.VISIBLE);
        mColorId = getResources().getColor(R.color.palette5);
        mEditorView.setBackgroundColor(mColorId);

        mMaskView = (FrameLayout) rootView.findViewById(R.id.maskTransition);
        mMaskView.setVisibility(View.GONE);
        mMaskView.setBackgroundColor(mColorId);

        setupColorPicker(rootView);
        setupLabelPicker(rootView);
        setupRepeatPicker(rootView);
    }

    private void setupRepeatPicker(View rootView) {
        mRepeatYearly = (TextView) rootView.findViewById(R.id.txt_repeat_yearly);
        mRepeatYearly.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                setRepeatType(Event.REPEAT_YEARLY);
            }
        });

        mRepeatMonthly = (TextView) rootView.findViewById(R.id.txt_repeat_monthly);
        mRepeatMonthly.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                setRepeatType(Event.REPEAT_MONTHLY);
            }
        });

        mRepeatWeekly = (TextView) rootView.findViewById(R.id.txt_repeat_weekly);
        mRepeatWeekly.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                setRepeatType(Event.REPEAT_WEEKLY);
            }
        });

        mRepeatDaysX = (EditText) rootView.findViewById(R.id.txt_repeat_days_num);
        mRepeatDaysX.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                setRepeatType(Event.REPEAT_DAYS);
            }
        });
        mRepeatDaysX.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (hasFocus) {
                    imm.showSoftInput(mRepeatDaysX, InputMethodManager.SHOW_IMPLICIT);
                    setRepeatType(Event.REPEAT_DAYS);
                } else {
                    imm.hideSoftInputFromWindow(mRepeatDaysX.getWindowToken(), 0);
                }
            }
        });

        mRepeatDays = (TextView) rootView.findViewById(R.id.txt_repeat_days);
        mRepeatDays.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                setRepeatType(Event.REPEAT_DAYS);
                mRepeatDaysX.requestFocus();
            }
        });

        setRepeatType(Event.REPEAT_NEVER);
    }

    private Date getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        switch (minute / 15) {
            case 0:
                cal.set(Calendar.MINUTE, 0);
                break;
            case 1:
            case 2:
                cal.set(Calendar.MINUTE, 30);
                break;
            default:
                cal.set(Calendar.HOUR_OF_DAY, hour+1);
                cal.set(Calendar.MINUTE, 0);
                break;
        }
        return cal.getTime();
    }

    private void setupLabelPicker(View rootView) {
        mLabelPicker = (RelativeLayout) rootView.findViewById(R.id.labelPicker);
        mLabelPicker.setVisibility(View.GONE);

        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        mLabelPicker.removeAllViews();
        mLabelPicker.addView(relativeLayout, params);

        LinearLayout linearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        relativeLayout.addView(linearLayout, params1);

        labelButtons = new ImageButton[Event.LabelArray.length];
        for (int i = 0; i < (Event.LabelArray.length/6); i++) {
            LinearLayout linearLayout1 = new LinearLayout(getContext());
            linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.addView(linearLayout1, params1);
            for (int j = 0; j < 6; j++) {
                ImageButton ib = newLabelButton(Event.LabelArray[i * 6 + j]);
                labelButtons[i * 6 + j] = ib;
                linearLayout1.addView(ib);
            }
        }
    }

    private ImageButton newLabelButton(final int label) {
        final ImageButton ib = new ImageButton(getContext());
        ib.setImageResource(label);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[] attrs = new int[]{android.R.attr.selectableItemBackgroundBorderless};
            ib.setBackground(getActivity().obtainStyledAttributes(attrs).getDrawable(0));
        } else {
            int[] attrs = new int[]{android.R.attr.selectableItemBackground};
            ib.setBackground(getActivity().obtainStyledAttributes(attrs).getDrawable(0));
        }

        ib.setColorFilter(getResources().getColor(R.color.primary_dark));
        ib.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                int[] location = new int[2];
                ib.getLocationOnScreen(location);
                mLabelId = Event.getLabelId(label);
                mFavorite.setImageResource(label);
                hidePopupView(mLabelPicker, new Point(location[0] + 60, location[1] - 100));
            }
        });
        return ib;
    }

    private void setupColorPicker(View rootView) {
        mColorPicker = (RelativeLayout) rootView.findViewById(R.id.colorPicker);
        mColorPicker.setVisibility(View.GONE);

        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        mColorPicker.removeAllViews();
        mColorPicker.addView(relativeLayout, params);

        LinearLayout linearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        relativeLayout.addView(linearLayout, params1);

        int[] palette = getResources().getIntArray(R.array.palette);
        for (int i = 0; i < (palette.length/5); i++) {
            LinearLayout linearLayout1 = new LinearLayout(getContext());
            linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.addView(linearLayout1, params1);
            for (int j = 0; j < 5; j++) {
                linearLayout1.addView(newPaletteButton(palette[i * 5 + j]));
            }
        }
    }

    private CircleButton newPaletteButton(final int color) {
        final CircleButton cb = new CircleButton(getContext());
        int size = getResources().getDimensionPixelSize(R.dimen.color_circle_diameter);
        cb.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        cb.setColor(color);
        cb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                int[] location = new int[2];
                cb.getLocationOnScreen(location);
                mColorId = color;
                mEditorView.setBackgroundColor(mColorId);
                mMaskView.setBackgroundColor(mColorId);
                hidePopupView(mColorPicker, new Point(location[0] + 60, location[1] - 100));
            }
        });
        return cb;
    }

    private void showColorPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[] location = new int[2];
            mPalette.getLocationOnScreen(location);
            Point center = new Point(location[0], location[1]);
            int finalRadius = Math.max(mColorPicker.getWidth(), mColorPicker.getHeight());
            Animator anim = ViewAnimationUtils.createCircularReveal(mColorPicker, center.x, center.y, 0, finalRadius);
            anim.setDuration(300);
            mColorPicker.setVisibility(View.VISIBLE);
            anim.start();
        } else {
            mColorPicker.setVisibility(View.VISIBLE);
        }
    }

    private void showLabelPicker() {
        for (int i = 0; i < labelButtons.length; i++) {
            labelButtons[i].setColorFilter(mColorId);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[] location = new int[2];
            mFavorite.getLocationOnScreen(location);
            Point center = new Point(location[0], location[1]);
            int finalRadius = Math.max(mLabelPicker.getWidth(), mLabelPicker.getHeight());
            Animator anim = ViewAnimationUtils.createCircularReveal(mLabelPicker, center.x, center.y, 0, finalRadius);
            anim.setDuration(300);
            mLabelPicker.setVisibility(View.VISIBLE);
            anim.start();
        } else {
            mLabelPicker.setVisibility(View.VISIBLE);
        }
    }

    public void hidePopupView(final View view, Point center) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int finalRadius = mMaskView.getWidth();
            Animator anim = ViewAnimationUtils.createCircularReveal(mMaskView, center.x, center.y, 0, finalRadius);
            anim.setDuration(300);
            mMaskView.setVisibility(View.VISIBLE);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.GONE);
                    mMaskView.setVisibility(View.GONE);
                }
            });
            anim.start();
        } else {
            view.setVisibility(View.GONE);
        }
    }

    public void showNextView(int next) {
        if (mListener != null) {
            mListener.onEditorUpdate(next);
        }
    }

    public void setNewEvent(Event event) {
        mColorPicker.setVisibility(View.GONE);
        mLabelPicker.setVisibility(View.GONE);
        mMaskView.setVisibility(View.GONE);
        SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy");
        SimpleDateFormat tf = new SimpleDateFormat("hh:mm a");
        if(event != null) {
            _eventId = event.get_id();
            mTitleText.setText(event.get_title());
            mFavorite.setImageResource(event.get_favorite());
            setRepeatType(event.get_repeat());
            mDate = event.get_startDate();
            mColorId = event.get_colorId();
            mLabelId = event.get_favoriteIndex();
        } else {
            _eventId = -1;
            mTitleText.setText("");
            setRepeatType(Event.REPEAT_NEVER);
            mDate = getCurrentTime();
        }
        mDateText.setText(df.format(mDate));
        mTimeText.setText(tf.format(mDate));
        mEditorView.setBackgroundColor(mColorId);
        mMaskView.setBackgroundColor(mColorId);
    }

    private void setRepeatType(int repeat) {
        mRepeatType = repeat;
        if(repeat == Event.REPEAT_NEVER) {
            mRepeatView.setVisibility(View.GONE);
            mRepeat.setSelected(false);
            mRepeat.setAlpha(0.3f);
        } else {
            mRepeatView.setVisibility(View.VISIBLE);
            mRepeat.setSelected(true);
            mRepeat.setAlpha(1.0f);
        }
        mRepeatYearly.setTextColor(getResources().getColor(R.color.light_white));
        mRepeatMonthly.setTextColor(getResources().getColor(R.color.light_white));
        mRepeatWeekly.setTextColor(getResources().getColor(R.color.light_white));
        mRepeatDays.setTextColor(getResources().getColor(R.color.light_white));
        mRepeatDaysX.setTextColor(getResources().getColor(R.color.light_white));
        mRepeatDaysX.setHintTextColor(getResources().getColor(R.color.light_white));
        switch (repeat) {
            case Event.REPEAT_YEARLY:
                mRepeatYearly.setTextColor(Color.WHITE);
                mRepeatDaysX.clearFocus();
                break;
            case Event.REPEAT_MONTHLY:
                mRepeatMonthly.setTextColor(Color.WHITE);
                mRepeatDaysX.clearFocus();
                break;
            case Event.REPEAT_WEEKLY:
                mRepeatWeekly.setTextColor(Color.WHITE);
                mRepeatDaysX.clearFocus();
                break;
            case Event.REPEAT_DAYS:
                mRepeatDays.setTextColor(Color.WHITE);
                mRepeatDaysX.setTextColor(Color.WHITE);
                mRepeatDaysX.setHintTextColor(Color.WHITE);
                mRepeatDaysX.requestFocus();
                break;
            case Event.REPEAT_NEVER:
                mRepeatDaysX.setText("");
                break;
            default:
                mRepeatDaysX.setText(""+repeat);
                mRepeatDays.setTextColor(Color.WHITE);
                mRepeatDaysX.setTextColor(Color.WHITE);
                mRepeatDaysX.setHintTextColor(Color.WHITE);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        _db = new DataProvider(getActivity());
        _db.open();
    }

    @Override
    public void onPause() {
        super.onPause();
        _db.close();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void dateSelected(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DATE, day);
        mDate = c.getTime();
        SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy");
        mDateText.setText(df.format(mDate));
    }

    public void timeSelected(int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.setTime(mDate);
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        mDate = c.getTime();
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        mTimeText.setText(df.format(mDate));
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onEditorUpdate(int next);
    }
}
