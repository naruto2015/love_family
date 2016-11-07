package com.jiedu.project.lovefamily.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;
import android.view.View;

import com.jiedu.project.lovefamily.R;

/**
 * Created by naruto on 2016/4/18.
 */
public class MySelfDialog {
    Context context;
    AlertDialog alertDialog;

    TextView tv_close;

    public MySelfDialog(Context context){
        this.context=context;
        alertDialog=new AlertDialog.Builder(context)
                .setCancelable(false)
                .create();
        alertDialog.show();
        Window window=alertDialog.getWindow();
        window.setContentView(R.layout.alertdialog);
        tv_close= (TextView) window.findViewById(R.id.tv_close);


    }

    public void setPositiveButton(final View.OnClickListener listener){

        tv_close.setOnClickListener(listener);
    }

    public void dismiss(){
        alertDialog.dismiss();
    }

}
