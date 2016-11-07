package com.jiedu.project.lovefamily.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.jiedu.project.lovefamily.R;

/**
 * Created by naruto on 2016/5/17.
 */
public class EditPhoneDialog {
    Context context;
    AlertDialog alertDialog;

    TextView et_phone,et_number,et_message,et_cancle;

    public EditPhoneDialog(Context context,String number){
        this.context=context;
        alertDialog=new AlertDialog.Builder(context)
                .setCancelable(false)
                .create();
        alertDialog.show();
        Window window=alertDialog.getWindow();
        window.setContentView(R.layout.edit_phone);
        et_phone= (TextView) window.findViewById(R.id.et_phone);
        et_cancle= (TextView) window.findViewById(R.id.et_cancle);
        et_message= (TextView) window.findViewById(R.id.et_message);
        et_number= (TextView) window.findViewById(R.id.et_number);

        init(number);
    }

    public void init(String number){
         et_number.setText(number+"");
    }


    public void setPhoneOnClick(final View.OnClickListener listener){
        et_phone.setOnClickListener(listener);
    }

    public void setMessageOnClick(final View.OnClickListener listener){
        et_message.setOnClickListener(listener);
    }

    public void setCancleOnClick(final View.OnClickListener listener){
        et_cancle.setOnClickListener(listener);
    }




    public void dismiss(){
        alertDialog.dismiss();
    }
}
