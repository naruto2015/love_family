package com.jiedu.project.lovefamily.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.bean.TCallRecordInfo;

import org.json.JSONException;
import org.json.JSONObject;

import jni.util.Utils;
import rtc.sdk.common.RtcConst;
import rtc.sdk.core.RtcRules;
import rtc.sdk.iface.Connection;
import rtc.sdk.iface.ConnectionListener;
import rtc.sdk.iface.Device;
import rtc.sdk.iface.RtcClient;

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
    private Button cancle_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vedio);

        initview();
        initData();
    }
    private void initview() {
        button= (ImageView) findViewById(R.id.button);
        editText= (EditText) findViewById(R.id.edit_vedio);
        editText2= (EditText) findViewById(R.id.edit_vedio2);
        layoutlocal = (LinearLayout) findViewById(R.id.ll_local);
        layoutremote = (LinearLayout) findViewById(R.id.ll_remote);
        cancle_phone= (Button) findViewById(R.id.cancle_phone);
    }

    private void initData() {
        Intent intent = getIntent();
        toUser = intent.getStringExtra("touser");
         fromUser = intent.getStringExtra("fromuser");
        editText.setText(toUser);
        editText2.setText(fromUser);

    }
    public void onBtnHangup(View viw) {
        if (mCall!=null) {
            mCall.disconnect();
            Utils.PrintLog(5, LOGTAG, "onBtnHangup timerDur"+mCall.getCallDuration());
            mCall = null;
            setVideoSurfaceVisibility(View.INVISIBLE);
//            setBtnText(R.id.bt_Call,"Call");
        }
//        setStatusText("call hangup ");
    }
    void setVideoSurfaceVisibility(int visible) {
        if(mvLocal!=null)
            mvLocal.setVisibility(visible);
        if(mvRemote!=null)
            mvRemote.setVisibility(visible);
    }



    /**
     * 视频呼叫
     * @param view
     */
    RtcClient mClt;
    Device mAcc = null;  //reg
    boolean mIncoming = false;
    int mCT = RtcConst.CallType_A_V;

    public void onBtnCall(View view) {
        Utils.PrintLog(5,"onBtnCall", "onBtnCall(): transtype"+ RtcConst.TransType);
        if (mAcc==null)
            return;
        if (mCall==null) {
            mIncoming = false;
            try {
                //;transport=tls
                String remoteuri = "";
                remoteuri = RtcRules.UserToRemoteUri_new(toUser, RtcConst.UEType_Any);
                JSONObject jinfo = new JSONObject();
                jinfo.put(RtcConst.kCallRemoteUri,remoteuri);
                jinfo.put(RtcConst.kCallInfo,"逍遙神龍--->"); //opt
                jinfo.put(RtcConst.kCallType,mCT);
                mCall = mAcc.connect(jinfo.toString(),mCListener);
//                setBtnText(R.id.bt_Call,"Calling");
                //IM test
                //mAcc.sendIm(remoteuri, RtcConst.ImText, "聊天吧");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else { // acceptcall
            if (mIncoming) {
                mIncoming = false;
                mCall.accept(mCT); //视频来电可以选择仅音频接听
//                setBtnText(R.id.bt_Call,"Calling");
                Utils.PrintLog(5,LOGTAG, "onBtnCall mIncoming " +
                        "accept(mCT)");
//                setStatusText("ConnectionListener:onConnected");
            }
        }
       // setBtnText(R.id.bt_Call,"Calling");

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.PrintLog(5,LOGTAG, "onDestroy()");
        if (mCall!=null) {
            mCall.disconnect();
            mCall = null;
        }
        if(layoutremote!=null)
            layoutremote.removeAllViews();
        if(layoutlocal!=null)
            layoutlocal.removeAllViews();
        mvLocal = null;
        mvRemote = null;

        if(mAcc!=null) {
            mAcc.release();
            mAcc = null;
        }
        if(mClt!=null) {
            mClt.release();
            mClt = null;
        }
    }

    Connection mCall;

    /** The m c listener. */
    ConnectionListener mCListener = new ConnectionListener() {
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

    Handler mHandler = new Handler() {

    };


    SurfaceView mvLocal = null;
    SurfaceView mvRemote = null;
    LinearLayout layoutlocal;
    LinearLayout layoutremote;

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        if(layoutremote!=null) {
            if(mCall!=null)
                mCall.resetVideoViews();
        }
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}