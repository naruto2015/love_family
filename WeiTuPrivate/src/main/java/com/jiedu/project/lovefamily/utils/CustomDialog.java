package com.jiedu.project.lovefamily.utils;

import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;
import android.widget.TextView;
import android.view.View;

import com.jiedu.project.lovefamily.R;

/**
 * Created by naruto on 2016/7/11.
 */
public class CustomDialog  {

    private Context context;
    private AlertDialog alertDialog;

    private TextView title,message,sure,cancle;
    public CustomDialog(Context context) {

        this.context=context;
        alertDialog=new AlertDialog.Builder(context,R.style.Dialog).create();
        alertDialog.show();
        Window window=alertDialog.getWindow();
        window.setContentView(R.layout.my_customer_dialog);

        title= (TextView) window.findViewById(R.id.title);
        message= (TextView) window.findViewById(R.id.message);
        sure= (TextView) window.findViewById(R.id.sure);
        cancle= (TextView) window.findViewById(R.id.cancel);

    }


    public void setTitle(String titleStr){
        title.setText(titleStr);
    }

    public void setMessage(String messageStr){
        message.setText(messageStr);
    }

    public void setPositiveButton(String text, View.OnClickListener listener){
        sure.setText(text);
        sure.setOnClickListener(listener);
    }


    public void setNegativeButton(String text, View.OnClickListener listener){
        cancle.setText(text);
        cancle.setOnClickListener(listener);
    }


    public void dismiss() {
        alertDialog.dismiss();
    }


}
