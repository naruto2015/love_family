package com.jiedu.project.lovefamily.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.json.JsonHelp;
import com.jiedu.project.lovefamily.message.MessageInfoUtil;
import com.jiedu.project.lovefamily.mode.User;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.jiedu.project.lovefamily.sqlite.UserDao;
import com.jiedu.project.lovefamily.sqlite.UserDaoImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.com.chinatelecom.account.lib.apk.MsgBroadcastReciver;
import cn.com.chinatelecom.account.lib.apk.TelecomProcessState;
import jni.http.HttpManager;
import jni.http.HttpResult;
import jni.http.RtcHttpClient;
import jni.util.Utils;
import rtc.sdk.aidl.clt.RtcClientImpl;
import rtc.sdk.common.RtcConst;
import rtc.sdk.common.SdkSettings;
import rtc.sdk.iface.ClientListener;
import rtc.sdk.iface.Connection;
import rtc.sdk.iface.ConnectionListener;
import rtc.sdk.iface.Device;
import rtc.sdk.iface.DeviceListener;
import rtc.sdk.iface.RtcClient;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private String LOGTAG= "DemoApp";
    private boolean bRegreveiver = false;
    private Button login;
    private TextView to_register;
    EditText verification;
    Button senVerification;
    EditText phone;
    RequestHelp help;
    JsonHelp jsonHelp;
    HashMap<String,String>hashMap;
    boolean mInit = false; //init
    Device mAcc = null;  //reg
    RtcClient mClt;
    private String Username;
    public static  String APP_ID = "70063";// 70063
    public static  String APP_KEY ="MTQzMTQyNTM2MzA4NQ==";// MTQzMTQyNTM2MzA4NQ==

    public static final int MSG_GETTOKEN=10001;
    private String capabailitytoken;
    Connection mCall;
    boolean mIncoming = false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        initRTCSDK();
        UserDao userDao=new UserDaoImpl(this);
        User user=userDao.selectUserInfo();
        if(user.getUserName()!=null && user.getUserName().trim()!=""){
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
            finish();
        }

    }

    private void initRTCSDK() {

        mInit = !mInit;
        Utils.PrintLog(5, LOGTAG, "onBtnInit mInit :"+mInit);

        if (mInit) {
            initRtcClientImpl();
        }
        else {
            if(mAcc!=null) {
                mAcc.release();
                mAcc = null;
            }
//            setBtnText(R.id.bt_register,"Login");
            if(mClt!=null)
                mClt.release();
            mClt = null;
//            setBtnText(R.id.bt_init,"Init");
//            setStatusText("");
        }

    }

    private void initRtcClientImpl() {

        Utils.PrintLog(5,"DemoApp", "initRtcClientImpl()");
        mClt = new RtcClientImpl();
        mClt.initialize(this.getApplicationContext(), new ClientListener() {
            @Override   //初始化结果回调
            public void onInit(int result) {
                Utils.PrintLog(5,"ClientListener","onInit,result="+result);//常见错误9003:网络异常或系统时间差的太多
                if(result == 0) {
                    mClt.setAudioCodec(RtcConst.ACodec_OPUS);
                    mClt.setVideoCodec(RtcConst.VCodec_VP8);
                    mClt.setVideoAttr(RtcConst.Video_SD);
                }
                else
                    mInit = false;
            }
        });

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
            loginRTCsdk();
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

    private void loginRTCsdk() {
        if (mClt==null) return;
        if(mInit==false){
            return;
        }
        Username = phone.getText().toString();
        Utils.PrintLog(5, LOGTAG, "onBtnLogin:"+Username);
        if (mAcc==null) {
            restRegister();
        } else {
            mAcc.release();
            mAcc = null;
        }
    }
    private void restRegister() {
        new Thread(){
            public void run(){
                System.out.println("Thread is running.");
                opt_getToken();
            }
        }.start();
    }

    private  void  opt_getToken() {
        RtcConst.UEAPPID_Current = RtcConst.UEAPPID_Self;//账号体系，包括私有、微博、QQ等，必须在获取token之前确定。
        JSONObject jsonobj= HttpManager.getInstance().CreateTokenJson(0,Username,RtcHttpClient.grantedCapabiltyID,"");
        HttpResult  ret = HttpManager.getInstance().getCapabilityToken(jsonobj, APP_ID, APP_KEY);
//        setStatusText("获取token:"+ret.getStatus()+" reason:"+ret.getErrorMsg());
        Utils.PrintLog(5, LOGTAG, "获取token:"+ret.getStatus());
        Utils.PrintLog(5, LOGTAG, "reason:"+ret.getErrorMsg());

        Message msg = new Message();
        msg.what = MSG_GETTOKEN;
        msg.obj = ret;
        UIHandler.sendMessage(msg);
    }

    /** The UI handler. */
    private Handler UIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_GETTOKEN:
                    OnResponse_getToken(msg);
                    break;
                default:
                    break;
            }
        }

    };

    private void OnResponse_getToken(Message msg) {

        HttpResult  ret = (HttpResult)msg.obj;
        Utils.PrintLog(5,LOGTAG, "handleMessage getCapabilityToken status:"+ret.getStatus());
        JSONObject jsonrsp = (JSONObject)ret.getObject();
        if(jsonrsp!=null && jsonrsp.isNull("code")==false) {
            try {
                String code = jsonrsp.getString(RtcConst.kcode);
                String reason = jsonrsp.getString(RtcConst.kreason);
                Utils.PrintLog(5, LOGTAG, "Response getCapabilityToken code:"+code+" reason:"+reason);
                if(code.equals("0")) {
                    capabailitytoken =jsonrsp.getString(RtcConst.kcapabilityToken);

                    Utils.PrintLog(5,LOGTAG, "handleMessage getCapabilityToken:"+capabailitytoken);
                    OnRegister(Username,capabailitytoken);
                }
                else {
                    Utils.DisplayToast(this, "获取token失败 [status:"+ret.getStatus()+"]"+ret.getErrorMsg());
                    Utils.PrintLog(5, LOGTAG, "获取token失败 [status:"+ret.getStatus()+"]"+ret.getErrorMsg());
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private void OnRegister(String sname, String spwd) {

        Utils.PrintLog(5, LOGTAG, "OnRegister:"+sname+"spwd:"+spwd);
        try {
            JSONObject jargs = SdkSettings.defaultDeviceSetting();
            jargs.put(RtcConst.kAccPwd,spwd);
          //  Utils.PrintLog(5, LOGTAG, "user:"+getEditText(R.id.ed_user) +"token:"+spwd);

            //账号格式形如“账号体系-号码~应用id~终端类型”，以下主要设置账号内各部分内容，其中账号体系的值要在获取token之前确定，默认为私有账号
            jargs.put(RtcConst.kAccAppID,APP_ID);//应用id
            //jargs.put(RtcConst.kAccName,"逍遥神龙");
            jargs.put(RtcConst.kAccUser,sname); //号码
            jargs.put(RtcConst.kAccType,RtcConst.UEType_Current);//终端类型

            mAcc = mClt.createDevice(jargs.toString(), mAListener); //注册
          //  setBtnText(R.id.bt_register,"Logout");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    DeviceListener mAListener = new DeviceListener(){

        @Override
        public void onDeviceStateChanged(int result) {
            Utils.PrintLog(5,"DeviceListener","onDeviceStateChanged,result="+result);
         //   setStatusText("StateChanged,result="+result);
            if(result == RtcConst.CallCode_Success) { //注销也存在此处

            }
            else if(result == RtcConst.NoNetwork) {
                onNoNetWork();
            }
            else if(result == RtcConst.ChangeNetwork) {
                ChangeNetWork();
            }
            else if(result == RtcConst.PoorNetwork) {
                onPoorNetWork();
            }
            else if(result == RtcConst.ReLoginNetwork) {
                // 网络原因导致多次登陆不成功，由用户选择是否继续，如想继续尝试，可以重建device
                Utils.PrintLog(5,"DeviceListener","onDeviceStateChanged,ReLoginNetwork");
            }
            else if(result == RtcConst.DeviceEvt_KickedOff) {
                // 被另外一个终端踢下线，由用户选择是否继续，如果再次登录，需要重新获取token，重建device
                Utils.PrintLog(5,"DeviceListener","onDeviceStateChanged,DeviceEvt_KickedOff");
            }
            else if(result == RtcConst.DeviceEvt_MultiLogin) {
                Utils.PrintLog(5,"DeviceListener","onDeviceStateChanged,DeviceEvt_MultiLogin");
            }
            else {
                //  CommFunc.DisplayToast(MyApplication.this, "注册失败:"+result);
            }
        }
        private void onPoorNetWork() {
            Utils.PrintLog(5, LOGTAG, "onPoorNetWork");
        }
        private void onNoNetWork() {
            Utils.PrintLog(5, LOGTAG, "onNoNetWork");
            Utils.DisplayToast(LoginActivity.this, "onNoNetWork");
            //断网销毁
            if (mCall!=null) {
                mCall.disconnect();
                mCall = null;
            }
           // setStatusText("pls check network");
        }
        private void ChangeNetWork() {
            Utils.PrintLog(5, LOGTAG, "ChangeNetWork");
            //自动重连接
        }

        @Override
        public void onSendIm(int nStatus) {
            if(nStatus == RtcConst.CallCode_Success)
                Utils.PrintLog(5, LOGTAG, "发送IM成功");
            else
                Utils.PrintLog(5, LOGTAG, "发送IM失败:"+nStatus);
        }
        @Override
        public void onReceiveIm(String from,String mime,String content) {
            Utils.PrintLog(5, LOGTAG, "onReceiveIm:"+from+mime+content);
        }

        @Override
        public void onNewCall(Connection call) {

            Utils.PrintLog(5,"DeviceListener","onNewCall,call="+call.info());
           // setStatusText("DeviceListener:onNewCall,call="+call.info());
            if (mCall!=null) {
                call.reject();
                call = null;
                Utils.PrintLog(5,"DeviceListener","onNewCall,reject call");
             //   setStatusText("DeviceListener:onNewCall,reject call");
                return;
            }
            mIncoming = true;
            mCall = call;
            call.setIncomingListener(mCListener);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                   // setBtnText(R.id.bt_Call,"Accept");
                }
            });
        }
        @Override
        public void onQueryStatus(int status, String paramers) {
            // TODO Auto-generated method stub
        }

    };

    private void RegisterTeleReceiver() {
        if (!telecomReciver.Registered) {
            // 注册消息接收器
            IntentFilter filter = new IntentFilter(
                    TelecomProcessState.TelecomBroadCastTag);
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
                    break;

                case TelecomProcessState.TelecomUserFinishLoginFlag:
                    break;

                default:
                    break;
            }

        }
    };

    Handler mHandler = new Handler() {

    };
    ConnectionListener mCListener = new ConnectionListener(){

        @Override
        public void onConnecting() {
//            setStatusText("ConnectionListener:onConnecting");
        }
        @Override
        public void onConnected() {
//            setStatusText("ConnectionListener:onConnected");
        }
        @Override
        public void onDisconnected(int code) {
//            setStatusText("ConnectionListener:onDisconnect,code="+code);
            Utils.PrintLog(5, LOGTAG, "onDisconnected timerDur"+mCall.getCallDuration());
            mCall = null;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
//                    setBtnText(R.id.bt_Call,"Call");
//                    setVideoSurfaceVisibility(View.INVISIBLE);
                }
            });
        }
        @Override
        public void onVideo() {
//            setStatusText("ConnectionListener:onVideo");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    initVideoViews();
                    mCall.buildVideo(mvRemote);
//                    setVideoSurfaceVisibility(View.VISIBLE);
                }
            });
        }
        @Override
        public void onNetStatus(int msg, String info) {
            // TODO Auto-generated method stub
        }

    };
    SurfaceView mvLocal = null;
    SurfaceView mvRemote = null;
    LinearLayout layoutlocal;
    LinearLayout layoutremote;
    private void initVideoViews() {
        if (mvLocal != null)
            return;
        if(mCall != null)
            mvLocal = (SurfaceView)mCall.createVideoView(true, this, true);
        mvLocal.setVisibility(View.INVISIBLE);
        layoutlocal.addView(mvLocal);
        mvLocal.setKeepScreenOn(true);
        mvLocal.setZOrderMediaOverlay(true);
        mvLocal.setZOrderOnTop(true);
        if (mvRemote != null)
            return;
        if(mCall != null)
            mvRemote = (SurfaceView)mCall.createVideoView(false, this, true);
        mvRemote.setVisibility(View.INVISIBLE);
        mvRemote.setKeepScreenOn(true);
        mvRemote.setZOrderMediaOverlay(true);
        mvRemote.setZOrderOnTop(true);
        layoutremote.addView(mvRemote);

    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MessageInfoUtil.LOGIN:

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
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





}
