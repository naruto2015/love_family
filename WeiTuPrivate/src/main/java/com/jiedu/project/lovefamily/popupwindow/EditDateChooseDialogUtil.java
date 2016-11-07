package com.jiedu.project.lovefamily.popupwindow;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.jiedu.project.lovefamily.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 2016/4/5.
 */
public class EditDateChooseDialogUtil {

    Dialog dialog;
    View view;
    Context context;
    TextView dateTextView;
    Button submit;
    Button cancel;
    DatePicker datePicker;
    Calendar date;
    int year2;
    int month;
    int day;
    public void showDialog(Context context,TextView dateText) {
        dateTextView=dateText;
        this.context = context;
        if (null == dialog) {
            dialog = new Dialog(context, R.style.NoFrameDialog);
            view = LayoutInflater.from(context).inflate(R.layout.dialog_date_choose, null);
            submit= (Button) view.findViewById(R.id.submit);
            cancel= (Button) view.findViewById(R.id.cancel);
            datePicker= (DatePicker) view.findViewById(R.id.date_view);


           // SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

            Calendar now=Calendar.getInstance();
            year2=now.get(Calendar.YEAR);
            month=now.get(Calendar.MONTH);
            day=now.get(Calendar.DAY_OF_MONTH);


            datePicker.init(year2, month, day, new DatePicker.OnDateChangedListener() {

                @Override
                public void onDateChanged(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {


                    if(year==year2 && monthOfYear>month){
                        view.init(year2, month, day, this);
                    }

                    if(year==year2 && monthOfYear==month && dayOfMonth>day){
                        view.init(year2, month, day, this);
                    }

                    if (isDateAfter(view)) {
                        view.init(year2, month, day, this);
                    }
                    if (isDateBefore(view)) {
                        view.init(1949, 1, 1, this);
                    }



                }

                private boolean isDateAfter(DatePicker tempView) {
                  /* if (tempView.getYear() > year2 ) {
                        return true;
                    } else*/ if(tempView.getYear() > year2){

                        return true;
                    }else{
                        return false;
                    }



                }

                private boolean isDateBefore(DatePicker tempView) {
                    if (tempView.getYear() < 1949) {
                        return true;
                    } else
                        return false;
                }
            });

            dialog.setContentView(view);
        }
        if(!TextUtils.isEmpty(dateText.getText().toString())){
            date=  ConverToDate(dateText.getText().toString());
            if(null!=date){
                Log.e("0011","年"+date.get(Calendar.YEAR)+"月"+date.get(Calendar.MONTH)+"日"+date.get(Calendar.DATE));
                datePicker.updateDate(date.get(Calendar.YEAR),date.get(Calendar.MONTH),date.get(Calendar.DATE));
            }

        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String s=datePicker.getYear()+"-"+(datePicker.getMonth()+1 ) +"-"+datePicker.getDayOfMonth();
                dateTextView.setText(s);

            dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //把字符串转为日期
    public static Calendar ConverToDate(String strDate)
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(df.parse(strDate));
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
