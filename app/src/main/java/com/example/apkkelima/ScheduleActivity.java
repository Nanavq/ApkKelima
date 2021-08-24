package com.example.apkkelima;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.lang.reflect.InvocationTargetException;


public class ScheduleActivity extends AppCompatActivity {
    CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Toast.makeText(getApplicationContext(), year + "-" + month + "-" + dayOfMonth, Toast.LENGTH_SHORT).show();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment previous = getSupportFragmentManager().findFragmentByTag(CustomMyLibDialog.TAG);
                if(previous != null){
                    transaction.remove(previous);
                }
                CustomMyLibDialog dialog = CustomMyLibDialog.newInstance();
                dialog.show(transaction, CustomMyLibDialog.TAG);
            }
        });
//        try {
//
//        } catch (Exception e) {
//
//            // generic exception handling
//            e.printStackTrace();
//        }
    }
}
//    public void calendarView(){
//        Intent calendarView = new Intent(ScheduleActivity.this, ScheduleCrud.class );
//        startActivity(calendarView);
//    }
