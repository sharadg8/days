package com.sharad.days;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sharad.common.CircleButton;
import com.sharad.common.DatePickerFragment;

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
    private EditText    mDateText;
    private RelativeLayout  mColorPalette;
    private LinearLayout    mEditorView;
    private LinearLayout    mMaskView;
    private int             mColorId;
    DataProvider _db;
    private Date mDate;

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
        mPalette = (ImageButton) rootView.findViewById(R.id.btn_palette);
        mPalette.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                showColorPalette();
            }
        });

        mFavorite = (ImageButton) rootView.findViewById(R.id.btn_favorite);
        mFavorite.setSelected(true);
        mFavorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                mFavorite.setSelected(!mFavorite.isSelected());
                float alpha = mFavorite.isSelected() ? 1.0f : 0.3f;
                mFavorite.setAlpha(alpha);
            }
        });

        mNotification = (ImageButton) rootView.findViewById(R.id.btn_notification);
        mNotification.setSelected(true);
        mNotification.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                mNotification.setSelected(!mNotification.isSelected());
                float alpha = mNotification.isSelected() ? 1.0f : 0.3f;
                mNotification.setAlpha(alpha);
            }
        });

        mSave = (ImageButton) rootView.findViewById(R.id.btn_save);
        mSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                String title = getItemTitle();
                if(title.length() > 0) {
                    _db.insertEvent(new Event(0, mTitleText.getText().toString(),
                            mDate, mColorId,
                            mNotification.isSelected(), mFavorite.isSelected()));
                    showNextView(NEXT_ADDED_EDITOR);
                }
            }
        });

        mCancel = (ImageButton) rootView.findViewById(R.id.btn_cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                showNextView(NEXT_CLOSE_EDITOR);
            }
        });

        mTitleText = (EditText) rootView.findViewById(R.id.txt_title);

        SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy");
        Calendar cal = Calendar.getInstance();
        mDate = cal.getTime();
        mDateText = (EditText) rootView.findViewById(R.id.txt_date);
        mDateText.setText(df.format(mDate));
        mDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        mColorPalette = (RelativeLayout) rootView.findViewById(R.id.colorPicker);
        mColorPalette.setVisibility(View.GONE);

        mEditorView = (LinearLayout) rootView.findViewById(R.id.view_add_new);
        mEditorView.setVisibility(View.VISIBLE);
        mColorId = getResources().getColor(R.color.palette5);
        mEditorView.setBackgroundColor(mColorId);

        mMaskView = (LinearLayout) rootView.findViewById(R.id.maskPalette);
        mMaskView.setVisibility(View.GONE);

        configurePaletteButton(rootView, R.id.palette_cb0, R.color.palette0);
        configurePaletteButton(rootView, R.id.palette_cb1, R.color.palette1);
        configurePaletteButton(rootView, R.id.palette_cb2, R.color.palette2);
        configurePaletteButton(rootView, R.id.palette_cb3, R.color.palette3);
        configurePaletteButton(rootView, R.id.palette_cb4, R.color.palette4);
        configurePaletteButton(rootView, R.id.palette_cb5, R.color.palette5);
        configurePaletteButton(rootView, R.id.palette_cb6, R.color.palette6);
        configurePaletteButton(rootView, R.id.palette_cb7, R.color.palette7);
        configurePaletteButton(rootView, R.id.palette_cb8, R.color.palette8);
        configurePaletteButton(rootView, R.id.palette_cb9, R.color.palette9);
    }

    private void configurePaletteButton(View rootView, int palette_cb, final int color) {
        final CircleButton cb = (CircleButton) rootView.findViewById(palette_cb);
        cb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                int[] location = new int[2];
                cb.getLocationOnScreen(location);
                mColorId = getResources().getColor(color);
                mEditorView.setBackgroundColor(mColorId);
                mMaskView.setBackgroundColor(mColorId);
                hideColorPalette(new Point(location[0] + 60, location[1] - 100));
            }
        });
    }

    private void showColorPalette() {
        int[] location = new int[2];
        mPalette.getLocationOnScreen(location);
        Point center = new Point(location[0], location[1]);

        int finalRadius = Math.max(mColorPalette.getWidth(), mColorPalette.getHeight());
        Animator anim = ViewAnimationUtils.createCircularReveal(mColorPalette, center.x, center.y, 0, finalRadius);
        anim.setDuration(300);
        mColorPalette.setVisibility(View.VISIBLE);
        anim.start();
    }

    public void hideColorPalette(Point center) {
        int finalRadius = mMaskView.getWidth();
        Animator anim = ViewAnimationUtils.createCircularReveal(mMaskView, center.x, center.y, 0, finalRadius);
        anim.setDuration(300);
        mMaskView.setVisibility(View.VISIBLE);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mColorPalette.setVisibility(View.GONE);
                mMaskView.setVisibility(View.GONE);
            }
        });
        anim.start();
    }

    private String getItemTitle() {
        mTitleText.setError(null);
        if(mTitleText.getText().toString().trim().length() == 0) {
            mTitleText.setError("Enter Title");
            return "";
        }
        return mTitleText.getText().toString();
    }

    public void showNextView(int next) {
        if (mListener != null) {
            mListener.onEditorUpdate(next);
        }
    }

    public void setNewEvent(Event event) {
        mColorPalette.setVisibility(View.GONE);
        mMaskView.setVisibility(View.GONE);
        SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy");
        if(event != null) {
            mTitleText.setText(event.get_title());
            mFavorite.setSelected(event.is_favorite());
            mNotification.setSelected(event.is_notify());
            mDateText.setText(df.format(event.get_startDate()));
        } else {
            mTitleText.setText("");
            mFavorite.setSelected(true);
            mNotification.setSelected(true);
            Calendar c = Calendar.getInstance();
            mDate = c.getTime();
            mDateText.setText(df.format(mDate));
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
        mDate = new Date(year - 1900, month, day);
        SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy");
        mDateText.setText(df.format(mDate));
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
