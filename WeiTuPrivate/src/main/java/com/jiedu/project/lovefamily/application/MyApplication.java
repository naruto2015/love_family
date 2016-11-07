package com.jiedu.project.lovefamily.application;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.SurfaceView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.jiedu.project.lovefamily.HBaseApp;
import com.jiedu.project.lovefamily.activity.CallingActivity;
import com.jiedu.project.lovefamily.bean.TAccountInfo;
import com.jiedu.project.lovefamily.config.MsgKey;
import com.jiedu.project.lovefamily.config.SysConfig;
import com.jiedu.project.lovefamily.db.SQLiteManager;
import com.jiedu.project.lovefamily.service.ReloginService;
import com.jiedu.project.lovefamily.tools.LocalActManager;
import com.jiedu.project.lovefamily.utils.CommFunc;
import com.jiedu.project.lovefamily.utils.WriteLog;
import com.jiedu.project.lovefamily.weibo.OAuthSharepreference;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.umeng.socialize.PlatformConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.UUID;

import jni.http.HttpManager;
import jni.sip.Call;
import jni.util.Utils;
import rtc.sdk.clt.RtcClientImpl;
import rtc.sdk.common.RtcConst;
import rtc.sdk.common.SdkSettings;
import rtc.sdk.core.RtcRules;
import rtc.sdk.iface.ClientListener;
import rtc.sdk.iface.Connection;
import rtc.sdk.iface.ConnectionListener;
import rtc.sdk.iface.Device;
import rtc.sdk.iface.DeviceListener;
import rtc.sdk.iface.GroupMgr;
import rtc.sdk.iface.RtcClient;


/**
 * Created by Administrator on 2016/3/2.
 */
public class MyApplication extends HBaseApp {
    private static MyApplication mApplication;
    private TAccountInfo accountinfo = null;
    private SQLiteManager sqLiteManager;
    private String fileFolder = "vv";
    private SysConfig sysconfig;
    private String LOGTAG = "MyApplication";
    private RtcClient mClt;
    public boolean SdkInit = false;
    private LocalActManager localactmanager = null;
    private HttpManager httpManager = null;
    @Override
    public void onCreate() {
        super.onCreate();
        initSdkLog();
        init();
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);

        //PlatformConfig.setWeixin("wx530e106581e3f050", "95afd6d101e29cac8b76161dc0aa0d58");
        //PlatformConfig.setQQZone("1105261796", "BXsizUXA8hMmkNOw");
        PlatformConfig.setWeixin("wx1db1c4430257f8c9", "46ae98c644feeae0074a98a3744cec42");
        PlatformConfig.setQQZone("1105290281", "UPTNS17wXd6CjwY2");
        SDKInitializer.initialize(getApplicationContext());

