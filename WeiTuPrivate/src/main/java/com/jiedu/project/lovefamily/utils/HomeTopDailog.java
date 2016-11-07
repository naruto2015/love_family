package com.jiedu.project.lovefamily.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jiedu.project.lovefamily.R;

/**
 * Created by naruto on 2016/5/17.
 */
public class HomeTopDailog extends PopupWindow{
    Context context;
    AlertDialog alertDialog;
    private View view;

    private TextView home_jia,home_msg,home_share;

    public HomeTopDailog(Context context,View.OnClickListener itemsOnClick){

        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=inflater.inflate(R.layout.home_top,null);


        //设置SelectPicPopupWindow的View
        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        //this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        //ColorDrawable dw = new ColorDrawable(0x90000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        //this.setBackgroundDrawable(dw);

        home_jia= (TextView) view.findViewById(R.id.home_jia);
        home_msg= (TextView) view.findViewById(R.id.home_msg);
        home_share= (TextView) view.findViewById(R.id.home_share);

        home_jia.setOnClickListener(itemsOnClick);
        home_msg.setOnClickListener(itemsOnClick);
        home_share.setOnClickListener(itemsOnClick);


        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int height = view.findViewById(R.id.home_line).getTop();
                int y=(int) event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y>height){
                        dismiss();
                    }
                }
                return true;
            }
        });

    }


}
