package bignerdranch2nded.com.criminalintent2nded;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Juan on 9/3/2015.
 */
public class DatePickerFragment extends DialogFragment {

    public static final String EXTRA_DATE = "bignerdranch2nded.com.criminalintent2nded.date";

    private static final String ARG_DATE = "date";
    private DatePicker mDatePicker;

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        //Add arguments to the fragment
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //The FragmentManager of the hosting activity calls this method as part of putting the DialogFragment on screen.
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Retrieve argument
        Date date = (Date)getArguments().getSerializable(ARG_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //Inflate the layout
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        //Initialize date with the date pass from crimeFragment
        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
        mDatePicker.init(year, month, day, null);

        return new AlertDialog.Builder(getActivity())
                //Add a DatePicker widget to your AlertDialog using AlertDialog.Builder.setView(View v)
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();

                        Date date = new GregorianCalendar(year, month, day).getTime();

                        //Send date back to CrimeFragment
                        sendResult(Activity.RESULT_OK, date);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, Date date) {

        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        //getTargetRequestCode() is REQUEST_DATE=0 that you set in the CrimeFragment
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);

    }
}
