package com.sharad.days;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

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
    DataProvider _db;
    int mYear;
    int mMonth;
    int mDay;

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
                int resId = mFavorite.isSelected() ? R.color.primary : R.color.primary_light;
                mFavorite.setColorFilter(getResources().getColor(resId));
            }
        });

        mNotification = (ImageButton) rootView.findViewById(R.id.btn_notification);
        mNotification.setSelected(true);
        mNotification.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                mNotification.setSelected(!mNotification.isSelected());
                int resId = mNotification.isSelected() ? R.color.primary : R.color.primary_light;
                mNotification.setColorFilter(getResources().getColor(resId));
            }
        });

        mSave = (ImageButton) rootView.findViewById(R.id.btn_save);
        mSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                String title = getItemTitle();
                if(title.length() > 0) {
                    _db.insertEvent(new Event(0, mTitleText.getText().toString(),
                            new Date(mYear, mMonth, mDay), 0,
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

        final SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy");
        Calendar cal = Calendar.getInstance();
        mDateText = (EditText) rootView.findViewById(R.id.txt_date);
        mDateText.setText(df.format(cal.getTime()));
        mDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                mYear = mcurrentDate.get(Calendar.YEAR);
                mMonth = mcurrentDate.get(Calendar.MONTH);
                mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog mDatePicker = new DatePickerDialog(getActivity().getApplicationContext(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int year, int month, int day) {
                        EditText field = (EditText) rootView.findViewById(R.id.txt_date);
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

        mColorPalette = (RelativeLayout) rootView.findViewById(R.id.colorPicker);
        mColorPalette.setVisibility(View.GONE);

        mEditorView = (LinearLayout) rootView.findViewById(R.id.view_add_new);
        mEditorView.setVisibility(View.VISIBLE);

        configurePaletteButton(rootView, R.id.palette_cb0, R.color.palette0);
    }

    private void configurePaletteButton(View rootView, int palette_cb0, int palette0) {
        final ImageView cb = (ImageView) rootView.findViewById(palette_cb0);
        cb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                int[] location = new int[2];
                cb.getLocationOnScreen(location);
                Point center = new Point(location[0], location[1]);
                hideColorPalette(center);
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
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mEditorView.setVisibility(View.GONE);
            }
        });
        anim.start();
    }

    public void hideColorPalette(Point center) {
        int finalRadius = mEditorView.getWidth();
        Animator anim = ViewAnimationUtils.createCircularReveal(mEditorView, center.x, center.y, 0, finalRadius);
        anim.setDuration(300);
        mEditorView.setVisibility(View.VISIBLE);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mColorPalette.setVisibility(View.GONE);
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
        if(event != null) {
            mTitleText.setText(event.get_title());
            mFavorite.setSelected(event.is_favorite());
            mNotification.setSelected(event.is_notify());
        } else {
            mTitleText.setText("");
            mFavorite.setSelected(true);
            mNotification.setSelected(true);
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
