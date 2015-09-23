package com.sharad.days;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sharad.common.CircleButton;
import com.sharad.common.DatePickerFragment;
import com.sharad.common.TimePickerFragment;

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
    private ImageButton mNotification;
    private ImageButton mPalette;
    private ImageButton mSave;
    private ImageButton mCancel;
    private EditText    mTitleText;
    private TextView    mDateText;
    private TextView    mTimeText;
    private RelativeLayout  mColorPicker;
    private RelativeLayout  mLabelPicker;
    private LinearLayout    mEditorView;
    private FrameLayout     mMaskView;
    private int             mColorId;
    private int             mLabelId;
    private int             mNotifyId;
    DataProvider _db;
    private Date mDate;
    private long _eventId;

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
            TypedArray ta = getActivity().obtainStyledAttributes(attrs);
            Drawable rippleDrawable = ta.getDrawable(0);
            ta.recycle();
            mPalette.setBackground(rippleDrawable);
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
            TypedArray ta = getActivity().obtainStyledAttributes(attrs);
            Drawable rippleDrawable = ta.getDrawable(0);
            ta.recycle();
            mFavorite.setBackground(rippleDrawable);
        }

        mNotifyId = 0;
        mNotification = (ImageButton) rootView.findViewById(R.id.btn_notification);
        mNotification.setSelected(true);
        mNotification.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                mNotification.setSelected(!mNotification.isSelected());
                float alpha = mNotification.isSelected() ? 1.0f : 0.3f;
                mNotification.setAlpha(alpha);
            }
        });
        mNotification.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[] attrs = new int[]{android.R.attr.selectableItemBackgroundBorderless};
            TypedArray ta = getActivity().obtainStyledAttributes(attrs);
            Drawable rippleDrawable = ta.getDrawable(0);
            ta.recycle();
            mNotification.setBackground(rippleDrawable);
        }

        mSave = (ImageButton) rootView.findViewById(R.id.btn_save);
        mSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                String title = mTitleText.getText().toString();
                if(title.equals("")) { title = "(No Title)"; }
                if(_eventId != -1) {
                    _db.updateEvent(new Event(_eventId, title, mDate, mColorId, mNotifyId, mLabelId));
                } else {
                    _db.insertEvent(new Event(0, title, mDate, mColorId, mNotifyId, mLabelId));
                }
                showNextView(NEXT_ADDED_EDITOR);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[] attrs = new int[]{android.R.attr.selectableItemBackgroundBorderless};
            TypedArray ta = getActivity().obtainStyledAttributes(attrs);
            Drawable rippleDrawable = ta.getDrawable(0);
            ta.recycle();
            mSave.setBackground(rippleDrawable);
        }

        mCancel = (ImageButton) rootView.findViewById(R.id.btn_cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                showNextView(NEXT_CLOSE_EDITOR);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[] attrs = new int[]{android.R.attr.selectableItemBackgroundBorderless};
            TypedArray ta = getActivity().obtainStyledAttributes(attrs);
            Drawable rippleDrawable = ta.getDrawable(0);
            ta.recycle();
            mCancel.setBackground(rippleDrawable);
        }

        mTitleText = (EditText) rootView.findViewById(R.id.txt_title);

        SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy");
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        switch (minute / 15) {
            case 1:
                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, 0);
                break;
            case 2:
            case 3:
                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, 30);
                break;
            default:
                cal.set(Calendar.HOUR_OF_DAY, hour+1);
                cal.set(Calendar.MINUTE, 0);
                break;
        }
        mDate = cal.getTime();
        mDateText = (TextView) rootView.findViewById(R.id.txt_date);
        mDateText.setText(df.format(mDate));
        mDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
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

        mEditorView = (LinearLayout) rootView.findViewById(R.id.view_add_new);
        mEditorView.setVisibility(View.VISIBLE);
        mColorId = getResources().getColor(R.color.palette5);
        mEditorView.setBackgroundColor(mColorId);

        mMaskView = (FrameLayout) rootView.findViewById(R.id.maskTransition);
        mMaskView.setVisibility(View.GONE);
        mMaskView.setBackgroundColor(mColorId);

        setupColorPicker(rootView);
        setupLabelPicker(rootView);
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

        for (int i = 0; i < (Event.LabelArray.length/6); i++) {
            LinearLayout linearLayout1 = new LinearLayout(getContext());
            linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.addView(linearLayout1, params1);
            for (int j = 0; j < 6; j++) {
                linearLayout1.addView(newLabelButton(Event.LabelArray[i * 6 + j]));
            }
        }
    }

    private View newLabelButton(final int label) {
        final ImageButton ib = new ImageButton(getContext());
        ib.setImageResource(label);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[] attrs = new int[]{android.R.attr.selectableItemBackgroundBorderless};
            TypedArray ta = getActivity().obtainStyledAttributes(attrs);
            Drawable rippleDrawable = ta.getDrawable(0);
            ta.recycle();
            ib.setBackground(rippleDrawable);
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
                linearLayout1.addView(newPaletteButton(palette[i*5+j]));
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
            //mNotification.setSelected(event.is_notify());
            mDate = event.get_startDate();
            mColorId = event.get_colorId();
            mNotifyId = event.get_notify();
            mLabelId = event.get_favoriteIndex();
        } else {
            _eventId = -1;
            mTitleText.setText("");
            mNotification.setSelected(true);
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 9);
            c.set(Calendar.MINUTE, 0);
            mDate = c.getTime();
        }
        mDateText.setText(df.format(mDate));
        mTimeText.setText(tf.format(mDate));
        mEditorView.setBackgroundColor(mColorId);
        mMaskView.setBackgroundColor(mColorId);
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
