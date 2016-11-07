package com.jiedu.project.lovefamily.popupwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jiedu.project.lovefamily.R;

/**
 * Created by Administrator on 2016/3/7.
 */
public class PayPopupwindowUtil implements View.OnClickListener {
    private PopupWindow popupWindow;
    private View view;
    private Button weixin;
    private Button zhifubao;
    TextView bg_gray;
    private Context context;
    private String titleStr;
    private  Button btn;
    public void showPopupwindow(Button v, Context context) {
        btn=v;
        if (popupWindow == null) {
            this.context = context;
            view = LayoutInflater.from(context).inflate(R.layout.pay_style_popupwindow, null);
            weixin = (Button) view.findViewById(R.id.pay_weixin);
            zhifubao = (Button) view.findViewById(R.id.pay_zhifubao);
            bg_gray=(TextView)view.findViewById(R.id.bg_gray);
            popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setTouchable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
        }
        weixin.setOnClickListener(this);
        zhifubao.setOnClickListener(this);
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
            case R.id.pay_weixin:
                btn.setText("微信支付");
                break;
            case R.id.pay_zhifubao:
                btn.setText("支付宝支付");
                break;
            case R.id.bg_gray:
                closePopupwindow();
                break;

        }
    }

}
