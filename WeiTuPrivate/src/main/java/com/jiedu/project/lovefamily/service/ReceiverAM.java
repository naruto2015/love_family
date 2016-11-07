package com.jiedu.project.lovefamily.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jiedu.project.lovefamily.activity.HomeActivity;
import com.jiedu.project.lovefamily.application.MyApplication;

import java.util.Timer;
import java.util.TimerTask;

public class ReceiverAM extends BroadcastReceiver {
    public ReceiverAM() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");

        Log.e("0012", "Service1 Run: " + System.currentTimeMillis());
        boolean b = HomeActivity.isServiceWorked(MyApplication.getInstance(), "com.jiedu.project.lovefamily.service.Service2");
        if (!b) {
            Intent service = new Intent(MyApplication.getInstance(), Service2.class);
            MyApplication.getInstance().startService(service);
            Log.e("0012", "Start Service2");
        }

//        //守护service2
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//              //  PendingIntent pend = PendingIntent.getService(Service1.this,0,new Intent(Service1.this,Service1.class), 0);
//              //  AlarmManager am =(AlarmManager)Service1.this.getSystemService(Context.ALARM_SERVICE);
//              //  long firstTime = SystemClock.elapsedRealtime();
//              //  am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 1000*60*15, pend);
//                Log.e("0012","Service1守护 ---》Service2");
//                Timer timer = new Timer();
//                TimerTask task = new TimerTask() {
//
//                    @Override
//                    public void run() {
//                        Log.e("0012", "Service1 Run: " + System.currentTimeMillis());
//                        boolean b = HomeActivity.isServiceWorked(MyApplication.getInstance(), "com.jiedu.project.lovefamily.service.Service2");
//                        if (!b) {
//                            Intent service = new Intent(MyApplication.getInstance(), Service2.class);
//                            MyApplication.getInstance().startService(service);
//                            Log.e("0012", "Start Service2");
//                        }
//                    }
//                };
//                timer.schedule(task, 0, 1000*60);
//            }
//        }).start();

    }
}
