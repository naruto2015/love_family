package com.jiedu.project.lovefamily.popupwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jiedu.project.lovefamily.R;

/**
 * Created by Administrator on 2016/3/7.
 */
public class PhoneStyleUtil implements View.OnClickListener {
    private PopupWindow popupWindow;
    private View view;
    private Button ios;
    private Button android;
    private Button saipan;
    TextView bg_gray;
    private Context context;
    private String titleStr;
    private  Button btn;

    private RelativeLayout android_relay;
    private RelativeLayout iphone_relay;

    public void showPopupwindow(Button v, Context context) {
        btn=v;
        if (popupWindow == null) {
            this.context = context;
            view = LayoutInflater.from(context).inflate(R.layout.phone_style_popupwindow, null);
           /* ios = (Button) view.findViewById(R.id.phone_ios);
            android = (Button) view.findViewById(R.id.phone_android);
            saipan=(Button)view.findViewById(R.id.phone_saipan);*/
            android_relay= (RelativeLayout) view.findViewById(R.id.android_relay);
            iphone_relay= (RelativeLayout) view.findViewById(R.id.iphone_relay);
            bg_gray=(TextView)view.findViewById(R.id.bg_gray);
            popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setTouchable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
        }
       /* ios.setOnClickListener(this);
        android.setOnClickListener(this);
        saipan.setOnClickListener(this);*/
        android_relay.setOnClickListener(this);
        iphone_relay.setOnClickListener(this);
        bg_gray.setOnClickListener(this);
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
    }

    public boolean isPopupWindowShowing() {
        if (null != popupWindow && popupWindow.isShowing()) {
            return true;
        } else {
            return false;
        }
    }

    public void closePopupwindow() {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        popupWindow.dismiss();
        switch (v.getId()) {
           /* case R.id.phone_ios:
                btn.setText("苹果用户");
                break;
            case R.id.phone_android:
                btn.setText("安卓用户");
                break;
            case R.id.phone_saipan:
                btn.setText("非智能机用户");
                break;*/
            case R.id.iphone_relay:
                btn.setText("苹果用户");
                break;

            case R.id.android_relay:
                btn.setText("安卓用户");
                break;

            case R.id.bg_gray:
                closePopupwindow();
                break;

        }
    }

}
