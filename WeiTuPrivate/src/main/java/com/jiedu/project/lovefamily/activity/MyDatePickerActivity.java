package com.jiedu.project.lovefamily.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.DatePicker;

import com.jiedu.project.lovefamily.R;

public class MyDatePickerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_date_picker);

        final DatePicker datePicker = (DatePicker) findViewById(R.id.datepicker);


    }
}
