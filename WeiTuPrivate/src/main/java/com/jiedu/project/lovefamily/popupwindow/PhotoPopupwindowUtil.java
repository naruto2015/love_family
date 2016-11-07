package com.jiedu.project.lovefamily.popupwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.jiedu.project.lovefamily.R;

/**
 * Created by Administrator on 2016/3/8.
 */
public class PhotoPopupwindowUtil implements View.OnClickListener {

    private PopupWindow popupWindow;
    private View view;
    private Button choose;
    private Button take;
    private  Button cancel;
    private Context context;
    private String titleStr;
    private  ImageView btn;
    private Handler handler;
    public static final int CHOOSE_PHOTO=1;
    public static final int TAKE_PHOTO=2;
    public static final int RESULT_CHOOSE_PHOTO = 3;
    public static final int RESULT_TAKE_PHOTO = 4;
    public static final int RESULT_CROP = 5;
    public void showPopupwindow(ImageView v, Context context,Handler handler) {
        btn=v;
        this.handler=handler;
        if (popupWindow == null) {
            this.context = context;
            view = LayoutInflater.from(context).inflate(R.layout.photo_choose_popupwindow, null);
            choose = (Button) view.findViewById(R.id.choose_photo_sd);
            take =    (Button) view.findViewById(R.id.take_photo);
            cancel=(Button)view.findViewById(R.id.cancel);
            popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setTouchable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
        }
        choose.setOnClickListener(this);
        take.setOnClickListener(this);
        cancel.setOnClickListener(this);
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 10, 10);
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
            case R.id.choose_photo_sd:
                handler.sendEmptyMessage(CHOOSE_PHOTO);
                break;
            case R.id.take_photo:
                handler.sendEmptyMessage(TAKE_PHOTO);
                break;
            case R.id.cancel:
                closePopupwindow();
                break;

        }
    }

}