        //queues= Volley.newRequestQueue(getApplicationContext());

    }



    /**
     * 开启Service //开启服务是否启动登陆
     */
    private void startService(boolean bStartLogin) {

        CommFunc.PrintLog(5, LOGTAG, "startService() bStartLogin:" + bStartLogin);
        Intent intent = new Intent(this, ReloginService.class);
        intent.putExtra("key_bstartLogin", true);
        CommFunc.PrintLog(5, LOGTAG, "startService() bStartLogin:" + bStartLogin);
        startService(intent);
    }

    public void setAppId(int appid) {
        switch (appid) {
            case 0:
                SysConfig.APP_ID = "70038";
                SysConfig.APP_KEY = "MTQxMDkyMzU1NTI4Ng==";
                break;
            case 1:
                SysConfig.APP_ID = "123";
                SysConfig.APP_KEY = "123456";
                break;
            case 2:
                SysConfig.APP_ID = "70063";
                SysConfig.APP_KEY = "MTQzMTQyNTM2MzA4NQ==";
                break;
            default:
                SysConfig.APP_ID = "70063";
                SysConfig.APP_KEY = "MTQzMTQyNTM2MzA4NQ==";
                break;
        }
    }

    /**
     * 初始化
     */
    public void init() {
        getSqlManager();
        // 需要根据登陆类型取值
        if (getVersionName() != null && getVersionName().equals("1.0")){//getAppVersionName())) {
            SysConfig.login_type = getIntSharedXml(MsgKey.key_logintype, SysConfig.USERTYPE_TIANYI);
            if (SysConfig.login_type == SysConfig.USERTYPE_TIANYI) {
                String defaultVal = SysConfig.bDEBUG == true ? "0" : SysConfig.userid;
                SysConfig.userid = getSharePrefValue(MsgKey.key_userid, defaultVal);
            } else {
                SysConfig.userid = OAuthSharepreference.getUid(this);
            }
            MyApplication.getInstance().getAccountInfo().setUserid(SysConfig.userid);
            int nNormalExit = getIntSharedXml(MsgKey.key_isNormalExit, 0);

            CommFunc.PrintLog(5, LOGTAG, "init() nNormalExit:" + nNormalExit);
            if (nNormalExit == 1) {
                startService(false);
            } else {
                // 从非登录页面进入登陆
                if (RtcConst.bNewVersion) {
                    int appid = MyApplication.getInstance().getIntSharedXml(MsgKey.pref_appid, 2);
                    setAppId(appid);
                    Utils.PrintLog(5, LOGTAG, "initView appid:" + appid);
                }

                if (RtcConst.bAddressCfg) {

                    String addr = MyApplication.getInstance().getStringSharedXml(MsgKey.pref_addcfg, "http://cloud.chinartc.com:8090");
                    if (addr != null && !addr.equals("")) {
                        RtcConst.getServerDomain = addr;

                    }
                }
                startService(true);
            }
        } else {
            startService(false);
        }
        CommFunc.PrintLog(5, LOGTAG, "init() userid:" + SysConfig.userid + "logintype:" + SysConfig.login_type);
    }

    private void onIncomingCall(Connection call) {
        // {"id":0,"dir":2,"uri":"<sip:18601305661_123@chinartc.com>","t":1}
        JSONObject json;
        try {
            json = new JSONObject(call.info().toString());
            String uri = json.getString(RtcConst.kCallRemoteUri);
            int calltype = json.getInt(RtcConst.kCallType);
            CommFunc.PrintLog(5, LOGTAG, "onIncomingCall:" + uri + "  calltype:" + calltype);
            SysConfig.getInstance().setCallType(calltype);

            Intent intent = new Intent(MyApplication.getInstance(), CallingActivity.class);
            intent.putExtra("callNumber", uri);
            intent.putExtra("inCall", true);
            intent.putExtra("isVideo", (calltype == Call.CT_Audio) ? false : true);
            intent.putExtra("callRecordId", UUID.randomUUID().toString());
            AlarmManager am = (AlarmManager) MyApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.getInstance(), 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            long time = Calendar.getInstance().getTimeInMillis();
            CommFunc.PrintLog(5, LOGTAG, "pendingIntent time:" + time);
            am.set(AlarmManager.RTC_WAKEUP, 100, pendingIntent);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getFileFolder() {
        return fileFolder;
    }

    public TAccountInfo getAccountInfo() {
        if (accountinfo == null) {
            accountinfo = new TAccountInfo();
        }
        return accountinfo;
    }


    public String getUserID() {
        if (accountinfo != null) {
            return accountinfo.getUserid();
        }
        return "";
    }

    public String getAppAccountID() {
        if (accountinfo != null) {
            return accountinfo.getUserid() + "_" + SysConfig.APP_ID;
        }
        return "";
    }

    public void initAudioCodec() {
        final int audiocodec = MyApplication.getInstance().getIntSharedXml(MsgKey.KEY_ACODEC, MsgKey.ACODEC_OPUS);
        CommFunc.PrintLog(5, LOGTAG, "initAudioCodec:" + audiocodec);
        if (mClt == null) {
            return;
        }
        HBaseApp.post2WorkRunnable(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                switch (audiocodec) {
                    case 1: // opus
                        mClt.setAudioCodec(RtcConst.ACodec_OPUS);
                        break;
                    default: // ilbc
                        mClt.setAudioCodec(RtcConst.ACodec_ILBC);
                        break;
                }
            }

        });
    }

    public void initVideoAdapt() {
        int adapt = MyApplication.getInstance().getIntSharedXml(MsgKey.KEY_VIDEOADAPT, 0);
        JSONObject jsonobj = new JSONObject();
        CommFunc.PrintLog(5, LOGTAG, "initVideoAdapt:" + adapt); //
        if (mClt == null) {
            return;
        }

        mClt.setVideoAdapt(adapt);

        CommFunc.PrintLog(5, LOGTAG, "initVideoAdapt:" + jsonobj.toString());
    }

    public void initVideoCodec() {
        int videocodec = MyApplication.getInstance().getIntSharedXml(MsgKey.KEY_VCODEC, MsgKey.VCODEC_VP8);
        JSONObject jsonobj = new JSONObject();
        CommFunc.PrintLog(5, LOGTAG, "initVideoCodec:" + videocodec); //
        if (mClt == null) {
            return;
        }
        switch (videocodec) {
            case 2: // H264MC
                mClt.setVideoCodec(RtcConst.VCodec_H264MC);
                break;
            case 1: // H264
                mClt.setVideoCodec(RtcConst.VCodec_H264);
                break;
            default: // Vp8
                mClt.setVideoCodec(RtcConst.VCodec_VP8);
                break;
        }
        CommFunc.PrintLog(5, LOGTAG, "initVideoCodec:" + jsonobj.toString());
    }

    public void initSdkLog() {
        // ils.initlogFolder(getFileFolder()); //sd卡路径 /mnt/sdcard/vv/log/
        // 需要有读写权限
        WriteLog.getInstance().startLog();
    }

    public RtcClient getRtcClient() {
        return mClt;
    }

    public RtcClient InitSdk() {
        if (mClt == null) {
            CommFunc.PrintLog(1, LOGTAG, "getRtcClientSip()");
            mClt = new RtcClientImpl();
            mClt.initialize(getApplicationContext(), new ClientListener() {
                @Override
                // 初始化结果回调
                public void onInit(int result) {
                    CommFunc.PrintLog(5, LOGTAG, "getRtcClientSip,result=" + result);
                    if (result == 0) {
                        Intent intent = new Intent(SysConfig.BROADCAST_RELOGIN_SERVICE);
                        intent.putExtra("what", SysConfig.MSG_SDKInitOK);
                        intent.putExtra("arg1", result);
                        intent.putExtra("arg2", "sdk init ok");
                        MyApplication.getInstance().sendBroadcast(intent);

                        SdkInit = true;
                        initVideoAttr();
                        initAudioCodec();
                        initVideoCodec();
                        initVideoAdapt();
                    } else {
                        mClt.release();
                        mClt = null; // 如果中间登陆过程获取服务器地址失败 在继续登陆不走获取服务器地址了
                        SdkInit = false;
                        CommFunc.PrintLog(5, LOGTAG, "sdk initialize failed,result=" + result);
                        // 需要上报失败处理
                        Intent intent = new Intent(SysConfig.BROADCAST_RELOGIN_SERVICE);
                        intent.putExtra("what", SysConfig.MSG_GETSERVERADRR_FAILED);
                        intent.putExtra("arg1", result);
                        intent.putExtra("arg2", "登陆失败,sdk初始化失败！");
                        MyApplication.getInstance().sendBroadcast(intent);

                        // returnValueBroadcast(SysConfig.MSG_TIANYI_VERIFY, -2,
                        // "登陆失败请查看错误代码和错误描述！"+ckResult.result);
                    }
                }
            });
        }
        return mClt;
    }

    public void initVideoAttr() {
        if (mClt == null) {
            return;
        }

        int formate = MyApplication.getInstance().getIntSharedXml(
                MsgKey.KEY_VFORMAT, MsgKey.VIDEO_HD);
        CommFunc.PrintLog(5, LOGTAG,
                "initVideoAttr setVideoAttr" + formate);

        switch (formate) {
            case 1:
                mClt.setVideoAttr(RtcConst.Video_FL);
                break;
            case 2:
                mClt.setVideoAttr(RtcConst.Video_HD);
                break;
            case 3:
                mClt.setVideoAttr(RtcConst.Video_720P);
                break;
            case 4:
                mClt.setVideoAttr(RtcConst.Video_1080P);
                break;
            case 5:
                mClt.setVideoAttr(RtcConst.Video_HD_Landscape);
                break;
            case 6:
                mClt.setVideoAttr(RtcConst.Video_720P_Landscape);
                break;
            case 7:
                mClt.setVideoAttr(RtcConst.Video_1080P_Landscape);
                break;
            default:
                mClt.setVideoAttr(RtcConst.Video_SD);
                break;
        }

    }

    private void destroyRtcClient() {
        if (mCall != null) {
            mCall.disconnect();
            mCall = null;
        }
        if (mAcc != null) {
            mAcc.release();
            mAcc = null;
        }
        if (mClt != null) {
            mClt.release();
            mClt = null;
        }
    }

    /**
     * @return
     */
    public static MyApplication getInstance() {
        return (MyApplication) HBaseApp.getInstance();
    }

    public LocalActManager getLocalActManager() {
        if (localactmanager == null) {
            localactmanager = new LocalActManager();
        }
        return localactmanager;
    }

    public HttpManager getHttpManager() {
        if (httpManager == null) {
            httpManager = new HttpManager();
        }
        return httpManager;
    }

    public void destroyHttpManager() {
        httpManager = null;
    }


    /**
     * 获取SQLiteManager
     *
     * @return
     */
    public synchronized SQLiteManager getSqlManager() {
        if (sqLiteManager == null) {
            sqLiteManager = new SQLiteManager();
        }
        return sqLiteManager;
    }

    /**
     * 销毁SQLiteManager
     */
    public synchronized void destroySqlManager() {
        if (sqLiteManager != null) {
            sqLiteManager.clearInstance();
            sqLiteManager = null;
        }
    }

    public void clearSysConfig() {
        sysconfig = null;
    }

    /**
     * @return
     */
    public SysConfig getSysConfig() {
        if (sysconfig == null) {
            sysconfig = new SysConfig();
        }
        return sysconfig;
    }

    public void exit() {
        CommFunc.PrintLog(5, LOGTAG, "exit()");
        try {
            localactmanager.finishActivity();
            mAListener = null;
            mCListener = null;
            stopService(new Intent().setClass(this, ReloginService.class));

            destroyRtcClient();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HBaseApp.post2WorkDelayed(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    sysconfig = null;
                    localactmanager = null;
                    System.exit(0);
                    int pid = android.os.Process.myPid();
                    android.os.Process.killProcess(pid);
                }
            }, 1000);
        }
    }

    public void saveSharePrefValue(String key, String value) {
        SharedPreferences sp = getSharedPreferences(SysConfig.SHARE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getSharePrefValue(String key, String defaultvalue) {
        SharedPreferences sp = getSharedPreferences(SysConfig.SHARE_NAME, Activity.MODE_PRIVATE);
        if (key.equals(MsgKey.KEY_ACODEC)) {
            return sp.getString(key, "" + MsgKey.ACODEC_ILBC);
        } else if (key.equals(MsgKey.KEY_VCODEC)) {
            return sp.getString(key, "" + MsgKey.VCODEC_VP8);
        } else if (key.equals(MsgKey.KEY_VFRAMES)) {
            return sp.getString(key, "8");
        } else {
            return sp.getString(key, defaultvalue);
        }

    }

    public void saveDataToSharedXml(String[] key, Object[] value) {
        SharedPreferences sharedPreferences_share = getSharedPreferences(SysConfig.SHARE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor shared_editor = sharedPreferences_share.edit();

        for (int i = 0; i < key.length; i++) {
            if (value[i] instanceof String) {
                shared_editor.putString(key[i], (String) value[i]);
            } else if (value[i] instanceof Boolean) {
                shared_editor.putBoolean(key[i], (Boolean) value[i]);
            } else if (value[i] instanceof Integer) {
                shared_editor.putInt(key[i], (Integer) value[i]);
            }
        }
        shared_editor.commit();
    }

    /**
     * 获取xml中的boolean类型
     *
     * @param key
     * @param defValue
     *            没有值时给的默认值
     * @return
     */
    // public boolean getBooleanSharedXml(String key, boolean defValue) {
    // SharedPreferences sp = getSharedPreferences(SysConfig.SHARE_NAME,
    // Activity.MODE_PRIVATE);
    // String ret = sp.getString(key, (defValue==false)?"0":"1");
    // if(ret!=null && ret.equals("1"))
    // {
    // return true;
    // }
    // return false;
    // }

    /**
     * 获取xml中的int类型
     *
     * @param key
     * @param defValue 没有取到值时给的默认值
     * @return
     */
    public int getIntSharedXml(String key, int defValue) {
        SharedPreferences sp = getSharedPreferences(SysConfig.SHARE_NAME, Activity.MODE_PRIVATE);
        String ret = sp.getString(key, "" + defValue);
        if (ret != null && ret.equals("") == false) {
            return Integer.parseInt(ret);
        }
        return defValue;
    }

    /**
     * 获取xml中的String类型
     *
     * @param key
     * @param defValue 没有值时给的默认值
     * @return
     */
    public String getStringSharedXml(String key, String defValue) {
        SharedPreferences sp = getSharedPreferences(SysConfig.SHARE_NAME, Activity.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    public String getShare() {
        if (sysconfig == null) {
            return "temp";
        } else {
            return getAppAccountID();
        }
    }

    public String getVersionName() {
        SharedPreferences sp = getSharedPreferences(SysConfig.SHARE_NAME, Activity.MODE_PRIVATE);
        String a = sp.getString(MsgKey.key_version_name, "1.0");
        return sp.getString(MsgKey.key_version_name, "1.0");
    }

    public void setVersionName(String versionName) {
        SharedPreferences sp = getSharedPreferences(SysConfig.SHARE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(MsgKey.key_version_name, versionName);
        editor.commit();
    }

    public String getAppVersionName() {
        String version = "1.0.0";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    DeviceListener mAListener = new DeviceListener() {
        @Override
        public void onDeviceStateChanged(int result) {
            CommFunc.PrintLog(5, LOGTAG, "onDeviceStateChanged,result=" + result);
            if (result == RtcConst.CallCode_Success) { // 此处需要区分注册还是注销

                sendBroadcastValue(SysConfig.MSG_SIP_REGISTER, result, " 注册成功");
                if (mAcc != null) {
                    grpmgr = mAcc.getGroup();
                    grpmgr.setGroupCallListener(null);
                }
            } else if (result == RtcConst.NoNetwork) {
                CommFunc.PrintLog(5, LOGTAG, "onDeviceStateChanged onNoNetWork");
                onNoNetWork();
                // getRtcClientSip(); //销毁完后需要重新启动监听保留
                sendBroadNetWorkChange(result); // 用于关闭会议或电话页面
            } else if (result == RtcConst.ChangeNetwork) {
                CommFunc.PrintLog(5, LOGTAG, "onDeviceStateChanged ChangeNetWork");
                // ChangeNetWork();
                sendBroadNetWorkChange(result); // 用于关闭会议或电话页面
            } else if (result == RtcConst.PoorNetwork) {
                CommFunc.PrintLog(5, LOGTAG, "onDeviceStateChanged PoorNetwork");

            } else if (result == RtcConst.ReLoginNetwork) {
                CommFunc.PrintLog(5, LOGTAG, "onDeviceStateChanged ReLoginNetwork");
                ReloginNetWork();
            } else if (result == RtcConst.DeviceEvt_KickedOff) {
                CommFunc.PrintLog(5, LOGTAG, "onDeviceStateChanged DeviceEvt_KickedOff");
                OnKickedOff();
            } else if (result == RtcConst.DeviceEvt_MultiLogin) {
                CommFunc.PrintLog(5, LOGTAG, "onDeviceStateChanged DeviceEvt_MultiLogin");
                HBaseApp.post2UIRunnable(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        CommFunc.DisplayToast(MyApplication.this, "该账号在其他终端类型登陆");
                    }
                });
            } else {
                sendBroadcastValue(SysConfig.MSG_SIP_REGISTER, result, "注册失败");
            }
        }



        @Override
        public void onSendIm(int nStatus) {
        }

        @Override
        public void onReceiveIm(String from, String mime, String content) {
        }

        private void OnKickedOff() {
            destroyRtcClient();
            Intent intent = new Intent(MsgKey.key_msg_kickoff);
            sendBroadcast(intent);

        }

        private void sendBroadNetWorkChange(int result) {
            Intent intent = new Intent(CallingActivity.BROADCAST_CALLING_ACTION);
            intent.putExtra("what", MsgKey.SLN_NetWorkChange);
            intent.putExtra("arg1", result); //
            intent.putExtra("arg2", MsgKey.broadmsg_sip); // 用作多人区分消息
            sendBroadcast(intent);
        }

        private void onNoNetWork() {
            CommFunc.PrintLog(5, LOGTAG, "onNoNetWork");
            ReloginService.getInstance().removeLoginAlarm();
            SysConfig.getInstance().setmLoginOK(false);
            SysConfig.getInstance().setCalling(false);
            SysConfig.getInstance().setbIncoming(false);
            // 当前没有呼叫 呼叫标志位销毁
            // 断网销毁
            if (mCall != null) {
                mCall.disconnect();
                mCall = null;
            }
            if (mGroupCall != null) {
                mGroupCall.disconnect();
                mGroupCall = null;
            }

        }

        public void ChangeNetWork() {
            CommFunc.PrintLog(5, LOGTAG, "ChangeNetWork");
            // onNoNetWork();
            // 账户销毁如果放在无网络时销毁将不能监听到网络状态通知，监听后在销毁处理
            // if (mAcc!=null) {
            // mAcc.release();
            // mAcc = null;
            // }
            // ReloginService.getInstance().StartAlarmTimer();

        }

        private void ReloginNetWork() {
            onNoNetWork();
            // 账户销毁如果放在无网络时销毁将不能监听到网络状态通知，监听后在销毁处理
            if (mAcc != null) {
                mAcc.release();
                mAcc = null;
            }
            if (SysConfig.getInstance().isLoginByBtn() == false) {
                ReloginService.getInstance().StartAlarmTimer();
            }
        }

        @Override
        public void onNewCall(Connection call) {
            CommFunc.PrintLog(5, LOGTAG, "DeviceListener onNewCall,call=" + call.info());
            if (mCall != null || mGroupCall != null) {
                call.reject();
                call = null;
                CommFunc.PrintLog(5, LOGTAG, "DeviceListener onNewCall,reject call");
                return;
            }

            SysConfig.getInstance().setbIncoming(true);
            mCall = call;
            call.setIncomingListener(mCListener);
            onIncomingCall(mCall);

        }

        @Override
        public void onQueryStatus(int status, String paramers) {
            // TODO Auto-generated method stub

			/*
			 * { "userStatusList": [ { "presenceTime": "2014-03-10",
			 * "othOnlineInfoList": null, "status": -1, "appAccountId":
			 * "10-13691261873~123~any" }, { "presenceTime": "2014-03-20",
			 * "othOnlineInfoList": null, "status": -1, "appAccountId":
			 * "10-5662~123~any" } ], "reason": "查询成功", "code": "0",
			 * "requestId": "2014-03-20 14:04:12:012" }
			 */
            // wwyue

            CommFunc.PrintLog(5, LOGTAG, "DeviceListener onQueryStatus,status:" + status);
            CommFunc.PrintLog(5, LOGTAG, "DeviceListener onQueryStatus,paramers:" + paramers);
        }
    };



    private Device mAcc = null; // sipreg
    private GroupMgr grpmgr;

    public GroupMgr getGrpMgr() {
        return grpmgr;
    }

    public void setConfMgr(GroupMgr confMgr) {
        grpmgr = confMgr;
    }

    public DeviceListener getDeviceListener() {
        return mAListener;
    }

    private Connection mCall;

    public Connection getMCall() // 获取点对点呼叫对象
    {
        return mCall;
    }

    ConnectionListener mCListener = new ConnectionListener() {
        @Override
        public void onConnecting() {
            CommFunc.PrintLog(5, LOGTAG, "mCListener onConnecting");
            Intent intent = new Intent(CallingActivity.BROADCAST_CALLING_ACTION);
            intent.putExtra("what", MsgKey.SLN_180Ring);
            intent.putExtra("arg2", MsgKey.broadmsg_sip);
            MyApplication.getInstance().sendBroadcast(intent);
        }

        @Override
        public void onConnected() {
            SysConfig.getInstance().setCalling(true);
            CommFunc.PrintLog(5, LOGTAG, "mCListener onConnected");
            Intent intent = new Intent(CallingActivity.BROADCAST_CALLING_ACTION);
            intent.putExtra("what", MsgKey.SLN_CallAccepted);
            intent.putExtra("arg2", MsgKey.broadmsg_sip);
            MyApplication.getInstance().sendBroadcast(intent);
        }

        @Override
        public void onDisconnected(int code) {
            if (mCall != null) {
                CommFunc.PrintLog(5, LOGTAG, "onCallHangup timerDur" + mCall.getCallDuration());
                CommFunc.PrintLog(5, LOGTAG, "onCallHangup timerDur" + FormatTime(mCall.getCallDuration()));
            }
            SysConfig.getInstance().setCalling(false);
            SysConfig.getInstance().setbIncoming(false);
            CommFunc.PrintLog(5, LOGTAG, "mCListener onDisconnect,code=" + code);
            Intent intent = new Intent(CallingActivity.BROADCAST_CALLING_ACTION);
            if (code == 200) // 此处需要确认disconnect 状态 正常挂断和呼叫失败
            {
                intent.putExtra("what", MsgKey.SLN_CallClosed);
            } else if (code == RtcConst.CallCode_hasAccepted) {
                intent.putExtra("what", MsgKey.SLN_CallHasAccepted);
            } else {
                intent.putExtra("what", MsgKey.SLN_CallFailed);
            }
            intent.putExtra("arg1", code);
            intent.putExtra("arg2", MsgKey.broadmsg_sip);
            MyApplication.getInstance().sendBroadcast(intent);
            mCall = null;

        }

        @Override
        public void onVideo() {
            CommFunc.PrintLog(5, LOGTAG, "onVideo");
            Intent intent = new Intent(CallingActivity.BROADCAST_CALLING_ACTION);
            intent.putExtra("what", MsgKey.SLN_CallVideo);
            intent.putExtra("arg2", MsgKey.broadmsg_sip);
            MyApplication.getInstance().sendBroadcast(intent);

        }

        @Override
        public void onNetStatus(int msg, String info) {
            // TODO Auto-generated method stub
            // CommFunc.PrintLog(5, LOGTAG,
            // "onNetStatus msg:"+msg+" info:"+info);
            // Log.e("Application", "onNetStatus msg:"+msg+" info:"+info);
            // by cpl
            Intent intent = new Intent(CallingActivity.BROADCAST_CALLING_ACTION);
            intent.putExtra("what", MsgKey.SLN_WebRTCStatus);
            intent.putExtra("arg1", msg); //
            intent.putExtra("arg2", 0);
            intent.putExtra("info", info);
            MyApplication.getInstance().sendBroadcast(intent);
        }
    };

    public void buildVideo(SurfaceView mvRemote) {
        if (mCall != null) {
            mCall.buildVideo(mvRemote);
        }
    }

    public boolean OnLoginFailReLogin() {
        if (SysConfig.getInstance().ismLoginOK() == false) {
            CommFunc.DisplayToast(this, "连接已断开正在登陆中");
            ReloginService.getInstance().StartAlarmTimer();
            return true;
        }
        return false;
    }

    public void CreateConf(String params) {
        CommFunc.PrintLog(5, LOGTAG, "CreateConf: " + params);
        // CommFunc.PrintLog(1, LOGTAG, "CreateConf:"+params);
        if (OnLoginFailReLogin()) {
            return;
        }
        if (mAcc == null) {
            CommFunc.PrintLog(5, LOGTAG, "CreateConf  mAcc == null");
            return;
        }
        grpmgr = mAcc.getGroup();
        grpmgr.setGroupCallListener(null);
        grpmgr.groupCall(RtcConst.groupcall_opt_create, params);

    }

    public void QueryStatus(String parameters) {
        CommFunc.PrintLog(5, LOGTAG, "parameters:" + parameters);
        if (mAcc != null) {
            mAcc.queryStatus(parameters);
        }
    }

    public void MakeCall(String remoteuser, int calltype) {
        if (OnLoginFailReLogin()) {
            return;
        }
        String remoteuri = "";
        remoteuri = RtcRules.UserToRemoteUri_new(remoteuser, RtcConst.UEType_Any);
        CommFunc.PrintLog(5, LOGTAG, "MakeCall user:" + remoteuser + "calltype:" + calltype + " remoteuri: " + remoteuri);
        SysConfig.getInstance().setbIncoming(false);
        JSONObject jinfo = new JSONObject();
        try {
            jinfo.put(RtcConst.kCallRemoteUri, remoteuri);
            jinfo.put(RtcConst.kCallType, calltype);
            mCall = mAcc.connect(jinfo.toString(), mCListener);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void onCallAccept() {

        int calltype = SysConfig.getInstance().getCallType();
        CommFunc.PrintLog(5, LOGTAG, "onCallAccept():" + calltype); // SysConfig.getInstance().isbIncoming()&&
        if (mCall != null) {
            SysConfig.getInstance().setbIncoming(false);
            mCall.accept(SysConfig.getInstance().getCallType());
            CommFunc.PrintLog(5, LOGTAG, "onBtnCall mIncoming accept:" + calltype);
        }
    }

    public void onCallHangup() {
        CommFunc.PrintLog(5, LOGTAG, "onCallHangup()");
        if (mCall != null) {
            mCall.disconnect();

            CommFunc.PrintLog(5, LOGTAG, "onCallHangup timerDur" + mCall.getCallDuration());
            CommFunc.PrintLog(5, LOGTAG, "onCallHangup timerDur" + FormatTime(mCall.getCallDuration()));
            mCall = null;
            CommFunc.PrintLog(5, LOGTAG, "onCallHangup disconnect");
        }
    }

    public String FormatTime(long date) {
        if (date != 0) { // kk:mm:ss
            long l = date;
            long day = l / (24 * 60 * 60 * 1000);
            long hour = (l / (60 * 60 * 1000) - day * 24);
            long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
            long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            System.out.println("" + day + "天" + hour + "小时" + min + "分" + s + "秒");
            String time = "hh:" + hour + "mm:" + min + "ss:" + s;
            return time;
        }
        return null;
    }

    public int srtp_mode = 1;

    public void disposeSipRegister() {
        Toast.makeText(MyApplication.this, "diaposeSipRegister", Toast.LENGTH_SHORT).show();

        if (mAcc != null) {
            mAcc.release();
            mAcc = null;
        }
        CommFunc.PrintLog(5, LOGTAG, "disposeSipRegister:" + getAccountInfo().getUserid() + "token:" + getAccountInfo().getResttoken());
        try {

            final JSONObject jargs = SdkSettings.defaultDeviceSetting();
            jargs.put(RtcConst.kAccAppID, SysConfig.APP_ID);
            jargs.put(RtcConst.kAccPwd, getAccountInfo().getResttoken());
            jargs.put(RtcConst.kAccUser, getUserID());
            jargs.put(RtcConst.kAccType, RtcConst.UEType_Current);
            jargs.put(RtcConst.kAccSrtp, srtp_mode);

            if (mClt != null) {
                Toast.makeText(MyApplication.this, "注册了device", Toast.LENGTH_SHORT).show();
                mAcc = mClt.createDevice(jargs.toString(), mAListener); // 注册
            } else {
                CommFunc.PrintLog(5, LOGTAG, "disposeSipRegister fail mcl == null");
                InitSdk().createDevice(jargs.toString(), mAListener);
            }
            if (mAcc == null) {
                mClt.release();
                mClt = null;
                SdkInit = false;
                CommFunc.PrintLog(5, LOGTAG, "sdk initialize failed");
                Intent intent = new Intent(SysConfig.BROADCAST_RELOGIN_SERVICE);
                intent.putExtra("what", SysConfig.MSG_GETSERVERADRR_FAILED);
                intent.putExtra("arg1", -1);
                intent.putExtra("arg2", "登陆失败,sdk初始化失败！");
                MyApplication.getInstance().sendBroadcast(intent);
            }

        } catch (JSONException e) {
            CommFunc.PrintLog(5, LOGTAG, "disposeSipRegister():JSONException:" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendBroadcastValue(int nCmdID, int nCmdArg, String sExtra) {
        Intent intent = new Intent(SysConfig.BROADCAST_RELOGIN_SERVICE);
        intent.putExtra("what", nCmdID);
        intent.putExtra("arg1", nCmdArg);
        intent.putExtra("arg2", sExtra);
        sendBroadcast(intent);
    }

    public Connection mGroupCall;

    public Connection getMGroupCall() // 获取点对点呼叫对象
    {
        return mGroupCall;
    }

    private ConnectionListener mConfListener = new ConnectionListener() {
        @Override
        public void onConnecting() {
            CommFunc.PrintLog(5, LOGTAG, "ConnectionListener mConfListener :onConnecting ");
            CommFunc.PrintLog(5, LOGTAG, "onConnecting");
            Intent intent = new Intent(CallingActivity.BROADCAST_CALLING_ACTION);
            intent.putExtra("what", MsgKey.SLN_180Ring);
            intent.putExtra("arg2", MsgKey.broadmsg_sip);
            MyApplication.getInstance().sendBroadcast(intent);
        }

        @Override
        public void onConnected() {
            CommFunc.PrintLog(5, LOGTAG, "ConnectionListener mConfListener onConnected");
            CommFunc.PrintLog(5, LOGTAG, "onConnected");
            Intent intent = new Intent(CallingActivity.BROADCAST_CALLING_ACTION);
            intent.putExtra("what", MsgKey.SLN_CallAccepted);
            intent.putExtra("arg2", MsgKey.broadmsg_sip);
            MyApplication.getInstance().sendBroadcast(intent);

        }

        @Override
        public void onDisconnected(int code) {
            CommFunc.PrintLog(5, LOGTAG, "mConfListener  onDisconnect,code=" + code);
            Intent intent = new Intent(CallingActivity.BROADCAST_CALLING_ACTION);
            if (code == 200) // 此处需要确认disconnect 状态 正常挂断和呼叫失败
            {
                intent.putExtra("what", MsgKey.SLN_CallClosed);
            } else {
                intent.putExtra("what", MsgKey.SLN_CallFailed);
            }
            intent.putExtra("arg1", code);
            intent.putExtra("arg2", MsgKey.broadmsg_sip);
            MyApplication.getInstance().sendBroadcast(intent);
            mGroupCall = null;
            // //此处处理会议挂断

        }

        @Override
        public void onVideo() {
            CommFunc.PrintLog(5, LOGTAG, "ConnectionListener:onVideo");
            CommFunc.PrintLog(5, LOGTAG, "onVideo");
            Intent intent = new Intent(CallingActivity.BROADCAST_CALLING_ACTION);
            intent.putExtra("what", MsgKey.SLN_CallVideo);
            intent.putExtra("arg2", MsgKey.broadmsg_sip);
            MyApplication.getInstance().sendBroadcast(intent);
            // 视频会议处理
        }

        @Override
        public void onNetStatus(int msg, String info) {
            // TODO Auto-generated method stub
            // CommFunc.PrintLog(5, LOGTAG,
            // "onNetStatus msg:"+msg+" info:"+info);
            // Log.e("MyApplication", "onNetStatus msg:"+msg+" info:"+info);
            // by cpl
            Intent intent = new Intent(CallingActivity.BROADCAST_CALLING_ACTION);
            intent.putExtra("what", MsgKey.SLN_WebRTCStatus);
            intent.putExtra("arg1", msg); //
            intent.putExtra("arg2", 0);
            MyApplication.getInstance().sendBroadcast(intent);
        }
    };




}









