package com.jiedu.project.lovefamily.popupwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jiedu.project.lovefamily.R;

/**
 * Created by Administrator on 2016/3/16.
 */
public class SharePopupwindowUtil implements View.OnClickListener {

    private PopupWindow popupWindow;
    private View view;
    private Context context;
    private Button pay;
    private Button code;
    private Button share;
    private TextView back;
    public void showPopupwindow(View v, Context context) {

        if (popupWindow == null) {
            this.context = context;
            view = LayoutInflater.from(context).inflate(R.layout.popup_share, null);
            pay= (Button) view.findViewById(R.id.pay);
            code= (Button) view.findViewById(R.id.code);
            share= (Button) view.findViewById(R.id.share);
            back= (TextView) view.findViewById(R.id.back);
            popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setTouchable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
        }
        pay.setOnClickListener(this);
        code.setOnClickListener(this);
        share.setOnClickListener(this);
        back.setOnClickListener(this);
        popupWindow.showAsDropDown(v);
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
        closePopupwindow();
switch(v.getId()){
    case R.id.pay:

        break;
    case R.id.code:

        break;
    case R.id.share:

        break;
    case R.id.back:

        break;
}
    }
}
