package com.jiedu.project.lovefamily.utils;

import android.content.Context;

import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.view.View;
import android.view.View.OnTouchListener;

import com.jiedu.project.lovefamily.R;

/**
 * Created by naruto on 2016/4/26.
 */
public class ShareUmengDialog  extends PopupWindow{


    private View view;
    private ImageView weixin,qq,weixinZong,qqZong;
    public ShareUmengDialog(Context context,View.OnClickListener itemsOnClick){

        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=inflater.inflate(R.layout.my_shareumeng_style,null);


        weixin= (ImageView) view.findViewById(R.id.weixin);
        qq= (ImageView) view.findViewById(R.id.qq);
        qqZong= (ImageView) view.findViewById(R.id.qqZong);
        weixinZong= (ImageView) view.findViewById(R.id.weixinZong);

        weixin.setOnClickListener(itemsOnClick);
        qqZong.setOnClickListener(itemsOnClick);
        qq.setOnClickListener(itemsOnClick);
        weixinZong.setOnClickListener(itemsOnClick);

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
        ColorDrawable dw = new ColorDrawable(0x90000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        view.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int height = view.findViewById(R.id.pop_layout).getTop();
                int y=(int) event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss();
                    }
                }
                return true;
            }
        });

    }

}
