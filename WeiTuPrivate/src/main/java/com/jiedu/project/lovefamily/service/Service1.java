package com.jiedu.project.lovefamily.service;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.baidu.location.BDLocation;
import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.activity.HomeActivity;
import com.jiedu.project.lovefamily.locationutil.BaiDuSimpleLocationHelp;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.jiedu.project.lovefamily.sharedPreferences.SharedPreferencesUtil;
import com.jiedu.project.lovefamily.sqlite.UserDao;
import com.jiedu.project.lovefamily.sqlite.UserDaoImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/4/5.
 */
public class Service1 extends Service{

    BaiDuSimpleLocationHelp baiDuSimpleLocationHelp;
    BDLocation location;
    RequestHelp requestHelp;

    private AlarmManager mAlarmManager = null;
    private PendingIntent mPendingIntent = null;

    private PowerManager.WakeLock wakeLock;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags=START_STICKY;

        startGetLocationThread2();

        //守护service2
        new Thread(new Runnable() {

            @Override
            public void run() {

//                PendingIntent pend = PendingIntent.getService(Service1.this,0,new Intent(Service1.this,Service1.class), 0);
//                AlarmManager am =(AlarmManager)Service1.this.getSystemService(Context.ALARM_SERVICE);
//                long firstTime = SystemClock.elapsedRealtime();
//                am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 1000*60*15, pend);
                Log.e("0012","Service1守护 ---》Service2");
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {

                    @Override
                    public void run() {
                        Log.e("0012", "Service1 Run: " + System.currentTimeMillis());
                        boolean b = HomeActivity.isServiceWorked(Service1.this, "com.jiedu.project.lovefamily.service.Service2");
                        if (!b) {
                            Intent service = new Intent(Service1.this, Service2.class);
                            startService(service);
                            Log.e("0012", "Start Service2");
                        }
                    }
                };
                timer.schedule(task, 0, 1000*60*20);
            }
        }).start();

