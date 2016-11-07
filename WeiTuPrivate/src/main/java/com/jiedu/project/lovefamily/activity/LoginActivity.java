package com.jiedu.project.lovefamily.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.application.MyApplication;
import com.jiedu.project.lovefamily.config.MsgKey;
import com.jiedu.project.lovefamily.config.SysConfig;
import com.jiedu.project.lovefamily.json.JsonHelp;
import com.jiedu.project.lovefamily.message.MessageInfoUtil;
import com.jiedu.project.lovefamily.mode.User;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.jiedu.project.lovefamily.service.ReloginService;
import com.jiedu.project.lovefamily.sqlite.UserDao;
import com.jiedu.project.lovefamily.sqlite.UserDaoImpl;
import com.jiedu.project.lovefamily.utils.CommFunc;
import com.jiedu.project.lovefamily.utils.LoginUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.com.chinatelecom.account.lib.apk.MsgBroadcastReciver;
import cn.com.chinatelecom.account.lib.apk.TelecomProcessState;
import rtc.sdk.common.RtcConst;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private boolean bRegreveiver = false;
    private Button login;
    private TextView to_register;
    EditText verification;
    Button senVerification;
    EditText phone;
    RequestHelp help;
    JsonHelp jsonHelp;
    HashMap<String,String>hashMap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
      //  hashMap= SharedPreferencesUtil.getUserLoginInfo(this);
      /*  if(!TextUtils.isEmpty(hashMap.get("customerId"))){
            new Thread(new Runnable() {
                @Override
                public void run() {
//                    Log.e("0011","自动登录手机号=="+hashMap.get("phone")+"验证码=="+hashMap.get("verification"));
                  String result=  help.loginIn(hashMap.get("phone"),hashMap.get("verification"));
                    if(jsonHelp.dealLoginJson(result,LoginActivity.this,hashMap.get("verification"))){
                        handler.sendEmptyMessage(MessageInfoUtil.LOGIN);
                    }
                }
            }).start();
        }*/

        UserDao userDao=new UserDaoImpl(this);
        User user=userDao.selectUserInfo();
        if(user.getUserName()!=null && user.getUserName().trim()!=""){
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
            finish();
        }

    }
    void init(){
        help=new RequestHelp();
        jsonHelp=new JsonHelp();
        login= (Button) findViewById(R.id.login);
        to_register=(TextView)findViewById(R.id.to_register);
        senVerification=(Button)findViewById(R.id.sen_verification);
        verification=(EditText)findViewById(R.id.verification);
        phone=(EditText)findViewById(R.id.phone);
        login.setOnClickListener(this);
        to_register.setOnClickListener(this);
        senVerification.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
    switch(v.getId()){
        case R.id.login:
            if(!TextUtils.isEmpty(phone.getText().toString())&&!TextUtils.isEmpty(verification.getText().toString())){
                phone.setClickable(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String result=  help.loginIn(phone.getText().toString(),verification.getText().toString());
                        if(jsonHelp.dealLoginJson(result,LoginActivity.this,verification.getText().toString())){
                            handler.sendEmptyMessage(MessageInfoUtil.LOGIN);
                        }else{
                            handler.sendEmptyMessage(MessageInfoUtil.LOGIN_FAILE);
                        }
                    }
                }).start();
            }else {
                Toast.makeText(LoginActivity.this,getString(R.string.null_phone_or_verification),Toast.LENGTH_SHORT).show();
            }

            if (SysConfig.bDEBUG == false) {
                RegisterTeleReceiver();
            }

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SysConfig.BROADCAST_RELOGIN_SERVICE);
            registerReceiver(Msgreceiver, intentFilter);
            bRegreveiver = true;
            break;
        case R.id.to_register:
          //  startActivity(new Intent(this,RegisterActivity.class));
            break;
        case R.id.sen_verification:
