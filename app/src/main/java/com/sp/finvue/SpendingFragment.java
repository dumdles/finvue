package com.sp.finvue;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.harrywhewell.scrolldatepicker.DayScrollDatePicker;
import com.harrywhewell.scrolldatepicker.OnDateSelectedListener;
import com.labstyle.darioweekviewdatepicker.DarioWeekViewDatePicker;

import org.joda.time.DateTime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SpendingFragment extends Fragment {
    DayScrollDatePicker mPicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalDateTime myDataObj = LocalDateTime.now();
        DateTimeFormatter myDayObj = DateTimeFormatter.ofPattern("dd");
        DateTimeFormatter myMonthObj = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter myYearObj = DateTimeFormatter.ofPattern("yyyy");

        // Logic for weekview
        final ThisLocalizedWeek usWeek = new ThisLocalizedWeek(Locale.US);
        String maindate = String.valueOf(usWeek.getFirstDay());
        int startdate = Integer.parseInt(maindate.substring(8, 10));
        int startmonth = Integer.parseInt(maindate.substring(5, 7));
        int startyear = Integer.parseInt(maindate.substring(0, 4));

        mPicker.setStartDate(startdate, startmonth, startyear);

        String endDate = String.valueOf(usWeek.getLastDay());

        int endDay = Integer.parseInt(endDate.substring(8, 10));
        int endMonth = Integer.parseInt(endDate.substring(5, 7));
        int endYear = Integer.parseInt(endDate.substring(0, 4));

        mPicker.setEndDate(endDay, endMonth, endYear);

        mPicker.getSelectedDate(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull Date date) {
                Toast.makeText(getActivity(), "Date: " + date, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_spending, container, false);

        // Find FAB
        FloatingActionButton fab = rootView.findViewById(R.id.newTransactionFAB);

        // Set click listener for FAB
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the dialog when FAB is clicked
                Intent intent = new Intent(getActivity(), NewTransaction.class);
                startActivity(intent);
            }
        });

        mPicker = rootView.findViewById(R.id.day_date_picker);

        return rootView;

    }

}