package com.jiedu.project.lovefamily.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.jiedu.project.lovefamily.R;

/**
 * Created by naruto on 2016/4/19.
 */
public class UpLoadDialog {


    Context context;
    AlertDialog alertDialog;

    TextView yi,shi,sanshi,liushi,cancle;

    public UpLoadDialog(Context context){
        this.context=context;
        alertDialog=new AlertDialog.Builder(context)
                .setCancelable(false)
                .create();
        alertDialog.show();
        Window window=alertDialog.getWindow();
        window.setContentView(R.layout.upload_dialog);
        yi= (TextView) window.findViewById(R.id.yi);
        shi= (TextView) window.findViewById(R.id.shi);
        sanshi= (TextView) window.findViewById(R.id.sanshi);
        liushi= (TextView) window.findViewById(R.id.liushi);
        cancle= (TextView) window.findViewById(R.id.cancle);
    }

    public void setYiluOnClick(final View.OnClickListener listener){
        yi.setOnClickListener(listener);
    }

    public void setShiOnClick(final View.OnClickListener listener){
        shi.setOnClickListener(listener);
    }

    public void setsanshiOnClick(final View.OnClickListener listener){
        sanshi.setOnClickListener(listener);
    }

    public void setliushiOnClick(final View.OnClickListener listener){
        liushi.setOnClickListener(listener);
    }

    public void setCancleOnClick(final View.OnClickListener listener){
        cancle.setOnClickListener(listener);
    }

    public void dismiss(){
        alertDialog.dismiss();
    }

}
