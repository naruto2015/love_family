package com.jiedu.project.lovefamily.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.jiedu.project.lovefamily.R;

/**
 * Created by naruto on 2016/4/19.
 */
public class MyAddSyleDialog {

    Context context;
    AlertDialog alertDialog;

    TextView tongxunlu,cancle,scan;

    public MyAddSyleDialog(Context context){
        this.context=context;
        alertDialog=new AlertDialog.Builder(context)
                .setCancelable(false)
                .create();
        alertDialog.show();
        Window window=alertDialog.getWindow();
        window.setContentView(R.layout.add_style_dialog);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.mystyle);  //添加动画

        tongxunlu= (TextView) window.findViewById(R.id.tongxunlu);

        cancle= (TextView) window.findViewById(R.id.tv_cancle);
        scan= (TextView) window.findViewById(R.id.scan);

    }


    public void setTongxunluOnClick(final View.OnClickListener listener){
        tongxunlu.setOnClickListener(listener);
    }

    public void setCancleOnClick(final View.OnClickListener listener){
        cancle.setOnClickListener(listener);
    }

    public void setScanOnClick(final View.OnClickListener listener){
        scan.setOnClickListener(listener);
    }


    public void dismiss(){
        alertDialog.dismiss();
    }

}