        return START_STICKY;
    }


    private PowerManager.WakeLock mWakeLock;
   /* //申请设备电源锁
    private void acquireWakeLock(Context context)
    {
        if (null == mWakeLock)
        {
            PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, "TAG");
            if (null != mWakeLock)
            {
                mWakeLock.acquire();
            }
        }
    }*/
    //释放设备电源锁
    private void releaseWakeLock()
    {
        if (null != mWakeLock)
        {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    int isUploadLocation=3;

    UserDao userDao=null;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();
        //Log.e("TAG","-----------onCreate");

        userDao=new UserDaoImpl(getApplicationContext());
        Intent notificationIntent= new Intent(Service1.this,Service1.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),0,notificationIntent,0);
        Notification notification=new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.logo)
                .setTicker("获取位置信息")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("爱家在线")
                .setContentText("获取位置信息")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        startForeground(1,notification);


        IntentFilter intentFilterIsUp=new IntentFilter();
        intentFilterIsUp.addCategory(Intent.CATEGORY_DEFAULT);
        intentFilterIsUp.addAction("receiverFrequency");

        registerReceiver(receiverFrequency,intentFilterIsUp);

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myLock");
        wakeLock.acquire();
        //acquireWakeLock(getApplicationContext());
        //start the service through alarm repeatly
      /*  Intent intent = new Intent(getApplicationContext(), Service1.class);
        mAlarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        mPendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        long now = System.currentTimeMillis();
        mAlarmManager.setInexactRepeating(AlarmManager.RTC, now, 60000, mPendingIntent);*/

        PendingIntent pend = PendingIntent.getService(this,0,new Intent(this,Service1.class), 0);
        AlarmManager am =(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        long firstTime = SystemClock.elapsedRealtime();
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 1000*60*20, pend);

        baiDuSimpleLocationHelp=new BaiDuSimpleLocationHelp();
        requestHelp=new RequestHelp();
        if(userDao.selectUserInfo().getUserName()!=null && userDao.selectUserInfo().getUserName().trim()!=""){
            Log.e("oncreate_userdao",userDao.selectUserInfo().getUserName());

            baiDuSimpleLocationHelp.startGetLocation(Service1.this, handler);
        }


       new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    HashMap map=new HashMap<String,String>();
                    map.put("customerId",SharedPreferencesUtil.getInfo(getApplicationContext(),"customerId"));
                    String json=requestHelp.requestPost(RequestHelp.QUERY_MESSAGE_COUNT,map);
                    if(json!=null){
                        Message msg=Message.obtain();
                        msg.obj=json;
                        msg.what=1;
                        handler2.sendMessage(msg);
                    }
                    try {
                        Thread.sleep(10*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        //startGetLocationThread();
    }

    Handler handler2=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    try {
                        JSONObject jsonObject=new JSONObject(msg.obj.toString());
                        int message_position=jsonObject.optInt("mp");
                        if(message_position!=0){
                            startGetLocationThread2();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    };

    BroadcastReceiver receiverFrequency=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            frequency=intent.getIntExtra("frequency",60);
            Log.e("0011","广播接收"+frequency);
        }
    };

    private int frequency=60;

    public void startGetLocationThread2(){

        if(userDao.selectUserInfo().getUserName()!=null && userDao.selectUserInfo().getUserName().trim()!=""){
            Log.e("oncreate_onstartmand",userDao.selectUserInfo().getUserName());
            baiDuSimpleLocationHelp.restartGetLocation();
        }


    }

    public void startGetLocationThread(){
        frequency=Integer.valueOf(SharedPreferencesUtil.getInfo(getApplicationContext(), "frequency"));
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){

                    try {
                        //if(/*Integer.valueOf(SharedPreferencesUtil.getInfo(Service1.this,"frequency"))!=0*/!SharedPreferencesUtil.getInfo(Service1.this,"frequency").equals("0")){
                        //如果内存中有上传位置时间，那么间隔固定时间上传位置
                       /* Log.e("0011","上传位置时间间隔"+(SharedPreferencesUtil.getInfo(getApplicationContext(), "frequency")));
                        Thread.sleep(Integer.valueOf(SharedPreferencesUtil.getInfo(Service1.this, "frequency"))*1000);*/
                        Log.e("0011","上传位置时间间隔"+frequency);
                        Thread.sleep(frequency*1000);
                        baiDuSimpleLocationHelp.restartGetLocation();
                        // }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    /*isUploadLocation=Integer.valueOf(SharedPreferencesUtil.getInfo(Service1.this,"isUploadLocation"));
                    Log.e("TAG","是否应该上传位置信息isUploadLocation："+isUploadLocation+"");
                    if(isUploadLocation==0){
                        try {
                            //if(*//*Integer.valueOf(SharedPreferencesUtil.getInfo(Service1.this,"frequency"))!=0*//*!SharedPreferencesUtil.getInfo(Service1.this,"frequency").equals("0")){
                                //如果内存中有上传位置时间，那么间隔固定时间上传位置
                                Log.e("0011","上传位置时间间隔"+(SharedPreferencesUtil.getInfo(Service1.this, "frequency")));
                                Thread.sleep(Integer.valueOf(SharedPreferencesUtil.getInfo(Service1.this, "frequency"))*1000);

                                baiDuSimpleLocationHelp.restartGetLocation();
                           // }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        try {
                            Thread.sleep(2*10000);
                            Log.e("TAG","位置上传被暂停");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }*/
                }
            }


        }).start();
    }


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.arg1){
                case BaiDuSimpleLocationHelp.getLocationSucceed:
                    location= (BDLocation) msg.obj;
                    new Thread(new Runnable() {
                        public void run() {
                            String result= requestHelp.upLocation(SharedPreferencesUtil.getInfo(getApplicationContext(), "customerId"),location.getLatitude(),location.getLongitude());
                            Log.e("0011","上传位置结果"+result);
                            SharedPreferences sp=getSharedPreferences("location", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sp.edit();
                            editor.putString("getLatitude",location.getLatitude()+"");
                            editor.putString("getLongitude",location.getLongitude()+"");
                            editor.commit();

                        }
                    }).start();
                    break;
            }

        }
    };


    @Override
    public void onDestroy() {
        //releaseWakeLock();
        Log.e("TAG","----------服务被销毁!!!");
        unregisterReceiver(receiverFrequency);
        super.onDestroy();
    }


}