//            netHelp.sendVerification("13611516375");
            if(!TextUtils.isEmpty(phone.getText().toString())){
//                countdown();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                      String json=  help.sendVerification(phone.getText().toString());
                        Log.e("0015",json);
                        if (json!=null && json!=""){
                                Message message=handler.obtainMessage();
                                message.what=0015;
                                message.obj=json;
                                handler.sendMessage(message);
                        }
                    }
                }).start();
            }else{
                Toast.makeText(LoginActivity.this,getString(R.string.null_phone),Toast.LENGTH_SHORT).show();
            }



            break;
    }
    }


    private void RegisterTeleReceiver() {
        if (!telecomReciver.Registered) {
            // 注册消息接收器
            IntentFilter filter = new IntentFilter(
                    TelecomProcessState.TelecomBroadCastTag);
            CommFunc.PrintLog(5, LOGTAG, "RegisterTeleReceiver:"
                    + telecomReciver);
            registerReceiver(telecomReciver, filter);
            telecomReciver.Registered = true;
        }
    }
    MsgBroadcastReciver telecomReciver = new MsgBroadcastReciver() {
        @Override
        public void switchMsg(int type, Intent intent) {
            // TODO Auto-generated method stub
            switch (type) {
                case TelecomProcessState.TelecomStateUserCanceledFlag:
                    CommFunc.PrintLog(5, LOGTAG, "TelecomStateUserCanceledFlag");
                    break;

                case TelecomProcessState.TelecomUserFinishLoginFlag:
                    CommFunc.PrintLog(5, LOGTAG, "TelecomUserFinishLoginFlag");
                    break;

                default:
                    break;
            }

        }
    };
    private BroadcastReceiver Msgreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SysConfig.BROADCAST_RELOGIN_SERVICE)) {
                switch (intent.getIntExtra("what", -1)) {
                    case SysConfig.MSG_SIP_REGISTER: {
                        CommFunc.PrintLog(5, LOGTAG, "MSG_SIP_REGISTER");
                        OnLoginResult(intent);
                        break;
                    }

                    case SysConfig.MSG_GETTOKEN_SUCCESS: {
                        CommFunc.PrintLog(5, LOGTAG,
                                "get token ok prepare sip register");
                        MyApplication.getInstance().disposeSipRegister();
                        break;
                    }

                    case SysConfig.MSG_SDKInitOK:
                        CommFunc.PrintLog(5, LOGTAG, "MSG_SDKInitOK");
                        // sdk初始ok后继续获取token sip 注册流程
                        ReloginService.getInstance().RestartLogin();
                        break;

                }
            }
        }
    };


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MessageInfoUtil.LOGIN:
                    SysConfig.getInstance().setmLoginOK(true);
                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));

                    finish();

                    break;
                case  MessageInfoUtil.COUNTDOWN:
                    senVerification.setText(""+msg.arg1);

                    break;
                case MessageInfoUtil.COUNTDOWN_END:
                    senVerification.setBackgroundResource(R.drawable.button);
                    senVerification.setText(getString(R.string.get_identifying));

                    senVerification.setClickable(true);
                    break;
                case MessageInfoUtil.LOGIN_FAILE:
                    Toast.makeText(LoginActivity.this,"登录失败，请检查手机号码和验证码是否正确",Toast.LENGTH_LONG).show();
                    login.setClickable(true);
                    break;
                case 0015:
                    String msg1=(String) msg.obj;
                    // 解析json
                    JSONObject jo = null;
                    String  msg2=null;
                    try {
                        jo = new JSONObject(msg1);
                        boolean  ok = jo.getBoolean("ok");
                        if (ok){
                            countdown();
                        }
                        msg2 = jo.getString("msg");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this,msg2,Toast.LENGTH_LONG).show();
                    login.setClickable(true);
                    break;

            }
        }
    };

    void countdown(){
        if(senVerification.isClickable()){
            senVerification.setClickable(false);
            senVerification.setBackgroundResource(R.drawable.bg_verification);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int i=60;
                while (i>=0){
                    try {
                        Thread.sleep(1000);
                      handler.sendEmptyMessage(MessageInfoUtil.COUNTDOWN);
                        Message message=handler.obtainMessage();
                        message.what=MessageInfoUtil.COUNTDOWN;
                        message.arg1=i;
                        message.sendToTarget();
                        i--;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                    handler.sendEmptyMessage(MessageInfoUtil.COUNTDOWN_END);
                }
            }).start();
        }
    }


    //11.6add

    private void OnLoginResult(Intent intent) {
        int result = intent.getIntExtra("arg1", -1);
        String desc = intent.getStringExtra("arg2");

        if (result == MsgKey.KEY_STATUS_200
                || result == MsgKey.KEY_RESULT_SUCCESS) {

            SysConfig.getInstance().setmLoginOK(true);
            CommFunc.PrintLog(5, LOGTAG, "OnLoginResult loginok  MainActivity");
            // save accountinfo
            String versionName = MyApplication.getInstance().getAppVersionName();
            MyApplication.getInstance().setVersionName(versionName);
            if (SysConfig.login_type == SysConfig.USERTYPE_TIANYI)
                MyApplication.getInstance().saveSharePrefValue(MsgKey.key_userid, SysConfig.userid);

            if (!RtcConst.bNewVersion)
                ReloginService.getInstance().testQueryStaus();

            // wwyue0430

            MyApplication.getInstance().saveSharePrefValue(LoginUtil.LOGIN_UID,
                    phone.getText().toString().trim());
            MyApplication.getInstance().saveSharePrefValue(LoginUtil.LOGIN_PWD,
                    verification.getText().toString().trim());
            MyApplication.getInstance().saveSharePrefValue(
                    MsgKey.key_logintype, "" + SysConfig.login_type);
        } else {
            SysConfig.getInstance().setmLoginOK(false);
            CommFunc.PrintLog(5, LOGTAG, "登陆失败 错误码[:" + result + "]desc:" + desc);
            if(result == RtcConst.CallCode_Forbidden) {
                MyApplication.getInstance().saveSharePrefValue(MsgKey.key_rtc_token, "invalid");
                CommFunc.DisplayToast(MyApplication.getInstance(), "该帐号在其他终端登录过，继续使用请再点击一次登录");
            } else if(result == RtcConst.CallCode_NotFound) {
                MyApplication.getInstance().saveSharePrefValue(MsgKey.key_rtc_token, "invalid");
                CommFunc.DisplayToast(MyApplication.getInstance(), "更换了未注册过的帐号，继续使用请再点击一次登录");
            } else {
                CommFunc.DisplayToast(MyApplication.getInstance(), "登陆失败:" + result + "错误原因:" + desc);
            }
        }
    }


}
