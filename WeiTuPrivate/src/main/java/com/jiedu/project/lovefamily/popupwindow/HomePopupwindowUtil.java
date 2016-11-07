package com.jiedu.project.lovefamily.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.activity.EditActivity;
import com.jiedu.project.lovefamily.activity.SafeListActivity;
import com.jiedu.project.lovefamily.mode.User;
import com.jiedu.project.lovefamily.stringutil.IntentString;
import com.jiedu.project.lovefamily.json.JsonHelp;
import com.jiedu.project.lovefamily.message.MessageInfoUtil;
import com.jiedu.project.lovefamily.net.RequestHelp;

/**
 * Created by Administrator on 2016/3/2.
 */
public class HomePopupwindowUtil implements View.OnClickListener {
    private PopupWindow popupWindow;
    private View view;
    private TextView edit;
    private TextView getLocation;
    private TextView setSafeArea;
    private  TextView delete;
    private  TextView back;
    private Context context;

    private Handler handler;
    private int index;

    RequestHelp help;
    JsonHelp jsonHelp;
    User user;
    public void showPopupwindow(View v, Context context, Handler handler,int index,User user) {
        this.handler=handler;
        this.index=index;
        this.user=user;


        jsonHelp=new JsonHelp();
        help=new RequestHelp();
        if (popupWindow == null) {
            this.context = context;
            view = LayoutInflater.from(context).inflate(R.layout.popupwindow_layout, null);
            edit = (TextView) view.findViewById(R.id.edit);
            getLocation = (TextView) view.findViewById(R.id.get_location);
            setSafeArea = (TextView) view.findViewById(R.id.set_safe_area);
            back=(TextView)view.findViewById(R.id.popup_back);
            delete= (TextView) view.findViewById(R.id.delete);
            popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            popupWindow.setOutsideTouchable(false);
            popupWindow.setTouchable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
        }
        Log.e("0011", "用户状态" + user.getStatueNum());
        if(user.getStatueNum().equals("1")){
            getLocation.setVisibility(View.GONE);
                    setSafeArea.setVisibility(View.GONE);

        }else{
            getLocation.setVisibility(View.VISIBLE);
            setSafeArea.setVisibility(View.VISIBLE);
        }
        getLocation.setOnClickListener(this);
        setSafeArea.setOnClickListener(this);
        edit.setOnClickListener(this);
        back.setOnClickListener(this);
        delete.setOnClickListener(this);
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
            case R.id.edit:
                Intent intent=new Intent(context, EditActivity.class);
                intent.putExtra(IntentString.INTENT_TITLE,user.getUserName());
                intent.putExtra(IntentString.CUSTOMER_ID,user.getMonitoredId());
                intent.putExtra(IntentString.NICK_NAME,user.getUserName());
                intent.putExtra(IntentString.BRITHDAY,user.getBirthday());
                intent.putExtra(IntentString.SEX,user.getSex());
                intent.putExtra(IntentString.ADDRESS,user.getAddress());
                intent.putExtra(IntentString.PHONO_URL,user.getPhotoUrl());
                intent.putExtra(IntentString.PHONE,user.getPhone());
                ( (Activity) context).startActivityForResult(intent,2);

                break;
            case R.id.get_location:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String result=help.getLocation(user.getMonitoredId());
                        jsonHelp.dealLocationResult(result,handler,index);
                    }
                }).start();

                break;
            case R.id.set_safe_area:
                Intent safeIntent=new Intent(context, SafeListActivity.class);
                safeIntent.putExtra(IntentString.REF_ID,user.getId());
                safeIntent.putExtra(IntentString.CUSTOMER_ID,user.getMonitoredId());
                context.startActivity(safeIntent);
                break;
            case R.id.delete:
                Message message=handler.obtainMessage();
                message.arg1= MessageInfoUtil.DELETE;
                message.arg2=index;
                message.sendToTarget();
                break;
            case R.id.popup_back:
                if(isPopupWindowShowing()){
                    closePopupwindow();
                }
                break;
        }
    }


}
