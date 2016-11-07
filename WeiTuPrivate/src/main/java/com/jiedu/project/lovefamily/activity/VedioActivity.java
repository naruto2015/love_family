package com.jiedu.project.lovefamily.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.application.MyApplication;
import com.jiedu.project.lovefamily.bean.TCallRecordInfo;
import com.jiedu.project.lovefamily.bean.TContactInfo;
import com.jiedu.project.lovefamily.config.SysConfig;
import com.jiedu.project.lovefamily.db.SQLiteManager;
import com.jiedu.project.lovefamily.service.ReloginService;
import com.jiedu.project.lovefamily.utils.CommFunc;

import java.util.Calendar;
import java.util.UUID;

import jni.sip.Call;
import rtc.sdk.common.NetWorkUtil;

/**
 * Created by Administrator on 2016/10/24.
 */
@SuppressWarnings("deprecation")
public class VedioActivity extends BaseActivity{
    public final static String BROADCAST_CALLING_ACTION = "com.yishitong.calling";
    private String fromUser;
    private String toUser;
    private TCallRecordInfo callrecordinfo= null;
    public final String LOGTAG = getClass().getName().substring(getClass().getName().lastIndexOf(".") + 1);
    private ImageView button;
    private EditText editText,editText2;
    private   String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vedio);

        SysConfig.getInstance().setIsLoginByBtn(false);
        initview();
        initData();
    }
    private void initview() {
        button= (ImageView) findViewById(R.id.button);
        editText= (EditText) findViewById(R.id.edit_vedio);
        editText2= (EditText) findViewById(R.id.edit_vedio2);
    }

    private void initData() {
        Intent intent = getIntent();
        toUser = intent.getStringExtra("touser");
         fromUser = intent.getStringExtra("fromuser");
        editText.setText(toUser);
        editText2.setText(fromUser);
        CommFunc.PrintLog(5, "ContactCallDetailActivity", "initData");
        TContactInfo info = null;
        if (fromUser.equals(SysConfig.userid)) {
            info = SQLiteManager.getInstance().getContactInfoByNumber(toUser);
        }else {
            info = SQLiteManager.getInstance().getContactInfoByNumber(fromUser);
        }
        CommFunc.PrintLog(5, "ContactCallDetailActivity", "initData  info:"+info);
    }




    /**
     * 视频呼叫
     * @param view
     */
    public void onVideo(View view) {
        if (checkCall(toUser)) {
            String callRecordId = UUID.randomUUID().toString();
            saveCallRecordInfo(callRecordId, toUser, Call.CT_AudioVideo);
            Intent intent = new Intent(this,CallingActivity.class);
            AlarmManager am = (AlarmManager) MyApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
            intent.putExtra("callNumber",toUser);
            intent.putExtra("inCall", false);
            intent.putExtra("isVideo", true);
            intent.putExtra("callRecordId", callRecordId);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.getInstance(), 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            long time = Calendar.getInstance().getTimeInMillis();
            CommFunc.PrintLog(5, LOGTAG, "pendingIntent time:"+time);
            am.set(AlarmManager.RTC_WAKEUP,100, pendingIntent);
        }
    }

    /**
     * 判断号码是否满足呼叫条件
     * TODO需要更改
     * @param numberString
     * @return
     */
    private boolean checkCall(String numberString){
        if (numberString == null || numberString.equals("")) {
            CommFunc.DisplayToast(this,R.string._calling_number_null);
            return false;
        } else {
            if (checkNet()) {
                if (numberString.equals(userid)) {
                    CommFunc.DisplayToast(this,R.string._calling_cannot_dial_self);
                    return false;
                }
                return true;
            }else {
                return false;
            }
        }
    }

    /**
     * 入库操作
     */
    private void saveCallRecordInfo(String callRecordId, String callNumber, int callType) {
        TCallRecordInfo info = new TCallRecordInfo();
        info.setCallRecordId(callRecordId);
        info.setDate(CommFunc.getStartDate());
        info.setStartTime(CommFunc.getStartTime());
        info.setEndTime("");
        info.setTotalTime("");
        String userid = MyApplication.getInstance().getUserID();
        info.setFromUser(userid); // TODO 需要更改
        info.setToUser(callNumber);
        info.setType(callType);
        info.setResult(TCallRecordInfo.CALL_RESULT_SUCCESS);
        info.setDirection(TCallRecordInfo.CALL_DIRECTION_OUT);
        SQLiteManager.getInstance().saveCallRecordInfo(info, true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}