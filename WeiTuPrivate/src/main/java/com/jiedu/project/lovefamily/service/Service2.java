package com.jiedu.project.lovefamily.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.jiedu.project.lovefamily.activity.HomeActivity;

import java.util.Timer;
import java.util.TimerTask;

public class Service2 extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("0012", "onStartCommand");
        thread.start();
        return START_REDELIVER_INTENT;
    }

    Thread thread = new Thread(new Runnable() {

        @Override
        public void run() {
//            PendingIntent pend = PendingIntent.getService(Service2.this,0,new Intent(Service2.this,Service1.class), 0);
//            AlarmManager am =(AlarmManager)Service2.this.getSystemService(Context.ALARM_SERVICE);
//            long firstTime = SystemClock.elapsedRealtime();
//            am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 1000*20*1, pend);

            Timer timer = new Timer();
            TimerTask task = new TimerTask() {

                @Override
                public void run() {
                    Log.e("0012", "Service2 Run: " + System.currentTimeMillis());
                    boolean b = HomeActivity.isServiceWorked(Service2.this, "com.jiedu.project.lovefamily.service.Service1");
                    if (!b) {
                        Intent service = new Intent(Service2.this, Service1.class);
                        startService(service);
                    }
                }
            };
            timer.schedule(task, 0, 1000*60*20);
        }
    });

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
